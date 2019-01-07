package org.markjay.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * Information about current phase status
 *
 * @author <a href="mailto:mark.jay.mk@gmail.com">mark jay</a>
 * @since 1/1/19 2:59 PM
 */
@AllArgsConstructor
@ToString
@Getter
public class CurrentPhaseInfo {

    /**
     * a number between 0 and 1
     */
    private final double percentageLeft;

    /**
     * a number between 1 and 4
     */
    private final int currentPhaseNumber;

    private static void checkValue(double percentageLeft) {
        if (percentageLeft < 0 || percentageLeft > 1) {
            throw new IllegalArgumentException("Illegal value : percentageLeft = " + percentageLeft);
        }
    }

    public static CurrentPhaseInfo of(double percentageLeft) {
        checkValue(percentageLeft);
        if (percentageLeft > 0.75) {
            return new CurrentPhaseInfo(4 * (percentageLeft - 0.75), 1);
        } else if (percentageLeft > 0.5) {
            return new CurrentPhaseInfo(4 * (percentageLeft - 0.5), 2);
        } else if (percentageLeft > 0.25) {
            return new CurrentPhaseInfo(4 * (percentageLeft - 0.25), 3);
        } else {
            return new CurrentPhaseInfo(4 * percentageLeft, 4);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrentPhaseInfo that = (CurrentPhaseInfo) o;
        double epsillon = 0.00001;
        return Math.abs(that.percentageLeft - percentageLeft) < epsillon &&
                currentPhaseNumber == that.currentPhaseNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(percentageLeft, currentPhaseNumber);
    }
}
