package com.abosultan.darbakos.core;

/** Pure P4 acceptance policy shared by live position and future trip recording. */
public final class PositionQualityPolicy {
    public static final long MAX_FIX_AGE_MS = 15_000L;
    private static final float MAX_ACCURACY_METERS = 100f;
    private static final float MAX_SPEED_MPS = 70f; // 252 km/h; reject implausible car fixes.

    private PositionQualityPolicy() {}

    public static boolean isUsable(PositionFix fix, long nowMonotonicMs) {
        if (fix == null || nowMonotonicMs < 0L) return false;
        if (fix.monotonicMs <= 0L || fix.monotonicMs > nowMonotonicMs) return false;
        if (nowMonotonicMs - fix.monotonicMs > MAX_FIX_AGE_MS) return false;
        return fix.accuracyMeters <= MAX_ACCURACY_METERS
                && fix.speedMetersPerSecond <= MAX_SPEED_MPS;
    }
}
