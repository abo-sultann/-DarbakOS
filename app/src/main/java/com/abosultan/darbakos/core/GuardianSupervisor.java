package com.abosultan.darbakos.core;

/**
 * Passive Guardian orchestration step.
 *
 * One caller-driven evaluation captures the registry, derives its assessment and asks the
 * recovery policy for a recommendation. This class owns no thread, timer, service, listener,
 * persistence or recovery executor; callers decide when to evaluate and whether to act.
 */
public final class GuardianSupervisor {
    private GuardianSupervisor() {}

    public static Result evaluate(GuardianRegistry registry, int escalationLevel) {
        GuardianSnapshot snapshot = GuardianSnapshot.capture(registry);
        GuardianAssessment assessment = GuardianAssessment.from(snapshot);
        GuardianRecoveryPolicy.Plan plan =
                GuardianRecoveryPolicy.recommend(assessment, escalationLevel);
        return new Result(snapshot, assessment, plan);
    }

    /** Immutable result from one internally consistent supervisor evaluation. */
    public static final class Result {
        public final GuardianSnapshot snapshot;
        public final GuardianAssessment assessment;
        public final GuardianRecoveryPolicy.Plan plan;

        private Result(GuardianSnapshot snapshot,
                       GuardianAssessment assessment,
                       GuardianRecoveryPolicy.Plan plan) {
            this.snapshot = snapshot;
            this.assessment = assessment;
            this.plan = plan;
        }
    }
}
