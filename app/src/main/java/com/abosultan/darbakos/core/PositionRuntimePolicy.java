package com.abosultan.darbakos.core;

/** Pure ownership policy for the future GPS runtime; it starts no Service by itself. */
public final class PositionRuntimePolicy {
    public enum Owner { NONE, ACTIVITY, CONTINUOUS }

    private PositionRuntimePolicy() { }

    public static Owner owner(boolean darbakUiVisible,
                              boolean tripRecording,
                              boolean externalNavigationActive) {
        if (tripRecording || externalNavigationActive) return Owner.CONTINUOUS;
        return darbakUiVisible ? Owner.ACTIVITY : Owner.NONE;
    }
}
