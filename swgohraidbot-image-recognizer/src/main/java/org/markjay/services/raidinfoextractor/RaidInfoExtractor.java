package org.markjay.services.raidinfoextractor;

import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.Word;
import org.markjay.domain.CurrentPhaseInfo;
import org.markjay.domain.RaidInfo;
import org.markjay.exceptions.RaidInfoExtractionException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:mark.jay.mk@gmail.com">mark jay</a>
 * @since 1/1/19 2:48 PM
 */
public class RaidInfoExtractor {

    private final ITesseract instance;

    private static final Logger log = Logger.getLogger(RaidInfoExtractor.class.getName());

    private final RaidInfoExtractorConfig config;

    private final DamageFromTextExtractor damageFromTextExtractor = new DamageFromTextExtractor();

    private final ImageResizer imageResizer = new ImageResizer();

    /**
     * Nullable field to write images to
     */
    private String logFile = "/tmp/out/image-log";

    /**
     * Constructor with default threshold values which was found by {@code RaidInfoExtractorGenSearchTest} in tests in this project
     */
    public RaidInfoExtractor() {
//        this(new RaidInfoExtractorConfig(164, 177, 75, 19));
//        this(new RaidInfoExtractorConfig(167, 176, 86, 19));
//        this(new RaidInfoExtractorConfig(167, 135, 68, 19, 1));
        this(new RaidInfoExtractorConfig(151, 129, 33, 19, 108.78897898526779));
    }

    public RaidInfoExtractor(RaidInfoExtractorConfig config) {
        this.config = config;
        this.instance = new Tesseract();
        this.instance.setDatapath(getTessDataFolder());
        this.instance.setLanguage("eng");
    }

    private String getTessDataFolder() {
        String tessDataEnv = System.getenv("TESSDATA_PREFIX");
        if (tessDataEnv == null || tessDataEnv.isEmpty()) {
            return "/usr/share/tessdata";
        }
        return tessDataEnv;
    }

    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public RaidInfo extractFromImage(BufferedImage originalImage, String fileName) throws IOException, RaidInfoExtractionException {
        try {
            Objects.requireNonNull(originalImage);
//        drawWords(image);
//        drawRect(image);
            BufferedImage image = deepCopy(originalImage);

            // cropping working area
            final String folder = fileName;
            Rectangle workingArea = makeRect(image, 0.2, 0.5, 0.8, 0.65);
            final BufferedImage croppedImage = cropAndPreprocessWorkingArea(image, folder, workingArea);

            // cropping after tesseract
            Word word = extractWord(croppedImage);
//            BufferedImage tesseractCropped = cropByBoundingBox(folder, croppedImage, word);

//            writeImageLog(croppedImage, "./", fileName);

            BufferedImage resized = resizeImageForTextExtraction(croppedImage, word.getBoundingBox(), config);
            writeImageLog(resized, "./", "XXX" + fileName);
            File file = new File("/tmp/temp.png");
            ImageIO.write(resized, "png", file);
            resized = ImageIO.read(file);
            long damage = extractDamage(extractWord(resized));

            // extracting damage bounding box
            Rectangle progressBarRectangle = extractDamageProgressBarRectangle(workingArea, word);
            BufferedImage subimage = originalImage.getSubimage(progressBarRectangle.x, progressBarRectangle.y, progressBarRectangle.width, progressBarRectangle.height);
            double percentage = findPercentage(subimage, config);
            double percentageLeft = 1 - percentage;
            log.info("Percentage left = " + percentageLeft);

            ImageIO.write(subimage, "png", new File("/tmp/out/" + fileName));

            return new RaidInfo(percentageLeft, damage, CurrentPhaseInfo.of(percentageLeft));
        } catch (IOException e) {
            throw new RaidInfoExtractionException(e);
        }
    }

    // TODO: resize coef to a specific size!
    private BufferedImage resizeImageForTextExtraction(BufferedImage image, Rectangle boundingBox, RaidInfoExtractorConfig config) {
        BufferedImage subimage = image.getSubimage(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
        double factor = config.targetHeight / ((double)boundingBox.height);
        int targetWidth = (int) (boundingBox.width * factor);
        int targetHeight = (int) (boundingBox.height * factor);
//        System.out.println("targetHeight = " + targetHeight);
//        System.out.println("targetWidth = " + targetWidth);
//        BufferedImage resized = Scalr.resize(subimage, Scalr.Method.QUALITY, targetWidth, targetHeight);
        BufferedImage resized = imageResizer.resize(
                subimage, targetWidth, targetHeight, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        return resized;
    }

    private double findPercentage(BufferedImage originalImage, RaidInfoExtractorConfig config) {
        int totalFilled = 0;
        final Color notFilledColor = new Color((int)(0.09*255), (int)(0.23*255), (int)(0.34*255));
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        // init array as distances from the target color
        int[][] distances = new int[originalImage.getHeight()][originalImage.getWidth()];
        for (int i = 0; i < originalImage.getHeight(); i++) {
            for (int j = 0; j < originalImage.getWidth(); j++) {
                int rgb = originalImage.getRGB(j, i);
                Color color = new Color(rgb);

                int r = color.getRed() - notFilledColor.getRed();
                int g = color.getGreen() - notFilledColor.getGreen();
                int b = color.getBlue() - notFilledColor.getBlue();

                int res = r*r + g*g + b*b;
                min = Math.min(min, res);
                max = Math.max(max, res);
                distances[i][j] = res;
            }
        }

        // create image in array
        int[][] image = new int[originalImage.getHeight()][originalImage.getWidth()];
        for (int i = 0; i < originalImage.getHeight(); i++) {
            for (int j = 0; j < originalImage.getWidth(); j++) {
                double zeroToOneScaled = ((double)(distances[i][j] - min)) / ((double)(max - min));
                int zetoTo255 = (int) (zeroToOneScaled * 255);

                if (zetoTo255 > 33) {
                    image[i][j] = 255;
                } else {
                    image[i][j] = zetoTo255;
                    totalFilled++;
                }
            }
        }

        // update image using horizontal median filtering to avoid vertical lines from bridges
        int[][] filteredImage = new int[originalImage.getHeight()][originalImage.getWidth()];
        final int length = 6;
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                int start = Math.max(0, j - length);
                int end = Math.min(image[i].length, j + length);
                int[] array = new int[end - start];
                for (int j1 = start; j1 < end; j1++) {
                    array[j1 - start] = image[i][j1];
                }
                Arrays.sort(array);
                int medianValue = array[array.length / 2];
                filteredImage[i][j] = medianValue;
            }
        }

        // draw on the image
        for (int i = 0; i < originalImage.getHeight(); i++) {
            for (int j = 0; j < originalImage.getWidth(); j++) {
                int v = filteredImage[i][j];
                int color = new Color(v, v, v).getRGB();
                originalImage.setRGB(j, i, color);
            }
        }

        double size = originalImage.getHeight() * originalImage.getWidth();
        double res = ((double) totalFilled) / size;
        return res;
    }

    private Rectangle extractDamageProgressBarRectangle(Rectangle workingArea, Word word) {
        Rectangle box = word.getBoundingBox();
        int tweakWidth = (int) (box.getWidth() * 0.034);

        int originImageX = (int) (workingArea.getX() + box.getX());
        int originImageY = (int) (workingArea.getY() + box.getY());
        int width = (int) (box.getWidth() + tweakWidth);
        int height = (int) (box.getHeight() * 0.23);

        int tweakedX = originImageX - (tweakWidth / 2);
        return new Rectangle(tweakedX, originImageY, width, height);
    }

    private BufferedImage preprocessImage(BufferedImage image) {
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                int originalPixel = image.getRGB(j, i);
                Color color = new Color(originalPixel);

                boolean passedThreshold = color.getRed() > config.getRedThreshold() &&
                        color.getGreen() > config.getGreenThreshold() &&
                        color.getBlue() > config.getBlueThreshold();

                image.setRGB(j, i, passedThreshold ? Color.WHITE.getRGB() : Color.BLACK.getRGB());
            }
        }

        return image;
    }

    private long extractDamage(Word word) throws RaidInfoExtractionException {
        String text = word.getText();
        return damageFromTextExtractor.extractDamageFromText(text);
    }

    private BufferedImage cropByBoundingBox(String folder, BufferedImage croppedImage, Word word) throws IOException {
        Rectangle workingArea = word.getBoundingBox();
        BufferedImage tesseractCropped = croppedImage.getSubimage(workingArea.x, 0, workingArea.width, croppedImage.getHeight());
        writeImageLog(tesseractCropped, folder, "tesseractCropped");
        return tesseractCropped;
    }

    private Word extractWord(BufferedImage croppedImage) throws RaidInfoExtractionException {
        Objects.requireNonNull(croppedImage);
        List<Word> words = instance.getWords(croppedImage, ITessAPI.TessPageIteratorLevel.RIL_PARA);
        if (words.size() != 1) {
            throw new RaidInfoExtractionException("Could not extract 'Total damage dealt text from the image: " +
                    "expected to find only a single word, but found = " + words.size() + " : " + words);
        }
        return words.get(0);
    }

    private BufferedImage cropAndPreprocessWorkingArea(BufferedImage image, String folder, Rectangle workingArea) throws IOException {
        final BufferedImage croppedImage = preprocessImage(
                image.getSubimage(workingArea.x, workingArea.y, workingArea.width, workingArea.height));
        writeImageLog(croppedImage, folder, "cropped");
        return croppedImage;
    }

    private void writeImageLog(BufferedImage croppedImage, String folder, String fileName) throws IOException {
        if (logFile != null) {
            String parent = logFile + "/" + folder;
            new File(parent).mkdirs();
            ImageIO.write(croppedImage, "png", new File(parent + "/" + fileName + ".png"));
        }
    }

    private void drawRect(BufferedImage image) {
        drawRect(image, makeRect(image, 0.2, 0.5, 0.8, 0.65));
    }

    private void drawRect(BufferedImage image, Rectangle rect) {
        Graphics2D g2d = image.createGraphics();
        g2d.setStroke(new BasicStroke(5));
        g2d.setColor(Color.RED);
        g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
        g2d.dispose();
    }

    private Rectangle makeRect(BufferedImage image, double x1, double y1, double x2, double y2) {
        int x = (int) (image.getWidth() * x1);
        int y = (int) (image.getHeight() * y1);
        int width = (int) ((x2 - x1) * image.getWidth());
        int height = (int) ((y2 - y1) * image.getHeight());
        return new Rectangle(x, y, width, height);
    }

    private void drawLine(BufferedImage image) {
        Graphics2D g2d = image.createGraphics();
        g2d.setStroke(new BasicStroke(5));
        g2d.setColor(Color.RED);
        g2d.drawLine(0, image.getHeight() / 2, image.getWidth(), image.getHeight() / 2 - 1);
        g2d.dispose();
    }

    private void drawWords(BufferedImage image) {
        List<Word> words = instance.getWords(image, 1);

        int i = 0;
        Color[] colors = {Color.RED, Color.GREEN, Color.BLACK, Color.WHITE, Color.YELLOW, Color.CYAN, Color.MAGENTA};
        drawAllWords(image, words, i, colors);
//        drawAllWordsAsOne(image, words);
    }

    private void drawAllWordsAsOne(BufferedImage image, List<Word> words) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Word word : words) {
            Rectangle box = word.getBoundingBox();

            minX = Math.min(minX, box.x);
            minY = Math.min(minY, box.y);

            maxX = Math.max(maxX, box.x + box.width);
            maxY = Math.max(maxY, box.y + box.height);
        }
        Graphics2D g2d = image.createGraphics();
        g2d.setStroke(new BasicStroke(5));
        g2d.setColor(Color.RED);
        g2d.drawRect(minX, minY, maxX - minX, maxY - minY);
        g2d.dispose();
    }

    private void drawAllWords(BufferedImage image, List<Word> words, int i, Color[] colors) {
        for (Word word : words) {
            System.out.println("------------------------------------------------------");
            System.out.println("word = " + word);

            Graphics2D g2d = image.createGraphics();
            g2d.setColor(colors[i++]);
            Rectangle box = word.getBoundingBox();

//            Stroke oldStroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(5));
            g2d.drawRect(box.x, box.y, box.width, box.height);
            g2d.dispose();
        }
    }

    /*
    private String getImgText(String imageLocation) {
        try {
            File file = new File(imageLocation);
            String imgText = instance.doOCR(file);
            return imgText;
        }
        catch (TesseractException e) {
            e.getMessage();
            return "Error while reading image";
        }
    }
    */

}
