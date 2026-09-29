package com.abosultan.darbakos;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.abosultan.darbakos.core.GuardianMonitorConfig;
import com.abosultan.darbakos.core.GuardianMonitorSession;
import com.abosultan.darbakos.core.GuardianMonitorStep;
import com.abosultan.darbakos.core.GuardianRegistry;
import com.abosultan.darbakos.core.GuardianState;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class GuardianMonitorSessionTest {
    private GuardianRegistry guardian;

    @Before public void setUp() {
        guardian = GuardianRegistry.get();
        guardian.resetForColdBoot();
    }

    @After public void tearDown() {
        guardian.resetForColdBoot();
    }

    @Test public void generationRejectsDelayedHeartbeatAfterColdBoot() {
        GuardianMonitorSession s = new GuardianMonitorSession(
                guardian, GuardianMonitorConfig.conservativeDefault(), 8);
        long oldGeneration = s.generation();
        assertTrue(s.heartbeat(oldGeneration, GuardianRegistry.Component.CORE, 1, 1000));
        s.resetForColdBoot();
        assertEquals(oldGeneration + 1, s.generation());
        assertFalse(s.heartbeat(oldGeneration, GuardianRegistry.Component.CORE, 2, 2000));
        assertTrue(s.heartbeat(s.generation(), GuardianRegistry.Component.CORE, 1, 2000));
    }

    @Test public void coldBootClearsHeartbeatsJournalAndHealth() {
        GuardianMonitorSession s = new GuardianMonitorSession(
                guardian, GuardianMonitorConfig.conservativeDefault(), 8);
        for (GuardianRegistry.Component c : GuardianRegistry.Component.values()) {
            assertTrue(s.heartbeat(c, 1, 1000));
        }
        GuardianMonitorStep.Result before = s.evaluate(1000, 0);
        assertEquals(5, before.healthChanges);
        assertFalse(s.journal().isEmpty());

        s.resetForColdBoot();

        assertTrue(s.journal().isEmpty());
        GuardianMonitorStep.Result after = s.evaluate(1000, 0);
        assertEquals(0, after.healthChanges);
        assertEquals(GuardianState.Health.UNKNOWN, after.supervisor.snapshot.overall);
        assertEquals(5, after.liveness.count(
                com.abosultan.darbakos.core.GuardianLivenessPolicy.Liveness.UNKNOWN));
    }

    @Test public void diagnosticSnapshotIsImmutableAndNonMutating() {
        GuardianMonitorSession s = new GuardianMonitorSession(
                guardian, GuardianMonitorConfig.conservativeDefault(), 8);
        for (GuardianRegistry.Component c : GuardianRegistry.Component.values()) {
            s.heartbeat(c, 1, 1000);
        }
        s.evaluate(1000, 0);
        GuardianMonitorSession.Snapshot snap = s.snapshot();
        assertEquals(s.generation(), snap.generation);
        assertEquals(5, snap.events.size());
        assertSame(s.config(), s.config());
        boolean immutable = false;
        try { snap.events.clear(); } catch (UnsupportedOperationException expected) { immutable = true; }
        assertTrue(immutable);

        s.resetForColdBoot();
        assertTrue(s.snapshot().events.isEmpty());
        assertEquals(5, snap.events.size());
    }

    @Test public void retainedResultSurvivesSessionReset() {
        GuardianMonitorSession s = new GuardianMonitorSession(
                guardian, GuardianMonitorConfig.conservativeDefault(), 8);
        for (GuardianRegistry.Component c : GuardianRegistry.Component.values()) {
            s.heartbeat(c, 1, 1000);
        }
        GuardianMonitorStep.Result retained = s.evaluate(1000, 0);
        assertEquals(GuardianState.Health.HEALTHY, retained.supervisor.snapshot.overall);

        s.resetForColdBoot();
        s.evaluate(1000, 0);

        assertEquals(GuardianState.Health.HEALTHY, retained.supervisor.snapshot.overall);
    }
}
