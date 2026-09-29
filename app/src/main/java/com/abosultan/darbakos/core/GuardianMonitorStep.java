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
                GuardianState previous = state(before, component);
                GuardianState current = state(supervisor.snapshot, component);
                if (previous.health != current.health) {
                    changes++;
                    journal.append(new GuardianEvent(
                            current.revision, nowMonotonicMs, component,
                            GuardianEvent.Type.HEALTH_CHANGE, current.health));
                }
            }
        } else {
            for (GuardianRegistry.Component component : GuardianRegistry.Component.values()) {
                if (state(before, component).health !=
                        state(supervisor.snapshot, component).health) changes++;
            }
        }
        return new Result(liveness, supervisor, changes);
    }

    private static GuardianState state(GuardianSnapshot snapshot,
                                       GuardianRegistry.Component component) {
        switch (component) {
            case CORE: return snapshot.core;
            case HOME: return snapshot.home;
            case NAVIGATION: return snapshot.navigation;
            case MEDIA: return snapshot.media;
            case VEHICLE: return snapshot.vehicle;
            default: throw new AssertionError(component);
        }
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
