package com.abosultan.darbakos;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.abosultan.darbakos.core.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class GuardianDiagnosticRecordTest {
    private GuardianRegistry guardian;

    @Before public void setUp() {
        guardian = GuardianRegistry.get();
        guardian.resetForColdBoot();
    }

    @After public void tearDown() {
        guardian.resetForColdBoot();
    }

    @Test public void nullSnapshotIsConservative() {
        GuardianDiagnosticRecord r = GuardianDiagnosticRecord.from(null);
        assertEquals(GuardianState.Health.UNKNOWN, r.overall);
        assertEquals(0, r.healthy);
        assertEquals(0, r.degraded);
        assertEquals(0, r.failed);
        assertEquals(GuardianRegistry.Component.values().length, r.unknown);
        assertEquals(0, r.eventCount);
        assertNull(r.lastChangedComponent);
        assertEquals(GuardianState.Health.UNKNOWN, r.lastChangedHealth);
        assertEquals(0, r.lastChangedMonotonicMs);
    }

    @Test public void recordSummarizesFrozenSessionSnapshot() {
        GuardianMonitorSession s = new GuardianMonitorSession(
                guardian, GuardianMonitorConfig.conservativeDefault(), 8);
        for (GuardianRegistry.Component c : GuardianRegistry.Component.values()) {
            s.heartbeat(c, 1, 1000);
        }
        s.evaluate(1000, 0);
        GuardianMonitorSession.Snapshot frozen = s.snapshot();
        GuardianDiagnosticRecord r = GuardianDiagnosticRecord.from(frozen);

        assertEquals(frozen.generation, r.generation);
        assertEquals(GuardianState.Health.HEALTHY, r.overall);
        assertEquals(5, r.healthy);
        assertEquals(0, r.degraded);
        assertEquals(0, r.failed);
        assertEquals(0, r.unknown);
        assertEquals(5, r.eventCount);
        assertEquals(GuardianRegistry.Component.VEHICLE, r.lastChangedComponent);
        assertEquals(GuardianState.Health.HEALTHY, r.lastChangedHealth);
        assertEquals(1000, r.lastChangedMonotonicMs);

        s.resetForColdBoot();
        assertEquals(GuardianState.Health.HEALTHY, r.overall);
        assertEquals(5, r.eventCount);
    }
}
