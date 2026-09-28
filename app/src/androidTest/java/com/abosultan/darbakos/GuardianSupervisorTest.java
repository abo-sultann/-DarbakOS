package com.abosultan.darbakos;

import com.abosultan.darbakos.core.*;
import com.abosultan.darbakos.core.GuardianRegistry.Component;
import com.abosultan.darbakos.core.GuardianState.Health;
import com.abosultan.darbakos.core.GuardianRecoveryPolicy.Step;
import com.abosultan.darbakos.core.GuardianSupervisor.Result;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class GuardianSupervisorTest {
    private final GuardianRegistry registry = GuardianRegistry.get();
    private static final Component[] COMPONENTS = Component.values();
    private static final Health[] PRIORITY = { Health.HEALTHY, Health.UNKNOWN, Health.DEGRADED, Health.FAILED };
    private static final int[] BOUNDARIES = { Integer.MIN_VALUE, -3, -1, 0, 1, 2, 3, 4, 5, 6, 100, Integer.MAX_VALUE };
    // Contract oracle: do not call the production assessment or policy to calculate expectations.
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
    private static GuardianState[] states(GuardianSnapshot snapshot) {
        return new GuardianState[] { snapshot.core, snapshot.home, snapshot.navigation, snapshot.media, snapshot.vehicle };
    }
    private static void assertChain(Result result, Health[] health, long[] revision, int level) {
        assertNotNull(result); assertNotNull(result.snapshot);
        assertNotNull(result.assessment); assertNotNull(result.plan);
        GuardianState[] captured = states(result.snapshot);
        int[] counts = new int[4];
        int highest = 0;
        for (int i = 0; i < COMPONENTS.length; i++) {
            assertNotNull(captured[i]);
            assertEquals(health[i], captured[i].health);
            assertEquals(revision[i], captured[i].revision);
            for (int rank = 0; rank < PRIORITY.length; rank++) {
                if (health[i] == PRIORITY[rank]) { counts[rank]++; highest = Math.max(highest, rank); }
            }
            assertEquals(health[i] == Health.HEALTHY, result.assessment.isHealthy(COMPONENTS[i]));
            assertEquals(health[i] == Health.DEGRADED, result.assessment.isDegraded(COMPONENTS[i]));
            assertEquals(health[i] == Health.FAILED, result.assessment.isFailed(COMPONENTS[i]));
            assertEquals(health[i] == Health.UNKNOWN, result.assessment.isUnknown(COMPONENTS[i]));
        }
        assertEquals(PRIORITY[highest], result.snapshot.overall);
        assertEquals(PRIORITY[highest], result.assessment.overall);
        assertEquals(counts[0], result.assessment.healthyCount());
        assertEquals(counts[1], result.assessment.unknownCount());
        assertEquals(counts[2], result.assessment.degradedCount());
        assertEquals(counts[3], result.assessment.failedCount());
        assertEquals(5, counts[0] + counts[1] + counts[2] + counts[3]);
        assertFalse(result.assessment.isHealthy(null)); assertFalse(result.assessment.isDegraded(null));
        assertFalse(result.assessment.isFailed(null)); assertFalse(result.assessment.isUnknown(null));
        assertEquals(STEPS[highest][Math.max(0, Math.min(5, level))], result.plan.step);
        assertEquals(highest == 0 ? 0 : counts[highest], result.plan.targetCount());
        for (int i = 0; i < COMPONENTS.length; i++) {
            assertEquals(highest != 0 && health[i] == PRIORITY[highest], result.plan.targets(COMPONENTS[i]));
        }
        assertFalse(result.plan.targets(null));
    }
    private Result evaluateAndCheck(GuardianRegistry source, int level) {
        GuardianState[] before = new GuardianState[5];
        Health[] health = new Health[5];
        long[] revision = new long[5];
        for (int i = 0; i < 5; i++) {
            before[i] = registry.snapshot(COMPONENTS[i]);
            health[i] = before[i].health; revision[i] = before[i].revision;
        }
        Result result = GuardianSupervisor.evaluate(source, level);
        assertChain(result, source == null ? new Health[] { Health.UNKNOWN, Health.UNKNOWN, Health.UNKNOWN, Health.UNKNOWN, Health.UNKNOWN } : health,
                source == null ? new long[5] : revision, level);
        GuardianState[] captured = states(result.snapshot);
        for (int i = 0; i < 5; i++) {
            assertSame(before[i], registry.snapshot(COMPONENTS[i]));
            assertEquals(health[i], before[i].health); assertEquals(revision[i], before[i].revision);
            if (source != null) assertSame(before[i], captured[i]);
        }
        return result;
    }
    private static final class Frozen {
        final Result result;
        final GuardianSnapshot snapshot;
        final GuardianAssessment assessment;
        final GuardianRecoveryPolicy.Plan plan;
        final GuardianState[] saved;
        final Health[] health = new Health[5];
        final long[] revision = new long[5];
        final int level;
        Frozen(Result result, int level) {
            this.result = result; this.level = level;
            snapshot = result.snapshot; assessment = result.assessment; plan = result.plan;
            saved = states(snapshot);
            for (int i = 0; i < 5; i++) { health[i] = saved[i].health; revision[i] = saved[i].revision; }
            assertUnchanged();
        }
        void assertUnchanged() {
            assertSame(snapshot, result.snapshot); assertSame(assessment, result.assessment); assertSame(plan, result.plan);
            GuardianState[] current = states(result.snapshot);
            for (int i = 0; i < 5; i++) assertSame(saved[i], current[i]);
            assertChain(result, health, revision, level);
        }
    }

    @Test public void nullRegistryIsUnknownAtEveryBoundaryWithoutTouchingLiveState() {
        setAll(Health.FAILED);
        for (int level : BOUNDARIES) evaluateAndCheck(null, level);
    }
    @Test public void coldRegistryIsUnknownAtRevisionZeroAtEveryBoundary() {
        for (int level : BOUNDARIES) {
            Result result = evaluateAndCheck(registry, level);
            for (GuardianState state : states(result.snapshot)) assertEquals(0L, state.revision);
            assertEquals(5, result.assessment.unknownCount());
            assertEquals(Step.DIAGNOSE, result.plan.step);
        }
    }
    @Test public void all6144CombinationLevelsHaveExactChainAndDoNotMutateRegistry() {
        assertEquals(5, COMPONENTS.length);
        int checked = 0;
        for (int combination = 0; combination < 1024; combination++) {
            int digits = combination;
            for (Component component : COMPONENTS) { registry.update(component, PRIORITY[digits % 4]); digits /= 4; }
            for (int level = 0; level <= 5; level++) { evaluateAndCheck(registry, level); checked++; }
        }
        assertEquals(6144, checked);
    }
    @Test public void negativeAndHighLevelsPreserveEveryConservativePolicyBoundary() {
        for (Health health : PRIORITY) {
            setAll(health);
            for (int level : BOUNDARIES) evaluateAndCheck(registry, level);
        }
        for (Component component : COMPONENTS) {
            for (Health health : new Health[] { Health.UNKNOWN, Health.DEGRADED, Health.FAILED }) {
                setAll(Health.HEALTHY); registry.update(component, health);
                for (int level : BOUNDARIES) {
                    Result result = evaluateAndCheck(registry, level);
                    assertEquals(1, result.plan.targetCount()); assertTrue(result.plan.targets(component));
                }
            }
        }
    }
    @Test public void retainedResultsSurviveEveryComponentUpdateAndRepeatedReset() {
        Health[] mixed = { Health.FAILED, Health.DEGRADED, Health.UNKNOWN, Health.FAILED, Health.HEALTHY };
        for (int i = 0; i < 5; i++) registry.update(COMPONENTS[i], mixed[i]);
        Frozen[] retained = new Frozen[6];
        for (int level = 0; level < 6; level++) retained[level] = new Frozen(evaluateAndCheck(registry, level), level);
        for (Component component : COMPONENTS) for (Health next : PRIORITY) {
            registry.update(component, next);
            for (int level = 0; level < 6; level++) { evaluateAndCheck(registry, level); retained[level].assertUnchanged(); }
        }
        for (int reset = 0; reset < 3; reset++) {
            registry.resetForColdBoot();
            for (int level = 0; level < 6; level++) { evaluateAndCheck(registry, level); retained[level].assertUnchanged(); }
        }
    }
    @Test public void laterEvaluationReflectsChangesWhileOlderResultStaysFrozen() {
        setAll(Health.HEALTHY);
        for (Component component : COMPONENTS) for (Health problem : new Health[] { Health.UNKNOWN, Health.DEGRADED, Health.FAILED }) {
            registry.update(component, problem);
            Frozen old = new Frozen(evaluateAndCheck(registry, 5), 5);
            registry.update(component, Health.HEALTHY);
            Result next = evaluateAndCheck(registry, 5);
            assertEquals(Health.HEALTHY, next.snapshot.overall); assertEquals(Step.NONE, next.plan.step);
            GuardianState[] current = states(next.snapshot);
            for (int i = 0; i < 5; i++) {
                if (COMPONENTS[i] == component) {
                    assertNotSame(old.saved[i], current[i]); assertEquals(old.revision[i] + 1L, current[i].revision);
                } else assertSame(old.saved[i], current[i]);
            }
            old.assertUnchanged();
        }
    }
    @Test public void repeatedUnchangedEvaluationsKeepValuesAndEveryRevision() {
        for (int i = 0; i < 5; i++) registry.update(COMPONENTS[i], PRIORITY[i % 4]);
        Frozen[] first = new Frozen[6];
        for (int level = 0; level < 6; level++) first[level] = new Frozen(evaluateAndCheck(registry, level), level);
        for (int repeat = 0; repeat < 100; repeat++) for (int level = 0; level < 6; level++) {
            Result result = evaluateAndCheck(registry, level);
            assertChain(result, first[level].health, first[level].revision, level);
            GuardianState[] current = states(result.snapshot);
            for (int i = 0; i < 5; i++) assertSame(first[level].saved[i], current[i]);
            first[level].assertUnchanged();
        }
    }
    @Test public void concurrentCallersUpdatesAndResetCannotSplitAnyResult() throws Exception {
        AtomicBoolean running = new AtomicBoolean(true);
        AtomicInteger completed = new AtomicInteger();
        AtomicInteger cycles = new AtomicInteger();
        AtomicReference<Throwable> failure = new AtomicReference<>();
        CountDownLatch ready = new CountDownLatch(3);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch firstCycle = new CountDownLatch(1);
        CountDownLatch readersDone = new CountDownLatch(2);
        // Test-only caller threads. No worker, polling or scheduler is added to production.
        Thread writer = new Thread(() -> {
            ready.countDown();
            try {
                assertTrue(start.await(5, TimeUnit.SECONDS));
                while (running.get()) {
                    for (Health health : new Health[] { Health.HEALTHY, Health.DEGRADED, Health.FAILED, Health.UNKNOWN }) {
                        synchronized (registry) { setAll(health); }
                    }
                    registry.resetForColdBoot();
                    cycles.incrementAndGet(); firstCycle.countDown();
                }
            } catch (Throwable error) { failure.compareAndSet(null, error); firstCycle.countDown(); }
        }, "guardian-supervisor-test-writer");
        Thread[] readers = new Thread[2];
        for (int reader = 0; reader < readers.length; reader++) {
            final int offset = reader;
            readers[reader] = new Thread(() -> {
                ready.countDown();
                try {
                    assertTrue(start.await(5, TimeUnit.SECONDS));
                    assertTrue(firstCycle.await(5, TimeUnit.SECONDS));
                    Frozen[] retained = new Frozen[10];
                    for (int evaluation = 0; evaluation < 10000; evaluation++) {
                        int level = (evaluation + offset) % 6;
                        Result result = GuardianSupervisor.evaluate(registry, level);
                        Frozen checked = new Frozen(result, level);
                        Health health = checked.health[0];
                        long revision = checked.revision[0];
                        // Writer publishes only uniform atomic states, so mixed captures are impossible.
                        assertEquals(health, result.snapshot.overall);
                        if (health == Health.UNKNOWN) assertTrue(revision == 0L || revision == 4L);
                        else assertEquals(health == Health.HEALTHY ? 1L : health == Health.DEGRADED ? 2L : 3L, revision);
                        for (int i = 0; i < 5; i++) { assertEquals(health, checked.health[i]); assertEquals(revision, checked.revision[i]); }
                        if (evaluation % 1000 == 0) retained[evaluation / 1000] = checked;
                        completed.incrementAndGet();
                    }
                    for (Frozen old : retained) old.assertUnchanged();
                } catch (Throwable error) { failure.compareAndSet(null, error); }
                finally { readersDone.countDown(); }
            }, "guardian-supervisor-test-reader-" + reader);
        }
        writer.start(); for (Thread reader : readers) reader.start();
        try {
            assertTrue("All test callers must start", ready.await(5, TimeUnit.SECONDS));
            start.countDown();
            assertTrue("Concurrent evaluations must finish", readersDone.await(30, TimeUnit.SECONDS));
        } finally {
            running.set(false); start.countDown();
            writer.join(5000); for (Thread reader : readers) reader.join(5000);
            assertFalse("Writer must stop before teardown", writer.isAlive());
            for (Thread reader : readers) assertFalse("Reader must stop before teardown", reader.isAlive());
        }
        if (failure.get() != null) throw new AssertionError("Concurrent evaluation failed", failure.get());
        assertEquals(20000, completed.get()); assertTrue("Writer must exercise update/reset", cycles.get() > 0);
        registry.resetForColdBoot(); evaluateAndCheck(registry, 5);
    }
}
