package org.volume;

import android.media.AudioManager;

import static android.media.AudioManager.ADJUST_LOWER;
import static android.media.AudioManager.ADJUST_RAISE;
import static android.media.AudioManager.FLAG_PLAY_SOUND;
import static android.media.AudioManager.STREAM_MUSIC;

/**
 * Created by mtkachenko on 06/04/16.
 */
public class VolumeManager {
    public static final int SECOND_THRESHOLD = 85;
    public static final int FIRST_THRESHOLD = 60;

    public interface OnVolumeChangeListener {
        void onVolumeChange(int oldLevel, int newLevel, int maxLevel);
    }

    private AudioManager audioManager;
    private OnVolumeChangeListener onVolumeChangeListener;

    public VolumeManager(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    public void onManualAdjust(int speed, int direction) {
        adjustVolume(direction);
    }

    public void onSpeedChange(int oldSpeed, int newSpeed) {
        boolean speedRaises = oldSpeed < newSpeed;
        boolean speedPassesThreshold = passesThreshold(oldSpeed, newSpeed, FIRST_THRESHOLD, SECOND_THRESHOLD);

        if (!speedPassesThreshold) {
            return;
        }

        adjustVolume(speedRaises ? ADJUST_RAISE : ADJUST_LOWER);
    }

    public int getCurrentVolume() {
        return audioManager.getStreamVolume(STREAM_MUSIC);
    }

    private void adjustVolume(int direction) {
        int oldLevel = getCurrentVolume();
        audioManager.adjustStreamVolume(STREAM_MUSIC, direction, FLAG_PLAY_SOUND);
        notifyVolumeChange(oldLevel);
    }

    private boolean passesThreshold(int oldSpeed, int newSpeed, int... thresholds) {
        for (int threshold : thresholds) {
            if (oldSpeed < threshold && threshold < newSpeed) {
                return true;
            }

            if (oldSpeed > threshold && threshold > newSpeed) {
                return true;
            }
        }

        return false;
    }

    private void notifyVolumeChange(int oldLevel) {
        int newLevel = getCurrentVolume();
        int maxLevel = audioManager.getStreamMaxVolume(STREAM_MUSIC);

        if (onVolumeChangeListener != null) {
            onVolumeChangeListener.onVolumeChange(oldLevel, newLevel, maxLevel);
        }
    }

    public void setOnVolumeChangeListener(OnVolumeChangeListener onVolumeChangeListener) {
        this.onVolumeChangeListener = onVolumeChangeListener;
    }
}
