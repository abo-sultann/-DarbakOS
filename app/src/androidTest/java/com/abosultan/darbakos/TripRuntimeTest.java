package com.abosultan.darbakos;

import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.abosultan.darbakos.core.*;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class TripRuntimeTest {
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private LocationManager locations;

    @Before public void prepare() throws Exception {
        stopRealService();
        PositionStore.get().resetForColdBoot();
        locations = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try { locations.removeTestProvider(LocationManager.GPS_PROVIDER); }
        catch (IllegalArgumentException ignored) { }
        locations.addTestProvider(LocationManager.GPS_PROVIDER, false, true, false, false,
                true, true, true, Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
        locations.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
    }

    @After public void cleanup() throws Exception {
        stopRealService();
        if (locations != null) locations.removeTestProvider(LocationManager.GPS_PROVIDER);
        PositionStore.get().resetForColdBoot();
    }

    @Test public void storageDiscoveryAndShutdownWritesUseTheWorker() throws Exception {
        Harness h = new Harness(false);
        try {
            long t = SystemClock.elapsedRealtime() - 2_000;
            h.service.onFix(fix(t, 10f));
            h.service.onFix(fix(t + 1_000, 10f));
            h.drain();
        } finally { h.close(); }
        assertEquals(2, pointCount(h.directory));
        Log.i("DarbakRuntimeQA", "storage/close main-thread operations=" + h.mainDiskOperations);
        assertTrue("Storage discovery/commit touched UI thread: " + h.mainDiskOperations,
                h.mainDiskOperations.isEmpty());
    }

    @Test public void queuedFixesDrainBeforeDestroyAndStopInvalidatesPosition() throws Exception {
        Harness h = new Harness(false);
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        try {
            h.worker.post(() -> {
                entered.countDown();
                try { release.await(5, TimeUnit.SECONDS); }
                catch (InterruptedException error) { Thread.currentThread().interrupt(); }
            });
            assertTrue(entered.await(3, TimeUnit.SECONDS));
            long t = SystemClock.elapsedRealtime() - 2_000;
            h.service.onFix(fix(t, 10f));
            h.service.onFix(fix(t + 1_000, 10f));
            h.destroy(); // Main thread must enqueue close after the accepted input.
        } finally { release.countDown(); h.close(); }
        assertEquals("Queued points must persist before worker termination", 2, pointCount(h.directory));
        assertFalse("Stopped GPS must not remain live", PositionStore.get().available());
    }

    @Test public void nullStorageAndStickyRestartAreSafeAndIdempotent() throws Exception {
        for (int restart = 0; restart < 2; restart++) {
            Harness h = new Harness(true);
            try {
                assertNull(field(h.service, "recorder"));
                main(() -> {
                    assertEquals(Service.START_STICKY, h.service.onStartCommand(null, 0, 1));
                    assertEquals(Service.START_STICKY, h.service.onStartCommand(null, 0, 2));
                });
                h.drain();
                assertSame(h.thread, field(h.service, "workerThread"));
                assertEquals(1, workerCount());
                PositionFix value = fix(SystemClock.elapsedRealtime() - 1, 10f);
                h.service.onFix(value);
                h.drain();
                assertSame(value, PositionStore.get().latest());
                assertTrue(PositionStore.get().available());
                assertNull(h.service.onBind(null));
            } finally { h.close(); }
            assertEquals(0, workerCount());
            assertFalse("Destroyed runtime cannot leave available position", PositionStore.get().available());
        }
    }

    @Test public void retiringWorkerCannotInvalidateReplacementPosition() throws Exception {
        Harness old = new Harness(false);
        Harness replacement = null;
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        try {
            old.worker.post(() -> {
                entered.countDown();
                try { release.await(5, TimeUnit.SECONDS); }
                catch (InterruptedException error) { Thread.currentThread().interrupt(); }
            });
            assertTrue(entered.await(3, TimeUnit.SECONDS));
            old.destroy();
            replacement = new Harness(false);
            PositionFix latest = fix(SystemClock.elapsedRealtime() - 1, 10f);
            replacement.service.onFix(latest);
            replacement.drain();
            release.countDown();
            old.close();
            assertTrue(PositionStore.get().available());
            assertSame(latest, PositionStore.get().latest());
        } finally {
            release.countDown();
            old.close();
            if (replacement != null) replacement.close();
        }
    }

    @Test public void homeStopKeepsGpsAndNewActivityKeepsCurrentPosition() throws Exception {
        File directory = TripStorageLocator.locate(context);
        assertNotNull(directory);
        Set<String> before = new HashSet<>(Arrays.asList(directory.list()));
        AtomicReference<Thread> callbackThread = new AtomicReference<>();
        PositionStore.Listener listener = new PositionStore.Listener() {
            @Override public void onPosition(PositionFix fix) { callbackThread.set(Thread.currentThread()); }
            @Override public void onUnavailable() { }
        };
        PositionStore.get().addListener(listener);
        PositionFix background;
        try {
            try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
                waitFor(() -> workerCount() == 1, "one service worker");
                publishGps(10f);
                publishGps(10f);
                scenario.moveToState(Lifecycle.State.CREATED);
                background = publishGps(20f);
                assertEquals("DarbakTripRuntime", callbackThread.get().getName());
                assertNotSame(Looper.getMainLooper().getThread(), callbackThread.get());
                assertEquals(1, workerCount());
                scenario.moveToState(Lifecycle.State.RESUMED);
                scenario.onActivity(activity -> {
                    assertEquals("72", ((TextView) activity.findViewById(R.id.speed_value)).getText().toString());
                    assertEquals("GPS • مباشر", ((TextView) activity.findViewById(R.id.speed_source)).getText().toString());
                });
            }
            try (ActivityScenario<MainActivity> again = ActivityScenario.launch(MainActivity.class)) {
                assertSame("New Activity is not a new GPS process", background, PositionStore.get().latest());
                again.onActivity(activity -> assertEquals("72",
                        ((TextView) activity.findViewById(R.id.speed_value)).getText().toString()));
                assertEquals(1, workerCount());
            }
            stopRealService();
            boolean persistedBackground = false;
            for (File file : directory.listFiles()) {
                if (before.contains(file.getName()) || !TripChunkWriter.isCompleteFile(file)) continue;
                for (TripPoint point : new TripChunkReader().read(file).points) {
                    if (point.position.monotonicMs == background.monotonicMs) persistedBackground = true;
                }
            }
            assertTrue("Point received while Home stopped must reach committed history", persistedBackground);
            Log.i("DarbakRuntimeQA", "background GPS -> store -> main-thread Home -> committed trip PASS");
        } finally { PositionStore.get().removeListener(listener); }
    }

    private PositionFix publishGps(float speed) throws Exception {
        SystemClock.sleep(1_100); // Respect the source's real 1-second request interval.
        long time = SystemClock.elapsedRealtime();
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(24.7);
        location.setLongitude(46.7);
        location.setAccuracy(5f);
        location.setSpeed(speed);
        location.setBearing(0f);
        location.setTime(System.currentTimeMillis());
        location.setElapsedRealtimeNanos(time * 1_000_000L);
        locations.setTestProviderLocation(LocationManager.GPS_PROVIDER, location);
        waitFor(() -> PositionStore.get().latest() != null
                && PositionStore.get().latest().monotonicMs == time, "LocationManager delivery");
        return PositionStore.get().latest();
    }

    private PositionFix fix(long time, float speed) {
        return PositionFix.createStamped(24.7, 46.7, 5f, speed, time, System.currentTimeMillis());
    }

    private int pointCount(File directory) throws Exception {
        int count = 0;
        for (File file : directory.listFiles()) {
            if (TripChunkWriter.isCompleteFile(file)) count += new TripChunkReader().read(file).points.size();
        }
        return count;
    }

    private static Object field(Object target, String name) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }

    private static void main(Runnable action) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(action);
    }

    private interface Condition { boolean ready(); }
    private static void waitFor(Condition condition, String label) throws Exception {
        long deadline = SystemClock.elapsedRealtime() + 8_000;
        while (!condition.ready() && SystemClock.elapsedRealtime() < deadline) SystemClock.sleep(20);
        assertTrue("Timed out: " + label, condition.ready());
    }

    private static int workerCount() {
        int count = 0;
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread.isAlive() && thread.getName().equals("DarbakTripRuntime")) count++;
        }
        return count;
    }

    private void stopRealService() throws Exception {
        context.stopService(new Intent(context, TripRuntimeService.class));
        waitFor(() -> workerCount() == 0, "service worker shutdown");
    }

    private final class Harness implements AutoCloseable {
        final List<String> mainDiskOperations = Collections.synchronizedList(new ArrayList<>());
        final File directory;
        final TripRuntimeService service = new TripRuntimeService();
        final HandlerThread thread;
        final Handler worker;
        private boolean destroyed;

        Harness(boolean unavailableStorage) throws Exception {
            File root = new File(context.getCacheDir(), "runtime-" + System.nanoTime());
            assertTrue(root.mkdirs());
            directory = new File(root, "tracked") {
                private void probe(String name) {
                    if (Looper.myLooper() == Looper.getMainLooper()) mainDiskOperations.add(name);
                }
                @Override public boolean exists() { probe("exists"); return super.exists(); }
                @Override public boolean mkdirs() { probe("mkdirs"); return super.mkdirs(); }
                @Override public boolean isDirectory() { probe("isDirectory"); return super.isDirectory(); }
                @Override public boolean canWrite() { probe("canWrite"); return super.canWrite(); }
            };
            File blocked = new File(root, "blocked");
            assertTrue(blocked.createNewFile());
            ContextWrapper wrapped = new ContextWrapper(context) {
                @Override public File[] getExternalFilesDirs(String type) {
                    if (Looper.myLooper() == Looper.getMainLooper()) mainDiskOperations.add("externalDirs");
                    return unavailableStorage ? null : new File[] {directory};
                }
                @Override public File getFilesDir() { return unavailableStorage ? blocked : root; }
            };
            Method attach = ContextWrapper.class.getDeclaredMethod("attachBaseContext", Context.class);
            attach.setAccessible(true);
            attach.invoke(service, wrapped);
            main(service::onCreate);
            thread = (HandlerThread) field(service, "workerThread");
            worker = (Handler) field(service, "worker");
            drain();
        }

        void drain() throws Exception {
            CountDownLatch drained = new CountDownLatch(1);
            assertTrue(worker.post(drained::countDown));
            assertTrue("Worker barrier", drained.await(5, TimeUnit.SECONDS));
        }

        void destroy() {
            if (!destroyed) { destroyed = true; main(service::onDestroy); }
        }

        @Override public void close() throws Exception {
            destroy();
            thread.join(5_000);
            assertFalse("Service worker leaked", thread.isAlive());
        }
    }
}
