package org.volume;

import android.media.AudioManager;

import static android.media.AudioManager.FLAG_PLAY_SOUND;
import static android.media.AudioManager.STREAM_MUSIC;

/**
 * Created by mtkachenko on 06/04/16.
 */
public class VolumeManager {
    public interface OnVolumeChangeListener {
        void onVolumeChange(int newLevel, int maxLevel);
    }

    private AudioManager audioManager;
    private OnVolumeChangeListener onVolumeChangeListener;

    public VolumeManager(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    public void onManualAdjust(int speed, int direction) {
        adjustVolume(direction);
    }

    public void setVolume(int speed, int level) {
        audioManager.setStreamVolume(STREAM_MUSIC, level, FLAG_PLAY_SOUND);
        notifyVolumeChange();
    }

    public void onSpeedUpdate(int newSpeed) {

    }

    private void adjustVolume(int direction) {
        audioManager.adjustStreamVolume(STREAM_MUSIC, direction, FLAG_PLAY_SOUND);
        notifyVolumeChange();
    }

    private void notifyVolumeChange() {
        int newLevel = audioManager.getStreamVolume(STREAM_MUSIC);
        int maxLevel = audioManager.getStreamMaxVolume(STREAM_MUSIC);

        if (onVolumeChangeListener != null) {
            onVolumeChangeListener.onVolumeChange(newLevel, maxLevel);
        }
    }

    public void setInitialVolume() {
        int maxVolume = audioManager.getStreamMaxVolume(STREAM_MUSIC);
        int initialLevel = maxVolume * 2 / 3;
        setVolume(SpeedManager.SPEED_UNKNOWN, initialLevel);
    }


    public void setOnVolumeChangeListener(OnVolumeChangeListener onVolumeChangeListener) {
        this.onVolumeChangeListener = onVolumeChangeListener;
    }
}
