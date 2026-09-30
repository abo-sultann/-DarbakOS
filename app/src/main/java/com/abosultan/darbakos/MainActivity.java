package com.abosultan.darbakos;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.abosultan.darbakos.core.CoreStateStore;
import com.abosultan.darbakos.core.OsmAndBridge;
import com.abosultan.darbakos.core.OsmAndNavigationSnapshot;
import com.abosultan.darbakos.core.PositionFix;
import com.abosultan.darbakos.core.PositionStore;
import com.abosultan.darbakos.core.TripRuntimeService;

import java.util.Locale;

/** Darbak OS shell observing continuous GPS/trip runtime and the external OsmAnd engine. */
public final class MainActivity extends Activity {
    private static final String STATE_SECTION = "section";
    private static final int REQUEST_LOCATION = 40;
    private static final int REQUEST_OSMAND_INFO = 41;
    private static final long NAVIGATION_SNAPSHOT_FRESH_MS = 60_000L;

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
            runOnUiThread(() -> {
                showLiveSpeed(fix);
                if (section == 0) renderNavigationState();
                else if (section == 1) {
                    refreshMapLocationAction();
                    renderNavigationState();
                }
            });
        }

        @Override public void onUnavailable() {
            runOnUiThread(() -> {
                showSpeedUnavailable(R.string.gps_unavailable);
                if (section == 0) renderNavigationState();
                else if (section == 1) {
                    refreshMapLocationAction();
                    renderNavigationState();
                }
            });
        }
    };

    private int section;
    private boolean permissionRequested;
    private boolean refreshRouteWhenResumed;
    private boolean infoRequestInFlight;
    private boolean osmandLaunchable;
    private boolean osmandExternalApi;
    private OsmAndBridge osmandBridge;
    private OsmAndNavigationSnapshot navigationSnapshot = OsmAndNavigationSnapshot.unknown(0L);

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        osmandBridge = new OsmAndBridge(this);
        refreshOsmAndCapabilities();

        // PositionStore belongs to the process/runtime, not this Activity instance.
        if (state == null) CoreStateStore.get().resetForColdBoot();

        for (int i = 0; i < BUTTONS.length; i++) {
            final int destination = i;
            findViewById(BUTTONS[i]).setOnClickListener(v -> showSection(destination));
        }
        findViewById(R.id.back_home).setOnClickListener(v -> showSection(0));
        findViewById(R.id.map_back_home).setOnClickListener(v -> showSection(0));
        findViewById(R.id.quick_map).setOnClickListener(v -> showSection(1));
        findViewById(R.id.quick_media).setOnClickListener(v -> showSection(2));
        findViewById(R.id.quick_vehicle).setOnClickListener(v -> showSection(3));

        findViewById(R.id.map_open_button).setOnClickListener(v -> openOsmAnd());
        findViewById(R.id.map_location_button).setOnClickListener(v -> openCurrentLocation());
        findViewById(R.id.map_refresh_button).setOnClickListener(v -> requestNavigationInfo());
        findViewById(R.id.map_search_button).setOnClickListener(v -> searchDestination());
        ((EditText) findViewById(R.id.map_search_input)).setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchDestination();
                return true;
            }
            return false;
        });

        // Recreate restores only the visible shell; route state is refreshed from OsmAnd on demand.
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

    @Override protected void onResume() {
        super.onResume();
        refreshOsmAndCapabilities();
        if (section == 1) renderMapPanel();
        else if (section == 0) renderNavigationState();
        if (refreshRouteWhenResumed && !infoRequestInFlight) {
            refreshRouteWhenResumed = false;
            requestNavigationInfo();
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

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_OSMAND_INFO) return;
        infoRequestInFlight = false;
        if (resultCode == RESULT_OK) {
            navigationSnapshot = OsmAndBridge.parseNavigationInfo(
                    data, SystemClock.elapsedRealtime());
        } else {
            navigationSnapshot = OsmAndNavigationSnapshot.unknown(SystemClock.elapsedRealtime());
        }
        renderNavigationState();
        if (section == 1) refreshMapLocationAction();
    }

    private void startTripRuntime() {
        try {
            startService(new Intent(this, TripRuntimeService.class));
        } catch (RuntimeException ignored) {
            showSpeedUnavailable(R.string.gps_unavailable);
        }
    }

    private void openOsmAnd() {
        if (osmandBridge.open()) {
            refreshRouteWhenResumed = true;
            setMapFeedback("");
        } else {
            setMapFeedback(getString(R.string.map_osmand_required));
        }
    }

    private void openCurrentLocation() {
        PositionFix fix = PositionStore.get().available() ? PositionStore.get().latest() : null;
        if (fix == null) {
            setMapFeedback(getString(R.string.map_location_unavailable));
            return;
        }
        if (osmandBridge.openLocation(fix)) {
            refreshRouteWhenResumed = true;
            setMapFeedback("");
        } else {
            setMapFeedback(getString(R.string.map_osmand_required));
        }
    }

    private void searchDestination() {
        EditText search = (EditText) findViewById(R.id.map_search_input);
        String query = search.getText() == null ? "" : search.getText().toString().trim();
        if (query.length() == 0) {
            setMapFeedback(getString(R.string.map_search_empty));
            return;
        }
        PositionFix around = PositionStore.get().available() ? PositionStore.get().latest() : null;
        if (osmandBridge.openSearch(query, around)) {
            refreshRouteWhenResumed = true;
            setMapFeedback("");
        } else {
            setMapFeedback(getString(R.string.map_osmand_required));
        }
    }

    private void requestNavigationInfo() {
        if (infoRequestInFlight) return;
        Intent info = osmandBridge.navigationInfoIntent();
        if (info == null) {
            navigationSnapshot = OsmAndNavigationSnapshot.unknown(SystemClock.elapsedRealtime());
            renderNavigationState();
            if (section == 1) refreshMapLocationAction();
            return;
        }
        try {
            infoRequestInFlight = true;
            startActivityForResult(info, REQUEST_OSMAND_INFO);
        } catch (RuntimeException ignored) {
            infoRequestInFlight = false;
            navigationSnapshot = OsmAndNavigationSnapshot.unknown(SystemClock.elapsedRealtime());
            renderNavigationState();
            if (section == 1) refreshMapLocationAction();
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
        boolean map = section == 1;
        findViewById(R.id.home_panel).setVisibility(home ? View.VISIBLE : View.GONE);
        findViewById(R.id.map_panel).setVisibility(map ? View.VISIBLE : View.GONE);
        findViewById(R.id.section_panel).setVisibility(!home && !map ? View.VISIBLE : View.GONE);
        findViewById(R.id.apps_preview).setVisibility(section == 4 ? View.VISIBLE : View.GONE);

        if (!home && !map) {
            ((TextView) findViewById(R.id.section_title)).setText(TITLES[section]);
            ((TextView) findViewById(R.id.section_detail)).setText(DETAILS[section]);
        }
        if (map) {
            refreshOsmAndCapabilities();
            renderMapPanel();
        }
        if (home) renderNavigationState();

        for (int i = 0; i < BUTTONS.length; i++) {
            findViewById(BUTTONS[i]).setSelected(i == section);
        }
    }

    private void refreshOsmAndCapabilities() {
        osmandLaunchable = osmandBridge != null
                && osmandBridge.availability() == OsmAndBridge.Availability.LAUNCHABLE;
        osmandExternalApi = osmandLaunchable && osmandBridge.externalApiAvailable();
    }

    private void renderMapPanel() {
        ((TextView) findViewById(R.id.map_engine_state)).setText(
                !osmandLaunchable ? R.string.map_engine_missing
                        : osmandExternalApi ? R.string.map_engine_ready : R.string.map_engine_limited);

        findViewById(R.id.map_open_button).setEnabled(osmandLaunchable);
        findViewById(R.id.map_search_button).setEnabled(osmandLaunchable);
        findViewById(R.id.map_search_input).setEnabled(osmandLaunchable);
        findViewById(R.id.map_refresh_button).setEnabled(osmandExternalApi);
        refreshMapLocationAction();
        renderNavigationState();
        // Engine/route labels already explain capability. Reserve feedback for actual user actions.
        setMapFeedback("");
    }

    private void refreshMapLocationAction() {
        findViewById(R.id.map_location_button).setEnabled(
                osmandLaunchable && PositionStore.get().available()
                        && PositionStore.get().latest() != null);
    }

    private void renderNavigationState() {
        TextView homeInstruction = (TextView) findViewById(R.id.navigation_instruction);
        TextView homeDetail = (TextView) findViewById(R.id.navigation_eta);
        TextView homeState = (TextView) findViewById(R.id.navigation_state);
        TextView mapTitle = (TextView) findViewById(R.id.map_route_title);
        TextView mapDetail = (TextView) findViewById(R.id.map_route_detail);

        if (!osmandLaunchable) {
            homeInstruction.setText(R.string.map_route_unavailable);
            homeDetail.setText(R.string.map_osmand_required);
            homeState.setText(R.string.navigation_state);
            mapTitle.setText(R.string.map_route_unavailable);
            mapDetail.setText(R.string.map_osmand_required);
            return;
        }

        if (!osmandExternalApi) {
            homeInstruction.setText(R.string.map_route_unavailable);
            homeDetail.setText(R.string.map_route_limited_detail);
            homeState.setText(R.string.navigation_state);
            mapTitle.setText(R.string.map_route_unavailable);
            mapDetail.setText(R.string.map_route_limited_detail);
            return;
        }

        long now = SystemClock.elapsedRealtime();
        if (navigationSnapshot.state == OsmAndNavigationSnapshot.State.UNKNOWN
                || navigationSnapshot.stale(now, NAVIGATION_SNAPSHOT_FRESH_MS)) {
            homeInstruction.setText(R.string.map_route_unknown);
            homeDetail.setText(R.string.map_route_unknown_detail);
            homeState.setText(R.string.navigation_state_unknown);
            mapTitle.setText(R.string.map_route_unknown);
            mapDetail.setText(R.string.map_route_unknown_detail);
            return;
        }

        if (navigationSnapshot.state == OsmAndNavigationSnapshot.State.IDLE) {
            homeInstruction.setText(R.string.map_route_idle);
            homeDetail.setText(R.string.map_route_idle_detail);
            homeState.setText(R.string.navigation_state_ready);
            mapTitle.setText(R.string.map_route_idle);
            mapDetail.setText(R.string.map_route_idle_detail);
            return;
        }

        String title = navigationSnapshot.turnName.length() > 0
                ? navigationSnapshot.turnName : getString(R.string.map_route_active);
        String detail = formatRouteDetail(navigationSnapshot);
        homeInstruction.setText(title);
        homeDetail.setText(detail);
        homeState.setText(R.string.navigation_state_active);
        mapTitle.setText(title);
        mapDetail.setText(detail);
    }

    private String formatRouteDetail(OsmAndNavigationSnapshot snapshot) {
        StringBuilder value = new StringBuilder();
        if (snapshot.nextTurnDistanceMeters >= 0) {
            value.append("بعد ").append(formatDistance(snapshot.nextTurnDistanceMeters));
        }
        if (snapshot.distanceLeftMeters >= 0) {
            if (value.length() > 0) value.append(" • ");
            value.append("متبقي ").append(formatDistance(snapshot.distanceLeftMeters));
        }
        if (snapshot.timeLeftSeconds >= 0) {
            if (value.length() > 0) value.append(" • ");
            value.append(formatDuration(snapshot.timeLeftSeconds));
        }
        return value.length() == 0 ? getString(R.string.map_route_active) : value.toString();
    }

    private String formatDistance(int meters) {
        if (meters >= 1000) return String.format(Locale.US, "%.1f كم", meters / 1000f);
        return Math.max(0, meters) + " م";
    }

    private String formatDuration(int seconds) {
        int minutes = Math.max(1, (seconds + 59) / 60);
        if (minutes < 60) return minutes + " د";
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;
        return remainingMinutes == 0 ? hours + " س" : hours + " س " + remainingMinutes + " د";
    }

    private void setMapFeedback(String message) {
        TextView feedback = (TextView) findViewById(R.id.map_feedback);
        String value = message == null ? "" : message.trim();
        feedback.setText(value);
        feedback.setVisibility(value.length() == 0 ? View.GONE : View.VISIBLE);
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
