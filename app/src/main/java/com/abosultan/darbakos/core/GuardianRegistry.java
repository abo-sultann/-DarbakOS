package com.abosultan.darbakos.core;

import java.util.EnumMap;
import java.util.Map;

/**
 * In-process owner for Guardian health values.
 * This registry only stores explicit health supplied by future monitors; it does not poll,
 * schedule, persist, restart or infer health on its own.
 */
public final class GuardianRegistry {
    public enum Component { CORE, HOME, NAVIGATION, MEDIA, VEHICLE }

    private static final GuardianRegistry INSTANCE = new GuardianRegistry();
    private final Map<Component, GuardianState> states = new EnumMap<>(Component.class);

    private GuardianRegistry() {
        resetForColdBoot();
    }

    public static GuardianRegistry get() { return INSTANCE; }

    public synchronized GuardianState snapshot(Component component) {
        return component == null ? GuardianState.coldBoot() : states.get(component);
    }

    public synchronized GuardianState update(Component component, GuardianState.Health health) {
        if (component == null) return GuardianState.coldBoot();
        GuardianState current = states.get(component);
        GuardianState next = current.withHealth(health);
        states.put(component, next);
        return next;
    }

    public synchronized void resetForColdBoot() {
        states.clear();
        for (Component component : Component.values()) {
            states.put(component, GuardianState.coldBoot());
        }
    }
}
