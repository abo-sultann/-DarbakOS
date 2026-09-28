package com.abosultan.darbakos;

import com.abosultan.darbakos.core.GuardianPolicy;
import com.abosultan.darbakos.core.GuardianRegistry;
import com.abosultan.darbakos.core.GuardianRegistry.Component;
import com.abosultan.darbakos.core.GuardianState;
import com.abosultan.darbakos.core.GuardianState.Health;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class GuardianPolicyTest {
    private final GuardianRegistry registry = GuardianRegistry.get();
    private static final Component[] COMPONENTS = Component.values();
    // Specification order, independent of enum declaration order and production control flow.
    private static final Health[] PRECEDENCE = {
            Health.HEALTHY, Health.UNKNOWN, Health.DEGRADED, Health.FAILED };

    @Before public void setUp() { registry.resetForColdBoot(); }
    @After public void tearDown() { registry.resetForColdBoot(); }

    private void setAll(Health health) {
        for (Component component : COMPONENTS) registry.update(component, health);
    }

    private void assertAggregate(Health expected, GuardianRegistry input) {
        GuardianState[] before = new GuardianState[COMPONENTS.length];
        Health[] health = new Health[COMPONENTS.length];
        long[] revision = new long[COMPONENTS.length];
        for (int i = 0; i < COMPONENTS.length; i++) {
            before[i] = registry.snapshot(COMPONENTS[i]);
            health[i] = before[i].health;
            revision[i] = before[i].revision;
        }
        for (int repeat = 0; repeat < 2; repeat++) {
            assertEquals(expected, GuardianPolicy.aggregate(input));
            for (int i = 0; i < COMPONENTS.length; i++) {
                assertSame("Aggregation must not replace a component snapshot", before[i],
                        registry.snapshot(COMPONENTS[i]));
                assertEquals(health[i], before[i].health);
                assertEquals(revision[i], before[i].revision);
            }
        }
    }

    @Test public void nullRegistryIsUnknownWithoutMutatingExistingRegistry() {
        setAll(Health.HEALTHY);
        registry.update(Component.CORE, Health.FAILED);
        registry.update(Component.MEDIA, Health.DEGRADED);
        assertAggregate(Health.UNKNOWN, null);
        assertAggregate(Health.FAILED, registry);
    }

    @Test public void allUnknownColdBootRemainsUnknownAtRevisionZero() {
        assertAggregate(Health.UNKNOWN, registry);
        for (Component component : COMPONENTS) {
            assertEquals(Health.UNKNOWN, registry.snapshot(component).health);
            assertEquals(0L, registry.snapshot(component).revision);
        }
    }

    @Test public void allHealthyIsHealthyWithoutRevisionChanges() {
        setAll(Health.HEALTHY);
        assertAggregate(Health.HEALTHY, registry);
        for (Component component : COMPONENTS) assertEquals(1L, registry.snapshot(component).revision);
    }

    @Test public void everyComponentCanBeTheSoleFailedDegradedOrUnknownSource() {
        for (Component component : COMPONENTS) {
            for (Health health : new Health[] { Health.FAILED, Health.DEGRADED, Health.UNKNOWN }) {
                setAll(Health.HEALTHY);
                registry.update(component, health);
                assertAggregate(health, registry);
            }
        }
    }

    @Test public void all1024CombinationsRespectPrecedenceWithoutMutation() {
        assertEquals(5, COMPONENTS.length);
        int[] outcomes = new int[PRECEDENCE.length];
        for (int combination = 0; combination < 1024; combination++) {
            int digits = combination;
            int highestRank = 0;
            for (Component component : COMPONENTS) {
                int rank = digits % 4;
                digits /= 4;
                registry.update(component, PRECEDENCE[rank]);
                highestRank = Math.max(highestRank, rank);
            }
            assertEquals(0, digits);
            assertAggregate(PRECEDENCE[highestRank], registry);
            outcomes[highestRank]++;
        }
        // 1 all-healthy; 2^5-1 unknown; 3^5-2^5 degraded; 4^5-3^5 failed.
        assertArrayEquals(new int[] { 1, 31, 211, 781 }, outcomes);
    }

    @Test public void eachComponentRecoversFromFailedAndDegradedToAllHealthy() {
        for (Component component : COMPONENTS) {
            for (Health problem : new Health[] { Health.FAILED, Health.DEGRADED }) {
                setAll(Health.HEALTHY);
                GuardianState retained = registry.update(component, problem);
                long revision = retained.revision;
                assertAggregate(problem, registry);
                GuardianState recovered = registry.update(component, Health.HEALTHY);
                assertEquals(revision + 1L, recovered.revision);
                assertNotSame(retained, recovered);
                assertAggregate(Health.HEALTHY, registry);
                assertEquals(problem, retained.health);
                assertEquals(revision, retained.revision);
            }
        }
    }

    @Test public void mixedRecoveryAndResetFollowRemainingPriorityWithoutCachedHealth() {
        setAll(Health.HEALTHY);
        registry.update(Component.CORE, Health.FAILED);
        registry.update(Component.HOME, Health.DEGRADED);
        registry.update(Component.MEDIA, Health.UNKNOWN);
        assertAggregate(Health.FAILED, registry);
        registry.update(Component.CORE, Health.HEALTHY);
        assertAggregate(Health.DEGRADED, registry);
        registry.update(Component.HOME, Health.HEALTHY);
        assertAggregate(Health.UNKNOWN, registry);
        registry.update(Component.MEDIA, Health.HEALTHY);
        assertAggregate(Health.HEALTHY, registry);
        registry.resetForColdBoot();
        assertAggregate(Health.UNKNOWN, registry);
    }
}
