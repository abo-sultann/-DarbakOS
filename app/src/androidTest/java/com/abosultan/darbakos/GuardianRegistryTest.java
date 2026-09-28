package com.abosultan.darbakos;

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
public final class GuardianRegistryTest {
    private final GuardianRegistry registry = GuardianRegistry.get();

    @Before public void setUp() { registry.resetForColdBoot(); }
    @After public void tearDown() { registry.resetForColdBoot(); }

    private static void assertState(GuardianState state, Health health, long revision) {
        assertNotNull(state);
        assertEquals(health, state.health);
        assertEquals(revision, state.revision);
    }

    private final class Captured {
        final GuardianState[] states = new GuardianState[Component.values().length];
        final Health[] health = new Health[states.length];
        final long[] revision = new long[states.length];
        Captured() {
            for (Component component : Component.values()) {
                int i = component.ordinal();
                states[i] = registry.snapshot(component);
                health[i] = states[i].health;
                revision[i] = states[i].revision;
            }
        }
        void assertRetained() {
            for (int i = 0; i < states.length; i++) assertState(states[i], health[i], revision[i]);
        }
        void assertRegistryUnchangedExcept(Component changed) {
            assertRetained();
            for (Component component : Component.values()) {
                if (component != changed) assertSame(states[component.ordinal()], registry.snapshot(component));
            }
        }
    }

    private void seedMixedStates() {
        for (Component component : Component.values()) {
            registry.update(component, Health.HEALTHY);
            registry.update(component, Health.DEGRADED);
            registry.update(component, Health.values()[component.ordinal() % Health.values().length]);
        }
    }

    @Test public void coldBootInitializesAllFiveComponentsAndStableSnapshots() {
        assertSame(registry, GuardianRegistry.get());
        assertArrayEquals(new Component[] { Component.CORE, Component.HOME, Component.NAVIGATION,
                Component.MEDIA, Component.VEHICLE }, Component.values());
        for (Component component : Component.values()) {
            GuardianState state = registry.snapshot(component);
            assertState(state, Health.UNKNOWN, 0L);
            assertSame(state, registry.snapshot(component));
            for (Component other : Component.values()) {
                if (other != component) assertNotSame(state, registry.snapshot(other));
            }
        }
    }

    @Test public void everyComponentTransitionIsIsolatedAndAdvancesExactlyOnce() {
        for (Component component : Component.values()) {
            for (Health from : Health.values()) {
                for (Health to : Health.values()) {
                    if (from == to) continue;
                    registry.resetForColdBoot();
                    seedMixedStates();
                    registry.update(component, from);
                    Captured before = new Captured();
                    GuardianState next = registry.update(component, to);
                    assertSame(next, registry.snapshot(component));
                    assertNotSame(before.states[component.ordinal()], next);
                    assertState(next, to, before.revision[component.ordinal()] + 1L);
                    before.assertRegistryUnchangedExcept(component);
                }
            }
        }
    }

    @Test public void sameHealthPreservesIdentityRevisionAndAllOtherComponents() {
        seedMixedStates();
        for (Component component : Component.values()) {
            for (Health health : Health.values()) {
                GuardianState current = registry.update(component, health);
                Captured before = new Captured();
                assertSame(current, registry.update(component, health));
                assertSame(current, registry.update(component, health));
                before.assertRegistryUnchangedExcept(null);
            }
        }
    }

    @Test public void nullHealthPreservesIdentityRevisionAndAllOtherComponents() {
        seedMixedStates();
        for (Component component : Component.values()) {
            for (Health health : Health.values()) {
                GuardianState current = registry.update(component, health);
                Captured before = new Captured();
                assertSame(current, registry.update(component, null));
                before.assertRegistryUnchangedExcept(null);
            }
        }
    }

    @Test public void repeatedColdResetReplacesEverySnapshotAndRetainsOldStates() {
        seedMixedStates();
        Captured original = new Captured();
        for (int reset = 0; reset < 3; reset++) {
            Captured before = new Captured();
            registry.resetForColdBoot();
            for (Component component : Component.values()) {
                GuardianState state = registry.snapshot(component);
                assertState(state, Health.UNKNOWN, 0L);
                assertNotSame(before.states[component.ordinal()], state);
                assertSame(state, registry.snapshot(component));
            }
            before.assertRetained();
            original.assertRetained();
        }
    }

    @Test public void nullComponentLookupAndUpdatesDoNotAlterRegistryContents() {
        seedMixedStates();
        Captured before = new Captured();
        assertState(registry.snapshot(null), Health.UNKNOWN, 0L);
        for (Health health : Health.values()) {
            assertState(registry.update(null, health), Health.UNKNOWN, 0L);
            before.assertRegistryUnchangedExcept(null);
        }
        assertState(registry.update(null, null), Health.UNKNOWN, 0L);
        before.assertRegistryUnchangedExcept(null);
    }

    @Test public void eachComponentCanRecoverFromFailedWithoutAffectingOthers() {
        seedMixedStates();
        for (Component component : Component.values()) {
            GuardianState failed = registry.update(component, Health.FAILED);
            Captured before = new Captured();
            GuardianState healthy = registry.update(component, Health.HEALTHY);
            assertNotSame(failed, healthy);
            assertSame(healthy, registry.snapshot(component));
            assertState(healthy, Health.HEALTHY, before.revision[component.ordinal()] + 1L);
            assertState(failed, Health.FAILED, before.revision[component.ordinal()]);
            before.assertRegistryUnchangedExcept(component);
        }
    }
}
