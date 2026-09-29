package com.abosultan.darbakos.core;

/**
 * One caller-driven Guardian monitoring cycle.
 *
 * Converts stored heartbeats into liveness, applies the resulting health, evaluates the
 * Supervisor, and optionally records a compact diagnostic event. It owns no clock, thread,
 * timer, service, listener, persistence or recovery executor.
 */
public final class GuardianMonitorStep {
    private GuardianMonitorStep() {}

    public static Result run(GuardianHeartbeatRegistry heartbeats,
                             GuardianRegistry guardian,
                             GuardianEventJournal journal,
                             long nowMonotonicMs,
                             long lateAfterMs,
                             long staleAfterMs,
                             int escalationLevel) {
        GuardianLivenessSnapshot liveness = GuardianLivenessSnapshot.capture(
                heartbeats, nowMonotonicMs, lateAfterMs, staleAfterMs);
        liveness.applyTo(guardian);
        GuardianSupervisor.Result supervisor =
                GuardianSupervisor.evaluate(guardian, escalationLevel);

        if (journal != null) {
            long sequence = Math.max(0L, nowMonotonicMs);
            GuardianState.Health aggregate = supervisor.snapshot.aggregateHealth;
            journal.append(new GuardianEvent(
                    sequence, nowMonotonicMs, null,
                    GuardianEvent.Type.ASSESSMENT, aggregate));
        }
        return new Result(liveness, supervisor);
    }

    public static final class Result {
        public final GuardianLivenessSnapshot liveness;
        public final GuardianSupervisor.Result supervisor;

        private Result(GuardianLivenessSnapshot liveness,
                       GuardianSupervisor.Result supervisor) {
            this.liveness = liveness;
            this.supervisor = supervisor;
        }
    }
}
