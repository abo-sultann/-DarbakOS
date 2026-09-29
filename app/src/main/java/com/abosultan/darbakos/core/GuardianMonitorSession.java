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

    public synchronized void resetForColdBoot() {
        heartbeats.reset();
        journal.clear();
        if (guardian != null) guardian.resetForColdBoot();
    }
}
