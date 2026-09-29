package com.abosultan.darbakos.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Fixed-capacity in-memory Guardian event journal.
 * Caller-driven only: no disk, thread, timer, listener or background work.
 */
public final class GuardianEventJournal {
    public static final int DEFAULT_CAPACITY = 64;

    private final GuardianEvent[] entries;
    private int start;
    private int size;

    public GuardianEventJournal() {
        this(DEFAULT_CAPACITY);
    }

    public GuardianEventJournal(int capacity) {
        entries = new GuardianEvent[Math.max(1, capacity)];
    }

    public synchronized int capacity() { return entries.length; }
    public synchronized int size() { return size; }

    public synchronized void append(GuardianEvent event) {
        if (event == null) return;
        int index = (start + size) % entries.length;
        if (size == entries.length) {
            entries[start] = event;
            start = (start + 1) % entries.length;
        } else {
            entries[index] = event;
            size++;
        }
    }

    public synchronized List<GuardianEvent> snapshot() {
        ArrayList<GuardianEvent> copy = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            copy.add(entries[(start + i) % entries.length]);
        }
        return Collections.unmodifiableList(copy);
    }

    public synchronized void clear() {
        for (int i = 0; i < entries.length; i++) entries[i] = null;
        start = 0;
        size = 0;
    }
}
