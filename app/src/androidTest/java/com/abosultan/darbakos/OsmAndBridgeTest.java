package com.abosultan.darbakos;

import android.net.Uri;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.abosultan.darbakos.core.OsmAndBridge;
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

        String[] copy = OsmAndPackages.ordered();
        copy[0] = "changed";
        assertEquals(OsmAndPackages.FULL, OsmAndPackages.ordered()[0]);
    }

    @Test public void absentOsmAndIsTruthfulAndSafe() {
        OsmAndBridge bridge = new OsmAndBridge(
                InstrumentationRegistry.getInstrumentation().getTargetContext());
        assertEquals(OsmAndBridge.Availability.UNAVAILABLE, bridge.availability());
        assertNull(bridge.resolvedPackage());
        assertFalse(bridge.open());
        PositionFix fix = PositionFix.create(24.7136, 46.6753, 5f, 0f, 1000L);
        assertNotNull(fix);
        assertFalse(bridge.openLocation(fix));
        assertFalse(bridge.openLocation(null));
    }
}
