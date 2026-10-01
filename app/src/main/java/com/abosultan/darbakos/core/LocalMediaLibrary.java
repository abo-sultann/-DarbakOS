package com.abosultan.darbakos.core;

import android.os.Handler;
import android.os.HandlerThread;

import java.io.File;
import java.util.List;

/** Owns the single bounded background worker used for local-media discovery. */
public final class LocalMediaLibrary {
    public interface Callback { void onScanned(List<LocalMediaTrack> tracks); }
    private final HandlerThread worker = new HandlerThread("DarbakLocalMedia");
    private Handler handler;

    public void start() {
        if (handler != null) return;
        worker.start();
        handler = new Handler(worker.getLooper());
    }

    public void scan(List<File> roots, Callback callback) {
        start();
        handler.post(() -> callback.onScanned(new LocalMediaScanner().scan(roots)));
    }

    public void close() {
        if (handler == null) return;
        handler.removeCallbacksAndMessages(null);
        worker.quitSafely();
        handler = null;
    }
}
