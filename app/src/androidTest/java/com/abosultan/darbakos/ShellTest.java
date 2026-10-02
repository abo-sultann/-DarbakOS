package com.abosultan.darbakos;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class ShellTest {
    @Test public void launchIsArabicLandscapeAndSpeedStateIsTruthful() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            scenario.onActivity(activity -> {
                assertEquals(25, Build.VERSION.SDK_INT);
                assertEquals(Configuration.ORIENTATION_LANDSCAPE,
                    activity.getResources().getConfiguration().orientation);
                assertEquals(View.LAYOUT_DIRECTION_RTL,
                    activity.findViewById(R.id.shell_root).getLayoutDirection());
                assertEquals(View.VISIBLE, activity.findViewById(R.id.home_panel).getVisibility());
                String speed = ((TextView) activity.findViewById(R.id.speed_value)).getText().toString();
                String source = ((TextView) activity.findViewById(R.id.speed_source)).getText().toString();
                if ("—".equals(speed)) {
                    assertEquals("السرعة غير متاحة",
                        activity.findViewById(R.id.speed_value).getContentDescription().toString());
                } else {
                    assertTrue("Live speed must be numeric", speed.matches("\\d+"));
                    assertEquals("GPS • مباشر", source);
                }
                assertTrue(activity.findViewById(R.id.nav_home).isSelected());
            });
        }
    }

    @Test public void everyDestinationAndBackWorks() {
        int[] buttons = {R.id.nav_map, R.id.nav_media, R.id.nav_vehicle, R.id.nav_apps, R.id.settings_button};
        int[] titles = {R.string.map, R.string.media, R.string.vehicle, R.string.apps, R.string.settings};
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            for (int i = 0; i < buttons.length; i++) {
                final int index = i;
                scenario.onActivity(activity -> {
                    activity.findViewById(buttons[index]).performClick();
                    if (index == 0) {
                        assertEquals(View.VISIBLE, activity.findViewById(R.id.map_panel).getVisibility());
                        assertEquals(View.GONE, activity.findViewById(R.id.section_panel).getVisibility());
                        assertTrue(((TextView) activity.findViewById(R.id.map_route_title))
                                .getText().toString().length() > 0);
                    } else if (index == 1) {
                        assertEquals(View.VISIBLE, activity.findViewById(R.id.media_panel).getVisibility());
                        assertEquals(View.GONE, activity.findViewById(R.id.section_panel).getVisibility());
                    } else if (index == 2) {
                        assertEquals(View.VISIBLE, activity.findViewById(R.id.vehicle_panel).getVisibility());
                        assertEquals(View.GONE, activity.findViewById(R.id.section_panel).getVisibility());
                        assertEquals("غير متاح",
                            ((TextView) activity.findViewById(R.id.vehicle_pressure)).getText().toString()
                                .replace("ضغط الإطار: ", ""));
                    } else {
                        assertEquals(View.VISIBLE, activity.findViewById(R.id.section_panel).getVisibility());
                        assertEquals(activity.getString(titles[index]),
                            ((TextView) activity.findViewById(R.id.section_title)).getText().toString());
                    }
                    assertTrue(activity.findViewById(buttons[index]).isSelected());
                    assertFalse(activity.findViewById(R.id.nav_home).isSelected());
                    activity.onBackPressed();
                    assertEquals(View.VISIBLE, activity.findViewById(R.id.home_panel).getVisibility());
                });
            }
        }
    }

    @Test public void recreatePreservesSectionAndHomeButtonWorks() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> activity.findViewById(R.id.nav_media).performClick());
            scenario.recreate();
            scenario.onActivity(activity -> {
                assertTrue(activity.findViewById(R.id.nav_media).isSelected());
                assertEquals(View.VISIBLE, activity.findViewById(R.id.media_panel).getVisibility());
                activity.findViewById(R.id.media_back_home).performClick();
                assertTrue(activity.findViewById(R.id.nav_home).isSelected());
            });
        }
    }

    @Test public void homeFits1024x600AndRtlNavigation() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            scenario.onActivity(activity -> {
                View root = activity.findViewById(R.id.shell_root);
                assertEquals(1024, root.getWidth());
                assertEquals(600, root.getHeight());
                assertVisibleWithin(root, new Rect(0, 0, 1024, 600));
                assertTrue(activity.findViewById(R.id.nav_home).getLeft()
                    > activity.findViewById(R.id.nav_apps).getLeft());
                int[] speed = new int[2];
                int[] map = new int[2];
                activity.findViewById(R.id.speed_value).getLocationOnScreen(speed);
                activity.findViewById(R.id.navigation_card).getLocationOnScreen(map);
                assertTrue(speed[0] < map[0]);
                View speedCard = (View) activity.findViewById(R.id.speed_value).getParent();
                speedCard.getLocationOnScreen(speed);
                assertEquals("RTL must keep the gap between dashboard cards",
                    activity.getResources().getDimensionPixelSize(R.dimen.space),
                    map[0] - speed[0] - speedCard.getWidth());
            });
        }
    }

    @Test public void mapSurfaceFitsAndAbsentEngineStaysTruthful() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> activity.findViewById(R.id.nav_map).performClick());
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            scenario.onActivity(activity -> {
                View root = activity.findViewById(R.id.shell_root);
                assertEquals(View.VISIBLE, activity.findViewById(R.id.map_panel).getVisibility());
                assertVisibleWithin(activity.findViewById(R.id.map_panel), new Rect(0, 0, 1024, 600));
                assertEquals("OsmAnd • غير متاح",
                    ((TextView) activity.findViewById(R.id.map_engine_state)).getText().toString());
                assertFalse(activity.findViewById(R.id.map_open_button).isEnabled());
                assertFalse(activity.findViewById(R.id.map_refresh_button).isEnabled());
                assertFalse(activity.findViewById(R.id.map_location_button).isEnabled());
                assertTrue(activity.findViewById(R.id.nav_map).isSelected());
                assertTrue(root.getWidth() > 0);
                activity.findViewById(R.id.map_back_home).performClick();
                assertEquals(View.VISIBLE, activity.findViewById(R.id.home_panel).getVisibility());
                assertEquals(View.GONE, activity.findViewById(R.id.map_panel).getVisibility());
                assertTrue(activity.findViewById(R.id.nav_home).isSelected());
            });
        }
    }

    @Test public void mediaSurfaceFitsAndNeverOffersAutoplayWithoutAccess() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> activity.findViewById(R.id.nav_media).performClick());
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            scenario.onActivity(activity -> {
                assertEquals(View.VISIBLE, activity.findViewById(R.id.media_panel).getVisibility());
                assertVisibleWithin(activity.findViewById(R.id.media_panel), new Rect(0, 0, 1024, 600));
                assertEquals("وصول الوسائط غير مفعّل",
                        ((TextView) activity.findViewById(R.id.media_now_title)).getText().toString());
                assertEquals(View.VISIBLE, activity.findViewById(R.id.media_access_button).getVisibility());
                assertFalse(activity.findViewById(R.id.media_play_pause_button).isEnabled());
                assertFalse(activity.findViewById(R.id.media_previous_button).isEnabled());
                assertFalse(activity.findViewById(R.id.media_next_button).isEnabled());
                assertEquals("وصول الوسائط غير مفعّل",
                        ((TextView) activity.findViewById(R.id.media_state)).getText().toString());
                activity.findViewById(R.id.media_back_home).performClick();
                assertTrue(activity.findViewById(R.id.nav_home).isSelected());
            });
        }
    }

    @Test public void appsFitRtlAndUnavailableActionsStayInApps() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> activity.findViewById(R.id.nav_apps).performClick());
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            scenario.onActivity(activity -> {
                assertVisibleWithin(activity.findViewById(R.id.shell_root), new Rect(0, 0, 1024, 600));
                assertEquals(View.VISIBLE, activity.findViewById(R.id.apps_preview).getVisibility());
                int[] ids = {R.id.apps_recent, R.id.apps_favorite, R.id.apps_manage};
                String[] labels = {"الأخيرة", "المفضلة", "إدارة التطبيقات"};
                int previousLeft = 1024;
                for (int i = 0; i < ids.length; i++) {
                    TextView button = activity.findViewById(ids[i]);
                    assertEquals(labels[i], button.getText().toString());
                    assertFalse("Unconnected action must be disabled", button.isEnabled());
                    assertTrue(button.getHeight() >= 56 && button.getWidth() >= 56);
                    assertTrue("Apps actions must run right to left", button.getLeft() < previousLeft);
                    previousLeft = button.getLeft();
                }
                activity.findViewById(R.id.back_home).performClick();
                assertEquals(View.GONE, activity.findViewById(R.id.apps_preview).getVisibility());
                assertTrue(activity.findViewById(R.id.nav_home).isSelected());
            });
        }
    }

    private static void assertVisibleWithin(View view, Rect screen) {
        if (view.getVisibility() != View.VISIBLE) return;
        int[] xy = new int[2];
        view.getLocationOnScreen(xy);
        Rect bounds = new Rect(xy[0], xy[1], xy[0] + view.getWidth(), xy[1] + view.getHeight());
        assertTrue("View has no size: " + view.getId(), view.getWidth() > 0 && view.getHeight() > 0);
        assertTrue("Clipped view " + view.getId() + ": " + bounds, screen.contains(bounds));
        if (view instanceof TextView) {
            TextView text = (TextView) view;
            assertNotNull(text.getLayout());
            assertTrue("Clipped text: " + text.getText(), text.getLayout().getHeight()
                <= text.getHeight() - text.getCompoundPaddingTop() - text.getCompoundPaddingBottom());
            for (int line = 0; line < text.getLineCount(); line++) {
                assertEquals("Ellipsized text", 0, text.getLayout().getEllipsisCount(line));
            }
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) assertVisibleWithin(group.getChildAt(i), bounds);
        }
    }
}
