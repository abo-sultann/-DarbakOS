package com.abosultan.darbakos;

import android.view.View;
import android.widget.TextView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class AlertTest {
    @Test public void quietUntilActionExists() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                TextView alert = activity.findViewById(R.id.actionable_alert);
                activity.renderActionableAlertState(true);
                assertEquals(View.GONE, alert.getVisibility());
                activity.renderActionableAlertState(false);
                assertEquals(View.VISIBLE, alert.getVisibility());
                assertEquals("تنبيه • فعّل إذن الموقع لاستمرار GPS وتسجيل الرحلة", alert.getText().toString());
            });
        }
    }
}
