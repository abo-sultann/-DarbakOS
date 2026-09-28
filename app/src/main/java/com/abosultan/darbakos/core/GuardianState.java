package com.abosultan.darbakos.core;

/**
 * Lightweight in-process health model for Darbak core components.
 * Pure state only: no Service, timer, thread, disk, network or restart action.
 */
public final class GuardianState {
    public enum Health { HEALTHY, DEGRADED, FAILED, UNKNOWN }

    public final Health health;
    public final long revision;

    private GuardianState(Health health, long revision) {
        this.health = health;
        this.revision = revision;
    }

    public static GuardianState coldBoot() {
        return new GuardianState(Health.UNKNOWN, 0L);
    }

    public GuardianState withHealth(Health next) {
        if (next == null || next == health) return this;
        return new GuardianState(next, revision + 1L);
    }
}
