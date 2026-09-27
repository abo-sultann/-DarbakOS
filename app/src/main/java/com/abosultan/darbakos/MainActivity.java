package com.abosultan.darbakos;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/** P1 shell only. No services, sensors, media playback or external app launches. */
public final class MainActivity extends Activity {
    private static final String STATE_SECTION = "section";
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
    private int section;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        for (int i = 0; i < BUTTONS.length; i++) {
            final int destination = i;
            findViewById(BUTTONS[i]).setOnClickListener(v -> showSection(destination));
        }
        findViewById(R.id.back_home).setOnClickListener(v -> showSection(0));
        // Recreate restores only the visible shell; a fresh process launch starts at Home.
        showSection(state == null ? 0 : state.getInt(STATE_SECTION, 0));
        enterFullscreen();
    }

    private void showSection(int destination) {
        section = destination >= 0 && destination < TITLES.length ? destination : 0;
        boolean home = section == 0;
        findViewById(R.id.home_panel).setVisibility(home ? View.VISIBLE : View.GONE);
        findViewById(R.id.section_panel).setVisibility(home ? View.GONE : View.VISIBLE);
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
        else super.onBackPressed(); // P1 is a regular test app, not a device lock.
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
