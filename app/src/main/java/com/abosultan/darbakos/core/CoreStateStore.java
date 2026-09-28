package com.abosultan.darbakos.core;

import java.util.ArrayList;
import java.util.List;

/** Single in-process owner for current Darbak state. No service, disk, sensor or network dependency. */
public final class CoreStateStore {
    public interface Listener { void onStateChanged(DarbakState state); }
    private static final CoreStateStore INSTANCE = new CoreStateStore();
    private final List<Listener> listeners = new ArrayList<>();
    private DarbakState state = DarbakState.coldBoot();
    private CoreStateStore() {}
    public static CoreStateStore get() { return INSTANCE; }
    public synchronized DarbakState snapshot() { return state; }

    /** Cold boot is a state publication too, so existing in-process consumers cannot retain stale state. */
    public void resetForColdBoot() { publish(DarbakState.coldBoot()); }

    public synchronized void addListener(Listener listener) {
        if (listener != null && !listeners.contains(listener)) listeners.add(listener);
    }
    public synchronized void removeListener(Listener listener) { listeners.remove(listener); }

    public void publish(DarbakState next) {
        if (next == null) return;
        final List<Listener> copy;
        synchronized (this) { state = next; copy = new ArrayList<>(listeners); }
        for (Listener listener : copy) listener.onStateChanged(next);
    }
}
