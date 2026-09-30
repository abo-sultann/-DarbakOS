package com.abosultan.darbakos;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.abosultan.darbakos.core.MediaSnapshot;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class MediaSnapshotTest {
    @Test public void unavailableAndIdleAreConservative() {
        MediaSnapshot unavailable = MediaSnapshot.accessUnavailable();
        assertEquals(MediaSnapshot.State.ACCESS_UNAVAILABLE, unavailable.state);
        assertFalse(unavailable.active());
        assertFalse(unavailable.playing);
        assertFalse(unavailable.canPlayPause);
        assertFalse(unavailable.canPrevious);
        assertFalse(unavailable.canNext);
        assertEquals("", unavailable.title);
        assertEquals("", unavailable.artist);

        MediaSnapshot idle = MediaSnapshot.idle();
        assertEquals(MediaSnapshot.State.IDLE, idle.state);
        assertFalse(idle.active());
        assertFalse(idle.canPlayPause);
        assertFalse(idle.canPrevious);
        assertFalse(idle.canNext);
    }

    @Test public void activeSnapshotTrimsMetadataAndKeepsCapabilities() {
        MediaSnapshot active = MediaSnapshot.active("  طريق الرس  ", "  الفنان  ",
                "player.example", true, true, false, true);
        assertEquals(MediaSnapshot.State.ACTIVE, active.state);
        assertTrue(active.active());
        assertTrue(active.playing);
        assertEquals("طريق الرس", active.title);
        assertEquals("الفنان", active.artist);
        assertEquals("player.example", active.packageName);
        assertTrue(active.canPlayPause);
        assertFalse(active.canPrevious);
        assertTrue(active.canNext);
    }
}
