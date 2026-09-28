package com.abosultan.darbakos;

import com.abosultan.darbakos.core.CoreStateStore;
import com.abosultan.darbakos.core.DarbakState;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class CoreStateTest {
    private static void assertCold(DarbakState state) {
        assertNotNull(state);
        assertEquals(0L, state.revision);
        assertEquals(DarbakState.Availability.UNAVAILABLE, state.speed);
        assertEquals(DarbakState.Availability.UNAVAILABLE, state.navigation);
        assertEquals(DarbakState.Availability.UNAVAILABLE, state.vehicle);
        assertFalse("Cold boot must never start playback", state.mediaPlaying);
    }

    @Test public void coldBootHasNoInventedAvailabilityOrPlayback() {
        assertCold(DarbakState.coldBoot());
    }

    @Test public void singletonSnapshotsRemainStableAcrossReset() {
        CoreStateStore store = CoreStateStore.get();
        assertSame(store, CoreStateStore.get());
        store.resetForColdBoot();
        DarbakState retained = store.snapshot();
        assertCold(retained);
        assertSame("Read must not replace the snapshot", retained, store.snapshot());
        store.resetForColdBoot();
        DarbakState reset = store.snapshot();
        assertNotSame("Reset must establish a new cold snapshot", retained, reset);
        assertCold(reset);
        assertCold(retained); // Readers holding the old immutable snapshot remain valid.
        assertSame(reset, store.snapshot());
    }

    @Test public void freshActivityResetsButRecreationKeepsCoreSnapshot() {
        CoreStateStore store = CoreStateStore.get();
        store.resetForColdBoot();
        DarbakState beforeLaunch = store.snapshot();
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            DarbakState launched = store.snapshot();
            assertNotSame(beforeLaunch, launched);
            assertCold(launched);
            scenario.onActivity(activity -> assertTrue(activity.findViewById(R.id.nav_home).isSelected()));
            scenario.recreate();
            assertSame("Recreation is not a cold boot", launched, store.snapshot());
            assertCold(store.snapshot());
        }
    }
}
