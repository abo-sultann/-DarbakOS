package com.abosultan.darbakos;

import android.content.Context;
import android.media.MediaMetadata;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Handler;
import android.os.Looper;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.abosultan.darbakos.core.MediaSessionBridge;
import com.abosultan.darbakos.core.MediaSnapshot;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class MediaSessionBridgeTest {
    @Test public void observesRealActiveSessionWithoutStartingPlayback() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        MediaSession session = new MediaSession(context, "DarbakP5Observe");
        AtomicInteger playCalls = new AtomicInteger();
        session.setCallback(new MediaSession.Callback() {
            @Override public void onPlay() { playCalls.incrementAndGet(); }
        }, new Handler(Looper.getMainLooper()));
        session.setMetadata(new MediaMetadata.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, "المقطع الحالي")
                .putString(MediaMetadata.METADATA_KEY_ARTIST, "الفنان")
                .build());
        session.setPlaybackState(state(PlaybackState.STATE_PAUSED,
                PlaybackState.ACTION_PLAY | PlaybackState.ACTION_SKIP_TO_NEXT));
        session.setActive(true);

        AtomicReference<MediaSnapshot> latest = new AtomicReference<>();
        MediaSessionBridge bridge = new MediaSessionBridge(context, latest::set);
        try {
            assertTrue("Notification-listener access must be enabled by the focused runner",
                    bridge.start());
            MediaSnapshot snapshot = awaitActive(latest);
            assertEquals("المقطع الحالي", snapshot.title);
            assertEquals("الفنان", snapshot.artist);
            assertFalse(snapshot.playing);
            assertTrue(snapshot.canPlayPause);
            assertFalse(snapshot.canPrevious);
            assertTrue(snapshot.canNext);
            assertEquals("Observing a session must never trigger playback", 0, playCalls.get());
        } finally {
            bridge.stop();
            session.release();
        }
    }

    @Test public void userTransportHonorsAdvertisedActions() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        MediaSession session = new MediaSession(context, "DarbakP5Transport");
        CountDownLatch play = new CountDownLatch(1);
        CountDownLatch next = new CountDownLatch(1);
        AtomicInteger previousCalls = new AtomicInteger();
        session.setCallback(new MediaSession.Callback() {
            @Override public void onPlay() { play.countDown(); }
            @Override public void onSkipToNext() { next.countDown(); }
            @Override public void onSkipToPrevious() { previousCalls.incrementAndGet(); }
        }, new Handler(Looper.getMainLooper()));
        session.setMetadata(new MediaMetadata.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, "تحكم")
                .build());
        session.setPlaybackState(state(PlaybackState.STATE_PAUSED,
                PlaybackState.ACTION_PLAY | PlaybackState.ACTION_SKIP_TO_NEXT));
        session.setActive(true);

        AtomicReference<MediaSnapshot> latest = new AtomicReference<>();
        MediaSessionBridge bridge = new MediaSessionBridge(context, latest::set);
        try {
            assertTrue(bridge.start());
            awaitActive(latest);
            assertFalse("Unsupported previous must be rejected", bridge.previous());
            assertEquals(0, previousCalls.get());
            assertTrue(bridge.playPause());
            assertTrue("Explicit play command not delivered", play.await(2, TimeUnit.SECONDS));
            assertTrue(bridge.next());
            assertTrue("Explicit next command not delivered", next.await(2, TimeUnit.SECONDS));
        } finally {
            bridge.stop();
            session.release();
        }
    }

    private static PlaybackState state(int state, long actions) {
        return new PlaybackState.Builder()
                .setActions(actions)
                .setState(state, 0L, 1f)
                .build();
    }

    private static MediaSnapshot awaitActive(AtomicReference<MediaSnapshot> latest)
            throws InterruptedException {
        for (int i = 0; i < 40; i++) {
            MediaSnapshot value = latest.get();
            if (value != null && value.state == MediaSnapshot.State.ACTIVE) return value;
            Thread.sleep(50L);
        }
        fail("Active MediaSession was not observed");
        return null;
    }
}
