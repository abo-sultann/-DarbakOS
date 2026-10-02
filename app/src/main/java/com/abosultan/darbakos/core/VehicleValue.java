package com.abosultan.darbakos.core;

/** Immutable vehicle reading with provenance and freshness. Unknown/stale values are never live. */
public final class VehicleValue {
    public enum Source { TPMS, OBD_CAN, FRIDGE, SENSOR, UNKNOWN }
    public final Double value;
    public final String unit;
    public final Source source;
    public final long observedAtMs;

    private VehicleValue(Double value, String unit, Source source, long observedAtMs) {
        this.value=value; this.unit=unit==null?"":unit; this.source=source==null?Source.UNKNOWN:source;
        this.observedAtMs=observedAtMs;
    }
    public static VehicleValue unavailable(String unit) { return new VehicleValue(null,unit,Source.UNKNOWN,0L); }
    public static VehicleValue observed(double value,String unit,Source source,long observedAtMs) {
        return new VehicleValue(value,unit,source,observedAtMs);
    }
    public boolean available() { return value != null; }
    public boolean fresh(long nowMs,long maxAgeMs) {
        return available() && observedAtMs > 0L && nowMs >= observedAtMs && nowMs-observedAtMs <= maxAgeMs;
    }
    public VehicleValue liveOrUnavailable(long nowMs,long maxAgeMs) {
        return fresh(nowMs,maxAgeMs) ? this : unavailable(unit);
    }
}
