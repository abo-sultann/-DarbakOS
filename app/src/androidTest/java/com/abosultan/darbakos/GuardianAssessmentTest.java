package com.abosultan.darbakos;

import com.abosultan.darbakos.core.GuardianAssessment;
import com.abosultan.darbakos.core.GuardianRegistry;
import com.abosultan.darbakos.core.GuardianRegistry.Component;
import com.abosultan.darbakos.core.GuardianSnapshot;
import com.abosultan.darbakos.core.GuardianState;
import com.abosultan.darbakos.core.GuardianState.Health;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class GuardianAssessmentTest {
    private final GuardianRegistry registry = GuardianRegistry.get();
    private static final Component[] COMPONENTS = Component.values();
    private static final Health[] PRIORITY = {
            Health.HEALTHY, Health.UNKNOWN, Health.DEGRADED, Health.FAILED };

    @Before public void setUp() { registry.resetForColdBoot(); }
    @After public void tearDown() { registry.resetForColdBoot(); }

    private void setAll(Health health) {
        for (Component component : COMPONENTS) registry.update(component, health);
    }

    private static GuardianState[] states(GuardianSnapshot snapshot) {
        return new GuardianState[] { snapshot.core, snapshot.home, snapshot.navigation,
                snapshot.media, snapshot.vehicle };
    }

    private static Health[] uniform(Health health) {
        return new Health[] { health, health, health, health, health };
    }

    private static void assertClassification(GuardianAssessment assessment, Health[] expected) {
        int healthy = 0, degraded = 0, failed = 0, unknown = 0, rank = 0;
        for (int i = 0; i < COMPONENTS.length; i++) {
            Component component = COMPONENTS[i];
            boolean h = assessment.isHealthy(component), d = assessment.isDegraded(component);
            boolean f = assessment.isFailed(component), u = assessment.isUnknown(component);
            assertEquals(1, (h ? 1 : 0) + (d ? 1 : 0) + (f ? 1 : 0) + (u ? 1 : 0));
            assertEquals(expected[i] == Health.HEALTHY, h);
            assertEquals(expected[i] == Health.DEGRADED, d);
            assertEquals(expected[i] == Health.FAILED, f);
            assertEquals(expected[i] == Health.UNKNOWN, u);
            if (h) healthy++;
            if (d) degraded++;
            if (f) failed++;
            if (u) unknown++;
            for (int p = 0; p < PRIORITY.length; p++) if (expected[i] == PRIORITY[p]) rank = Math.max(rank, p);
        }
        assertEquals(healthy, assessment.healthyCount());
        assertEquals(degraded, assessment.degradedCount());
        assertEquals(failed, assessment.failedCount());
        assertEquals(unknown, assessment.unknownCount());
        assertEquals(5, assessment.healthyCount() + assessment.degradedCount()
                + assessment.failedCount() + assessment.unknownCount());
        assertEquals(PRIORITY[rank], assessment.overall);
    }

    private GuardianAssessment assessWithoutMutation(GuardianSnapshot snapshot) {
        GuardianState[] live = new GuardianState[5];
        Health[] liveHealth = new Health[5];
        long[] liveRevision = new long[5];
        GuardianState[] frozen = snapshot == null ? new GuardianState[0] : states(snapshot);
        Health[] frozenHealth = new Health[frozen.length];
        long[] frozenRevision = new long[frozen.length];
        Health overall = snapshot == null ? null : snapshot.overall;
        for (int i = 0; i < COMPONENTS.length; i++) {
            live[i] = registry.snapshot(COMPONENTS[i]);
            liveHealth[i] = live[i].health;
            liveRevision[i] = live[i].revision;
        }
        for (int i = 0; i < frozen.length; i++) {
            frozenHealth[i] = frozen[i].health;
            frozenRevision[i] = frozen[i].revision;
        }
        GuardianAssessment result = GuardianAssessment.from(snapshot);
        for (int i = 0; i < COMPONENTS.length; i++) {
            assertSame(live[i], registry.snapshot(COMPONENTS[i]));
            assertEquals(liveHealth[i], live[i].health);
            assertEquals(liveRevision[i], live[i].revision);
        }
        if (snapshot != null) {
            assertEquals(overall, snapshot.overall);
            assertEquals(overall, result.overall);
            GuardianState[] after = states(snapshot);
            for (int i = 0; i < frozen.length; i++) {
                assertSame(frozen[i], after[i]);
                assertEquals(frozenHealth[i], after[i].health);
                assertEquals(frozenRevision[i], after[i].revision);
            }
        }
        return result;
    }

    @Test public void nullSnapshotClassifiesAllFiveUnknownWithoutTouchingLiveState() {
        setAll(Health.FAILED);
        assertClassification(assessWithoutMutation(null), uniform(Health.UNKNOWN));
    }

    @Test public void coldSnapshotClassifiesUnknownAndPreservesRevisionZero() {
        GuardianSnapshot snapshot = GuardianSnapshot.capture(registry);
        assertClassification(assessWithoutMutation(snapshot), uniform(Health.UNKNOWN));
        for (GuardianState state : states(snapshot)) assertEquals(0L, state.revision);
    }

    @Test public void allHealthyHasFiveHealthyMembersAndHealthyOverall() {
        setAll(Health.HEALTHY);
        GuardianSnapshot snapshot = GuardianSnapshot.capture(registry);
        assertClassification(assessWithoutMutation(snapshot), uniform(Health.HEALTHY));
        for (GuardianState state : states(snapshot)) assertEquals(1L, state.revision);
    }

    @Test public void all1024CombinationsHaveExactExclusiveMembershipCountsAndOverall() {
        assertEquals(5, COMPONENTS.length);
        for (int combination = 0; combination < 1024; combination++) {
            int digits = combination;
            Health[] expected = new Health[5];
            for (int i = 0; i < COMPONENTS.length; i++) {
                expected[i] = PRIORITY[digits % 4];
                digits /= 4;
                registry.update(COMPONENTS[i], expected[i]);
            }
            assertClassification(assessWithoutMutation(GuardianSnapshot.capture(registry)), expected);
        }
    }

    @Test public void retainedAssessmentSurvivesAllUpdatesResetAndNewSnapshots() {
        Health[] original = { Health.HEALTHY, Health.DEGRADED, Health.FAILED, Health.UNKNOWN, Health.HEALTHY };
        for (int i = 0; i < COMPONENTS.length; i++) registry.update(COMPONENTS[i], original[i]);
        GuardianSnapshot oldSnapshot = GuardianSnapshot.capture(registry);
        GuardianAssessment retained = assessWithoutMutation(oldSnapshot);
        for (Component component : COMPONENTS) {
            for (Health health : Health.values()) {
                registry.update(component, health);
                assessWithoutMutation(GuardianSnapshot.capture(registry));
                assertClassification(retained, original);
                assertClassification(assessWithoutMutation(oldSnapshot), original);
            }
        }
        for (int reset = 0; reset < 3; reset++) {
            registry.resetForColdBoot();
            assertClassification(assessWithoutMutation(GuardianSnapshot.capture(registry)), uniform(Health.UNKNOWN));
            assertClassification(retained, original);
            assertClassification(assessWithoutMutation(oldSnapshot), original);
        }
    }

    @Test public void repeatedAssessmentUsesFrozenInputRatherThanCurrentRegistry() {
        setAll(Health.DEGRADED);
        registry.update(Component.MEDIA, Health.UNKNOWN);
        Health[] expected = { Health.DEGRADED, Health.DEGRADED, Health.DEGRADED, Health.UNKNOWN, Health.DEGRADED };
        GuardianSnapshot snapshot = GuardianSnapshot.capture(registry);
        GuardianAssessment first = assessWithoutMutation(snapshot);
        setAll(Health.HEALTHY);
        for (int repeat = 0; repeat < 5; repeat++) {
            assertClassification(first, expected);
            assertClassification(assessWithoutMutation(snapshot), expected);
        }
        assertClassification(assessWithoutMutation(GuardianSnapshot.capture(registry)), uniform(Health.HEALTHY));
    }

    @Test public void nullComponentQueriesAreFalseAndPreserveAllAssessmentValues() {
        Health[] expected = { Health.HEALTHY, Health.DEGRADED, Health.FAILED, Health.UNKNOWN, Health.FAILED };
        for (int i = 0; i < COMPONENTS.length; i++) registry.update(COMPONENTS[i], expected[i]);
        GuardianSnapshot snapshot = GuardianSnapshot.capture(registry);
        GuardianAssessment assessment = assessWithoutMutation(snapshot);
        for (int repeat = 0; repeat < 3; repeat++) {
            assertFalse(assessment.isHealthy(null));
            assertFalse(assessment.isDegraded(null));
            assertFalse(assessment.isFailed(null));
            assertFalse(assessment.isUnknown(null));
            assertClassification(assessment, expected);
        }
        for (int i = 0; i < COMPONENTS.length; i++) assertSame(states(snapshot)[i], registry.snapshot(COMPONENTS[i]));
    }
}
