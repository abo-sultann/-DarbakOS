package com.abosultan.darbakos.core;

import java.util.EnumMap;

/**
 * Stores the latest caller-supplied heartbeat for each Guardian component.
 * In-process only; it owns no clock, scheduler, listener or background worker.
 */
public final class GuardianHeartbeatRegistry {
    private final EnumMap<GuardianRegistry.Component, GuardianHeartbeat> latest =
            new EnumMap<>(GuardianRegistry.Component.class);

    public synchronized GuardianHeartbeat snapshot(GuardianRegistry.Component component) {
        return component == null ? null : latest.get(component);
    }

    public synchronized boolean update(GuardianHeartbeat heartbeat) {
        if (heartbeat == null || heartbeat.component == null) return false;
        GuardianHeartbeat current = latest.get(heartbeat.component);
        if (current != null) {
            if (heartbeat.sequence < current.sequence) return false;
            if (heartbeat.sequence == current.sequence &&
                    heartbeat.monotonicMs <= current.monotonicMs) return false;
            if (heartbeat.monotonicMs < current.monotonicMs) return false;
        }
        latest.put(heartbeat.component, heartbeat);
        return true;
    }

    public synchronized void reset() {
        latest.clear();
    }
}
