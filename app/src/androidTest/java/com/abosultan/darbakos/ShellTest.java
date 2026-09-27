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
    @Test public void launchIsArabicLandscapeWithNoLiveReading() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            scenario.onActivity(activity -> {
                assertEquals(25, Build.VERSION.SDK_INT);
                assertEquals(Configuration.ORIENTATION_LANDSCAPE,
                    activity.getResources().getConfiguration().orientation);
                assertEquals(View.LAYOUT_DIRECTION_RTL,
                    activity.findViewById(R.id.shell_root).getLayoutDirection());
                assertEquals(View.VISIBLE, activity.findViewById(R.id.home_panel).getVisibility());
                assertEquals("—", ((TextView) activity.findViewById(R.id.speed_value)).getText().toString());
                assertTrue(((TextView) activity.findViewById(R.id.test_badge)).getText().toString().contains("تجريبية"));
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
                    assertEquals(View.VISIBLE, activity.findViewById(R.id.section_panel).getVisibility());
                    assertEquals(activity.getString(titles[index]),
                        ((TextView) activity.findViewById(R.id.section_title)).getText().toString());
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
                assertEquals(activity.getString(R.string.media),
                    ((TextView) activity.findViewById(R.id.section_title)).getText().toString());
                activity.findViewById(R.id.back_home).performClick();
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
