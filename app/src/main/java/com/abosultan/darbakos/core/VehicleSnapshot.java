package com.abosultan.darbakos.core;

/** Unified truthful vehicle state. Physical adapters may populate any subset independently. */
public final class VehicleSnapshot {
    public final VehicleValue tirePressure;
    public final VehicleValue tireTemperature;
    public final VehicleValue fridgeTemperature;

    public VehicleSnapshot(VehicleValue pressure, VehicleValue tireTemp, VehicleValue fridgeTemp) {
        tirePressure=pressure==null?VehicleValue.unavailable("PSI"):pressure;
        tireTemperature=tireTemp==null?VehicleValue.unavailable("°C"):tireTemp;
        fridgeTemperature=fridgeTemp==null?VehicleValue.unavailable("°C"):fridgeTemp;
    }
    public static VehicleSnapshot unavailable() { return new VehicleSnapshot(null,null,null); }
    public VehicleSnapshot liveOnly(long nowMs,long maxAgeMs) {
        return new VehicleSnapshot(tirePressure.liveOrUnavailable(nowMs,maxAgeMs),
                tireTemperature.liveOrUnavailable(nowMs,maxAgeMs),
                fridgeTemperature.liveOrUnavailable(nowMs,maxAgeMs));
    }
}
