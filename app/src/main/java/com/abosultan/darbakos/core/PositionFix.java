package com.abosultan.darbakos.core;

/** Immutable platform-neutral position value for P4. No Android location dependency. */
public final class PositionFix {
    public final double latitude;
    public final double longitude;
    public final float accuracyMeters;
    public final float speedMetersPerSecond;
    public final long monotonicMs;
    /** UTC epoch milliseconds when known; 0 means unavailable. */
    public final long wallTimeMs;

    private PositionFix(double latitude, double longitude, float accuracyMeters,
                        float speedMetersPerSecond, long monotonicMs, long wallTimeMs) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracyMeters = accuracyMeters;
        this.speedMetersPerSecond = speedMetersPerSecond;
        this.monotonicMs = monotonicMs;
        this.wallTimeMs = wallTimeMs;
    }

    /** Compatibility factory for sources/tests that do not provide wall time. */
    public static PositionFix create(double latitude, double longitude, float accuracyMeters,
                                     float speedMetersPerSecond, long monotonicMs) {
        return createStamped(latitude, longitude, accuracyMeters, speedMetersPerSecond,
                monotonicMs, 0L);
    }

    public static PositionFix createStamped(double latitude, double longitude, float accuracyMeters,
                                            float speedMetersPerSecond, long monotonicMs,
                                            long wallTimeMs) {
        if (Double.isNaN(latitude) || Double.isInfinite(latitude)
                || latitude < -90d || latitude > 90d) return null;
        if (Double.isNaN(longitude) || Double.isInfinite(longitude)
                || longitude < -180d || longitude > 180d) return null;
        if (Float.isNaN(accuracyMeters) || Float.isInfinite(accuracyMeters)
                || accuracyMeters < 0f) return null;
        if (Float.isNaN(speedMetersPerSecond) || Float.isInfinite(speedMetersPerSecond)
                || speedMetersPerSecond < 0f) speedMetersPerSecond = 0f;
        if (monotonicMs < 0L || wallTimeMs < 0L) return null;
        return new PositionFix(latitude, longitude, accuracyMeters,
                speedMetersPerSecond, monotonicMs, wallTimeMs);
    }

    public int speedKmh() {
        return Math.round(speedMetersPerSecond * 3.6f);
    }
}
