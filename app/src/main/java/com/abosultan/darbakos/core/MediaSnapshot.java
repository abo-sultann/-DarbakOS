package com.abosultan.darbakos.core;

/** Immutable process-local view of one external Android MediaSession. */
public final class MediaSnapshot {
    public enum State { ACCESS_UNAVAILABLE, IDLE, ACTIVE }

    public final State state;
    public final String title;
    public final String artist;
    public final String packageName;
    public final boolean playing;

    private MediaSnapshot(State state, String title, String artist,
                          String packageName, boolean playing) {
        this.state = state;
        this.title = clean(title);
        this.artist = clean(artist);
        this.packageName = clean(packageName);
        this.playing = playing;
    }

    public static MediaSnapshot accessUnavailable() {
        return new MediaSnapshot(State.ACCESS_UNAVAILABLE, null, null, null, false);
    }

    public static MediaSnapshot idle() {
        return new MediaSnapshot(State.IDLE, null, null, null, false);
    }

    public static MediaSnapshot active(String title, String artist,
                                       String packageName, boolean playing) {
        return new MediaSnapshot(State.ACTIVE, title, artist, packageName, playing);
    }

    public boolean active() { return state == State.ACTIVE; }

    private static String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
