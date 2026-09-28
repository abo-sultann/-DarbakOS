package com.abosultan.darbakos.core;

import java.util.EnumSet;

/**
 * Immutable diagnostic classification derived from one GuardianSnapshot.
 * It summarizes which components are healthy/degraded/failed/unknown without taking any action.
 */
public final class GuardianAssessment {
    private final EnumSet<GuardianRegistry.Component> healthy;
    private final EnumSet<GuardianRegistry.Component> degraded;
    private final EnumSet<GuardianRegistry.Component> failed;
    private final EnumSet<GuardianRegistry.Component> unknown;
    public final GuardianState.Health overall;

    private GuardianAssessment(EnumSet<GuardianRegistry.Component> healthy,
                               EnumSet<GuardianRegistry.Component> degraded,
                               EnumSet<GuardianRegistry.Component> failed,
                               EnumSet<GuardianRegistry.Component> unknown,
                               GuardianState.Health overall) {
        this.healthy = healthy.clone();
        this.degraded = degraded.clone();
        this.failed = failed.clone();
        this.unknown = unknown.clone();
        this.overall = overall;
    }

    public static GuardianAssessment from(GuardianSnapshot snapshot) {
        EnumSet<GuardianRegistry.Component> healthy = EnumSet.noneOf(GuardianRegistry.Component.class);
        EnumSet<GuardianRegistry.Component> degraded = EnumSet.noneOf(GuardianRegistry.Component.class);
        EnumSet<GuardianRegistry.Component> failed = EnumSet.noneOf(GuardianRegistry.Component.class);
        EnumSet<GuardianRegistry.Component> unknown = EnumSet.noneOf(GuardianRegistry.Component.class);

        if (snapshot == null) {
            for (GuardianRegistry.Component component : GuardianRegistry.Component.values()) unknown.add(component);
            return new GuardianAssessment(healthy, degraded, failed, unknown, GuardianState.Health.UNKNOWN);
        }

        classify(GuardianRegistry.Component.CORE, snapshot.core, healthy, degraded, failed, unknown);
        classify(GuardianRegistry.Component.HOME, snapshot.home, healthy, degraded, failed, unknown);
        classify(GuardianRegistry.Component.NAVIGATION, snapshot.navigation, healthy, degraded, failed, unknown);
        classify(GuardianRegistry.Component.MEDIA, snapshot.media, healthy, degraded, failed, unknown);
        classify(GuardianRegistry.Component.VEHICLE, snapshot.vehicle, healthy, degraded, failed, unknown);

        GuardianState.Health overall = snapshot.overall == null
                ? deriveOverall(failed, degraded, unknown)
                : snapshot.overall;
        return new GuardianAssessment(healthy, degraded, failed, unknown, overall);
    }

    private static void classify(GuardianRegistry.Component component, GuardianState state,
                                 EnumSet<GuardianRegistry.Component> healthy,
                                 EnumSet<GuardianRegistry.Component> degraded,
                                 EnumSet<GuardianRegistry.Component> failed,
                                 EnumSet<GuardianRegistry.Component> unknown) {
        GuardianState.Health health = state == null || state.health == null
                ? GuardianState.Health.UNKNOWN : state.health;
        switch (health) {
            case HEALTHY: healthy.add(component); break;
            case DEGRADED: degraded.add(component); break;
            case FAILED: failed.add(component); break;
            default: unknown.add(component); break;
        }
    }

    private static GuardianState.Health deriveOverall(EnumSet<GuardianRegistry.Component> failed,
                                                       EnumSet<GuardianRegistry.Component> degraded,
                                                       EnumSet<GuardianRegistry.Component> unknown) {
        if (!failed.isEmpty()) return GuardianState.Health.FAILED;
        if (!degraded.isEmpty()) return GuardianState.Health.DEGRADED;
        if (!unknown.isEmpty()) return GuardianState.Health.UNKNOWN;
        return GuardianState.Health.HEALTHY;
    }

    public int healthyCount() { return healthy.size(); }
    public int degradedCount() { return degraded.size(); }
    public int failedCount() { return failed.size(); }
    public int unknownCount() { return unknown.size(); }

    public boolean isHealthy(GuardianRegistry.Component component) { return component != null && healthy.contains(component); }
    public boolean isDegraded(GuardianRegistry.Component component) { return component != null && degraded.contains(component); }
    public boolean isFailed(GuardianRegistry.Component component) { return component != null && failed.contains(component); }
    public boolean isUnknown(GuardianRegistry.Component component) { return component != null && unknown.contains(component); }
}
