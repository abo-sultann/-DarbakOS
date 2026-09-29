package com.abosultan.darbakos;

import com.abosultan.darbakos.core.*;
import com.abosultan.darbakos.core.GuardianRegistry.Component;
import com.abosultan.darbakos.core.GuardianState.Health;
import com.abosultan.darbakos.core.GuardianLivenessPolicy.Liveness;
import com.abosultan.darbakos.core.GuardianRecoveryPolicy.Step;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/** Same pure-Java checks run locally before the single Android regression, then on API25. */
public final class MonitoringFocusedChecks {
    private static final Component[] C = Component.values();
    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
    private static GuardianState[] states(GuardianSnapshot s) {
        return new GuardianState[] { s.core, s.home, s.navigation, s.media, s.vehicle };
    }
    public static void heartbeatOrderingAndIsolation() {
        GuardianHeartbeatRegistry r = new GuardianHeartbeatRegistry();
        check(!r.update(null) && !r.update(new GuardianHeartbeat(null, 1, 1)), "invalid heartbeat");
        check(r.snapshot(null) == null, "null lookup");
        for (Component c : C) {
            check(r.snapshot(c) == null, "initially missing");
            GuardianHeartbeat first = new GuardianHeartbeat(c, -1, -1);
            check(first.sequence == 0 && first.monotonicMs == 0 && r.update(first), "clamp initial");
            check(!r.update(new GuardianHeartbeat(c, 0, 0)), "duplicate rejected");
            check(r.update(new GuardianHeartbeat(c, 0, 10)), "same sequence newer time");
            check(r.update(new GuardianHeartbeat(c, 1, 10)), "higher sequence equal time");
            GuardianHeartbeat saved = r.snapshot(c);
            check(!r.update(new GuardianHeartbeat(c, 0, 20)), "sequence regression");
            check(!r.update(new GuardianHeartbeat(c, 2, 9)), "time regression");
            check(r.snapshot(c) == saved, "rejection must retain identity");
            check(r.update(new GuardianHeartbeat(c, Long.MAX_VALUE, Long.MAX_VALUE)), "upper bounds");
            check(saved.sequence == 1 && saved.monotonicMs == 10, "retained sample");
        }
        GuardianHeartbeat[] retained = new GuardianHeartbeat[5];
        for (int i = 0; i < 5; i++) retained[i] = r.snapshot(C[i]);
        r.update(new GuardianHeartbeat(C[0], Long.MAX_VALUE, Long.MAX_VALUE));
        for (int i = 0; i < 5; i++) check(r.snapshot(C[i]) == retained[i], "component isolation");
        r.reset(); r.reset();
        for (int i = 0; i < 5; i++) check(r.snapshot(C[i]) == null && retained[i].sequence == Long.MAX_VALUE, "reset/retention");
    }
    public static void exactLivenessAndUnknownMapping() {
        GuardianHeartbeat h = new GuardianHeartbeat(C[0], 1, 100);
        long[] now = { -1, 0, 99, 100, 109, 110, 119, 120, Long.MAX_VALUE };
        Liveness[] expected = { Liveness.LIVE, Liveness.LIVE, Liveness.LIVE, Liveness.LIVE,
                Liveness.LIVE, Liveness.LATE, Liveness.LATE, Liveness.STALE, Liveness.STALE };
        for (int i = 0; i < now.length; i++) check(GuardianLivenessPolicy.classify(h, now[i], 10, 20) == expected[i], "boundary " + now[i]);
        check(GuardianLivenessPolicy.classify(null, 100, 10, 20) == Liveness.UNKNOWN, "missing");
        check(GuardianLivenessPolicy.classify(new GuardianHeartbeat(null, 0, 0), 100, 10, 20) == Liveness.UNKNOWN, "invalid component");
        check(GuardianLivenessPolicy.classify(h, 100, -1, -2) == Liveness.STALE, "raw zero thresholds");
        check(GuardianLivenessPolicy.classify(h, 110, 10, 5) == Liveness.STALE, "raw stale clamped to late");
        check(GuardianLivenessPolicy.toHealth(null) == Health.UNKNOWN, "null liveness");
        Liveness[] ls = { Liveness.LIVE, Liveness.LATE, Liveness.STALE, Liveness.UNKNOWN };
        Health[] hs = { Health.HEALTHY, Health.DEGRADED, Health.FAILED, Health.UNKNOWN };
        for (int i = 0; i < 4; i++) check(GuardianLivenessPolicy.toHealth(ls[i]) == hs[i], "mapping");
    }
    public static void configExtremeSanitization() {
        long[][] cases = { {0,-1,1,2}, {Long.MIN_VALUE,Long.MIN_VALUE,1,2}, {10,5,10,11},
                {10,10,10,11}, {10,20,10,20}, {Long.MAX_VALUE,0,Long.MAX_VALUE-1,Long.MAX_VALUE},
                {Long.MAX_VALUE,Long.MAX_VALUE,Long.MAX_VALUE-1,Long.MAX_VALUE} };
        for (long[] v : cases) {
            GuardianMonitorConfig c = new GuardianMonitorConfig(v[0], v[1]);
            check(c.lateAfterMs == v[2] && c.staleAfterMs == v[3], "config overflow/order: " + v[0] + "," + v[1] + " -> " + c.lateAfterMs + "," + c.staleAfterMs);
        }
        GuardianMonitorConfig d = GuardianMonitorConfig.conservativeDefault();
        check(d.lateAfterMs == 5000 && d.staleAfterMs == 15000, "defaults");
    }
    public static void journalCapacityOrderClearAndImmutableSnapshots() {
        for (int capacity : new int[] { -3, 0, 1, 3, 64 }) {
            GuardianEventJournal j = new GuardianEventJournal(capacity);
            int cap = Math.max(1, capacity);
            check(j.capacity() == cap && j.isEmpty() && j.latest() == null, "initial journal");
            j.append(null); check(j.size() == 0, "null ignored");
            for (int n = 0; n < cap * 3; n++) j.append(new GuardianEvent(n, n, C[n % 5], GuardianEvent.Type.HEALTH_CHANGE, Health.HEALTHY));
            List<GuardianEvent> saved = j.snapshot();
            check(saved.size() == cap && j.size() == cap && !j.isEmpty(), "bounded size");
            for (int i = 0; i < cap; i++) check(saved.get(i).sequence == cap * 2 + i, "oldest first");
            check(j.latest() == saved.get(cap - 1), "latest identity");
            boolean rejected = false;
            try { saved.clear(); } catch (UnsupportedOperationException expected) { rejected = true; }
            check(rejected, "snapshot read only");
            j.clear(); j.clear(); check(j.isEmpty() && j.latest() == null, "clear");
            j.append(new GuardianEvent(99, 99, null, GuardianEvent.Type.ASSESSMENT, null));
            check(j.size() == 1 && j.latest().health == Health.UNKNOWN, "reuse after clear");
            check(saved.size() == cap && saved.get(0).sequence == cap * 2, "retained snapshot after clear/append");
        }
        check(new GuardianEventJournal().capacity() == 64, "default capacity");
    }
    public static void eventValueSanitization() {
        GuardianEvent e = new GuardianEvent(-1, Long.MIN_VALUE, null, null, null);
        check(e.sequence == 0 && e.monotonicMs == 0 && e.health == Health.UNKNOWN && e.component == null && e.type == null, "nullable immutable event");
        for (GuardianEvent.Type type : GuardianEvent.Type.values()) {
            GuardianEvent v = new GuardianEvent(Long.MAX_VALUE, Long.MAX_VALUE, C[4], type, Health.FAILED);
            check(v.sequence == Long.MAX_VALUE && v.monotonicMs == Long.MAX_VALUE && v.type == type && v.component == C[4] && v.health == Health.FAILED, "event values");
        }
    }
    private static GuardianHeartbeatRegistry mixed() {
        GuardianHeartbeatRegistry h = new GuardianHeartbeatRegistry();
        h.update(new GuardianHeartbeat(C[0], 1, 2000));
        h.update(new GuardianHeartbeat(C[1], 1, 1000));
        h.update(new GuardianHeartbeat(C[2], 1, 0));
        return h;
    }
    public static void livenessCaptureRetentionCountsAndApply() {
        GuardianHeartbeatRegistry h = mixed();
        GuardianLivenessSnapshot s = GuardianLivenessSnapshot.capture(h, 2000, 500, 1500);
        Liveness[] expected = { Liveness.LIVE, Liveness.LATE, Liveness.STALE, Liveness.UNKNOWN, Liveness.UNKNOWN };
        for (int i = 0; i < 5; i++) check(s.state(C[i]) == expected[i], "mixed capture");
        check(s.count(Liveness.LIVE) == 1 && s.count(Liveness.LATE) == 1 && s.count(Liveness.STALE) == 1 && s.count(Liveness.UNKNOWN) == 2, "counts");
        check(s.state(null) == Liveness.UNKNOWN && s.count(null) == 0 && s.monotonicMs == 2000, "null/time");
        h.reset();
        GuardianRegistry g = GuardianRegistry.get(); g.resetForColdBoot();
        s.applyTo(null); s.applyTo(g);
        GuardianState[] saved = states(GuardianSnapshot.capture(g));
        s.applyTo(g);
        for (int i = 0; i < 5; i++) {
            check(s.state(C[i]) == expected[i] && g.snapshot(C[i]).health == GuardianLivenessPolicy.toHealth(expected[i]), "retention/apply");
            check(g.snapshot(C[i]) == saved[i], "idempotent apply");
        }
        GuardianLivenessSnapshot empty = GuardianLivenessSnapshot.capture(null, -1, 500, 1500);
        check(empty.monotonicMs == 0 && empty.count(Liveness.UNKNOWN) == 5, "null capture");
        empty.applyTo(g);
        for (int i = 0; i < 5; i++) check(g.snapshot(C[i]).health == Health.UNKNOWN && s.state(C[i]) == expected[i], "missing replaces previous health");
    }
    public static void mixedCompositionAndNoOpJournaling() {
        GuardianRegistry g = GuardianRegistry.get(); g.resetForColdBoot();
        GuardianHeartbeatRegistry h = mixed(); GuardianEventJournal j = new GuardianEventJournal();
        GuardianMonitorStep.Result first = GuardianMonitorStep.run(h, g, j, 2000, 500, 1500, 2);
        check(first.healthChanges == 3 && j.size() == 3, "only three initial transitions");
        check(first.supervisor.snapshot.overall == Health.FAILED && first.supervisor.assessment.failedCount() == 1, "aggregate/assessment");
        check(first.supervisor.plan.step == Step.RESTART_COMPONENT && first.supervisor.plan.targetCount() == 1 && first.supervisor.plan.targets(C[2]), "recommendation only");
        List<GuardianEvent> events = j.snapshot(); GuardianState[] saved = states(first.supervisor.snapshot);
        for (int i = 0; i < 3; i++) {
            GuardianEvent e = events.get(i);
            check(e.component == C[i] && e.type == GuardianEvent.Type.HEALTH_CHANGE && e.sequence == saved[i].revision && e.monotonicMs == 2000 && e.health == saved[i].health, "exact event");
        }
        for (int repeat = 0; repeat < 30; repeat++) {
            GuardianMonitorStep.Result r = GuardianMonitorStep.run(h, g, j, 2000, 500, 1500, repeat % 6);
            check(r.healthChanges == 0 && j.size() == 3, "no-op cycles must not journal plans");
            for (int i = 0; i < 5; i++) check(g.snapshot(C[i]) == saved[i], "no-op revisions");
        }
        for (Component c : C) h.update(new GuardianHeartbeat(c, 2, 2000));
        GuardianMonitorStep.Result live = GuardianMonitorStep.run(h, g, j, 2000, 500, 1500, 5);
        check(live.healthChanges == 4 && j.size() == 7 && live.supervisor.plan.step == Step.NONE, "recovery to healthy values only");
        check(first.liveness.count(Liveness.STALE) == 1 && first.supervisor.snapshot.overall == Health.FAILED && first.supervisor.plan.targets(C[2]), "old result retained");
        h.reset(); GuardianMonitorStep.Result unknown = GuardianMonitorStep.run(h, g, j, 2000, 500, 1500, 5);
        check(unknown.healthChanges == 5 && unknown.supervisor.assessment.unknownCount() == 5 && unknown.supervisor.plan.step == Step.DIAGNOSE && j.size() == 12, "reset to unknown");
    }
    public static void monitorDefaultsNullsAndThresholdTransitions() {
        GuardianRegistry g = GuardianRegistry.get(); g.resetForColdBoot();
        GuardianHeartbeatRegistry h = new GuardianHeartbeatRegistry();
        for (Component c : C) h.update(new GuardianHeartbeat(c, 1, 1000));
        long[] times = {1000, 5999, 6000, 15999, 16000};
        Health[] health = {Health.HEALTHY, Health.HEALTHY, Health.DEGRADED, Health.DEGRADED, Health.FAILED};
        int[] changes = {5,0,5,0,5}; long[] revisions = {1,1,2,2,3};
        Step[] plans = {Step.NONE,Step.NONE,Step.LIGHT_REPAIR,Step.LIGHT_REPAIR,Step.SAFE_MODE};
        for (int n = 0; n < times.length; n++) {
            GuardianMonitorStep.Result r = GuardianMonitorStep.run(h,g,null,times[n],null,5);
            check(r.healthChanges == changes[n] && r.supervisor.snapshot.overall == health[n] && r.supervisor.plan.step == plans[n], "default boundary cycle");
            for (GuardianState s : states(r.supervisor.snapshot)) check(s.revision == revisions[n], "transition revision");
        }
        GuardianMonitorStep.Result absent = GuardianMonitorStep.run(null,g,null,16000,null,-1);
        check(absent.healthChanges == 5 && absent.supervisor.plan.step == Step.DIAGNOSE, "null heartbeat registry");
        GuardianEventJournal journal = new GuardianEventJournal();
        GuardianMonitorStep.Result noTarget = GuardianMonitorStep.run(h,null,journal,1000,null,0);
        check(noTarget.liveness.count(Liveness.LIVE) == 5 && noTarget.supervisor.snapshot.overall == Health.UNKNOWN && noTarget.healthChanges == 0 && journal.isEmpty(), "null health registry conservative/no writes");
    }
    private static void concurrent(Runnable a, Runnable b) throws Exception {
        AtomicReference<Throwable> error = new AtomicReference<>();
        Thread[] threads = { new Thread(() -> { try { a.run(); } catch (Throwable t) { error.compareAndSet(null,t); } }),
                new Thread(() -> { try { b.run(); } catch (Throwable t) { error.compareAndSet(null,t); } }) };
        for (Thread t : threads) t.start();
        try { for (Thread t : threads) t.join(15000); }
        finally { for (Thread t : threads) if (t.isAlive()) t.interrupt(); for (Thread t : threads) t.join(5000); }
        for (Thread t : threads) check(!t.isAlive(), "bounded test worker joined");
        if (error.get() != null) throw new AssertionError("bounded concurrency", error.get());
    }
    private static void rendezvous(CyclicBarrier barrier) {
        try { barrier.await(5, TimeUnit.SECONDS); } catch (Exception e) { throw new AssertionError(e); }
    }
    public static void boundedHeartbeatCaptureConcurrency() throws Exception {
        GuardianHeartbeatRegistry h = new GuardianHeartbeatRegistry(); CyclicBarrier barrier = new CyclicBarrier(2);
        concurrent(() -> {
            for (int n = 0; n < 200; n++) {
                rendezvous(barrier);
                synchronized (h) {
                    h.reset();
                    if (n % 3 != 0) for (Component c : C) h.update(new GuardianHeartbeat(c,n,n % 3 == 1 ? 1000 : 0));
                }
                rendezvous(barrier);
            }
        }, () -> {
            for (int n = 0; n < 200; n++) {
                rendezvous(barrier);
                GuardianLivenessSnapshot s = GuardianLivenessSnapshot.capture(h,1000,100,500);
                Liveness first = s.state(C[0]); check(s.count(first) == 5, "capture cannot split atomic writer state");
                for (Component c : C) check(s.state(c) == first, "uniform retained capture");
                rendezvous(barrier);
                check(s.count(first) == 5, "retained concurrent snapshot");
            }
        });
    }
    public static void boundedJournalConcurrency() throws Exception {
        GuardianEventJournal j = new GuardianEventJournal(7); CyclicBarrier barrier = new CyclicBarrier(2);
        concurrent(() -> {
            for (int n = 0; n < 200; n++) {
                rendezvous(barrier);
                if (n % 11 == 0) j.clear();
                j.append(new GuardianEvent(n,n,C[n%5],GuardianEvent.Type.HEALTH_CHANGE,Health.HEALTHY));
                rendezvous(barrier);
            }
        }, () -> {
            for (int n = 0; n < 200; n++) {
                rendezvous(barrier); List<GuardianEvent> saved;
                synchronized (j) {
                    saved = j.snapshot(); check(saved.size() <= 7 && saved.size() == j.size(), "atomic bounded journal");
                    check(j.latest() == (saved.isEmpty() ? null : saved.get(saved.size()-1)), "concurrent latest/order");
                }
                long[] sequences = new long[saved.size()];
                for (int i = 0; i < saved.size(); i++) { sequences[i] = saved.get(i).sequence; if (i > 0) check(sequences[i] == sequences[i-1]+1, "no torn order"); }
                rendezvous(barrier);
                for (int i = 0; i < saved.size(); i++) check(saved.get(i).sequence == sequences[i], "retained journal");
            }
        });
        check(j.latest().sequence == 199, "writer completed");
    }
    public static void main(String[] args) throws Exception {
        String[] names = args.length > 0 ? args : new String[] { "heartbeatOrderingAndIsolation", "exactLivenessAndUnknownMapping",
            "configExtremeSanitization", "journalCapacityOrderClearAndImmutableSnapshots", "eventValueSanitization",
            "livenessCaptureRetentionCountsAndApply", "mixedCompositionAndNoOpJournaling", "monitorDefaultsNullsAndThresholdTransitions",
            "boundedHeartbeatCaptureConcurrency", "boundedJournalConcurrency" };
        for (String name : names) {
            GuardianRegistry.get().resetForColdBoot();
            try { MonitoringFocusedChecks.class.getMethod(name).invoke(null); System.out.println("PASS " + name); }
            catch (java.lang.reflect.InvocationTargetException e) { throw new AssertionError("FAIL " + name,e.getCause()); }
            finally { GuardianRegistry.get().resetForColdBoot(); }
        }
        System.out.println("PASS focused checks: " + names.length);
    }
}
