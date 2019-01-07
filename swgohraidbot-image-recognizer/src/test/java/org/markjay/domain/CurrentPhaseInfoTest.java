package org.markjay.domain;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:mark.jay.mk@gmail.com">mark jay</a>
 * @since 1/2/19 11:05 PM
 */
public class CurrentPhaseInfoTest {

    @Test
    public void of() {
        Assert.assertEquals(new CurrentPhaseInfo(1, 1), CurrentPhaseInfo.of(1));
        Assert.assertEquals(new CurrentPhaseInfo(0.2, 1), CurrentPhaseInfo.of(0.80));
        Assert.assertEquals(new CurrentPhaseInfo(0.2, 2), CurrentPhaseInfo.of(0.55));
        Assert.assertEquals(new CurrentPhaseInfo(0.4, 2), CurrentPhaseInfo.of(0.60));
        Assert.assertEquals(new CurrentPhaseInfo(0.6, 3), CurrentPhaseInfo.of(0.40));
        Assert.assertEquals(new CurrentPhaseInfo(0.8, 4), CurrentPhaseInfo.of(0.20));
    }
}