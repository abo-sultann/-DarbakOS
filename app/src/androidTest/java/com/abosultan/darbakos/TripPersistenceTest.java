package com.abosultan.darbakos;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.abosultan.darbakos.core.PositionFix;
import com.abosultan.darbakos.core.PositionRuntimePolicy;
import com.abosultan.darbakos.core.TripChunkReader;
import com.abosultan.darbakos.core.TripChunkWriter;
import com.abosultan.darbakos.core.TripPoint;
import com.abosultan.darbakos.core.TripRecorderBuffer;
import com.abosultan.darbakos.core.TripRecorderStore;
import com.abosultan.darbakos.core.TripStorageLocator;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class TripPersistenceTest {
    private File freshDir(String name) {
        File root = new File(InstrumentationRegistry.getInstrumentation()
                .getTargetContext().getCacheDir(), name + "-" + System.nanoTime());
        assertTrue(root.mkdirs());
        return root;
    }

    private PositionFix fix(long time, double lat) {
        PositionFix fix = PositionFix.createStamped(lat, 46.6753, 4f, 10f, time,
                1700000000000L + time);
        assertNotNull(fix);
        return fix;
    }

    @Test public void writeReadAndDuplicateNamingAreSafe() throws Exception {
        File dir = freshDir("trip-store");
        TripRecorderBuffer buffer = new TripRecorderBuffer(4);
        assertTrue(buffer.append(fix(100, 24.70)));
        assertTrue(buffer.append(fix(200, 24.71)));

        TripRecorderStore store = new TripRecorderStore(new TripChunkWriter(dir));
        File first = store.flush(buffer, "drive/one", 0);
        assertNotNull(first);
        assertTrue(TripChunkWriter.isCompleteFile(first));
        assertFalse(TripChunkWriter.isPartialFile(first));
        assertEquals(0, buffer.size());

        TripChunkReader.Chunk read = new TripChunkReader().read(first);
        assertEquals("drive_one", read.sessionId);
        assertEquals(0L, read.chunkIndex);
        assertEquals(2, read.points.size());
        assertEquals(0L, read.points.get(0).sequence);
        assertEquals(1L, read.points.get(1).sequence);
        assertEquals(100L, read.points.get(0).position.monotonicMs);
        assertEquals(1700000000100L, read.points.get(0).position.wallTimeMs);

        assertTrue(buffer.append(fix(300, 24.72)));
        File duplicateIndex = store.flush(buffer, "drive/one", 0);
        assertNotEquals(first.getName(), duplicateIndex.getName());
        assertTrue(duplicateIndex.exists());
    }

    @Test public void failedOrPartialStateCannotMasqueradeAsCommittedHistory() throws Exception {
        File dir = freshDir("trip-partial");
        File partial = new File(dir, "trip_session_0.dtrip.part");
        FileOutputStream output = new FileOutputStream(partial);
        output.write(new byte[] {1, 2, 3});
        output.close();
        assertTrue(TripChunkWriter.isPartialFile(partial));
        assertFalse(TripChunkWriter.isCompleteFile(partial));
        try {
            new TripChunkReader().read(partial);
            fail("partial file must not be accepted");
        } catch (java.io.IOException expected) { }

        TripRecorderBuffer buffer = new TripRecorderBuffer(4);
        assertTrue(buffer.append(fix(100, 24.70)));
        List<TripPoint> before = buffer.snapshot();
        assertTrue(buffer.append(fix(200, 24.71)));
        assertTrue(buffer.commitPrefix(before.size()));
        assertEquals(1, buffer.size());
        assertEquals(1L, buffer.snapshot().get(0).sequence);
    }

    @Test public void storagePreferenceAndRuntimeOwnershipAreExplicit() {
        File root = freshDir("trip-locator");
        File primary = new File(root, "primary");
        File removable = new File(root, "removable");
        File fallback = new File(root, "fallback");
        assertEquals(removable, TripStorageLocator.select(
                new File[] {primary, removable}, new boolean[] {false, true}, fallback));

        File fallbackOnly = new File(root, "fallback-only");
        assertEquals(fallbackOnly, TripStorageLocator.select(null, null, fallbackOnly));

        assertEquals(PositionRuntimePolicy.Owner.NONE,
                PositionRuntimePolicy.owner(false, false, false));
        assertEquals(PositionRuntimePolicy.Owner.ACTIVITY,
                PositionRuntimePolicy.owner(true, false, false));
        assertEquals(PositionRuntimePolicy.Owner.CONTINUOUS,
                PositionRuntimePolicy.owner(false, true, false));
        assertEquals(PositionRuntimePolicy.Owner.CONTINUOUS,
                PositionRuntimePolicy.owner(false, false, true));
    }
}
