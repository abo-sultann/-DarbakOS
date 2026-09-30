package com.abosultan.darbakos.core;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import java.io.File;
import java.io.IOException;

/**
 * P4 continuous position/trip runtime for Android 7.1.
 * One worker owns GPS callbacks and trip persistence so disk I/O never blocks Darbak UI.
 */
public final class TripRuntimeService extends Service implements AndroidGpsSource.Callback {
    // A retiring worker must not invalidate a replacement runtime's current position.
    private static TripRuntimeService currentRuntime;
    private HandlerThread workerThread;
    private Handler worker;
    private AndroidGpsSource gps;
    private TripAutoRecorder recorder;
    private boolean closing;

    @Override public void onCreate() {
        super.onCreate();
        workerThread = new HandlerThread("DarbakTripRuntime");
        workerThread.start();
        worker = new Handler(workerThread.getLooper());
        synchronized (TripRuntimeService.class) { currentRuntime = this; }
        worker.post(() -> {
            File directory = TripStorageLocator.locate(this);
            if (directory != null) recorder = new TripAutoRecorder(directory);
            gps = new AndroidGpsSource(this, this);
            gps.start(workerThread.getLooper());
        });
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        enqueue(() -> {
            if (gps != null && !gps.started()) gps.start(workerThread.getLooper());
        });
        return START_STICKY;
    }

    @Override public void onFix(final PositionFix fix) {
        enqueue(() -> {
            synchronized (TripRuntimeService.class) {
                if (currentRuntime == this) PositionStore.get().publish(fix);
            }
            record(fix);
        });
    }

    @Override public void onUnavailable() {
        enqueue(() -> {
            synchronized (TripRuntimeService.class) {
                if (currentRuntime == this) PositionStore.get().publishUnavailable();
            }
        });
    }

    @Override public void onDestroy() {
        synchronized (this) {
            if (!closing && worker != null) {
                closing = true;
                // Enqueue atomically after all accepted fixes; never flush or join on the UI.
                worker.post(() -> {
                    try {
                        if (gps != null) gps.stop();
                        if (recorder != null) recorder.close();
                    } catch (IOException ignored) {
                        // Already committed chunks remain readable after a failed final save.
                    } finally {
                        synchronized (TripRuntimeService.class) {
                            if (currentRuntime == this) {
                                PositionStore.get().publishUnavailable();
                                currentRuntime = null;
                            }
                        }
                        workerThread.quitSafely();
                    }
                });
            }
        }
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) { return null; }

    private synchronized void enqueue(Runnable action) {
        if (!closing && worker != null) worker.post(action);
    }

    private void record(PositionFix fix) {
        if (recorder == null) return;
        try {
            recorder.accept(fix);
        } catch (IOException ignored) {
            // Position remains live even if trip storage temporarily fails.
        }
    }
}
