package com.abosultan.darbakos;

import com.abosultan.darbakos.core.GuardianState;
import com.abosultan.darbakos.core.GuardianState.Health;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class GuardianStateTest {
    private static void assertState(GuardianState state, Health health, long revision) {
        assertNotNull(state);
        assertEquals(health, state.health);
        assertEquals(revision, state.revision);
    }

    @Test public void coldBootIsUnknownAtRevisionZero() {
        assertState(GuardianState.coldBoot(), Health.UNKNOWN, 0L);
    }

    @Test public void everyDistinctTransitionAdvancesExactlyOnceAndRetainsPriorState() {
        for (Health from : Health.values()) {
            GuardianState source = GuardianState.coldBoot().withHealth(from);
            long revision = from == Health.UNKNOWN ? 0L : 1L;
            for (Health to : Health.values()) {
                if (from == to) continue;
                GuardianState next = source.withHealth(to);
                assertNotSame(source, next);
                assertState(next, to, revision + 1L);
                assertState(source, from, revision);
            }
        }
    }

    @Test public void sameHealthPreservesIdentityAndRevisionInEveryState() {
        for (Health health : Health.values()) {
            GuardianState source = GuardianState.coldBoot().withHealth(Health.FAILED)
                    .withHealth(Health.HEALTHY).withHealth(health);
            long revision = source.revision;
            assertSame(source, source.withHealth(health));
            assertSame(source, source.withHealth(health).withHealth(health));
            assertState(source, health, revision);
        }
    }

    @Test public void nullPreservesIdentityAndRevisionInEveryState() {
        for (Health health : Health.values()) {
            GuardianState source = GuardianState.coldBoot().withHealth(health);
            long revision = source.revision;
            assertSame(source, source.withHealth(null));
            assertState(source, health, revision);
        }
    }

    @Test public void failedRecoveryIsAnImmutableStateChangeOnly() {
        GuardianState cold = GuardianState.coldBoot();
        GuardianState healthy = cold.withHealth(Health.HEALTHY);
        GuardianState degraded = healthy.withHealth(Health.DEGRADED);
        GuardianState failed = degraded.withHealth(Health.FAILED);
        GuardianState recovered = failed.withHealth(Health.HEALTHY);
        assertState(cold, Health.UNKNOWN, 0L);
        assertState(healthy, Health.HEALTHY, 1L);
        assertState(degraded, Health.DEGRADED, 2L);
        assertState(failed, Health.FAILED, 3L);
        assertState(recovered, Health.HEALTHY, 4L);
        assertNotSame(healthy, recovered);
        assertNotSame(failed, recovered);
        assertState(GuardianState.coldBoot(), Health.UNKNOWN, 0L);
    }
}
