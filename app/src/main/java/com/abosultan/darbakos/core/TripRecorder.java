package com.abosultan.darbakos.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * In-memory P4 trip recorder foundation.
 * Explicit caller input only: no Location provider, Service, timer or disk I/O.
 */
public final class TripRecorder {
    private final ArrayList<ArrayList<TripPoint>> segments = new ArrayList<>();
    private boolean recording;
    private boolean paused;
    private long nextSequence;

    public synchronized void start() {
        if (recording) return;
        recording = true;
        paused = false;
        ensureNewSegment();
    }

    public synchronized void pause() {
        if (!recording) return;
        paused = true;
    }

    public synchronized void resume() {
        if (!recording || !paused) return;
        paused = false;
        ensureNewSegment();
    }

    public synchronized boolean accept(PositionFix fix, long nowMonotonicMs) {
        if (!recording || paused || !PositionQualityPolicy.isUsable(fix, nowMonotonicMs)) return false;
        ensureSegment();
        ArrayList<TripPoint> current = segments.get(segments.size() - 1);
        if (!current.isEmpty()) {
            PositionFix previous = current.get(current.size() - 1).position;
            if (fix.monotonicMs <= previous.monotonicMs) return false;
            if (fix.monotonicMs - previous.monotonicMs > PositionQualityPolicy.MAX_FIX_AGE_MS) {
                ensureNewSegment();
                current = segments.get(segments.size() - 1);
            }
        }
        current.add(new TripPoint(fix, nextSequence++));
        return true;
    }

    public synchronized void finish() {
        recording = false;
        paused = false;
    }

    public synchronized void reset() {
        segments.clear();
        recording = false;
        paused = false;
        nextSequence = 0L;
    }

    public synchronized boolean isRecording() { return recording; }
    public synchronized boolean isPaused() { return paused; }

    public synchronized List<List<TripPoint>> snapshot() {
        ArrayList<List<TripPoint>> copy = new ArrayList<>();
        for (ArrayList<TripPoint> segment : segments) {
            copy.add(Collections.unmodifiableList(new ArrayList<>(segment)));
        }
        return Collections.unmodifiableList(copy);
    }

    private void ensureSegment() {
        if (segments.isEmpty()) ensureNewSegment();
    }

    private void ensureNewSegment() {
        if (segments.isEmpty() || !segments.get(segments.size() - 1).isEmpty()) {
            segments.add(new ArrayList<TripPoint>());
        }
    }
}
