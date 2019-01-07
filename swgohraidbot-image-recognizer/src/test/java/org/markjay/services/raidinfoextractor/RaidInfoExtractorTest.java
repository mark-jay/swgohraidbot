package org.markjay.services.raidinfoextractor;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.markjay.domain.CurrentPhaseInfo;
import org.markjay.domain.RaidInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Collection;

/**
 * @author <a href="mailto:mark.jay.mk@gmail.com">mark jay</a>
 * @since 1/1/19 3:08 PM
 */
@RunWith(Parameterized.class)
public class RaidInfoExtractorTest {

    @Parameters
    public static Collection<Object[]> data() {
        return TestData.getObjects();
    }

    private String path;
    private RaidInfo expected;

    public RaidInfoExtractorTest(String path, RaidInfo expected) {
        this.path = path;
        this.expected = expected;
    }

    @Test
    public void extractFromImage() throws Exception {
        testImage(path, expected);
    }

    private void testImage(String path1, RaidInfo expected) throws Exception {
        try {
            testImage(path1, expected, 0.05);
        } catch (Exception e) {
            throw new Exception("Exception on testcase " + path1, e);
        }
    }

    private void testImage(String path, RaidInfo expected, double epsilon) throws Exception {
        InputStream inputStream = RaidInfoExtractorTest.class.getClassLoader().getResourceAsStream(path);
        BufferedImage image = ImageIO.read(inputStream);

        RaidInfo result = new RaidInfoExtractor().extractFromImage(image, path.replaceAll(".*/", ""));
        String message = "Error on case " + path;
        Assert.assertEquals(message, expected.getDamageDealt(), result.getDamageDealt());
//        Assert.assertEquals(expected.getTotalPercentage(), result.getTotalPercentage(), epsilon);
//        Assert.assertEquals(expected.getCurrentPhaseInfo().getCurrentPhaseNumber(), result.getCurrentPhaseInfo().getCurrentPhaseNumber());
//        Assert.assertEquals(expected.getCurrentPhaseInfo().getPercentageLeft(), result.getCurrentPhaseInfo().getPercentageLeft(), epsilon);
    }
}
