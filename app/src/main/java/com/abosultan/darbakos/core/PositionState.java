package com.abosultan.darbakos.core;

/** In-memory latest-position state. Publication is explicit; no GPS polling or background work. */
public final class PositionState {
    private PositionFix latest;
    private long revision;

    public synchronized PositionFix latest() { return latest; }
    public synchronized long revision() { return revision; }
    public synchronized boolean available() { return latest != null; }

    public synchronized boolean publish(PositionFix fix) {
        if (fix == null) return false;
        if (latest != null && fix.monotonicMs <= latest.monotonicMs) return false;
        latest = fix;
        revision++;
        return true;
    }

    public synchronized void resetForColdBoot() {
        latest = null;
        revision = 0L;
    }
}
