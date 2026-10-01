package com.abosultan.darbakos.core;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/** Single-process local player. Playback is only started by an explicit user action. */
public final class LocalMediaPlayer {
    public interface Listener { void onState(LocalMediaTrack track, boolean playing, boolean error); }

    private MediaPlayer player;
    private LocalMediaTrack current;
    private final Listener listener;

    public LocalMediaPlayer(Listener listener) { this.listener = listener; }

    public void play(LocalMediaTrack track) {
        if (track == null || track.file == null || !track.file.isFile()) {
            publish(track, false, true); return;
        }
        releasePlayer();
        MediaPlayer next = new MediaPlayer();
        player = next;
        current = track;
        try {
            next.setAudioStreamType(AudioManager.STREAM_MUSIC);
            next.setDataSource(track.file.getAbsolutePath());
            next.setOnCompletionListener(mp -> publish(current, false, false));
            next.setOnErrorListener((mp, what, extra) -> { publish(current, false, true); return true; });
            next.prepare();
            next.start();
            publish(current, true, false);
        } catch (IOException | RuntimeException e) {
            releasePlayer();
            publish(track, false, true);
        }
    }

    public void playPause() {
        if (player == null) return;
        try {
            if (player.isPlaying()) player.pause(); else player.start();
            publish(current, player.isPlaying(), false);
        } catch (IllegalStateException e) { publish(current, false, true); }
    }

    public void stop() {
        releasePlayer();
        current = null;
        publish(null, false, false);
    }

    public void release() { releasePlayer(); current = null; }

    private void releasePlayer() {
        MediaPlayer old = player; player = null;
        if (old != null) try { old.release(); } catch (RuntimeException ignored) {}
    }

    private void publish(LocalMediaTrack track, boolean playing, boolean error) {
        if (listener != null) listener.onState(track, playing, error);
    }
}
