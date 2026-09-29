package com.abosultan.darbakos.core;

/**
 * Lightweight owner for passive Guardian monitoring state.
 * Components explicitly emit heartbeats; callers explicitly request evaluation.
 * No scheduler, Service, clock read, persistence or recovery execution is owned here.
 */
public final class GuardianMonitorSession {
    private final GuardianHeartbeatRegistry heartbeats = new GuardianHeartbeatRegistry();
    private final GuardianEventJournal journal;
    private final GuardianRegistry guardian;
    private final GuardianMonitorConfig config;
    private long generation;

    public GuardianMonitorSession(GuardianRegistry guardian,
                                  GuardianMonitorConfig config,
                                  int journalCapacity) {
        this.guardian = guardian;
        this.config = config == null ? GuardianMonitorConfig.conservativeDefault() : config;
        this.journal = new GuardianEventJournal(journalCapacity);
    }

    public synchronized boolean heartbeat(GuardianRegistry.Component component,
                                          long sequence,
                                          long monotonicMs) {
        return heartbeat(generation, component, sequence, monotonicMs);
    }

    /** Rejects a delayed heartbeat that belongs to an older cold-boot generation. */
    public synchronized boolean heartbeat(long expectedGeneration,
                                          GuardianRegistry.Component component,
                                          long sequence,
                                          long monotonicMs) {
        if (expectedGeneration != generation) return false;
        return heartbeats.update(new GuardianHeartbeat(component, sequence, monotonicMs));
    }

    public synchronized GuardianMonitorStep.Result evaluate(long nowMonotonicMs,
                                                            int escalationLevel) {
        return GuardianMonitorStep.run(
                heartbeats, guardian, journal, nowMonotonicMs, config, escalationLevel);
    }

    public GuardianEventJournal journal() {
        return journal;
    }

    public GuardianMonitorConfig config() {
        return config;
    }

    /** Immutable point-in-time view for diagnostics; does not evaluate or mutate health. */
    public synchronized Snapshot snapshot() {
        return new Snapshot(generation, journal.snapshot());
    }

    public synchronized long generation() {
        return generation;
    }

    /**
     * Starts a fresh logical monitoring generation. Old heartbeats cannot leak across a
     * cold-start boundary, while retained Result/Event snapshots held by callers stay immutable.
     */
    public synchronized void resetForColdBoot() {
        heartbeats.reset();
        journal.clear();
        if (guardian != null) guardian.resetForColdBoot();
        if (generation != Long.MAX_VALUE) generation++;
    }

    public static final class Snapshot {
        public final long generation;
        public final java.util.List<GuardianEvent> events;

        private Snapshot(long generation, java.util.List<GuardianEvent> events) {
            this.generation = generation;
            this.events = events;
        }
    }
}
