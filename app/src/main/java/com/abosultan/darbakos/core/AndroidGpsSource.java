package com.abosultan.darbakos.core;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Thin API25-compatible GPS adapter. Lifecycle is owned by the caller; no Service/thread/timer.
 * It publishes only real GPS fixes and never fabricates a location or speed.
 */
public final class AndroidGpsSource implements LocationListener {
    public interface Callback {
        void onFix(PositionFix fix);
        void onUnavailable();
    }

    private final Context context;
    private final LocationManager manager;
    private final Callback callback;
    private boolean started;

    public AndroidGpsSource(Context context, Callback callback) {
        this.context = context.getApplicationContext();
        this.manager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
        this.callback = callback;
    }

    public boolean hasPermission() {
        return context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public boolean start() {
        if (started) return true;
        if (manager == null || !hasPermission()) {
            unavailable();
            return false;
        }
        try {
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                unavailable();
                return false;
            }
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 0f, this);
            started = true;
            return true;
        } catch (SecurityException | IllegalArgumentException error) {
            unavailable();
            return false;
        }
    }

    public void stop() {
        if (manager != null && started) {
            try {
                manager.removeUpdates(this);
            } catch (SecurityException ignored) {
                // Permission may be revoked while running; stopping remains best-effort.
            }
        }
        started = false;
    }

    public boolean started() { return started; }

    @Override public void onLocationChanged(Location location) {
        if (location == null) return;
        long monotonicMs = location.getElapsedRealtimeNanos() / 1000000L;
        float accuracy = location.hasAccuracy() ? location.getAccuracy() : 0f;
        float speed = location.hasSpeed() ? location.getSpeed() : 0f;
        PositionFix fix = PositionFix.create(location.getLatitude(), location.getLongitude(),
                accuracy, speed, monotonicMs);
        if (fix != null && callback != null) callback.onFix(fix);
    }

    @Override public void onProviderDisabled(String provider) {
        if (LocationManager.GPS_PROVIDER.equals(provider)) unavailable();
    }

    @Override public void onProviderEnabled(String provider) { }
    @Override public void onStatusChanged(String provider, int status, Bundle extras) { }

    private void unavailable() {
        if (callback != null) callback.onUnavailable();
    }
}
