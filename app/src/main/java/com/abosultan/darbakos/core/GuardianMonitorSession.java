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
    private boolean generationExhausted;

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
        if (generationExhausted || expectedGeneration != generation) return false;
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
        GuardianSnapshot health = GuardianSnapshot.capture(guardian);
        java.util.EnumMap<GuardianRegistry.Component, GuardianHeartbeat> heartbeatCopy =
                new java.util.EnumMap<>(GuardianRegistry.Component.class);
        for (GuardianRegistry.Component component : GuardianRegistry.Component.values()) {
            GuardianHeartbeat heartbeat = heartbeats.snapshot(component);
            if (heartbeat != null) heartbeatCopy.put(component, heartbeat);
        }
        return new Snapshot(generation, health, heartbeatCopy, journal.snapshot());
    }

    public synchronized long generation() {
        return generation;
    }

    /**
     * Starts a fresh logical monitoring generation. Old heartbeats cannot leak across a
     * cold-start boundary, while retained Result/Event snapshots held by callers stay immutable.
     * If the last generation is exhausted, keep the counter saturated and reject further
     * heartbeats: reusing its token would admit delayed input from before this reset.
     */
    public synchronized void resetForColdBoot() {
        heartbeats.reset();
        journal.clear();
        if (guardian != null) guardian.resetForColdBoot();
        if (generation == Long.MAX_VALUE) generationExhausted = true;
        else generation++;
    }

    public static final class Snapshot {
        public final long generation;
        public final GuardianSnapshot health;
        private final java.util.EnumMap<GuardianRegistry.Component, GuardianHeartbeat> heartbeats;
        public final java.util.List<GuardianEvent> events;

        private Snapshot(long generation, GuardianSnapshot health,
                         java.util.EnumMap<GuardianRegistry.Component, GuardianHeartbeat> heartbeats,
                         java.util.List<GuardianEvent> events) {
            this.generation = generation;
            this.health = health;
            this.heartbeats = heartbeats;
            this.events = events;
        }

        public GuardianHeartbeat heartbeat(GuardianRegistry.Component component) {
            return component == null ? null : heartbeats.get(component);
        }
    }
}
