package com.abosultan.darbakos.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Lightweight OsmAnd boundary for P4. It detects official package variants and uses only
 * standard Android intents. No OsmAnd SDK/AIDL classes, background work or Position ownership.
 */
public final class OsmAndBridge {
    public static final String AIDL_SERVICE_ACTION = "net.osmand.aidl.OsmandAidlService";

    public enum Availability { UNAVAILABLE, LAUNCHABLE }

    private final Context context;
    private final PackageManager packages;

    public OsmAndBridge(Context context) {
        if (context == null) throw new IllegalArgumentException("context");
        this.context = context.getApplicationContext();
        this.packages = this.context.getPackageManager();
    }

    public Availability availability() {
        return resolvePackage() == null ? Availability.UNAVAILABLE : Availability.LAUNCHABLE;
    }

    public String resolvedPackage() {
        return resolvePackage();
    }

    /** Detects the official exported OsmAnd AIDL service without binding or copying its API. */
    public boolean aidlServiceAvailable() {
        String packageName = resolvePackage();
        if (packageName == null) return false;
        Intent intent = new Intent(AIDL_SERVICE_ACTION);
        intent.setPackage(packageName);
        try {
            List<ResolveInfo> services = packages.queryIntentServices(
                    intent, PackageManager.MATCH_DEFAULT_ONLY);
            return services != null && !services.isEmpty();
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    /** Opens OsmAnd's normal launcher Activity only when one of the known variants resolves. */
    public boolean open() {
        String packageName = resolvePackage();
        if (packageName == null) return false;
        Intent launch = packages.getLaunchIntentForPackage(packageName);
        return startSafely(launch);
    }

    /** Opens a real validated location in OsmAnd; falls back to its launcher if geo is unsupported. */
    public boolean openLocation(PositionFix fix) {
        if (fix == null) return false;
        String packageName = resolvePackage();
        if (packageName == null) return false;
        Intent geo = new Intent(Intent.ACTION_VIEW, geoUri(fix.latitude, fix.longitude));
        geo.setPackage(packageName);
        if (geo.resolveActivity(packages) != null && startSafely(geo)) return true;
        return open();
    }

    static Uri geoUri(double latitude, double longitude) {
        String point = String.format(Locale.US, "%.6f,%.6f", latitude, longitude);
        return Uri.parse("geo:" + point + "?q=" + point);
    }

    private String resolvePackage() {
        Set<String> launchable = new HashSet<>();
        for (String candidate : OsmAndPackages.ordered()) {
            try {
                Intent launch = packages.getLaunchIntentForPackage(candidate);
                if (launch != null && launch.resolveActivity(packages) != null) {
                    launchable.add(candidate);
                }
            } catch (RuntimeException ignored) {
                // Broken/partially installed packages remain unavailable instead of crashing Darbak.
            }
        }
        return OsmAndPackages.select(launchable);
    }

    private boolean startSafely(Intent intent) {
        if (intent == null) return false;
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
            return true;
        } catch (RuntimeException ignored) {
            return false;
        }
    }
}
