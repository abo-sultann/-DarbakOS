package com.abosultan.darbakos;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.abosultan.darbakos.core.LocalMediaIndex;
import com.abosultan.darbakos.core.LocalMediaQueue;
import com.abosultan.darbakos.core.LocalMediaState;
import com.abosultan.darbakos.core.LocalMediaTrack;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class LocalMediaIntegrationTest {
    @Test public void manifestRestoresValidTracksAndSelectionWithoutPlayback() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        File dir = new File(context.getCacheDir(), "local-media-test");
        assertTrue(dir.exists() || dir.mkdirs());
        File first = file(dir, "one.mp3", new byte[] {1,2,3});
        File second = file(dir, "two.mp3", new byte[] {4,5,6,7});
        LocalMediaTrack a = new LocalMediaTrack(first, "One", "Artist A", first.length(), first.lastModified());
        LocalMediaTrack b = new LocalMediaTrack(second, "Two", "Artist B", second.length(), second.lastModified());

        LocalMediaIndex index = new LocalMediaIndex(context);
        LocalMediaState state = new LocalMediaState(context);
        index.invalidate();
        state.remember(null);
        index.save(Arrays.asList(a, b));
        state.remember(b);

        List<LocalMediaTrack> restored = index.restoreValid();
        assertEquals(2, restored.size());
        LocalMediaQueue queue = new LocalMediaQueue();
        queue.replace(restored);
        assertEquals(1, state.restoreSelection(queue));
        assertEquals(second.getAbsolutePath(), queue.current().file.getAbsolutePath());

        assertTrue(second.delete());
        restored = index.restoreValid();
        assertEquals(1, restored.size());
        assertEquals(first.getAbsolutePath(), restored.get(0).file.getAbsolutePath());

        index.invalidate();
        state.remember(null);
        first.delete();
        dir.delete();
    }

    private static File file(File dir, String name, byte[] bytes) throws Exception {
        File file = new File(dir, name);
        try (FileOutputStream out = new FileOutputStream(file, false)) { out.write(bytes); }
        return file;
    }
}
