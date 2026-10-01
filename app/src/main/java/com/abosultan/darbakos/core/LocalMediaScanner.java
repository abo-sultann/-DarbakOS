package com.abosultan.darbakos.core;

import android.media.MediaMetadataRetriever;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Bounded iterative audio discovery for removable/local storage; never runs on the UI thread. */
public final class LocalMediaScanner {
    private static final int MAX_FILES = 5000;
    private static final int MAX_DIRS = 1500;

    public List<LocalMediaTrack> scan(List<File> roots) {
        ArrayList<LocalMediaTrack> out = new ArrayList<>();
        ArrayDeque<File> dirs = new ArrayDeque<>();
        if (roots != null) for (File root : roots) if (root != null && root.isDirectory()) dirs.add(root);
        int visitedDirs = 0;
        while (!dirs.isEmpty() && out.size() < MAX_FILES && visitedDirs < MAX_DIRS) {
            File dir = dirs.removeFirst();
            visitedDirs++;
            File[] children = safeList(dir);
            for (File child : children) {
                if (child.isDirectory()) dirs.addLast(child);
                else if (isAudio(child)) out.add(readTrack(child));
                if (out.size() >= MAX_FILES) break;
            }
        }
        Collections.sort(out, Comparator.comparing(t -> t.title.toLowerCase(Locale.ROOT)));
        return Collections.unmodifiableList(out);
    }

    private static File[] safeList(File dir) {
        try { File[] files = dir.listFiles(); return files == null ? new File[0] : files; }
        catch (SecurityException ignored) { return new File[0]; }
    }

    private static boolean isAudio(File file) {
        String n = file.getName().toLowerCase(Locale.ROOT);
        return n.endsWith(".mp3") || n.endsWith(".m4a") || n.endsWith(".aac")
                || n.endsWith(".ogg") || n.endsWith(".wav") || n.endsWith(".flac");
    }

    private static LocalMediaTrack readTrack(File file) {
        String title = stripExtension(file.getName());
        String artist = "";
        MediaMetadataRetriever r = new MediaMetadataRetriever();
        try {
            r.setDataSource(file.getAbsolutePath());
            String t = r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String a = r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            if (t != null && !t.trim().isEmpty()) title = t;
            if (a != null) artist = a;
        } catch (RuntimeException ignored) {
            // Filename remains truthful fallback for damaged/unsupported metadata.
        } finally {
            try { r.release(); } catch (RuntimeException ignored) {}
        }
        return new LocalMediaTrack(file, title, artist, file.length(), file.lastModified());
    }

    private static String stripExtension(String name) {
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }
}
