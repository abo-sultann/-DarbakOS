package com.abosultan.darbakos.core;

import java.util.ArrayList;

/** Process-local latest position publication used by the GPS runtime and final Home UI. */
public final class PositionStore {
    public interface Listener {
        void onPosition(PositionFix fix);
        void onUnavailable();
    }

    private static final PositionStore INSTANCE = new PositionStore();
    public static PositionStore get() { return INSTANCE; }

    private final PositionState state = new PositionState();
    private final ArrayList<Listener> listeners = new ArrayList<>();
    private boolean available;

    private PositionStore() { }

    public synchronized PositionFix latest() { return state.latest(); }
    public synchronized boolean available() { return available && state.available(); }

    public void addListener(Listener listener) {
        if (listener == null) return;
        PositionFix current;
        boolean currentAvailable;
        synchronized (this) {
            if (!listeners.contains(listener)) listeners.add(listener);
            current = state.latest();
            currentAvailable = available && current != null;
        }
        if (currentAvailable) listener.onPosition(current);
        else listener.onUnavailable();
    }

    public synchronized void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public void publish(PositionFix fix) {
        ArrayList<Listener> copy;
        synchronized (this) {
            if (!state.publish(fix)) return;
            available = true;
            copy = new ArrayList<>(listeners);
        }
        for (Listener listener : copy) listener.onPosition(fix);
    }

    public void publishUnavailable() {
        ArrayList<Listener> copy;
        synchronized (this) {
            if (!available) return;
            available = false;
            copy = new ArrayList<>(listeners);
        }
        for (Listener listener : copy) listener.onUnavailable();
    }

    public synchronized void resetForColdBoot() {
        state.resetForColdBoot();
        available = false;
    }
}
