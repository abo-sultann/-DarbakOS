package com.abosultan.darbakos.core;

/** Immutable user-facing core state. Sources update this later; UI reads it without inventing values. */
public final class DarbakState {
    public enum Availability { AVAILABLE, UNAVAILABLE, UNKNOWN }
    public final Availability speed;
    public final Availability navigation;
    public final Availability vehicle;
    public final boolean mediaPlaying;
    public final long revision;

    private DarbakState(Availability speed, Availability navigation, Availability vehicle,
                        boolean mediaPlaying, long revision) {
        this.speed = speed;
        this.navigation = navigation;
        this.vehicle = vehicle;
        this.mediaPlaying = mediaPlaying;
        this.revision = revision;
    }

    public static DarbakState coldBoot() {
        return new DarbakState(Availability.UNAVAILABLE, Availability.UNAVAILABLE,
                Availability.UNAVAILABLE, false, 0L);
    }

    public DarbakState withAvailability(Availability speed, Availability navigation,
                                        Availability vehicle, boolean mediaPlaying) {
        return new DarbakState(speed, navigation, vehicle, mediaPlaying, revision + 1L);
    }
}
