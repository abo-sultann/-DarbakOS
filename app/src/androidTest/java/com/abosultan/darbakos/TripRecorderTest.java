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

    @Test public void monotonicOrderSurvivesPauseResumeAndFinishStart() {
        TripRecorder r = new TripRecorder();
        r.start();
        assertTrue(r.accept(fix(10_000), 10_000));
        assertFalse(r.accept(fix(10_000), 10_000));
        assertFalse(r.accept(fix(9_999), 10_000));
        r.pause();
        r.start(); // Starting an active recorder must not bypass pause.
        assertTrue(r.isPaused());
        r.resume();
        assertFalse(r.accept(fix(9_999), 10_000));
        assertFalse(r.accept(fix(10_000), 10_000));
        assertTrue(r.snapshot().get(1).isEmpty());
        assertTrue(r.accept(fix(11_000), 11_000));
        r.finish();
        assertFalse(r.isRecording());
        assertFalse(r.isPaused());
        r.start();
        assertFalse(r.accept(fix(10_999), 11_000));
        assertFalse(r.accept(fix(11_000), 11_000));
        assertTrue(r.accept(fix(12_000), 12_000));
        List<List<TripPoint>> s = r.snapshot();
        assertEquals(3, s.size());
        for (int i = 0; i < 3; i++) {
            assertEquals(1, s.get(i).size());
            assertEquals(i, s.get(i).get(0).sequence);
            assertEquals(10_000L + i * 1_000L, s.get(i).get(0).position.monotonicMs);
        }
    }

    @Test public void qualityAndGapBoundariesAreConservative() {
        assertFalse(PositionQualityPolicy.isUsable(fix(0), 0));
        assertFalse(PositionQualityPolicy.isUsable(fix(1), -1));
        PositionFix edge = PositionFix.create(24.7, 46.7, 100f, 70f, 1);
        assertTrue(PositionQualityPolicy.isUsable(edge, 15_001));
        assertFalse(PositionQualityPolicy.isUsable(edge, 15_002));
        assertTrue(PositionQualityPolicy.isUsable(fix(Long.MAX_VALUE), Long.MAX_VALUE));
        assertFalse(PositionQualityPolicy.isUsable(fix(1), Long.MAX_VALUE));
        TripRecorder r = new TripRecorder();
        r.start();
        assertTrue(r.accept(fix(1), 1));
        assertFalse(r.accept(null, 1));
        assertFalse(r.accept(fix(2), 15_003));
        assertFalse(r.accept(fix(15_002), 15_001));
        assertTrue(r.accept(fix(15_001), 15_001)); // Exactly 15 seconds stays contiguous.
        assertTrue(r.accept(fix(30_002), 30_002)); // More than 15 seconds splits.
        List<List<TripPoint>> s = r.snapshot();
        assertEquals(2, s.size());
        assertEquals(2, s.get(0).size());
        assertEquals(1, s.get(1).size());
        assertEquals(2L, s.get(1).get(0).sequence); // Rejections consume no sequence.
    }

    @Test public void snapshotsRemainImmutableAfterAcceptAndReset() {
        TripRecorder r = new TripRecorder();
        r.pause();
        r.resume();
        assertFalse(r.isRecording());
        assertFalse(r.isPaused());
        r.start();
        PositionFix first = fix(1_000);
        assertTrue(r.accept(first, 1_000));
        List<List<TripPoint>> retained = r.snapshot();
        try { retained.clear(); fail("Outer snapshot must be immutable"); }
        catch (UnsupportedOperationException expected) { }
        try { retained.get(0).clear(); fail("Segment snapshot must be immutable"); }
        catch (UnsupportedOperationException expected) { }
        assertTrue(r.accept(fix(2_000), 2_000));
        r.pause();
        r.resume();
        assertTrue(r.accept(fix(3_000), 3_000));
        assertEquals(2, r.snapshot().size());
        r.reset();
        assertTrue(r.snapshot().isEmpty());
        r.start();
        assertTrue(r.accept(fix(1), 1)); // Reset clears ordering and sequence state.
        assertEquals(0L, r.snapshot().get(0).get(0).sequence);
        assertEquals(1, retained.size());
        assertEquals(1, retained.get(0).size());
        assertSame(first, retained.get(0).get(0).position);
        assertEquals(0L, retained.get(0).get(0).sequence);
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
