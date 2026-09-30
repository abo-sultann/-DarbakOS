package com.abosultan.darbakos;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.abosultan.darbakos.core.*;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class TripRecorderTest {
    private PositionFix fix(long t) {
        return PositionFix.create(24.7, 46.7, 5f, 10f, t);
    }

    @Test public void qualityRejectsStaleFutureAndImplausibleFixes() {
        assertFalse(PositionQualityPolicy.isUsable(null, 20_000));
        assertFalse(PositionQualityPolicy.isUsable(fix(1), 20_000));
        assertFalse(PositionQualityPolicy.isUsable(fix(20_001), 20_000));
        assertFalse(PositionQualityPolicy.isUsable(PositionFix.create(24.7,46.7,101f,10f,10_000), 10_000));
        assertFalse(PositionQualityPolicy.isUsable(PositionFix.create(24.7,46.7,5f,71f,10_000), 10_000));
        assertTrue(PositionQualityPolicy.isUsable(fix(10_000), 10_000));
    }

    @Test public void pauseResumeAndGpsGapCreateSegmentsWithoutFakeBridge() {
        TripRecorder r = new TripRecorder();
        assertFalse(r.accept(fix(1_000), 1_000));
        r.start();
        assertTrue(r.accept(fix(1_000), 1_000));
        assertTrue(r.accept(fix(2_000), 2_000));
        r.pause();
        assertFalse(r.accept(fix(3_000), 3_000));
        r.resume();
        assertTrue(r.accept(fix(4_000), 4_000));
        assertTrue(r.accept(fix(20_000), 20_000));

        List<List<TripPoint>> s = r.snapshot();
        assertEquals(3, s.size());
        assertEquals(2, s.get(0).size());
        assertEquals(1, s.get(1).size());
        assertEquals(1, s.get(2).size());
        assertEquals(0, s.get(0).get(0).sequence);
        assertEquals(3, s.get(2).get(0).sequence);

        r.finish();
        assertFalse(r.accept(fix(21_000), 21_000));
        assertEquals(3, s.size()); // retained immutable snapshot
    }

    @Test public void resetReturnsColdEmptyState() {
        TripRecorder r = new TripRecorder();
        r.start();
        assertTrue(r.accept(fix(1_000), 1_000));
        r.reset();
        assertFalse(r.isRecording());
        assertFalse(r.isPaused());
        assertTrue(r.snapshot().isEmpty());
    }
}
