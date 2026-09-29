package com.abosultan.darbakos.core;

/** Immutable in-memory diagnostic event. */
public final class GuardianEvent {
    public enum Type { HEARTBEAT, HEALTH_CHANGE, ASSESSMENT, RECOVERY_RECOMMENDED }

    public final long sequence;
    public final long monotonicMs;
    public final GuardianRegistry.Component component;
    public final Type type;
    public final GuardianState.Health health;

    public GuardianEvent(long sequence, long monotonicMs,
                         GuardianRegistry.Component component, Type type,
                         GuardianState.Health health) {
        this.sequence = Math.max(0L, sequence);
        this.monotonicMs = Math.max(0L, monotonicMs);
        this.component = component;
        this.type = type;
        this.health = health == null ? GuardianState.Health.UNKNOWN : health;
    }
}
