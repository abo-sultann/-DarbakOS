package com.abosultan.darbakos;

import com.abosultan.darbakos.core.*;
import com.abosultan.darbakos.core.GuardianRegistry.Component;
import com.abosultan.darbakos.core.GuardianState.Health;
import com.abosultan.darbakos.core.GuardianRecoveryPolicy.Plan;
import com.abosultan.darbakos.core.GuardianRecoveryPolicy.Step;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class GuardianRecoveryPolicyTest {
    private final GuardianRegistry registry = GuardianRegistry.get();
    private static final Component[] COMPONENTS = Component.values();
    private static final Health[] PRIORITY = { Health.HEALTHY, Health.UNKNOWN, Health.DEGRADED, Health.FAILED };
    private static final int[] BOUNDARIES = { Integer.MIN_VALUE, -3, -1, 0, 1, 2, 3, 4, 5, Integer.MAX_VALUE };
    // Expected contract table, independent of production branching.
    private static final Step[][] STEPS = {
        { Step.NONE, Step.NONE, Step.NONE, Step.NONE, Step.NONE, Step.NONE },
        { Step.DIAGNOSE, Step.DIAGNOSE, Step.DIAGNOSE, Step.DIAGNOSE, Step.DIAGNOSE, Step.DIAGNOSE },
        { Step.DIAGNOSE, Step.LIGHT_REPAIR, Step.LIGHT_REPAIR, Step.LIGHT_REPAIR, Step.LIGHT_REPAIR, Step.LIGHT_REPAIR },
        { Step.DIAGNOSE, Step.LIGHT_REPAIR, Step.RESTART_COMPONENT, Step.FALLBACK_STABLE, Step.SAFE_MODE, Step.SAFE_MODE }
    };

    @Before public void setUp() { registry.resetForColdBoot(); }
    @After public void tearDown() { registry.resetForColdBoot(); }

    private void setAll(Health health) {
        for (Component component : COMPONENTS) registry.update(component, health);
    }
    private static GuardianState[] states(GuardianSnapshot s) {
        return new GuardianState[] { s.core, s.home, s.navigation, s.media, s.vehicle };
    }
    private final class Input {
        final GuardianSnapshot snapshot = GuardianSnapshot.capture(registry);
        final GuardianAssessment assessment = GuardianAssessment.from(snapshot);
        final GuardianState[] saved = states(snapshot);
        final Health[] health = new Health[5];
        final long[] revision = new long[5];
        final Health overall = snapshot.overall;
        Input() {
            for (int i = 0; i < 5; i++) { health[i] = saved[i].health; revision[i] = saved[i].revision; }
        }
        void assertUnchanged() {
            assertEquals(overall, snapshot.overall);
            assertEquals(overall, assessment.overall);
            GuardianState[] now = states(snapshot);
            int h = 0, d = 0, f = 0, u = 0;
            for (int i = 0; i < 5; i++) {
                assertSame(saved[i], now[i]);
                assertEquals(health[i], now[i].health);
                assertEquals(revision[i], now[i].revision);
                assertEquals(health[i] == Health.HEALTHY, assessment.isHealthy(COMPONENTS[i]));
                assertEquals(health[i] == Health.DEGRADED, assessment.isDegraded(COMPONENTS[i]));
                assertEquals(health[i] == Health.FAILED, assessment.isFailed(COMPONENTS[i]));
                assertEquals(health[i] == Health.UNKNOWN, assessment.isUnknown(COMPONENTS[i]));
                if (health[i] == Health.HEALTHY) h++;
                if (health[i] == Health.DEGRADED) d++;
                if (health[i] == Health.FAILED) f++;
                if (health[i] == Health.UNKNOWN) u++;
            }
            assertEquals(h, assessment.healthyCount()); assertEquals(d, assessment.degradedCount());
            assertEquals(f, assessment.failedCount()); assertEquals(u, assessment.unknownCount());
        }
    }
    private static void assertOutput(Plan plan, Health[] health, int level) {
        int rank = 0;
        for (Health value : health) for (int p = 0; p < 4; p++) if (value == PRIORITY[p]) rank = Math.max(rank, p);
        assertEquals(STEPS[rank][Math.max(0, Math.min(5, level))], plan.step);
        int count = 0;
        for (int i = 0; i < 5; i++) {
            boolean target = rank != 0 && health[i] == PRIORITY[rank];
            assertEquals(target, plan.targets(COMPONENTS[i]));
            if (target) count++;
        }
        assertEquals(count, plan.targetCount());
        assertFalse(plan.targets(null));
    }
    private Plan verify(Input input, int level) {
        GuardianState[] live = new GuardianState[5];
        Health[] liveHealth = new Health[5];
        long[] liveRevision = new long[5];
        for (int i = 0; i < 5; i++) {
            live[i] = registry.snapshot(COMPONENTS[i]);
            liveHealth[i] = live[i].health; liveRevision[i] = live[i].revision;
        }
        Health[] expected = input == null ? new Health[] { Health.UNKNOWN, Health.UNKNOWN, Health.UNKNOWN, Health.UNKNOWN, Health.UNKNOWN } : input.health;
        Plan first = null;
        for (int repeat = 0; repeat < 2; repeat++) {
            Plan plan = GuardianRecoveryPolicy.recommend(input == null ? null : input.assessment, level);
            assertOutput(plan, expected, level);
            if (first == null) first = plan;
            else { assertEquals(first.step, plan.step); assertEquals(first.targetCount(), plan.targetCount()); }
            if (input != null) input.assertUnchanged();
            for (int i = 0; i < 5; i++) {
                assertSame(live[i], registry.snapshot(COMPONENTS[i]));
                assertEquals(liveHealth[i], live[i].health); assertEquals(liveRevision[i], live[i].revision);
            }
        }
        return first;
    }

    @Test public void nullAssessmentOnlyDiagnosesAllUnknownAtEveryBoundary() {
        setAll(Health.FAILED);
        for (int level : BOUNDARIES) verify(null, level);
    }
    @Test public void healthyAndUnknownNeverEscalateAtAnyBoundary() {
        for (Health health : new Health[] { Health.HEALTHY, Health.UNKNOWN }) {
            setAll(health); Input input = new Input();
            for (int level : BOUNDARIES) verify(input, level);
        }
    }
    @Test public void degradedWithUnknownNeverPassesLightRepair() {
        setAll(Health.DEGRADED);
        registry.update(Component.MEDIA, Health.UNKNOWN);
        Input input = new Input();
        for (int level : BOUNDARIES) verify(input, level);
    }
    @Test public void failedUsesExactLadderAndClampsNegativeLevels() {
        setAll(Health.FAILED);
        Input input = new Input();
        for (int level : BOUNDARIES) verify(input, level);
    }
    @Test public void everyComponentCanBeTheSoleRelevantTarget() {
        for (Component component : COMPONENTS) for (Health health : new Health[] { Health.FAILED, Health.DEGRADED, Health.UNKNOWN }) {
            setAll(Health.HEALTHY); registry.update(component, health);
            Input input = new Input();
            for (int level = 0; level <= 5; level++) {
                Plan plan = verify(input, level);
                assertEquals(1, plan.targetCount()); assertTrue(plan.targets(component));
            }
        }
    }
    @Test public void all6144CombinationLevelsAreDeterministicAndNonMutating() {
        assertEquals(5, COMPONENTS.length);
        int checked = 0;
        for (int combination = 0; combination < 1024; combination++) {
            int digits = combination;
            for (Component component : COMPONENTS) {
                registry.update(component, PRIORITY[digits % 4]); digits /= 4;
            }
            Input input = new Input();
            for (int level = 0; level <= 5; level++) { verify(input, level); checked++; }
        }
        assertEquals(6144, checked);
    }
    @Test public void retainedPlansSurviveUpdatesResetAndNewAssessments() {
        Health[] mixed = { Health.FAILED, Health.DEGRADED, Health.UNKNOWN, Health.FAILED, Health.HEALTHY };
        for (int i = 0; i < 5; i++) registry.update(COMPONENTS[i], mixed[i]);
        Input retained = new Input();
        Plan[] plans = new Plan[6];
        for (int level = 0; level < 6; level++) plans[level] = verify(retained, level);
        for (Health next : Health.values()) {
            setAll(next);
            Input current = new Input();
            for (int level = 0; level < 6; level++) { verify(current, level); assertOutput(plans[level], mixed, level); }
            retained.assertUnchanged();
        }
        registry.resetForColdBoot();
        Input cold = new Input();
        for (int level = 0; level < 6; level++) {
            verify(cold, level); verify(retained, level); assertOutput(plans[level], mixed, level);
        }
        retained.assertUnchanged();
    }
}
