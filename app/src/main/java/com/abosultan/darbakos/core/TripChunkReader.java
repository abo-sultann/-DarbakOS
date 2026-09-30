package com.abosultan.darbakos.core;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Strict reader for committed Darbak trip chunks; partial files are never accepted as history. */
public final class TripChunkReader {
    private static final int MAGIC = 0x44545250;
    private static final int VERSION = 1;
    private static final int END = 0x454E4421;
    private static final int MAX_POINTS_PER_CHUNK = 100000;

    public static final class Chunk {
        public final String sessionId;
        public final long chunkIndex;
        public final List<TripPoint> points;

        private Chunk(String sessionId, long chunkIndex, List<TripPoint> points) {
            this.sessionId = sessionId;
            this.chunkIndex = chunkIndex;
            this.points = Collections.unmodifiableList(points);
        }
    }

    public Chunk read(File file) throws IOException {
        if (!TripChunkWriter.isCompleteFile(file)) throw new IOException("Not a committed trip chunk");
        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
        try {
            if (in.readInt() != MAGIC) throw new IOException("Invalid trip magic");
            if (in.readInt() != VERSION) throw new IOException("Unsupported trip version");
            String sessionId = in.readUTF();
            long chunkIndex = in.readLong();
            int count = in.readInt();
            if (chunkIndex < 0L || count <= 0 || count > MAX_POINTS_PER_CHUNK) {
                throw new IOException("Invalid trip header");
            }
            ArrayList<TripPoint> points = new ArrayList<>(count);
            long lastSequence = -1L;
            long lastTime = -1L;
            for (int i = 0; i < count; i++) {
                long sequence = in.readLong();
                long monotonicMs = in.readLong();
                double latitude = in.readDouble();
                double longitude = in.readDouble();
                float accuracy = in.readFloat();
                float speed = in.readFloat();
                PositionFix fix = PositionFix.create(latitude, longitude, accuracy, speed, monotonicMs);
                if (fix == null || sequence <= lastSequence || monotonicMs <= lastTime) {
                    throw new IOException("Invalid trip point order/value");
                }
                points.add(new TripPoint(fix, sequence));
                lastSequence = sequence;
                lastTime = monotonicMs;
            }
            if (in.readInt() != END) throw new IOException("Trip chunk missing end marker");
            if (in.read() != -1) throw new IOException("Unexpected trailing trip data");
            return new Chunk(sessionId, chunkIndex, points);
        } catch (EOFException truncated) {
            throw new IOException("Truncated trip chunk", truncated);
        } finally {
            in.close();
        }
    }
}
