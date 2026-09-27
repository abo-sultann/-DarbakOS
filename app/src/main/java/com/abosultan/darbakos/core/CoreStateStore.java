package com.abosultan.darbakos.core;

/** Single in-process owner for current Darbak state. No service, disk write, sensor or network dependency. */
public final class CoreStateStore {
    private static final CoreStateStore INSTANCE = new CoreStateStore();
    private DarbakState state = DarbakState.coldBoot();
    private CoreStateStore() {}
    public static CoreStateStore get() { return INSTANCE; }
    public synchronized DarbakState snapshot() { return state; }
    public synchronized void resetForColdBoot() { state = DarbakState.coldBoot(); }
}
