package com.abosultan.darbakos.core;

/**
 * Pure decision model for Guardian health aggregation.
 * It consumes explicit component health only; it performs no monitoring or recovery action.
 */
public final class GuardianPolicy {
    private GuardianPolicy() {}

    public static GuardianState.Health aggregate(GuardianRegistry registry) {
        if (registry == null) return GuardianState.Health.UNKNOWN;

        boolean unknown = false;
        boolean degraded = false;
        for (GuardianRegistry.Component component : GuardianRegistry.Component.values()) {
            GuardianState state = registry.snapshot(component);
            if (state == null || state.health == GuardianState.Health.UNKNOWN) {
                unknown = true;
            } else if (state.health == GuardianState.Health.FAILED) {
                return GuardianState.Health.FAILED;
            } else if (state.health == GuardianState.Health.DEGRADED) {
                degraded = true;
            }
        }
        if (degraded) return GuardianState.Health.DEGRADED;
        if (unknown) return GuardianState.Health.UNKNOWN;
        return GuardianState.Health.HEALTHY;
    }
}
