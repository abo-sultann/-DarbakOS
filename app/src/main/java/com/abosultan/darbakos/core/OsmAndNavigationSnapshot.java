package com.abosultan.darbakos.core;

/** Immutable, truthful snapshot returned by OsmAnd's external API. */
public final class OsmAndNavigationSnapshot {
    public enum State { UNKNOWN, IDLE, ACTIVE }

    public final State state;
    public final long receivedElapsedMs;
    public final double destinationLatitude;
    public final double destinationLongitude;
    public final long etaEpochSeconds;
    public final int timeLeftSeconds;
    public final int distanceLeftMeters;
    public final int nextTurnDistanceMeters;
    public final String turnName;
    public final String turnType;

    private OsmAndNavigationSnapshot(State state, long receivedElapsedMs,
                                     double destinationLatitude, double destinationLongitude,
                                     long etaEpochSeconds, int timeLeftSeconds,
                                     int distanceLeftMeters, int nextTurnDistanceMeters,
                                     String turnName, String turnType) {
        this.state = state;
        this.receivedElapsedMs = Math.max(0L, receivedElapsedMs);
        this.destinationLatitude = destinationLatitude;
        this.destinationLongitude = destinationLongitude;
        this.etaEpochSeconds = Math.max(0L, etaEpochSeconds);
        this.timeLeftSeconds = Math.max(-1, timeLeftSeconds);
        this.distanceLeftMeters = Math.max(-1, distanceLeftMeters);
        this.nextTurnDistanceMeters = Math.max(-1, nextTurnDistanceMeters);
        this.turnName = clean(turnName);
        this.turnType = clean(turnType);
    }

    public static OsmAndNavigationSnapshot unknown(long receivedElapsedMs) {
        return new OsmAndNavigationSnapshot(State.UNKNOWN, receivedElapsedMs,
                Double.NaN, Double.NaN, 0L, -1, -1, -1, null, null);
    }

    public static OsmAndNavigationSnapshot idle(long receivedElapsedMs) {
        return new OsmAndNavigationSnapshot(State.IDLE, receivedElapsedMs,
                Double.NaN, Double.NaN, 0L, -1, -1, -1, null, null);
    }

    public static OsmAndNavigationSnapshot active(long receivedElapsedMs,
                                                   double destinationLatitude,
                                                   double destinationLongitude,
                                                   long etaEpochSeconds,
                                                   int timeLeftSeconds,
                                                   int distanceLeftMeters,
                                                   int nextTurnDistanceMeters,
                                                   String turnName,
                                                   String turnType) {
        if (!validCoordinate(destinationLatitude, destinationLongitude)) {
            return unknown(receivedElapsedMs);
        }
        return new OsmAndNavigationSnapshot(State.ACTIVE, receivedElapsedMs,
                destinationLatitude, destinationLongitude, etaEpochSeconds,
                timeLeftSeconds, distanceLeftMeters, nextTurnDistanceMeters,
                turnName, turnType);
    }

    public boolean active() { return state == State.ACTIVE; }

    public boolean stale(long nowElapsedMs, long maxAgeMs) {
        if (state == State.UNKNOWN || maxAgeMs < 0L || nowElapsedMs < receivedElapsedMs) return true;
        return nowElapsedMs - receivedElapsedMs > maxAgeMs;
    }

    private static boolean validCoordinate(double latitude, double longitude) {
        return !Double.isNaN(latitude) && !Double.isInfinite(latitude)
                && !Double.isNaN(longitude) && !Double.isInfinite(longitude)
                && latitude >= -90d && latitude <= 90d
                && longitude >= -180d && longitude <= 180d;
    }

    private static String clean(String value) {
        if (value == null) return "";
        String trimmed = value.trim();
        return trimmed.length() == 0 ? "" : trimmed;
    }
}
