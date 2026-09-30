package com.abosultan.darbakos.core;

import java.io.File;
import java.io.IOException;
import java.util.List;

/** Write-first, commit-second bridge between the in-memory trip buffer and persistent chunks. */
public final class TripRecorderStore {
    private final TripChunkWriter writer;

    public TripRecorderStore(TripChunkWriter writer) {
        if (writer == null) throw new IllegalArgumentException("writer");
        this.writer = writer;
    }

    public File flush(TripRecorderBuffer buffer, String sessionId, long chunkIndex) throws IOException {
        if (buffer == null) throw new IllegalArgumentException("buffer");
        List<TripPoint> snapshot = buffer.snapshot();
        if (snapshot.isEmpty()) return null;
        File committed = writer.write(sessionId, chunkIndex, snapshot);
        if (!buffer.commitPrefix(snapshot.size())) {
            throw new IllegalStateException("Trip buffer changed incompatibly after persistent write");
        }
        return committed;
    }
}
