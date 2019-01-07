package org.markjay.services.raidinfoextractor;

import org.markjay.domain.CurrentPhaseInfo;
import org.markjay.domain.RaidInfo;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:mark.jay.mk@gmail.com">mark jay</a>
 * @since 1/2/19 3:22 AM
 */
public class TestData {
    static List<Object[]> getObjects() {
        return Arrays.asList(new Object[][] {
                { "examples/1.png", expectedRaid(0.37, 1023325, 0.5, 3) },
                { "examples/2.jpg", expectedRaid(0.38, 1481074, 0.5, 3) },
                { "examples/3.png", expectedRaid(0.39, 771782, 0.5, 3) },
                { "examples/4.jpg", expectedRaid(0.40, 1077139, 0.5, 3) },
                { "examples/5.jpg", expectedRaid(0.43, 862615, 0.5, 3) },
                { "examples/6.png", expectedRaid(0.53, 2066019, 0.5, 2) },
                { "examples/7.png", expectedRaid(0.54, 1445836, 0.5, 2) },
                { "examples/8.png", expectedRaid(0.62, 3184066, 0.5, 2) },
                { "examples/9.jpg", expectedRaid(0.63, 1376971, 0.5, 2) },
                { "examples/10.jpg", expectedRaid(0.73, 618256, 0.5, 2) },
                { "examples/11.png", expectedRaid(0.70, 1223949, 0.5, 2) },
                { "examples/12.png", expectedRaid(0.71, 2841206, 0.5, 2) },
                { "examples/13.png", expectedRaid(0.77, 3386954, 0.5, 1) },
                { "examples/14.jpg", expectedRaid(0.83, 3073426, 0.5, 1) },
                { "examples/15.png", expectedRaid(0.35, 1281772, 0.4, 3) },
                { "examples/16.jpg", expectedRaid(0.82, 8993269, 0.2, 1) },
                { "examples/17.jpg", expectedRaid(0.87, 3313272, 0.5, 1) },

                { "examples/18.png", expectedRaid(0.58, 711810, 0.2, 2) },
                { "examples/19.jpg", expectedRaid(0.12, 4690713, 0.4, 4) },
                { "examples/20.jpg", expectedRaid(0.39, 1223269, -1, -1) },
                { "examples/21.jpg", expectedRaid(0.46, 7602197, -1, -1) },
                { "examples/22.png", expectedRaid(0.87, 1285867, -1, -1) },
                { "examples/23.jpg", expectedRaid(0.87, 10784557, -1, -1) },
        });
    }

    private static RaidInfo expectedRaid(double totalPercentageLeft, int damageDealt, double percentageLeft, int currentPhaseNumber) {
        return new RaidInfo(totalPercentageLeft, damageDealt, new CurrentPhaseInfo(percentageLeft, currentPhaseNumber));
    }
}
