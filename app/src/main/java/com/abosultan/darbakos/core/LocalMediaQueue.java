package com.abosultan.darbakos.core;

import java.util.Collections;
import java.util.List;

/** Deterministic local queue; no autoplay is performed by this class. */
public final class LocalMediaQueue {
    private List<LocalMediaTrack> tracks = Collections.emptyList();
    private int index = -1;

    public void replace(List<LocalMediaTrack> value) {
        tracks = value == null ? Collections.emptyList() : value;
        index = tracks.isEmpty() ? -1 : Math.min(Math.max(index, 0), tracks.size() - 1);
    }

    public int size() { return tracks.size(); }
    public LocalMediaTrack current() { return index >= 0 && index < tracks.size() ? tracks.get(index) : null; }

    public LocalMediaTrack select(int position) {
        if (position < 0 || position >= tracks.size()) return null;
        index = position; return tracks.get(index);
    }

    public LocalMediaTrack next() {
        if (tracks.isEmpty()) return null;
        index = index < 0 ? 0 : (index + 1) % tracks.size();
        return tracks.get(index);
    }

    public LocalMediaTrack previous() {
        if (tracks.isEmpty()) return null;
        index = index < 0 ? 0 : (index - 1 + tracks.size()) % tracks.size();
        return tracks.get(index);
    }

    public List<LocalMediaTrack> tracks() { return tracks; }
}
