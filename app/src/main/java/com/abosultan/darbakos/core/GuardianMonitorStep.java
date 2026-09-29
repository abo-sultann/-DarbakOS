package com.abosultan.darbakos.core;

/**
 * One caller-driven Guardian monitoring cycle.
 *
 * Converts stored heartbeats into liveness, applies the resulting health, evaluates the
 * Supervisor, and records only meaningful health transitions. It owns no clock, thread,
 * timer, service, listener, persistence or recovery executor.
 */
public final class GuardianMonitorStep {
    private GuardianMonitorStep() {}

    public static Result run(GuardianHeartbeatRegistry heartbeats,
                             GuardianRegistry guardian,
                             GuardianEventJournal journal,
                             long nowMonotonicMs,
                             GuardianMonitorConfig config,
                             int escalationLevel) {
        GuardianMonitorConfig effective = config == null
                ? GuardianMonitorConfig.conservativeDefault() : config;
        return run(heartbeats, guardian, journal, nowMonotonicMs,
                effective.lateAfterMs, effective.staleAfterMs, escalationLevel);
    }

    public static Result run(GuardianHeartbeatRegistry heartbeats,
                             GuardianRegistry guardian,
                             GuardianEventJournal journal,
                             long nowMonotonicMs,
                             long lateAfterMs,
                             long staleAfterMs,
                             int escalationLevel) {
        GuardianSnapshot before = GuardianSnapshot.capture(guardian);
        GuardianLivenessSnapshot liveness = GuardianLivenessSnapshot.capture(
                heartbeats, nowMonotonicMs, lateAfterMs, staleAfterMs);
        liveness.applyTo(guardian);
        GuardianSupervisor.Result supervisor =
                GuardianSupervisor.evaluate(guardian, escalationLevel);

        int changes = 0;
        if (journal != null) {
            for (GuardianRegistry.Component component : GuardianRegistry.Component.values()) {
                GuardianState previous = before.state(component);
                GuardianState current = supervisor.snapshot.state(component);
                if (previous.health != current.health) {
                    changes++;
                    journal.append(new GuardianEvent(
                            current.revision, nowMonotonicMs, component,
                            GuardianEvent.Type.HEALTH_CHANGE, current.health));
                }
            }
        } else {
            for (GuardianRegistry.Component component : GuardianRegistry.Component.values()) {
                if (before.state(component).health !=
                        supervisor.snapshot.state(component).health) changes++;
            }
        }
        return new Result(liveness, supervisor, changes);
    }

    public static final class Result {
        public final GuardianLivenessSnapshot liveness;
        public final GuardianSupervisor.Result supervisor;
        public final int healthChanges;

        private Result(GuardianLivenessSnapshot liveness,
                       GuardianSupervisor.Result supervisor,
                       int healthChanges) {
            this.liveness = liveness;
            this.supervisor = supervisor;
            this.healthChanges = healthChanges;
        }
    }
}
