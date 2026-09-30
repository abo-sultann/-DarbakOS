package com.abosultan.darbakos;

import android.location.Location;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.abosultan.darbakos.core.AndroidGpsSource;
import com.abosultan.darbakos.core.PositionFix;
import com.abosultan.darbakos.core.TripPoint;
import com.abosultan.darbakos.core.TripRecorderBuffer;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class P4GpsTripTest {
    @Test public void gpsCallbackPublishesRealLocationFields() {
        final PositionFix[] received = new PositionFix[1];
        AndroidGpsSource source = new AndroidGpsSource(
                androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
                        .getTargetContext(),
                new AndroidGpsSource.Callback() {
                    @Override public void onFix(PositionFix fix) { received[0] = fix; }
                    @Override public void onUnavailable() { }
                });
        Location location = new Location("gps");
        location.setLatitude(24.7136d);
        location.setLongitude(46.6753d);
        location.setAccuracy(4.5f);
        location.setSpeed(20f);
        location.setElapsedRealtimeNanos(1234000000L);
        source.onLocationChanged(location);

        assertNotNull(received[0]);
        assertEquals(24.7136d, received[0].latitude, 0d);
        assertEquals(46.6753d, received[0].longitude, 0d);
        assertEquals(4.5f, received[0].accuracyMeters, 0f);
        assertEquals(20f, received[0].speedMetersPerSecond, 0f);
        assertEquals(1234L, received[0].monotonicMs);
        assertEquals(72, received[0].speedKmh());
    }

    @Test public void tripBufferIsBoundedOrderedAndDrainable() {
        TripRecorderBuffer buffer = new TripRecorderBuffer(2);
        PositionFix one = PositionFix.create(24.7, 46.7, 5, 1, 100);
        PositionFix old = PositionFix.create(24.8, 46.8, 5, 1, 99);
        PositionFix two = PositionFix.create(24.9, 46.9, 5, 2, 200);
        PositionFix three = PositionFix.create(25.0, 47.0, 5, 3, 300);

        assertTrue(buffer.append(one));
        assertFalse(buffer.append(old));
        assertTrue(buffer.append(two));
        assertTrue(buffer.isFull());
        assertFalse(buffer.append(three));

        List<TripPoint> frozen = buffer.snapshot();
        assertEquals(2, frozen.size());
        assertEquals(0L, frozen.get(0).sequence);
        assertEquals(1L, frozen.get(1).sequence);
        try {
            frozen.clear();
            fail("snapshot must be immutable");
        } catch (UnsupportedOperationException expected) { }

        List<TripPoint> drained = buffer.drain();
        assertEquals(2, drained.size());
        assertEquals(0, buffer.size());
        assertTrue(buffer.append(three));
        assertEquals(2L, buffer.snapshot().get(0).sequence);
        buffer.resetForColdBoot();
        assertEquals(0, buffer.size());
        assertTrue(buffer.append(one));
        assertEquals(0L, buffer.snapshot().get(0).sequence);
    }
}
