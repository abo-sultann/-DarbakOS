package com.abosultan.darbakos.core;

/**
 * Pure heartbeat freshness policy. It classifies caller-supplied samples only; it never polls,
 * schedules work or reads a clock itself.
 */
public final class GuardianLivenessPolicy {
    public enum Liveness { LIVE, LATE, STALE, UNKNOWN }

    private GuardianLivenessPolicy() {}

    public static Liveness classify(GuardianHeartbeat heartbeat,
                                    long nowMonotonicMs,
                                    long lateAfterMs,
                                    long staleAfterMs) {
        if (heartbeat == null || heartbeat.component == null) return Liveness.UNKNOWN;

        long now = Math.max(0L, nowMonotonicMs);
        long late = Math.max(0L, lateAfterMs);
        long stale = Math.max(late, staleAfterMs);
        long age = now <= heartbeat.monotonicMs ? 0L : now - heartbeat.monotonicMs;

        if (age >= stale) return Liveness.STALE;
        if (age >= late) return Liveness.LATE;
        return Liveness.LIVE;
    }

    public static GuardianState.Health toHealth(Liveness liveness) {
        if (liveness == null) return GuardianState.Health.UNKNOWN;
        switch (liveness) {
            case LIVE: return GuardianState.Health.HEALTHY;
            case LATE: return GuardianState.Health.DEGRADED;
            case STALE: return GuardianState.Health.FAILED;
            default: return GuardianState.Health.UNKNOWN;
        }
    }
}
