package com.abosultan.darbakos.core;

/** Immutable accepted point for a future trip recording session. */
public final class TripPoint {
    public final PositionFix position;
    public final long sequence;

    public TripPoint(PositionFix position, long sequence) {
        if (position == null) throw new IllegalArgumentException("position");
        if (sequence < 0L) throw new IllegalArgumentException("sequence");
        this.position = position;
        this.sequence = sequence;
    }
}
