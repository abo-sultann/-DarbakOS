package com.abosultan.darbakos;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.abosultan.darbakos.core.CoreStateStore;
import com.abosultan.darbakos.core.PositionFix;
import com.abosultan.darbakos.core.PositionStore;
import com.abosultan.darbakos.core.TripRuntimeService;

/** Darbak OS shell observing the continuous P4 GPS/trip runtime. */
public final class MainActivity extends Activity {
    private static final String STATE_SECTION = "section";
    private static final int REQUEST_LOCATION = 40;
    private static final int[] BUTTONS = {
        R.id.nav_home, R.id.nav_map, R.id.nav_media, R.id.nav_vehicle, R.id.nav_apps,
        R.id.settings_button
    };
    private static final int[] TITLES = {
        R.string.home, R.string.map, R.string.media, R.string.vehicle, R.string.apps,
        R.string.settings
    };
    private static final int[] DETAILS = {
        R.string.placeholder_note, R.string.map_detail, R.string.media_detail,
        R.string.vehicle_detail, R.string.apps_detail, R.string.settings_detail
    };

    private final PositionStore.Listener positionListener = new PositionStore.Listener() {
        @Override public void onPosition(final PositionFix fix) {
            runOnUiThread(() -> showLiveSpeed(fix));
        }
        @Override public void onUnavailable() {
            runOnUiThread(() -> showSpeedUnavailable(R.string.gps_unavailable));
        }
    };
    private int section;
    private boolean permissionRequested;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        // PositionStore belongs to the process/runtime, not this Activity instance.
        // Its new-process state is already unavailable until a real source publishes.
        if (state == null) {
            CoreStateStore.get().resetForColdBoot();
        }
        for (int i = 0; i < BUTTONS.length; i++) {
            final int destination = i;
            findViewById(BUTTONS[i]).setOnClickListener(v -> showSection(destination));
        }
        findViewById(R.id.back_home).setOnClickListener(v -> showSection(0));
        findViewById(R.id.quick_map).setOnClickListener(v -> showSection(1));
        findViewById(R.id.quick_media).setOnClickListener(v -> showSection(2));
        findViewById(R.id.quick_vehicle).setOnClickListener(v -> showSection(3));
        // Recreate restores only the visible shell; a fresh process launch starts at Home.
        showSection(state == null ? 0 : state.getInt(STATE_SECTION, 0));
        enterFullscreen();
    }

    @Override protected void onStart() {
        super.onStart();
        PositionStore.get().addListener(positionListener);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            startTripRuntime();
        } else {
            showSpeedUnavailable(R.string.gps_permission_needed);
            if (!permissionRequested) {
                permissionRequested = true;
                requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                        REQUEST_LOCATION);
            }
        }
    }

    @Override protected void onStop() {
        PositionStore.get().removeListener(positionListener);
        super.onStop();
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                      int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_LOCATION) return;
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showSpeedUnavailable(R.string.gps_waiting);
            startTripRuntime();
        } else {
            showSpeedUnavailable(R.string.gps_permission_needed);
        }
    }

    private void startTripRuntime() {
        try {
            startService(new Intent(this, TripRuntimeService.class));
        } catch (RuntimeException ignored) {
            showSpeedUnavailable(R.string.gps_unavailable);
        }
    }

    private void showLiveSpeed(PositionFix fix) {
        TextView speed = (TextView) findViewById(R.id.speed_value);
        speed.setText(String.valueOf(fix.speedKmh()));
        speed.setContentDescription(getString(R.string.speed) + " " + fix.speedKmh()
                + " " + getString(R.string.kmh));
        ((TextView) findViewById(R.id.speed_source)).setText(R.string.gps_live);
    }

    private void showSpeedUnavailable(int statusText) {
        TextView speed = (TextView) findViewById(R.id.speed_value);
        speed.setText(R.string.speed_empty);
        speed.setContentDescription(getString(R.string.speed_accessibility));
        ((TextView) findViewById(R.id.speed_source)).setText(statusText);
    }

    private void showSection(int destination) {
        section = destination >= 0 && destination < TITLES.length ? destination : 0;
        boolean home = section == 0;
        findViewById(R.id.home_panel).setVisibility(home ? View.VISIBLE : View.GONE);
        findViewById(R.id.section_panel).setVisibility(home ? View.GONE : View.VISIBLE);
        findViewById(R.id.apps_preview).setVisibility(section == 4 ? View.VISIBLE : View.GONE);
        ((TextView) findViewById(R.id.section_title)).setText(TITLES[section]);
        ((TextView) findViewById(R.id.section_detail)).setText(DETAILS[section]);
        for (int i = 0; i < BUTTONS.length; i++) {
            findViewById(BUTTONS[i]).setSelected(i == section);
        }
    }

    @Override protected void onSaveInstanceState(Bundle state) {
        state.putInt(STATE_SECTION, section);
        super.onSaveInstanceState(state);
    }

    @Override public void onBackPressed() {
        if (section != 0) showSection(0);
        else super.onBackPressed();
    }

    @Override public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) enterFullscreen();
    }

    private void enterFullscreen() {
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }
}
