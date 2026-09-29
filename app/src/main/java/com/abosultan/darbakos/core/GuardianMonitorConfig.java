package com.abosultan.darbakos.core;

/**
 * Immutable thresholds for caller-driven Guardian monitoring.
 * Conservative defaults avoid aggressive failure classification on a slow T3 head unit.
 */
public final class GuardianMonitorConfig {
    public static final long DEFAULT_LATE_AFTER_MS = 5_000L;
    public static final long DEFAULT_STALE_AFTER_MS = 15_000L;

    public final long lateAfterMs;
    public final long staleAfterMs;

    public GuardianMonitorConfig(long lateAfterMs, long staleAfterMs) {
        // Reserve one representable millisecond for the strictly later stale boundary.
        long late = Math.min(Long.MAX_VALUE - 1L, Math.max(1L, lateAfterMs));
        long stale = Math.max(late + 1L, staleAfterMs);
        this.lateAfterMs = late;
        this.staleAfterMs = stale;
    }

    public static GuardianMonitorConfig conservativeDefault() {
        return new GuardianMonitorConfig(DEFAULT_LATE_AFTER_MS, DEFAULT_STALE_AFTER_MS);
    }
}
