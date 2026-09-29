package com.abosultan.darbakos.core;

/**
 * Immutable, compact diagnostic value derived from a Guardian session snapshot.
 * It contains no persistence, I/O or background behavior.
 */
public final class GuardianDiagnosticRecord {
    public final long generation;
    public final GuardianState.Health overall;
    public final int healthy;
    public final int degraded;
    public final int failed;
    public final int unknown;
    public final int eventCount;
    public final GuardianRegistry.Component lastChangedComponent;
    public final GuardianState.Health lastChangedHealth;
    public final long lastChangedMonotonicMs;

    private GuardianDiagnosticRecord(long generation,
                                     GuardianState.Health overall,
                                     int healthy,
                                     int degraded,
                                     int failed,
                                     int unknown,
                                     int eventCount,
                                     GuardianRegistry.Component lastChangedComponent,
                                     GuardianState.Health lastChangedHealth,
                                     long lastChangedMonotonicMs) {
        this.generation = generation;
        this.overall = overall == null ? GuardianState.Health.UNKNOWN : overall;
        this.healthy = healthy;
        this.degraded = degraded;
        this.failed = failed;
        this.unknown = unknown;
        this.eventCount = eventCount;
        this.lastChangedComponent = lastChangedComponent;
        this.lastChangedHealth = lastChangedHealth == null ? GuardianState.Health.UNKNOWN : lastChangedHealth;
        this.lastChangedMonotonicMs = Math.max(0L, lastChangedMonotonicMs);
    }

    public static GuardianDiagnosticRecord from(GuardianMonitorSession.Snapshot snapshot) {
        if (snapshot == null || snapshot.health == null) {
            return new GuardianDiagnosticRecord(0, GuardianState.Health.UNKNOWN,
                    0, 0, 0, GuardianRegistry.Component.values().length, 0,
                    null, GuardianState.Health.UNKNOWN, 0L);
        }
        GuardianAssessment assessment = GuardianAssessment.from(snapshot.health);
        GuardianEvent latest = snapshot.events == null || snapshot.events.isEmpty()
                ? null : snapshot.events.get(snapshot.events.size() - 1);
        return new GuardianDiagnosticRecord(
                snapshot.generation,
                snapshot.health.overall,
                assessment.healthyCount(),
                assessment.degradedCount(),
                assessment.failedCount(),
                assessment.unknownCount(),
                snapshot.events == null ? 0 : snapshot.events.size(),
                latest == null ? null : latest.component,
                latest == null ? GuardianState.Health.UNKNOWN : latest.health,
                latest == null ? 0L : latest.monotonicMs);
    }
}
