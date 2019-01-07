package org.markjay.services.raidinfoextractor;

import org.jenetics.Genotype;
import org.jenetics.IntegerChromosome;
import org.jenetics.IntegerGene;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
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
 * Just a useful tool to find the best threshold values for percentage left
 *
 * @author <a href="mailto:mark.jay.mk@gmail.com">mark jay</a>
 * @since 1/1/19 3:08 PM
 */
public class RaidInfoExtractorGenSearchPercentageTest {

    private static final Logger log = Logger.getLogger(RaidInfoExtractorGenSearchPercentageTest.class.getName());

    int i = 0;

    public final int evalTotalDamage(Genotype<IntegerGene> genotype) {
        int gene1Fit = genotype.getChromosome().as(IntegerChromosome.class).getGene(0).intValue();
        log.info("gene1 = " + gene1Fit);
        int number = this.i++;
        log.info("number " + number + " started");
        RaidInfoExtractorConfig config = new RaidInfoExtractorConfig(164, 177, 75, gene1Fit, 1);
        int result = -countFails(new RaidInfoExtractor(config));
        log.info("number " + number + " ended, result was = " + result);
        return result;
    }

    @Test
    public void extractFromImage() throws Exception {
        Factory<Genotype<IntegerGene>> gtf = Genotype.of(
                IntegerChromosome.of(IntRange.of(0, 100), 1));

        Engine<IntegerGene, Integer> engine = Engine
                .builder(this::evalTotalDamage, gtf)
                .build();

        Genotype<IntegerGene> result = engine.stream()
                .limit(4)
                .collect(EvolutionResult.toBestGenotype());

        System.out.println("Result:\n" + result);
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
        InputStream inputStream = RaidInfoExtractorGenSearchPercentageTest.class.getClassLoader().getResourceAsStream(path1);
        BufferedImage image = ImageIO.read(inputStream);

        RaidInfo result = service.extractFromImage(image, path1.replaceAll(".*/", ""));
        String message = "Error on case " + path1;
        Assert.assertEquals(expected.getCurrentPhaseInfo().getCurrentPhaseNumber(), result.getCurrentPhaseInfo().getCurrentPhaseNumber());
    }
}
