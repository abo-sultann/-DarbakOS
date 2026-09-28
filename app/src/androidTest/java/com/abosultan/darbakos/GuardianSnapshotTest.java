package com.abosultan.darbakos;

import com.abosultan.darbakos.core.GuardianRegistry;
import com.abosultan.darbakos.core.GuardianRegistry.Component;
import com.abosultan.darbakos.core.GuardianSnapshot;
import com.abosultan.darbakos.core.GuardianState;
import com.abosultan.darbakos.core.GuardianState.Health;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class GuardianSnapshotTest {
    private final GuardianRegistry registry = GuardianRegistry.get();
    private static final Component[] COMPONENTS = Component.values();
    private static final Health[] PRECEDENCE = {
            Health.HEALTHY, Health.UNKNOWN, Health.DEGRADED, Health.FAILED };

    @Before public void setUp() { registry.resetForColdBoot(); }
    @After public void tearDown() { registry.resetForColdBoot(); }

    private static GuardianState[] states(GuardianSnapshot snapshot) {
        return new GuardianState[] { snapshot.core, snapshot.home, snapshot.navigation,
                snapshot.media, snapshot.vehicle };
    }

    private void setAll(Health health) {
        for (Component component : COMPONENTS) registry.update(component, health);
    }

    private static final class Frozen {
        final GuardianSnapshot snapshot;
        final GuardianState[] states;
        final Health[] health = new Health[5];
        final long[] revision = new long[5];
        final Health overall;
        Frozen(GuardianSnapshot snapshot) {
            this.snapshot = snapshot;
            this.states = states(snapshot);
            this.overall = snapshot.overall;
            for (int i = 0; i < states.length; i++) {
                health[i] = states[i].health;
                revision[i] = states[i].revision;
            }
        }
        void assertUnchanged() {
            assertEquals(overall, snapshot.overall);
            GuardianState[] retained = states(snapshot);
            for (int i = 0; i < states.length; i++) {
                assertSame(states[i], retained[i]);
                assertEquals(health[i], retained[i].health);
                assertEquals(revision[i], retained[i].revision);
            }
        }
    }

    private GuardianSnapshot captureAndCheck(Health overall) {
        GuardianState[] before = new GuardianState[5];
        Health[] health = new Health[5];
        long[] revision = new long[5];
        for (int i = 0; i < COMPONENTS.length; i++) {
            before[i] = registry.snapshot(COMPONENTS[i]);
            health[i] = before[i].health;
            revision[i] = before[i].revision;
        }
        GuardianSnapshot captured = GuardianSnapshot.capture(registry);
        assertEquals(overall, captured.overall);
        GuardianState[] values = states(captured);
        for (int i = 0; i < COMPONENTS.length; i++) {
            assertSame(before[i], values[i]);
            assertSame(before[i], registry.snapshot(COMPONENTS[i]));
            assertEquals(health[i], values[i].health);
            assertEquals(revision[i], values[i].revision);
        }
        return captured;
    }

    @Test public void nullRegistryReturnsColdValuesWithoutChangingLiveState() {
        setAll(Health.FAILED);
        Frozen live = new Frozen(captureAndCheck(Health.FAILED));
        GuardianSnapshot empty = GuardianSnapshot.capture(null);
        assertEquals(Health.UNKNOWN, empty.overall);
        for (GuardianState value : states(empty)) {
            assertNotNull(value);
            assertEquals(Health.UNKNOWN, value.health);
            assertEquals(0L, value.revision);
        }
        live.assertUnchanged();
        for (int i = 0; i < COMPONENTS.length; i++) {
            assertSame(live.states[i], registry.snapshot(COMPONENTS[i]));
        }
    }

    @Test public void coldCapturePreservesExactUnknownSnapshotsAtRevisionZero() {
        GuardianSnapshot captured = captureAndCheck(Health.UNKNOWN);
        for (GuardianState value : states(captured)) {
            assertEquals(Health.UNKNOWN, value.health);
            assertEquals(0L, value.revision);
        }
    }

    @Test public void all1024CombinationsCaptureExactStatesAndCorrectAggregate() {
        assertEquals(5, COMPONENTS.length);
        Frozen previous = null;
        for (int combination = 0; combination < 1024; combination++) {
            int digits = combination;
            int highestRank = 0;
            for (Component component : COMPONENTS) {
                int rank = digits % 4;
                digits /= 4;
                registry.update(component, PRECEDENCE[rank]);
                highestRank = Math.max(highestRank, rank);
            }
            if (previous != null) previous.assertUnchanged();
            previous = new Frozen(captureAndCheck(PRECEDENCE[highestRank]));
        }
        assertNotNull(previous);
        previous.assertUnchanged();
    }

    @Test public void retainedSnapshotSurvivesEveryComponentUpdateAndRepeatedReset() {
        for (int i = 0; i < COMPONENTS.length; i++) {
            registry.update(COMPONENTS[i], Health.FAILED);
            registry.update(COMPONENTS[i], PRECEDENCE[i % 4]);
        }
        Frozen retained = new Frozen(captureAndCheck(Health.FAILED));
        for (Component component : COMPONENTS) {
            for (Health health : Health.values()) {
                registry.update(component, health);
                retained.assertUnchanged();
            }
        }
        for (int reset = 0; reset < 3; reset++) {
            registry.resetForColdBoot();
            retained.assertUnchanged();
            GuardianSnapshot cold = captureAndCheck(Health.UNKNOWN);
            for (int i = 0; i < COMPONENTS.length; i++) {
                assertNotSame(retained.states[i], states(cold)[i]);
                assertEquals(0L, states(cold)[i].revision);
            }
        }
    }

    @Test public void repeatedUnchangedCapturePreservesComponentIdentitiesAndRevisions() {
        setAll(Health.DEGRADED);
        registry.update(Component.HOME, Health.UNKNOWN);
        Frozen first = new Frozen(captureAndCheck(Health.DEGRADED));
        for (int repeat = 0; repeat < 3; repeat++) {
            GuardianSnapshot next = captureAndCheck(Health.DEGRADED);
            GuardianState[] values = states(next);
            for (int i = 0; i < values.length; i++) assertSame(first.states[i], values[i]);
            first.assertUnchanged();
        }
    }

    @Test public void newCaptureReflectsRecoveryWhileOldSnapshotRemainsUnchanged() {
        setAll(Health.HEALTHY);
        for (Component component : COMPONENTS) {
            for (Health problem : new Health[] { Health.FAILED, Health.DEGRADED }) {
                registry.update(component, problem);
                Frozen before = new Frozen(captureAndCheck(problem));
                registry.update(component, Health.HEALTHY);
                GuardianSnapshot recovered = captureAndCheck(Health.HEALTHY);
                GuardianState[] values = states(recovered);
                for (int i = 0; i < COMPONENTS.length; i++) {
                    if (COMPONENTS[i] == component) {
                        assertNotSame(before.states[i], values[i]);
                        assertEquals(before.revision[i] + 1L, values[i].revision);
                    } else assertSame(before.states[i], values[i]);
                }
                before.assertUnchanged();
            }
        }
    }

    @Test public void concurrentUpdatesAndResetCannotSplitSnapshotOrAggregate() throws Exception {
        AtomicBoolean running = new AtomicBoolean(true);
        CountDownLatch ready = new CountDownLatch(1);
        // Finite test-only writer; production creates no thread or scheduled work.
        Thread writer = new Thread(() -> {
            while (running.get()) {
                synchronized (registry) { setAll(Health.HEALTHY); }
                ready.countDown();
                synchronized (registry) { setAll(Health.FAILED); }
                registry.resetForColdBoot();
            }
        }, "guardian-snapshot-test");
        writer.start();
        try {
            assertTrue("Test writer must start", ready.await(5, TimeUnit.SECONDS));
            for (int capture = 0; capture < 20000; capture++) {
                GuardianSnapshot snapshot = GuardianSnapshot.capture(registry);
                Health health = snapshot.core.health;
                long revision = snapshot.core.revision;
                assertEquals(health, snapshot.overall);
                assertTrue(health == Health.HEALTHY || health == Health.FAILED || health == Health.UNKNOWN);
                assertEquals(health == Health.UNKNOWN ? 0L : health == Health.HEALTHY ? 1L : 2L, revision);
                for (GuardianState state : states(snapshot)) {
                    assertEquals(health, state.health);
                    assertEquals(revision, state.revision);
                }
            }
        } finally {
            running.set(false);
            writer.join(5000);
            assertFalse("Test writer must be joined before teardown", writer.isAlive());
        }
    }
}
