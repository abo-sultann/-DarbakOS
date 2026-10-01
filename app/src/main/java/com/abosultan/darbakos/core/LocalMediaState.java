package com.abosultan.darbakos.core;

import android.content.Context;
import android.content.SharedPreferences;

/** Persists selection only. Restoring state never starts playback. */
public final class LocalMediaState {
    private static final String PREFS = "darbak_local_media";
    private static final String KEY_PATH = "selected_path";
    private final SharedPreferences prefs;

    public LocalMediaState(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void remember(LocalMediaTrack track) {
        if (track == null || track.file == null) prefs.edit().remove(KEY_PATH).apply();
        else prefs.edit().putString(KEY_PATH, track.file.getAbsolutePath()).apply();
    }

    public String selectedPath() { return prefs.getString(KEY_PATH, ""); }

    public int restoreSelection(LocalMediaQueue queue) {
        String wanted = selectedPath();
        if (wanted.isEmpty() || queue == null) return -1;
        for (int i = 0; i < queue.tracks().size(); i++) {
            LocalMediaTrack track = queue.tracks().get(i);
            if (track.file != null && wanted.equals(track.file.getAbsolutePath())) {
                queue.select(i); return i;
            }
        }
        return -1;
    }
}
