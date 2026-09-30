package com.abosultan.darbakos;

import android.content.Intent;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.abosultan.darbakos.core.OsmAndBridge;
import com.abosultan.darbakos.core.OsmAndNavigationSnapshot;
import com.abosultan.darbakos.core.OsmAndPackages;
import com.abosultan.darbakos.core.PositionFix;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class OsmAndBridgeTest {
    @Test public void packageSelectionIsConservativeAndStableFirst() {
        assertNull(OsmAndPackages.select(null));
        assertNull(OsmAndPackages.select(new HashSet<String>()));
        assertNull(OsmAndPackages.select(new HashSet<>(Arrays.asList("example.maps"))));
        assertEquals(OsmAndPackages.NIGHTLY,
                OsmAndPackages.select(new HashSet<>(Arrays.asList(OsmAndPackages.NIGHTLY))));
        assertEquals(OsmAndPackages.FREE,
                OsmAndPackages.select(new HashSet<>(Arrays.asList(
                        OsmAndPackages.NIGHTLY, OsmAndPackages.FREE))));
        assertEquals(OsmAndPackages.FULL,
                OsmAndPackages.select(new HashSet<>(Arrays.asList(
                        OsmAndPackages.NIGHTLY, OsmAndPackages.FREE, OsmAndPackages.FULL))));
        assertTrue(OsmAndPackages.isKnown(OsmAndPackages.FULL));
        assertFalse(OsmAndPackages.isKnown("example.maps"));
        assertFalse(OsmAndPackages.isKnown(null));
        assertEquals("net.osmand.aidl.OsmandAidlService", OsmAndBridge.AIDL_SERVICE_ACTION);
        assertEquals("osmand.api", OsmAndBridge.API_SCHEME);

        String[] copy = OsmAndPackages.ordered();
        copy[0] = "changed";
        assertEquals(OsmAndPackages.FULL, OsmAndPackages.ordered()[0]);
    }

    @Test public void absentOsmAndIsTruthfulAndSafe() {
        OsmAndBridge bridge = new OsmAndBridge(
                InstrumentationRegistry.getInstrumentation().getTargetContext());
        assertEquals(OsmAndBridge.Availability.UNAVAILABLE, bridge.availability());
        assertNull(bridge.resolvedPackage());
        assertFalse(bridge.aidlServiceAvailable());
        assertFalse(bridge.externalApiAvailable());
        assertNull(bridge.navigationInfoIntent());
        assertFalse(bridge.open());
        PositionFix fix = PositionFix.create(24.7136, 46.6753, 5f, 0f, 1000L);
        assertNotNull(fix);
        assertFalse(bridge.openLocation(fix));
        assertFalse(bridge.openLocation(null));
        assertFalse(bridge.openSearch("الرس", fix));
        assertFalse(bridge.openSearch("", fix));
    }

    @Test public void navigationInfoIsIdleWithoutRouteAndUnknownWithoutResult() {
        OsmAndNavigationSnapshot unknown = OsmAndBridge.parseNavigationInfo(null, 100L);
        assertEquals(OsmAndNavigationSnapshot.State.UNKNOWN, unknown.state);
        assertTrue(unknown.stale(100L, 60_000L));

        OsmAndNavigationSnapshot idle = OsmAndBridge.parseNavigationInfo(new Intent(), 200L);
        assertEquals(OsmAndNavigationSnapshot.State.IDLE, idle.state);
        assertFalse(idle.active());
        assertFalse(idle.stale(60_200L, 60_000L));
        assertTrue(idle.stale(60_201L, 60_000L));
    }

    @Test public void navigationInfoParsesOnlyDocumentedRouteFields() {
        Intent result = new Intent();
        result.putExtra("destination_lat", 25.8697d);
        result.putExtra("destination_lon", 43.4973d);
        result.putExtra("eta", 1_800_000_000L);
        result.putExtra("time_left", 900);
        result.putExtra("time_distance_left", 12_400);
        result.putExtra("next_turn_distance", 350);
        result.putExtra("current_turn_name", "طريق الملك عبدالعزيز");
        result.putExtra("current_turn_type", "TR");

        OsmAndNavigationSnapshot snapshot = OsmAndBridge.parseNavigationInfo(result, 5_000L);
        assertEquals(OsmAndNavigationSnapshot.State.ACTIVE, snapshot.state);
        assertTrue(snapshot.active());
        assertEquals(25.8697d, snapshot.destinationLatitude, 0d);
        assertEquals(43.4973d, snapshot.destinationLongitude, 0d);
        assertEquals(900, snapshot.timeLeftSeconds);
        assertEquals(12_400, snapshot.distanceLeftMeters);
        assertEquals(350, snapshot.nextTurnDistanceMeters);
        assertEquals("طريق الملك عبدالعزيز", snapshot.turnName);
        assertEquals("TR", snapshot.turnType);
        assertFalse(snapshot.stale(65_000L, 60_000L));
    }

    @Test public void invalidDestinationCannotBecomeActiveRoute() {
        Intent result = new Intent();
        result.putExtra("destination_lat", 200d);
        result.putExtra("destination_lon", 43d);
        OsmAndNavigationSnapshot snapshot = OsmAndBridge.parseNavigationInfo(result, 1_000L);
        assertEquals(OsmAndNavigationSnapshot.State.UNKNOWN, snapshot.state);
        assertFalse(snapshot.active());
    }
}
