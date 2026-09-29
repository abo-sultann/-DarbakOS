package com.abosultan.darbakos;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.abosultan.darbakos.core.GuardianEvent;
import com.abosultan.darbakos.core.GuardianEventJournal;
import com.abosultan.darbakos.core.GuardianHeartbeat;
import com.abosultan.darbakos.core.GuardianHeartbeatRegistry;
import com.abosultan.darbakos.core.GuardianLivenessPolicy;
import com.abosultan.darbakos.core.GuardianMonitorConfig;
import com.abosultan.darbakos.core.GuardianMonitorStep;
import com.abosultan.darbakos.core.GuardianRegistry;
import com.abosultan.darbakos.core.GuardianState;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class GuardianMonitoringFoundationTest {
    @Test public void heartbeatRegistryRejectsRegression() {
        GuardianHeartbeatRegistry r = new GuardianHeartbeatRegistry();
        GuardianRegistry.Component c = GuardianRegistry.Component.CORE;
        assertTrue(r.update(new GuardianHeartbeat(c, 2, 200)));
        assertFalse(r.update(new GuardianHeartbeat(c, 1, 300)));
        assertFalse(r.update(new GuardianHeartbeat(c, 3, 199)));
        assertFalse(r.update(new GuardianHeartbeat(c, 2, 200)));
        assertTrue(r.update(new GuardianHeartbeat(c, 3, 300)));
        assertEquals(3, r.snapshot(c).sequence);
    }

    @Test public void livenessBoundariesAreExact() {
        GuardianHeartbeat h = new GuardianHeartbeat(GuardianRegistry.Component.HOME, 1, 1000);
        assertEquals(GuardianLivenessPolicy.Liveness.LIVE,
                GuardianLivenessPolicy.classify(h, 5999, 5000, 15000));
        assertEquals(GuardianLivenessPolicy.Liveness.LATE,
                GuardianLivenessPolicy.classify(h, 6000, 5000, 15000));
        assertEquals(GuardianLivenessPolicy.Liveness.STALE,
                GuardianLivenessPolicy.classify(h, 16000, 5000, 15000));
    }

    @Test public void configSanitizesInvalidThresholds() {
        GuardianMonitorConfig c = new GuardianMonitorConfig(0, -1);
        assertEquals(1, c.lateAfterMs);
        assertEquals(2, c.staleAfterMs);
    }

    @Test public void journalOverwritesOldestAndRetainsSnapshots() {
        GuardianEventJournal j = new GuardianEventJournal(2);
        GuardianEvent a = new GuardianEvent(1,1,null,GuardianEvent.Type.ASSESSMENT,GuardianState.Health.UNKNOWN);
        GuardianEvent b = new GuardianEvent(2,2,null,GuardianEvent.Type.ASSESSMENT,GuardianState.Health.HEALTHY);
        GuardianEvent c = new GuardianEvent(3,3,null,GuardianEvent.Type.ASSESSMENT,GuardianState.Health.FAILED);
        j.append(a); j.append(b);
        List<GuardianEvent> retained = j.snapshot();
        j.append(c);
        assertSame(a, retained.get(0));
        assertSame(b, retained.get(1));
        assertSame(b, j.snapshot().get(0));
        assertSame(c, j.latest());
        assertEquals(2, j.size());
    }

    @Test public void monitorStepRecordsOnlyHealthChanges() {
        GuardianHeartbeatRegistry h = new GuardianHeartbeatRegistry();
        GuardianRegistry g = GuardianRegistry.get();
        g.resetForColdBoot();
        GuardianEventJournal j = new GuardianEventJournal();
        for (GuardianRegistry.Component c : GuardianRegistry.Component.values()) {
            h.update(new GuardianHeartbeat(c, 1, 1000));
        }
        GuardianMonitorStep.Result first = GuardianMonitorStep.run(
                h,g,j,1000,GuardianMonitorConfig.conservativeDefault(),0);
        assertEquals(5, first.healthChanges);
        assertEquals(5, j.size());
        GuardianMonitorStep.Result second = GuardianMonitorStep.run(
                h,g,j,1001,GuardianMonitorConfig.conservativeDefault(),0);
        assertEquals(0, second.healthChanges);
        assertEquals(5, j.size());
        assertEquals(GuardianState.Health.HEALTHY,
                second.supervisor.snapshot.aggregateHealth);
    }

    @Test public void missingHeartbeatsRemainUnknown() {
        GuardianRegistry g = GuardianRegistry.get();
        g.resetForColdBoot();
        GuardianMonitorStep.Result r = GuardianMonitorStep.run(
                new GuardianHeartbeatRegistry(),g,null,1000,
                GuardianMonitorConfig.conservativeDefault(),0);
        assertEquals(0, r.healthChanges);
        assertEquals(GuardianState.Health.UNKNOWN,r.supervisor.snapshot.aggregateHealth);
    }
}
