package com.abosultan.darbakos.core;

/** Immutable platform-neutral position value for P4. No Android location dependency. */
public final class PositionFix {
    public final double latitude;
    public final double longitude;
    public final float accuracyMeters;
    public final float speedMetersPerSecond;
    public final long monotonicMs;

    private PositionFix(double latitude, double longitude, float accuracyMeters,
                        float speedMetersPerSecond, long monotonicMs) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracyMeters = accuracyMeters;
        this.speedMetersPerSecond = speedMetersPerSecond;
        this.monotonicMs = monotonicMs;
    }

    public static PositionFix create(double latitude, double longitude, float accuracyMeters,
                                     float speedMetersPerSecond, long monotonicMs) {
        if (Double.isNaN(latitude) || Double.isInfinite(latitude)
                || latitude < -90d || latitude > 90d) return null;
        if (Double.isNaN(longitude) || Double.isInfinite(longitude)
                || longitude < -180d || longitude > 180d) return null;
        if (Float.isNaN(accuracyMeters) || Float.isInfinite(accuracyMeters)
                || accuracyMeters < 0f) return null;
        if (Float.isNaN(speedMetersPerSecond) || Float.isInfinite(speedMetersPerSecond)
                || speedMetersPerSecond < 0f) speedMetersPerSecond = 0f;
        if (monotonicMs < 0L) return null;
        return new PositionFix(latitude, longitude, accuracyMeters,
                speedMetersPerSecond, monotonicMs);
    }

    public int speedKmh() {
        return Math.round(speedMetersPerSecond * 3.6f);
    }
}
