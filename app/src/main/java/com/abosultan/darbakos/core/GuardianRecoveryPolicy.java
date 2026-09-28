package com.abosultan.darbakos.core;

import java.util.EnumSet;

/**
 * Pure staged recovery recommendation for a frozen Guardian assessment.
 * It never executes recovery, schedules work, writes state or touches Android services.
 */
public final class GuardianRecoveryPolicy {
    public enum Step {
        NONE,
        DIAGNOSE,
        LIGHT_REPAIR,
        RESTART_COMPONENT,
        FALLBACK_STABLE,
        SAFE_MODE
    }

    public static final class Plan {
        public final Step step;
        private final EnumSet<GuardianRegistry.Component> targets;

        private Plan(Step step, EnumSet<GuardianRegistry.Component> targets) {
            this.step = step;
            this.targets = targets.clone();
        }

        public int targetCount() { return targets.size(); }

        public boolean targets(GuardianRegistry.Component component) {
            return component != null && targets.contains(component);
        }
    }

    private GuardianRecoveryPolicy() {}

    public static Plan recommend(GuardianAssessment assessment, int escalationLevel) {
        GuardianAssessment safe = assessment == null ? GuardianAssessment.from(null) : assessment;
        int level = Math.max(0, escalationLevel);

        if (safe.overall == GuardianState.Health.HEALTHY) {
            return new Plan(Step.NONE, EnumSet.noneOf(GuardianRegistry.Component.class));
        }

        if (safe.overall == GuardianState.Health.FAILED) {
            EnumSet<GuardianRegistry.Component> failed = members(safe, GuardianState.Health.FAILED);
            Step step;
            if (level == 0) step = Step.DIAGNOSE;
            else if (level == 1) step = Step.LIGHT_REPAIR;
            else if (level == 2) step = Step.RESTART_COMPONENT;
            else if (level == 3) step = Step.FALLBACK_STABLE;
            else step = Step.SAFE_MODE;
            return new Plan(step, failed);
        }

        if (safe.overall == GuardianState.Health.DEGRADED) {
            EnumSet<GuardianRegistry.Component> degraded = members(safe, GuardianState.Health.DEGRADED);
            return new Plan(level == 0 ? Step.DIAGNOSE : Step.LIGHT_REPAIR, degraded);
        }

        return new Plan(Step.DIAGNOSE, members(safe, GuardianState.Health.UNKNOWN));
    }

    private static EnumSet<GuardianRegistry.Component> members(GuardianAssessment assessment,
                                                                GuardianState.Health health) {
        EnumSet<GuardianRegistry.Component> result = EnumSet.noneOf(GuardianRegistry.Component.class);
        for (GuardianRegistry.Component component : GuardianRegistry.Component.values()) {
            boolean match;
            switch (health) {
                case HEALTHY: match = assessment.isHealthy(component); break;
                case DEGRADED: match = assessment.isDegraded(component); break;
                case FAILED: match = assessment.isFailed(component); break;
                default: match = assessment.isUnknown(component); break;
            }
            if (match) result.add(component);
        }
        return result;
    }
}
