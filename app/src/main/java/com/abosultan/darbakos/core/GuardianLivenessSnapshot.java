package com.abosultan.darbakos.core;

import java.util.EnumMap;

/** Immutable liveness classification for all Guardian components at one caller-supplied time. */
public final class GuardianLivenessSnapshot {
    private final EnumMap<GuardianRegistry.Component, GuardianLivenessPolicy.Liveness> states;
    public final long monotonicMs;

    private GuardianLivenessSnapshot(
            EnumMap<GuardianRegistry.Component, GuardianLivenessPolicy.Liveness> states,
            long monotonicMs) {
        this.states = new EnumMap<>(states);
        this.monotonicMs = Math.max(0L, monotonicMs);
    }

    public static GuardianLivenessSnapshot capture(GuardianHeartbeatRegistry registry,
                                                   long nowMonotonicMs,
                                                   long lateAfterMs,
                                                   long staleAfterMs) {
        EnumMap<GuardianRegistry.Component, GuardianLivenessPolicy.Liveness> states =
                new EnumMap<>(GuardianRegistry.Component.class);
        synchronized (registry == null ? GuardianLivenessSnapshot.class : registry) {
            for (GuardianRegistry.Component component : GuardianRegistry.Component.values()) {
                GuardianHeartbeat heartbeat = registry == null ? null : registry.snapshot(component);
                states.put(component, GuardianLivenessPolicy.classify(
                        heartbeat, nowMonotonicMs, lateAfterMs, staleAfterMs));
            }
        }
        return new GuardianLivenessSnapshot(states, nowMonotonicMs);
    }

    public GuardianLivenessPolicy.Liveness state(GuardianRegistry.Component component) {
        if (component == null) return GuardianLivenessPolicy.Liveness.UNKNOWN;
        GuardianLivenessPolicy.Liveness value = states.get(component);
        return value == null ? GuardianLivenessPolicy.Liveness.UNKNOWN : value;
    }

    public int count(GuardianLivenessPolicy.Liveness liveness) {
        if (liveness == null) return 0;
        int count = 0;
        for (GuardianLivenessPolicy.Liveness value : states.values()) {
            if (value == liveness) count++;
        }
        return count;
    }

    /** Caller-driven bridge into Guardian health. No scheduling or automatic mutation. */
    public void applyTo(GuardianRegistry guardianRegistry) {
        if (guardianRegistry == null) return;
        for (GuardianRegistry.Component component : GuardianRegistry.Component.values()) {
            guardianRegistry.update(component, GuardianLivenessPolicy.toHealth(state(component)));
        }
    }
}
