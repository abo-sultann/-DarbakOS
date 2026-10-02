package com.abosultan.darbakos;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.abosultan.darbakos.core.VehicleDataStore;
import com.abosultan.darbakos.core.VehicleSnapshot;
import com.abosultan.darbakos.core.VehicleSourceAdapter;
import com.abosultan.darbakos.core.VehicleValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class VehicleDataTest {
    @Test public void emptySourcesStayUnavailable() {
        VehicleSnapshot s = new VehicleDataStore().snapshot(1000L,500L);
        assertFalse(s.tirePressure.available());
        assertFalse(s.tireTemperature.available());
        assertFalse(s.fridgeTemperature.available());
    }

    @Test public void freshReadingKeepsValueAndProvenance() {
        VehicleDataStore store = new VehicleDataStore();
        store.register(adapter("tpms", VehicleValue.observed(34.5,"PSI",VehicleValue.Source.TPMS,900L)));
        VehicleSnapshot s=store.snapshot(1000L,500L);
        assertEquals(34.5,s.tirePressure.value,0.001);
        assertEquals(VehicleValue.Source.TPMS,s.tirePressure.source);
    }

    @Test public void staleReadingBecomesUnavailable() {
        VehicleDataStore store = new VehicleDataStore();
        store.register(adapter("tpms", VehicleValue.observed(31,"PSI",VehicleValue.Source.TPMS,100L)));
        assertFalse(store.snapshot(1000L,500L).tirePressure.available());
    }

    @Test public void newestReadingWinsWithoutInventingOtherFields() {
        VehicleDataStore store = new VehicleDataStore();
        store.register(adapter("old",VehicleValue.observed(30,"PSI",VehicleValue.Source.OBD_CAN,800L)));
        store.register(adapter("new",VehicleValue.observed(35,"PSI",VehicleValue.Source.TPMS,950L)));
        VehicleSnapshot s=store.snapshot(1000L,500L);
        assertEquals(35,s.tirePressure.value,0.001);
        assertEquals(VehicleValue.Source.TPMS,s.tirePressure.source);
        assertFalse(s.tireTemperature.available());
    }

    private static VehicleSourceAdapter adapter(final String id, final VehicleValue pressure) {
        return new VehicleSourceAdapter() {
            public String id(){ return id; }
            public VehicleSnapshot snapshot(){ return new VehicleSnapshot(pressure,null,null); }
        };
    }
}
