package com.abosultan.darbakos.core;

import java.io.File;

/** Immutable local audio entry. File identity is kept separate from display metadata. */
public final class LocalMediaTrack {
    public final File file;
    public final String title;
    public final String artist;
    public final long size;
    public final long modified;

    public LocalMediaTrack(File file, String title, String artist, long size, long modified) {
        this.file = file;
        this.title = title == null || title.trim().isEmpty() ? file.getName() : title.trim();
        this.artist = artist == null ? "" : artist.trim();
        this.size = size;
        this.modified = modified;
    }
}
