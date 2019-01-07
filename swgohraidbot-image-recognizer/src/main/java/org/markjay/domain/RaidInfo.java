package org.markjay.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.apachecommons.CommonsLog;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:mark.jay.mk@gmail.com">mark jay</a>
 * @since 1/1/19 2:50 PM
 */
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
public class RaidInfo {

    /**
     * a number between 0 and 1, where 0 - raid just started, 1 - it's done
     */
    private final double totalPercentage;

    /**
     * total damage dealt
     */
    private final long damageDealt;


    private final CurrentPhaseInfo currentPhaseInfo;

    /**
     * source:
     * https://forums.galaxy-of-heroes.starwars.ea.com/discussion/165806/sith-raid-health-per-phase-all-tiers
     */
    private static final Map<Integer, Integer> HEALTH_BY_PHASE = new HashMap<Integer, Integer>() {{
        put(1, 46888776);
        put(2, 52105858);
        put(3, 38371455);
        put(4, 33499444);
    }};

    private static final DecimalFormat FORMATTER = new DecimalFormat("#,##0.00");
    private static final DecimalFormat FORMATTER_INTEGER = new DecimalFormat("#,##0");

    public String toReport() {
        long damageDealt = getDamageDealt();
        double percentage = getPercentage(damageDealt, getCurrentPhaseInfo().getCurrentPhaseNumber());
        String percentageAsString = FORMATTER.format(percentage * 100);
        String percentageLeftAsString = FORMATTER.format(getCurrentPhaseInfo().getPercentageLeft());
        return    "Damage dealt         : " + FORMATTER_INTEGER.format(damageDealt) + "\n"
                + "Damage dealt(%)      : " + percentageAsString + "%\n"
                + "Current phase        : " + getCurrentPhaseInfo().getCurrentPhaseNumber() + "\n"
//                + "Phase percentage left: " + getCurrentPhaseInfo().getPercentageLeft() + "\n"
                ;
    }

    private double getPercentage(long damageDealt, int currentPhaseNumber) {
        return ((double) damageDealt) / ((double) HEALTH_BY_PHASE.get(currentPhaseNumber));
    }
}
