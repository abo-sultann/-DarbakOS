package com.abosultan.darbakos.core;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** Compact persistent local-media manifest. Restoring it never starts playback. */
public final class LocalMediaIndex {
    private static final String PREFS = "darbak_local_media";
    private static final String KEY_MANIFEST = "track_manifest";
    private final SharedPreferences prefs;

    public LocalMediaIndex(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void save(List<LocalMediaTrack> tracks) {
        JSONArray array = new JSONArray();
        if (tracks != null) for (LocalMediaTrack track : tracks) {
            if (track == null || track.file == null) continue;
            try {
                JSONObject item = new JSONObject();
                item.put("p", track.file.getAbsolutePath());
                item.put("t", track.title);
                item.put("a", track.artist);
                item.put("s", track.size);
                item.put("m", track.modified);
                array.put(item);
            } catch (Exception ignored) {}
        }
        prefs.edit().putString(KEY_MANIFEST, array.toString()).apply();
    }

    public List<LocalMediaTrack> restoreValid() {
        ArrayList<LocalMediaTrack> tracks = new ArrayList<>();
        String raw = prefs.getString(KEY_MANIFEST, "");
        if (raw.isEmpty()) return tracks;
        try {
            JSONArray array = new JSONArray(raw);
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.optJSONObject(i);
                if (item == null) continue;
                File file = new File(item.optString("p", ""));
                long size = item.optLong("s", -1L);
                long modified = item.optLong("m", -1L);
                if (!file.isFile() || file.length() != size || file.lastModified() != modified) continue;
                tracks.add(new LocalMediaTrack(file, item.optString("t", ""),
                        item.optString("a", ""), size, modified));
            }
        } catch (Exception ignored) {}
        return tracks;
    }

    public void invalidate() { prefs.edit().remove(KEY_MANIFEST).apply(); }
}
