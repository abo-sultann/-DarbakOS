package com.abosultan.darbakos.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Small bounded in-memory staging buffer for trip points. */
public final class TripRecorderBuffer {
    private final int capacity;
    private final ArrayList<TripPoint> points;
    private long nextSequence;
    private long lastMonotonicMs = -1L;

    public TripRecorderBuffer(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity");
        this.capacity = capacity;
        this.points = new ArrayList<>(capacity);
    }

    public synchronized boolean append(PositionFix fix) {
        if (fix == null || points.size() >= capacity) return false;
        if (fix.monotonicMs <= lastMonotonicMs) return false;
        points.add(new TripPoint(fix, nextSequence++));
        lastMonotonicMs = fix.monotonicMs;
        return true;
    }

    public synchronized int size() { return points.size(); }
    public int capacity() { return capacity; }
    public synchronized boolean isFull() { return points.size() >= capacity; }

    public synchronized List<TripPoint> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(points));
    }

    /**
     * Commits only the already-persisted prefix. Points appended after a snapshot are retained.
     * This enables write-first, discard-second persistence without losing a failed chunk.
     */
    public synchronized boolean commitPrefix(int count) {
        if (count < 0 || count > points.size()) return false;
        if (count == 0) return true;
        points.subList(0, count).clear();
        return true;
    }

    /** Legacy convenience; persistence code must prefer snapshot + commitPrefix after success. */
    public synchronized List<TripPoint> drain() {
        List<TripPoint> drained = Collections.unmodifiableList(new ArrayList<>(points));
        points.clear();
        return drained;
    }

    public synchronized void resetForColdBoot() {
        points.clear();
        nextSequence = 0L;
        lastMonotonicMs = -1L;
    }
}
