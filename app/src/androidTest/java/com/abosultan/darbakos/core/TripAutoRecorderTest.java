package com.abosultan.darbakos.core;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class TripAutoRecorderTest {
    private File directory() {
        File dir = new File(InstrumentationRegistry.getInstrumentation().getTargetContext()
                .getCacheDir(), "auto-trip-" + System.nanoTime());
        assertTrue(dir.mkdirs());
        return dir;
    }

    private PositionFix fix(long time, float speed) {
        return PositionFix.createStamped(24.7, 46.7, 5f, speed, time, 1700000000000L + time);
    }

    private TripAutoRecorder recorder(File dir, int capacity) {
        return new TripAutoRecorder(dir, capacity, 2f, 1f, 50f, 10_000L);
    }

    private List<TripChunkReader.Chunk> read(File dir) throws IOException {
        File[] files = dir.listFiles((d, n) -> n.endsWith(".dtrip"));
        assertNotNull(files);
        Arrays.sort(files, Comparator.comparing(File::getName));
        List<TripChunkReader.Chunk> chunks = new ArrayList<>();
        for (File file : files) chunks.add(new TripChunkReader().read(file));
        return chunks;
    }

    @Test public void movementStartsAndStationaryTimeoutClosesDurably() throws Exception {
        File dir = directory();
        TripAutoRecorder r = recorder(dir, 60);
        r.accept(null);
        r.accept(fix(500, 0f));
        assertEquals(TripAutoRecorder.State.IDLE, r.state());
        r.accept(fix(1_000, 2f));
        assertEquals(TripAutoRecorder.State.IDLE, r.state());
        r.accept(fix(2_000, 2f));
        assertEquals(TripAutoRecorder.State.RECORDING, r.state());
        r.accept(fix(3_000, 1f));
        r.accept(fix(12_999, 1f));
        assertEquals(TripAutoRecorder.State.RECORDING, r.state());
        r.accept(fix(13_000, 1f));
        assertEquals(TripAutoRecorder.State.IDLE, r.state());
        List<TripChunkReader.Chunk> chunks = read(dir);
        assertEquals(1, chunks.size());
        assertEquals(5, chunks.get(0).points.size());
        assertEquals(1700000001000L, chunks.get(0).points.get(0).position.wallTimeMs);
        assertEquals(4L, chunks.get(0).points.get(4).sequence);
        r.close();
        assertEquals(1, read(dir).size());
    }

    @Test public void longGapSplitsRecordedHistoryWithoutBridge() throws Exception {
        File dir = directory();
        TripAutoRecorder r = recorder(dir, 60);
        r.accept(fix(1_000, 10f));
        r.accept(fix(2_000, 10f));
        r.accept(fix(30_000, 10f));
        r.accept(fix(31_000, 10f));
        r.close();
        List<TripChunkReader.Chunk> chunks = read(dir);
        assertEquals("GPS gap must separate histories", 2, chunks.size());
        assertNotEquals(chunks.get(0).sessionId, chunks.get(1).sessionId);
        assertEquals(2, chunks.get(0).points.size());
        assertEquals(2, chunks.get(1).points.size());
        assertEquals(2_000L, chunks.get(0).points.get(1).position.monotonicMs);
        assertEquals(30_000L, chunks.get(1).points.get(0).position.monotonicMs);
    }

    @Test public void pendingMovingFixDoesNotBridgeALongGap() throws Exception {
        TripAutoRecorder r = recorder(directory(), 60);
        r.accept(fix(1_000, 10f));
        r.accept(fix(30_000, 10f));
        assertEquals("Two separated fixes are not continuous movement",
                TripAutoRecorder.State.IDLE, r.state());
        r.accept(fix(31_000, 10f));
        assertEquals(TripAutoRecorder.State.RECORDING, r.state());
        assertEquals("t1700000030000", r.sessionId());
        r.close();
    }

    @Test public void rejectedOldFixCannotAdvanceStationaryTimeout() throws Exception {
        TripAutoRecorder r = recorder(directory(), 60);
        r.accept(fix(1_000, 10f));
        r.accept(fix(2_000, 10f));
        r.accept(fix(1_001, 0f));
        r.accept(fix(11_002, 0f));
        assertEquals("Rejected old stationary fix must not close the trip",
                TripAutoRecorder.State.RECORDING, r.state());
        r.close();
    }

    @Test public void failedFullChunkRetriesBeforeAcceptingNextPoint() throws Exception {
        File dir = new File(directory(), "blocked");
        assertTrue(dir.createNewFile());
        TripAutoRecorder r = recorder(dir, 2);
        r.accept(fix(1_000, 10f));
        try { r.accept(fix(2_000, 10f)); fail("Blocked storage must fail"); }
        catch (IOException expected) { }
        assertEquals(0L, r.nextChunkIndex());
        assertTrue(dir.delete());
        assertTrue(dir.mkdir());
        r.accept(fix(3_000, 10f));
        r.close();
        List<TripChunkReader.Chunk> chunks = read(dir);
        assertEquals("Recovered full buffer and new point both persist", 2, chunks.size());
        assertEquals(2, chunks.get(0).points.size());
        assertEquals(1, chunks.get(1).points.size());
        assertEquals(2L, chunks.get(1).points.get(0).sequence);
        assertEquals(3_000L, chunks.get(1).points.get(0).position.monotonicMs);
    }

    @Test public void unknownAndImplausibleFixesCannotStartATrip() throws Exception {
        TripAutoRecorder r = recorder(directory(), 60);
        r.accept(fix(0, 10f));
        r.accept(fix(1_000, 71f));
        r.accept(PositionFix.create(24.7, 46.7, 51f, 10f, 2_000));
        assertEquals(TripAutoRecorder.State.IDLE, r.state());
        r.accept(fix(3_000, 10f));
        assertEquals(TripAutoRecorder.State.IDLE, r.state());
        r.accept(fix(4_000, 10f));
        assertEquals(TripAutoRecorder.State.RECORDING, r.state());
        r.close();
    }
}
