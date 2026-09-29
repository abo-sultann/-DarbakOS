package com.abosultan.darbakos.core;

/**
 * Immutable heartbeat sample supplied by a component.
 * Time is caller-provided monotonic milliseconds so this model has no Android clock dependency.
 */
public final class GuardianHeartbeat {
    public final GuardianRegistry.Component component;
    public final long sequence;
    public final long monotonicMs;

    public GuardianHeartbeat(GuardianRegistry.Component component, long sequence, long monotonicMs) {
        this.component = component;
        this.sequence = sequence < 0L ? 0L : sequence;
        this.monotonicMs = monotonicMs < 0L ? 0L : monotonicMs;
    }
}
