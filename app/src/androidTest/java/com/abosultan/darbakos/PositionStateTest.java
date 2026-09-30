package com.abosultan.darbakos;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.abosultan.darbakos.core.PositionFix;
import com.abosultan.darbakos.core.PositionState;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PositionStateTest {
    @Test public void validationAndSpeedAreConservative() {
        assertNull(PositionFix.create(91, 0, 1, 1, 1));
        assertNull(PositionFix.create(0, 181, 1, 1, 1));
        assertNull(PositionFix.create(0, 0, -1, 1, 1));
        PositionFix fix = PositionFix.create(24.7, 46.7, 5, 10f, 100);
        assertNotNull(fix);
        assertEquals(36, fix.speedKmh());
        PositionFix negativeSpeed = PositionFix.create(24.7, 46.7, 5, -2f, 101);
        assertNotNull(negativeSpeed);
        assertEquals(0, negativeSpeed.speedKmh());
    }

    @Test public void stateRejectsNullAndNonMonotonicFixesAndResets() {
        PositionState state = new PositionState();
        assertFalse(state.available());
        assertFalse(state.publish(null));
        PositionFix first = PositionFix.create(24.7, 46.7, 5, 1, 100);
        PositionFix old = PositionFix.create(24.8, 46.8, 5, 2, 99);
        assertTrue(state.publish(first));
        assertFalse(state.publish(old));
        assertFalse(state.publish(PositionFix.create(24.9, 46.9, 5, 2, 100)));
        assertSame(first, state.latest());
        assertEquals(1, state.revision());
        state.resetForColdBoot();
        assertFalse(state.available());
        assertNull(state.latest());
        assertEquals(0, state.revision());
    }
}
