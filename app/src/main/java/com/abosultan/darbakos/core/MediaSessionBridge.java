package com.abosultan.darbakos.core;

import android.content.ComponentName;
import android.content.Context;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;

import java.util.List;

/**
 * Lightweight API21+ observer/controller for the currently active Android MediaSession.
 * It never starts playback on its own. Notification-listener access is required only so
 * MediaSessionManager may expose sessions from other apps.
 */
public final class MediaSessionBridge {
    public interface Callback {
        void onMedia(MediaSnapshot snapshot);
    }

    private final Context context;
    private final Callback callback;
    private final ComponentName listenerComponent;
    private final MediaSessionManager manager;
    private MediaController controller;
    private boolean started;

    private final MediaSessionManager.OnActiveSessionsChangedListener sessionsListener =
            controllers -> bind(pick(controllers));

    private final MediaController.Callback controllerCallback = new MediaController.Callback() {
        @Override public void onMetadataChanged(MediaMetadata metadata) { emit(); }
        @Override public void onPlaybackStateChanged(PlaybackState state) { emit(); }
        @Override public void onSessionDestroyed() { bind(null); }
    };

    public MediaSessionBridge(Context context, Callback callback) {
        if (context == null) throw new IllegalArgumentException("context");
        this.context = context.getApplicationContext();
        this.callback = callback;
        this.listenerComponent = new ComponentName(this.context,
                DarbakMediaNotificationListener.class);
        this.manager = (MediaSessionManager) this.context.getSystemService(
                Context.MEDIA_SESSION_SERVICE);
    }

    /** Starts observation only. Returns false when notification-listener access is unavailable. */
    public boolean start() {
        if (started) return true;
        if (manager == null) {
            publish(MediaSnapshot.accessUnavailable());
            return false;
        }
        try {
            manager.addOnActiveSessionsChangedListener(sessionsListener, listenerComponent);
            started = true;
            bind(pick(manager.getActiveSessions(listenerComponent)));
            return true;
        } catch (SecurityException | RuntimeException error) {
            started = false;
            bind(null);
            publish(MediaSnapshot.accessUnavailable());
            return false;
        }
    }

    public void stop() {
        if (manager != null && started) {
            try {
                manager.removeOnActiveSessionsChangedListener(sessionsListener);
            } catch (RuntimeException ignored) { }
        }
        started = false;
        if (controller != null) {
            try { controller.unregisterCallback(controllerCallback); }
            catch (RuntimeException ignored) { }
        }
        controller = null;
    }

    public boolean started() { return started; }

    /** User-triggered transport only; never called automatically on boot/wake. */
    public boolean playPause() {
        MediaController current = controller;
        if (current == null) return false;
        try {
            PlaybackState state = current.getPlaybackState();
            if (state != null && state.getState() == PlaybackState.STATE_PLAYING) {
                current.getTransportControls().pause();
            } else {
                current.getTransportControls().play();
            }
            return true;
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    public boolean next() { return transport(1); }
    public boolean previous() { return transport(-1); }

    private boolean transport(int direction) {
        MediaController current = controller;
        if (current == null) return false;
        try {
            if (direction > 0) current.getTransportControls().skipToNext();
            else current.getTransportControls().skipToPrevious();
            return true;
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private MediaController pick(List<MediaController> controllers) {
        if (controllers == null || controllers.isEmpty()) return null;
        for (MediaController item : controllers) {
            PlaybackState state = item == null ? null : item.getPlaybackState();
            if (state != null && state.getState() == PlaybackState.STATE_PLAYING) return item;
        }
        return controllers.get(0);
    }

    private void bind(MediaController next) {
        if (sameSession(controller, next)) {
            emit();
            return;
        }
        if (controller != null) {
            try { controller.unregisterCallback(controllerCallback); }
            catch (RuntimeException ignored) { }
        }
        controller = next;
        if (controller != null) {
            try { controller.registerCallback(controllerCallback); }
            catch (RuntimeException ignored) {
                controller = null;
            }
        }
        emit();
    }

    private void emit() {
        MediaController current = controller;
        if (!started) return;
        if (current == null) {
            publish(MediaSnapshot.idle());
            return;
        }
        try {
            MediaMetadata metadata = current.getMetadata();
            PlaybackState playback = current.getPlaybackState();
            String title = text(metadata, MediaMetadata.METADATA_KEY_TITLE);
            String artist = text(metadata, MediaMetadata.METADATA_KEY_ARTIST);
            if (artist.length() == 0) {
                artist = text(metadata, MediaMetadata.METADATA_KEY_ALBUM_ARTIST);
            }
            boolean playing = playback != null
                    && playback.getState() == PlaybackState.STATE_PLAYING;
            publish(MediaSnapshot.active(title, artist, current.getPackageName(), playing));
        } catch (RuntimeException ignored) {
            publish(MediaSnapshot.idle());
        }
    }

    private void publish(MediaSnapshot snapshot) {
        if (callback != null) callback.onMedia(snapshot);
    }

    private static String text(MediaMetadata metadata, String key) {
        if (metadata == null) return "";
        CharSequence value = metadata.getText(key);
        return value == null ? "" : value.toString().trim();
    }

    private static boolean sameSession(MediaController a, MediaController b) {
        if (a == null || b == null) return a == b;
        try { return a.getSessionToken().equals(b.getSessionToken()); }
        catch (RuntimeException ignored) { return false; }
    }
}
