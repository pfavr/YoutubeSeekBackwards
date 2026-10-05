package org.headsetrewind;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.SystemClock;

import java.util.List;

final class MediaControl {
    private final Context context;
    private final ComponentName listener;

    MediaControl(Context context) {
        this.context = context;
        listener = new ComponentName(context, SessionListener.class);
    }

    boolean hasAccess() {
        return context.getSystemService(NotificationManager.class)
                .isNotificationListenerAccessGranted(listener);
    }

    boolean normalAudioMode() {
        return context.getSystemService(AudioManager.class).getMode() == AudioManager.MODE_NORMAL;
    }

    private List<MediaController> sessions() {
        return context.getSystemService(MediaSessionManager.class).getActiveSessions(listener);
    }

    static boolean seekable(PlaybackState state) {
        return state != null && state.getPosition() >= 0
                && Float.isFinite(state.getPlaybackSpeed())
                && (state.getActions() & PlaybackState.ACTION_SEEK_TO) != 0
                && (state.getState() == PlaybackState.STATE_PLAYING
                    || state.getState() == PlaybackState.STATE_PAUSED);
    }

    MediaController findTarget() {
        if (!hasAccess()) {
            Status.report(context, context.getString(R.string.notification_access_required), true);
            return null;
        }
        if (!normalAudioMode()) {
            Status.report(context, context.getString(R.string.call_in_progress), true);
            return null;
        }
        try {
            MediaController paused = null;
            for (MediaController controller : sessions()) {
                PlaybackState state = controller.getPlaybackState();
                if (!seekable(state)) {
                    continue;
                }
                if (state.getState() == PlaybackState.STATE_PLAYING) {
                    return controller;
                }
                if (paused == null) {
                    paused = controller;
                }
            }
            if (paused == null) {
                Status.report(context, context.getString(R.string.no_session), true);
            }
            return paused;
        } catch (SecurityException | IllegalStateException error) {
            reportAccessError(error);
            return null;
        }
    }

    boolean seek(MediaController target, int seconds) {
        if (!hasAccess()) {
            Status.report(context, context.getString(R.string.notification_access_required), true);
            return false;
        }
        if (!normalAudioMode()) {
            Status.report(context, context.getString(R.string.call_in_progress), true);
            return false;
        }
        try {
            boolean active = false;
            for (MediaController controller : sessions()) {
                if (controller.getSessionToken().equals(target.getSessionToken())) {
                    active = true;
                    break;
                }
            }
            if (!active) {
                Status.report(context, context.getString(R.string.session_gone), true);
                return false;
            }
            PlaybackState state = target.getPlaybackState();
            if (!seekable(state)) {
                Status.report(context, context.getString(R.string.seek_unavailable), true);
                return false;
            }
            MediaMetadata metadata = target.getMetadata();
            long duration = metadata == null ? -1 : metadata.getLong(MediaMetadata.METADATA_KEY_DURATION);
            long position = SeekPosition.backward(state.getPosition(), state.getLastPositionUpdateTime(),
                    SystemClock.elapsedRealtime(), state.getPlaybackSpeed(),
                    state.getState() == PlaybackState.STATE_PLAYING, duration, seconds);
            target.getTransportControls().seekTo(position);
            Status.report(context, context.getString(R.string.seek_sent, target.getPackageName()), false);
            return true;
        } catch (SecurityException | IllegalStateException error) {
            reportAccessError(error);
            return false;
        }
    }

    private void reportAccessError(RuntimeException error) {
        android.util.Log.w("HeadsetRewind", "Media-session operation failed", error);
        Status.report(context, context.getString(R.string.media_access_error), true);
    }
}
