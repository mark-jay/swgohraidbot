package org.markjay.services.raidinfoextractor;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author <a href="mailto:mark.jay.mk@gmail.com">mark jay</a>
 * @since 1/2/19 3:35 AM
 */
@AllArgsConstructor
@Getter
public class RaidInfoExtractorConfig {

    final int greenThreshold;
    final int blueThreshold;
    final int redThreshold;

    final int thresholdPercentageLeft;

    final double targetHeight;
}
