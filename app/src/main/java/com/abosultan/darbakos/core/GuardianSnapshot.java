package com.abosultan.darbakos.core;

/**
 * Immutable point-in-time Guardian view for future diagnostics/reporting.
 * Captures only current in-process health state; performs no monitoring, I/O or recovery action.
 */
public final class GuardianSnapshot {
    public final GuardianState core;
    public final GuardianState home;
    public final GuardianState navigation;
    public final GuardianState media;
    public final GuardianState vehicle;
    public final GuardianState.Health overall;

    private GuardianSnapshot(GuardianState core, GuardianState home,
                             GuardianState navigation, GuardianState media,
                             GuardianState vehicle, GuardianState.Health overall) {
        this.core = core;
        this.home = home;
        this.navigation = navigation;
        this.media = media;
        this.vehicle = vehicle;
        this.overall = overall;
    }

    public static GuardianSnapshot capture(GuardianRegistry registry) {
        if (registry == null) {
            GuardianState unknown = GuardianState.coldBoot();
            return new GuardianSnapshot(unknown, unknown, unknown, unknown, unknown,
                    GuardianState.Health.UNKNOWN);
        }
        GuardianState core = registry.snapshot(GuardianRegistry.Component.CORE);
        GuardianState home = registry.snapshot(GuardianRegistry.Component.HOME);
        GuardianState navigation = registry.snapshot(GuardianRegistry.Component.NAVIGATION);
        GuardianState media = registry.snapshot(GuardianRegistry.Component.MEDIA);
        GuardianState vehicle = registry.snapshot(GuardianRegistry.Component.VEHICLE);
        return new GuardianSnapshot(core, home, navigation, media, vehicle,
                GuardianPolicy.aggregate(registry));
    }
}
