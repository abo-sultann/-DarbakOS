package com.abosultan.darbakos.core;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Tiny persistent index signature. Avoids rescanning unchanged storage on every Darbak start. */
public final class LocalMediaIndex {
    private static final String PREFS = "darbak_local_media";
    private static final String KEY_SIGNATURE = "roots_signature";

    private final SharedPreferences prefs;

    public LocalMediaIndex(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public String signature(List<File> roots) {
        ArrayList<String> parts = new ArrayList<>();
        if (roots != null) for (File root : roots) {
            if (root == null) continue;
            parts.add(root.getAbsolutePath() + "|" + root.exists() + "|" + root.lastModified());
        }
        Collections.sort(parts);
        StringBuilder b = new StringBuilder();
        for (String part : parts) b.append(part).append('\n');
        return b.toString();
    }

    public boolean rootsChanged(List<File> roots) {
        return !signature(roots).equals(prefs.getString(KEY_SIGNATURE, ""));
    }

    public void markScanned(List<File> roots) {
        prefs.edit().putString(KEY_SIGNATURE, signature(roots)).apply();
    }

    public void invalidate() {
        prefs.edit().remove(KEY_SIGNATURE).apply();
    }
}
