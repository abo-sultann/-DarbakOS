package com.abosultan.darbakos;

import com.abosultan.darbakos.core.*;
import com.abosultan.darbakos.core.GuardianRegistry.Component;
import com.abosultan.darbakos.core.GuardianState.Health;
import com.abosultan.darbakos.core.GuardianLivenessPolicy.Liveness;
import com.abosultan.darbakos.core.GuardianRecoveryPolicy.Step;
import java.lang.reflect.Field;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/** Shared local/API25 focused checks; all concurrency and reflection are test-only. */
public final class SessionFocusedChecks {
    private static final Component[] C = Component.values();
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
    private static GuardianState[] states(GuardianSnapshot s) {
        return new GuardianState[] {s.core, s.home, s.navigation, s.media, s.vehicle};
    }
    private static GuardianMonitorSession session(int capacity) {
        GuardianRegistry.get().resetForColdBoot();
        return new GuardianMonitorSession(GuardianRegistry.get(), new GuardianMonitorConfig(10, 20), capacity);
    }
    private static void cold(GuardianMonitorSession.Snapshot s) {
        check(s.health.overall == Health.UNKNOWN && s.events.isEmpty(), "cold aggregate/journal");
        GuardianState[] states = states(s.health);
        for (int i = 0; i < C.length; i++) {
            check(s.heartbeat(C[i]) == null, "cold heartbeat " + C[i]);
            check(states[i].health == Health.UNKNOWN && states[i].revision == 0, "cold health/revision");
        }
    }

    public static void generationOrderingAndAllComponentIsolation() {
        GuardianMonitorSession s = session(8);
        check(s.generation() == 0, "initial generation");
        for (int round = 0; round < 3; round++) {
            long generation = s.generation();
            for (Component c : C) {
                check(!s.heartbeat(generation - 1, c, 99, 999), "old token rejected");
                check(!s.heartbeat(generation + 1, c, 99, 999), "future token rejected");
                check(s.heartbeat(generation, c, 1, 100), "current token accepted");
                GuardianHeartbeat first = s.snapshot().heartbeat(c);
                check(!s.heartbeat(generation, c, 1, 100), "duplicate rejected");
                check(!s.heartbeat(generation, c, 0, 200), "sequence regression rejected");
                check(!s.heartbeat(generation, c, 2, 99), "time regression rejected");
                check(s.snapshot().heartbeat(c) == first, "rejections preserve latest");
                check(s.heartbeat(c, 2, 101), "current-generation convenience call");
            }
            GuardianMonitorSession.Snapshot saved = s.snapshot();
            s.resetForColdBoot();
            check(s.generation() == generation + 1, "one increment");
            cold(s.snapshot());
            for (Component c : C) {
                check(!s.heartbeat(generation, c, 100, 1000), "delayed post-reset rejected");
                check(saved.heartbeat(c).sequence == 2, "prior heartbeat retained");
            }
            check(saved.generation == generation, "prior generation frozen");
        }
    }

    public static void mixedDiagnosticsAndResultsSurviveUpdatesAndReset() {
        GuardianMonitorSession s = session(8);
        // At time100: LIVE, LATE, STALE, absent, LIVE respectively.
        long[] times = {100, 90, 80, -1, 99};
        Health[] health = {Health.HEALTHY, Health.DEGRADED, Health.FAILED, Health.UNKNOWN, Health.HEALTHY};
        Liveness[] live = {Liveness.LIVE, Liveness.LATE, Liveness.STALE, Liveness.UNKNOWN, Liveness.LIVE};
        for (int i = 0; i < C.length; i++) if (times[i] >= 0) check(s.heartbeat(C[i], i + 1, times[i]), "mixed heartbeat");
        GuardianMonitorStep.Result result = s.evaluate(100, 5);
        GuardianMonitorSession.Snapshot retained = s.snapshot();
        GuardianState[] frozen = states(retained.health);
        check(result.healthChanges == 4 && retained.events.size() == 4, "exact transitions");
        check(retained.health.overall == Health.FAILED && retained.generation == 0, "frozen aggregate/generation");
        int event = 0;
        for (int i = 0; i < C.length; i++) {
            check(frozen[i] == states(result.supervisor.snapshot)[i], "exact state identity");
            check(frozen[i].health == health[i] && frozen[i].revision == (i == 3 ? 0 : 1), "component health/revision");
            check(result.liveness.state(C[i]) == live[i], "component liveness");
            if (i == 3) check(retained.heartbeat(C[i]) == null, "missing component");
            else {
                GuardianHeartbeat h = retained.heartbeat(C[i]);
                check(h.component == C[i] && h.sequence == i + 1 && h.monotonicMs == times[i], "exact heartbeat");
                GuardianEvent e = retained.events.get(event++);
                check(e.component == C[i] && e.sequence == 1 && e.monotonicMs == 100 && e.health == health[i]
                        && e.type == GuardianEvent.Type.HEALTH_CHANGE, "exact ordered event");
            }
        }
        check(result.supervisor.assessment.healthyCount() == 2 && result.supervisor.assessment.degradedCount() == 1
                && result.supervisor.assessment.failedCount() == 1 && result.supervisor.assessment.unknownCount() == 1, "assessment");
        check(result.supervisor.plan.step == Step.SAFE_MODE && result.supervisor.plan.targetCount() == 1
                && result.supervisor.plan.targets(C[2]), "recommendation only");
        for (Component c : C) check(s.heartbeat(c, 10, 200), "later heartbeat");
        check(s.evaluate(200, 5).healthChanges == 3, "only changed health increments");
        GuardianMonitorSession.Snapshot updated = s.snapshot();
        check(updated.health.overall == Health.HEALTHY && updated.events.size() == 7, "later diagnostics");
        for (int i = 0; i < C.length; i++) check(states(updated.health)[i].revision == new long[] {1, 2, 2, 1, 1}[i], "exact later revision");
        for (int n = 0; n < 5; n++) check(s.evaluate(200, n).healthChanges == 0, "no-op evaluation");
        check(s.journal().size() == 7, "no-op journal");
        s.resetForColdBoot();
        cold(s.snapshot());
        check(retained.generation == 0 && retained.events.size() == 4 && retained.health.overall == Health.FAILED, "snapshot survives reset");
        check(updated.events.size() == 7 && updated.health.overall == Health.HEALTHY, "later snapshot retained");
        for (int i = 0; i < C.length; i++) {
            check(frozen[i].health == health[i] && result.liveness.state(C[i]) == live[i], "retained result per component");
            check(retained.heartbeat(C[i]) == null ? i == 3 : retained.heartbeat(C[i]).monotonicMs == times[i], "retained heartbeat value");
        }
        check(result.healthChanges == 4 && result.supervisor.assessment.failedCount() == 1
                && result.supervisor.plan.step == Step.SAFE_MODE && result.supervisor.plan.targets(C[2]), "retained result chain");
        boolean rejected = false;
        try { retained.events.set(0, null); } catch (UnsupportedOperationException expected) { rejected = true; }
        check(rejected, "events cannot be replaced");
    }

    public static void nullAndEmptySessionsRemainConservative() {
        GuardianRegistry guardian = GuardianRegistry.get();
        guardian.resetForColdBoot();
        guardian.update(C[0], Health.FAILED);
        GuardianState existing = guardian.snapshot(C[0]);
        GuardianMonitorSession s = new GuardianMonitorSession(null, null, 0);
        check(s.config().lateAfterMs == 5000 && s.config().staleAfterMs == 15000, "default config");
        check(s.journal().capacity() == 1 && s.journal().latest() == null, "minimum empty journal");
        cold(s.snapshot());
        check(s.snapshot().heartbeat(null) == null && !s.heartbeat(null, 1, 1)
                && !s.heartbeat(s.generation(), null, 1, 1), "null component");
        for (int level : new int[] {Integer.MIN_VALUE, 0, 5, Integer.MAX_VALUE}) {
            GuardianMonitorStep.Result r = s.evaluate(-1, level);
            check(r.healthChanges == 0 && r.liveness.count(Liveness.UNKNOWN) == 5
                    && r.supervisor.snapshot.overall == Health.UNKNOWN
                    && r.supervisor.plan.step == Step.DIAGNOSE && r.supervisor.plan.targetCount() == 5, "empty conservative result");
        }
        for (Component c : C) check(s.heartbeat(c, -1, -1), "clamped heartbeat");
        GuardianMonitorStep.Result r = s.evaluate(0, 5);
        check(r.liveness.count(Liveness.LIVE) == 5 && r.healthChanges == 0
                && r.supervisor.snapshot.overall == Health.UNKNOWN && s.journal().isEmpty(), "null guardian never claims healthy");
        s.resetForColdBoot(); s.resetForColdBoot();
        check(s.generation() == 2 && guardian.snapshot(C[0]) == existing, "null guardian reset isolation");
        cold(s.snapshot());
    }

    public static void healthChangesOnlyOnExplicitEvaluation() {
        GuardianMonitorSession s = session(8);
        GuardianMonitorConfig config = s.config();
        for (Component c : C) check(s.heartbeat(c, 1, 100), "input stored");
        for (int n = 0; n < 5; n++) {
            GuardianMonitorSession.Snapshot snap = s.snapshot();
            check(s.config() == config && s.generation() == 0 && snap.events.isEmpty(), "read-only access");
            for (GuardianState value : states(snap.health)) check(value.health == Health.UNKNOWN && value.revision == 0, "heartbeat/read does not evaluate");
        }
        GuardianMonitorStep.Result failed = s.evaluate(120, 5);
        check(failed.healthChanges == 5 && failed.supervisor.plan.step == Step.SAFE_MODE, "explicit stale evaluation");
        for (Component c : C) check(s.heartbeat(c, 2, 120), "fresh input stored");
        for (GuardianState value : states(s.snapshot().health)) check(value.health == Health.FAILED && value.revision == 1, "no automatic healing/recovery");
        check(s.journal().size() == 5 && s.evaluate(120, 5).healthChanges == 5, "explicit healthy evaluation");
        check(s.snapshot().health.overall == Health.HEALTHY && s.journal().size() == 8, "bounded journal in session");
    }

    public static void saturatedGenerationNeverReusesAnExpiredToken() throws Exception {
        GuardianMonitorSession s = session(8);
        // Reach the unrepresentable next-generation boundary without billions of resets.
        Field field = GuardianMonitorSession.class.getDeclaredField("generation");
        field.setAccessible(true);
        field.setLong(s, Long.MAX_VALUE - 1);
        check(s.heartbeat(Long.MAX_VALUE - 1, C[0], 1, 100), "penultimate generation");
        s.resetForColdBoot();
        check(s.generation() == Long.MAX_VALUE, "last generation available without overflow");
        for (Component c : C) {
            check(!s.heartbeat(Long.MAX_VALUE - 1, c, 2, 200), "penultimate token expired");
            check(s.heartbeat(Long.MAX_VALUE, c, 1, 100), "last current generation accepted");
        }
        s.evaluate(100, 0);
        GuardianMonitorSession.Snapshot saved = s.snapshot();
        s.resetForColdBoot();
        check(s.generation() == Long.MAX_VALUE, "generation saturates");
        cold(s.snapshot());
        for (Component c : C) {
            check(!s.heartbeat(Long.MAX_VALUE, c, 2, 200), "saturated reset must reject delayed MAX_VALUE heartbeat");
            check(!s.heartbeat(c, 3, 300), "exhausted session fails closed for convenience call");
            check(!s.heartbeat(Long.MIN_VALUE, c, 1, 1), "no wrapped generation");
        }
        s.resetForColdBoot();
        check(s.generation() == Long.MAX_VALUE && s.evaluate(300, 5).healthChanges == 0, "repeated saturation stable");
        cold(s.snapshot());
        check(saved.generation == Long.MAX_VALUE && saved.health.overall == Health.HEALTHY && saved.events.size() == 5, "saturated retained snapshot");
    }

    public static void boundedConcurrentResetAndCaptureStayCoherent() throws Exception {
        final GuardianMonitorSession s = session(8);
        final CyclicBarrier barrier = new CyclicBarrier(2);
        final AtomicReference<Throwable> failure = new AtomicReference<>();
        Thread writer = new Thread(() -> {
            try {
                for (int round = 0; round < 200; round++) {
                    barrier.await(5, TimeUnit.SECONDS);
                    long old = s.generation();
                    s.resetForColdBoot();
                    check(!s.heartbeat(old, C[0], 999, 999), "racing delayed heartbeat");
                    for (Component c : C) check(s.heartbeat(c, 1, 100), "writer input");
                    check(s.evaluate(100, 0).healthChanges == 5, "writer explicit evaluation");
                    barrier.await(5, TimeUnit.SECONDS);
                }
            } catch (Throwable t) { failure.compareAndSet(null, t); barrier.reset(); }
        }, "session-test-writer");
        writer.start();
        try {
            for (int round = 0; round < 200; round++) {
                barrier.await(5, TimeUnit.SECONDS);
                GuardianMonitorSession.Snapshot snap = s.snapshot();
                boolean healthy = snap.health.overall == Health.HEALTHY;
                check(snap.generation == round || snap.generation == round + 1, "generation in current round");
                check(snap.health.overall == Health.UNKNOWN || healthy, "only valid aggregate");
                check(snap.events.size() == (healthy ? 5 : 0), "health/journal cannot split during evaluation/reset");
                for (int i = 0; i < C.length; i++) {
                    GuardianState state = states(snap.health)[i];
                    check(state.health == (healthy ? Health.HEALTHY : Health.UNKNOWN)
                            && state.revision == (healthy ? 1 : 0), "uniform frozen health/revision");
                    if (healthy) check(snap.heartbeat(C[i]) != null, "health/reset heartbeat coherence");
                }
                barrier.await(5, TimeUnit.SECONDS);
                check(snap.events.size() == (healthy ? 5 : 0), "concurrent retained snapshot");
            }
        } catch (Throwable t) { failure.compareAndSet(null, t); barrier.reset(); }
        finally { writer.join(6000); if (writer.isAlive()) { writer.interrupt(); writer.join(1000); } }
        check(!writer.isAlive(), "test worker joined");
        if (failure.get() != null) throw new AssertionError("bounded session concurrency", failure.get());
        check(s.generation() == 200 && s.snapshot().health.overall == Health.HEALTHY, "completed rounds");
    }

    public static void main(String[] args) throws Exception {
        String[] methods = {"generationOrderingAndAllComponentIsolation", "mixedDiagnosticsAndResultsSurviveUpdatesAndReset",
                "nullAndEmptySessionsRemainConservative", "healthChangesOnlyOnExplicitEvaluation",
                "saturatedGenerationNeverReusesAnExpiredToken", "boundedConcurrentResetAndCaptureStayCoherent"};
        int failures = 0;
        for (String method : methods) {
            try { SessionFocusedChecks.class.getMethod(method).invoke(null); System.out.println("PASS " + method); }
            catch (java.lang.reflect.InvocationTargetException e) { failures++; System.out.println("FAIL " + method + ": " + e.getCause()); }
            finally { GuardianRegistry.get().resetForColdBoot(); }
        }
        System.out.println("Focused session checks: " + (methods.length - failures) + "/" + methods.length);
        if (failures != 0) throw new AssertionError("Focused session failures: " + failures);
    }
}
