package org.markjay.services.raidinfoextractor;

import org.jenetics.*;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.util.DoubleRange;
import org.jenetics.util.Factory;
import org.jenetics.util.IntRange;
import org.junit.Assert;
import org.junit.Test;
import org.markjay.domain.RaidInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * Just a useful tool to find the best threshold values
 *
 * @author <a href="mailto:mark.jay.mk@gmail.com">mark jay</a>
 * @since 1/1/19 3:08 PM
 */
public class RaidInfoExtractorGenSearchTest {

    private static final Logger log = Logger.getLogger(RaidInfoExtractorGenSearchTest.class.getName());

    int i = 0;

    public final int evalTotalDamage(Genotype<DoubleGene> genotype) {
        int gene1Fit = genotype.get(0, 0).intValue();
        int gene2Fit = genotype.get(1, 0).intValue();
        int gene3Fit = genotype.get(2, 0).intValue();
        double gene4Fit = genotype.get(3, 0).doubleValue();
        int number = this.i++;
        log.info("number " + number + " started(" + gene1Fit + ", " + gene2Fit + ", " + gene3Fit + ", " + gene4Fit + ")");
        RaidInfoExtractorConfig config = new RaidInfoExtractorConfig(gene1Fit, gene2Fit, gene3Fit, 127, gene4Fit);
        int result = -countFails(new RaidInfoExtractor(config));
        log.info("number " + number + " ended with result = " + result);
        return result;
    }

    @Test
    public void extractFromImage() throws Exception {
        Factory<Genotype<DoubleGene>> gtf = Genotype.of(
                DoubleChromosome.of(DoubleRange.of(120, 190)),
                DoubleChromosome.of(DoubleRange.of(100, 160)),
                DoubleChromosome.of(DoubleRange.of(20, 90)),
                DoubleChromosome.of(DoubleRange.of(70, 130))
        );

        Engine<DoubleGene, Integer> engine = Engine
                .builder(this::evalTotalDamage, gtf)
                .build();

        Genotype<DoubleGene> result = engine.stream()
                .limit(this::limitByBestFitness)
                .limit(1000)
                .collect(EvolutionResult.toBestGenotype());

        System.out.println("Result:\n" + result);
    }

    private boolean limitByBestFitness(EvolutionResult<DoubleGene, Integer> evolutionResult) {
        Integer bestFitness = evolutionResult.getBestFitness();
        String message = "current best fitness = " + bestFitness;
        System.out.println(message);
        log.info(message);
        return bestFitness < 0;
    }

    private int countFails(RaidInfoExtractor service) {
        int counter = 0;
        for (Object[] params : TestData.getObjects()) {
            try {
                testImage(((String) params[0]), (RaidInfo) params[1], service);
            } catch (Throwable e) {
                counter++;
            }
        }
        return counter;
    }

    private void testImage(String path1, RaidInfo expected, RaidInfoExtractor service) throws Exception {
        InputStream inputStream = RaidInfoExtractorGenSearchTest.class.getClassLoader().getResourceAsStream(path1);
        BufferedImage image = ImageIO.read(inputStream);

        RaidInfo result = service.extractFromImage(image, path1.replaceAll(".*/", ""));
        String message = "Error on case " + path1;
        Assert.assertEquals(message, expected.getDamageDealt(), result.getDamageDealt());
    }
}
