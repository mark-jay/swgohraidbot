package org.markjay.services.raidinfoextractor;

import org.apache.pdfbox.rendering.ImageType;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;

/**
 * @author <a href="mailto:mark.jay.mk@gmail.com">mark jay</a>
 * @since 1/7/19 2:50 AM
 */
class ImageResizer {

    BufferedImage resize(BufferedImage source, int destWidth, int destHeight, Object interpolation) {
        if (source == null)
            throw new NullPointerException("source image is NULL!");
        if (destWidth <= 0 && destHeight <= 0)
            throw new IllegalArgumentException("destination width & height are both <=0!");
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        double xScale = ((double) destWidth) / (double) sourceWidth;
        double yScale = ((double) destHeight) / (double) sourceHeight;
        if (destWidth <= 0) {
            xScale = yScale;
            destWidth = (int) Math.rint(xScale * sourceWidth);
        }
        if (destHeight <= 0) {
            yScale = xScale;
            destHeight = (int) Math.rint(yScale * sourceHeight);
        }
//        GraphicsConfiguration gc = getDefaultConfiguration();
        
        BufferedImage result = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_ARGB);
        // gc.createCompatibleImage(destWidth, destHeight, source.getColorModel().getTransparency());
        Graphics2D g2d = null;
        try {
            g2d = result.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, interpolation);
            AffineTransform at =
                    AffineTransform.getScaleInstance(xScale, yScale);
            g2d.drawRenderedImage(source, at);
        } finally {
            if (g2d != null)
                g2d.dispose();
        }
        return result;
    }

    private GraphicsConfiguration getDefaultConfiguration() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        return gd.getDefaultConfiguration();
    }
}
