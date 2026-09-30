package com.abosultan.darbakos.core;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/** Power-safe trip chunk writer: complete chunks are renamed from .part only after sync/close. */
public final class TripChunkWriter {
    private static final int MAGIC = 0x44545250; // DTRP
    private static final int VERSION = 1;
    private static final int END = 0x454E4421; // END!
    private static final Object FILE_LOCK = new Object();

    private final File directory;

    public TripChunkWriter(File directory) {
        if (directory == null) throw new IllegalArgumentException("directory");
        this.directory = directory;
    }

    public File directory() { return directory; }

    public File write(String sessionId, long chunkIndex, List<TripPoint> points) throws IOException {
        String safeSession = sanitize(sessionId);
        if (chunkIndex < 0L) throw new IllegalArgumentException("chunkIndex");
        validate(points);
        synchronized (FILE_LOCK) {
            ensureDirectory(directory);
            File complete = uniqueFile(directory,
                    "trip_" + safeSession + "_" + chunkIndex, ".dtrip");
            File partial = uniqueFile(directory, complete.getName(), ".part");
            writePartial(partial, safeSession, chunkIndex, points);
            // The process-wide lock keeps another Darbak writer from claiming this final path.
            if (complete.exists() || !partial.renameTo(complete)) {
                throw new IOException("Could not commit trip chunk: " + partial.getName());
            }
            return complete;
        }
    }

    private static void writePartial(File partial, String sessionId, long chunkIndex,
                                     List<TripPoint> points) throws IOException {
        FileOutputStream raw = new FileOutputStream(partial, false);
        boolean closed = false;
        try {
            DataOutputStream out = new DataOutputStream(raw);
            out.writeInt(MAGIC);
            out.writeInt(VERSION);
            out.writeUTF(sessionId);
            out.writeLong(chunkIndex);
            out.writeInt(points.size());
            for (TripPoint point : points) {
                PositionFix fix = point.position;
                out.writeLong(point.sequence);
                out.writeLong(fix.monotonicMs);
                out.writeDouble(fix.latitude);
                out.writeDouble(fix.longitude);
                out.writeFloat(fix.accuracyMeters);
                out.writeFloat(fix.speedMetersPerSecond);
            }
            out.writeInt(END);
            out.flush();
            raw.getFD().sync();
            out.close();
            closed = true;
        } finally {
            if (!closed) {
                try { raw.close(); } catch (IOException ignored) { }
            }
        }
    }

    public static boolean isCompleteFile(File file) {
        return file != null && file.isFile() && file.getName().endsWith(".dtrip");
    }

    public static boolean isPartialFile(File file) {
        return file != null && file.getName().endsWith(".part");
    }

    private static void validate(List<TripPoint> points) {
        if (points == null || points.isEmpty()) throw new IllegalArgumentException("points");
        long lastSequence = -1L;
        long lastTime = -1L;
        for (TripPoint point : points) {
            if (point == null || point.position == null) throw new IllegalArgumentException("point");
            if (point.sequence <= lastSequence || point.position.monotonicMs <= lastTime) {
                throw new IllegalArgumentException("unordered points");
            }
            lastSequence = point.sequence;
            lastTime = point.position.monotonicMs;
        }
    }

    private static String sanitize(String value) {
        if (value == null) return "session";
        String safe = value.replaceAll("[^A-Za-z0-9_-]", "_");
        if (safe.length() == 0) return "session";
        return safe.length() > 48 ? safe.substring(0, 48) : safe;
    }

    private static void ensureDirectory(File dir) throws IOException {
        if (!dir.exists() && !dir.mkdirs()) throw new IOException("Could not create trip directory");
        if (!dir.isDirectory() || !dir.canWrite()) throw new IOException("Trip directory not writable");
    }

    private static File uniqueFile(File dir, String base, String suffix) {
        File candidate = new File(dir, base + suffix);
        int attempt = 1;
        while (candidate.exists()) {
            candidate = new File(dir, base + "-" + attempt++ + suffix);
        }
        return candidate;
    }
}
