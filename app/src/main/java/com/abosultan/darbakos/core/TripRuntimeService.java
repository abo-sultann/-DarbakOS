package com.abosultan.darbakos.core;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;

import java.io.File;
import java.io.IOException;

/**
 * P4 continuous position/trip runtime for Android 7.1.
 * One worker owns GPS callbacks and trip persistence so disk I/O never blocks Darbak UI.
 */
public final class TripRuntimeService extends Service implements AndroidGpsSource.Callback {
    private HandlerThread workerThread;
    private Handler worker;
    private AndroidGpsSource gps;
    private TripAutoRecorder recorder;

    @Override public void onCreate() {
        super.onCreate();
        workerThread = new HandlerThread("DarbakTripRuntime");
        workerThread.start();
        worker = new Handler(workerThread.getLooper());

        File directory = TripStorageLocator.locate(this);
        if (directory != null) recorder = new TripAutoRecorder(directory);
        gps = new AndroidGpsSource(this, this);
        gps.start(workerThread.getLooper());
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (gps != null && !gps.started()) gps.start(workerThread.getLooper());
        return START_STICKY;
    }

    @Override public void onFix(final PositionFix fix) {
        PositionStore.get().publish(fix);
        if (recorder == null || worker == null) return;
        if (Looper.myLooper() == workerThread.getLooper()) {
            record(fix);
        } else {
            worker.post(() -> record(fix));
        }
    }

    @Override public void onUnavailable() {
        PositionStore.get().publishUnavailable();
    }

    @Override public void onDestroy() {
        if (gps != null) gps.stop();
        if (recorder != null) {
            try { recorder.close(); } catch (IOException ignored) { }
        }
        if (workerThread != null) workerThread.quitSafely();
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) { return null; }

    private void record(PositionFix fix) {
        try {
            recorder.accept(fix);
        } catch (IOException ignored) {
            // Position remains live even if trip storage temporarily fails.
        }
    }
}
