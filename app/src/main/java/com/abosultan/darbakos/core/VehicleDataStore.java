package com.abosultan.darbakos.core;

import java.util.LinkedHashMap;
import java.util.Map;

/** Process-local merger for optional vehicle sources. Newest available reading wins per field. */
public final class VehicleDataStore {
    private final Map<String, VehicleSourceAdapter> adapters = new LinkedHashMap<>();

    public synchronized void register(VehicleSourceAdapter adapter) {
        if (adapter != null && adapter.id() != null && !adapter.id().trim().isEmpty())
            adapters.put(adapter.id(), adapter);
    }

    public synchronized void unregister(String id) { if (id != null) adapters.remove(id); }

    public synchronized VehicleSnapshot snapshot(long nowMs, long maxAgeMs) {
        VehicleValue pressure = null, tireTemp = null, fridgeTemp = null;
        for (VehicleSourceAdapter adapter : adapters.values()) {
            VehicleSnapshot s;
            try { s = adapter.snapshot(); } catch (RuntimeException ignored) { continue; }
            if (s == null) continue;
            pressure = newest(pressure, s.tirePressure);
            tireTemp = newest(tireTemp, s.tireTemperature);
            fridgeTemp = newest(fridgeTemp, s.fridgeTemperature);
        }
        return new VehicleSnapshot(pressure,tireTemp,fridgeTemp).liveOnly(nowMs,maxAgeMs);
    }

    private static VehicleValue newest(VehicleValue a, VehicleValue b) {
        if (b == null || !b.available()) return a;
        if (a == null || !a.available() || b.observedAtMs > a.observedAtMs) return b;
        return a;
    }
}
