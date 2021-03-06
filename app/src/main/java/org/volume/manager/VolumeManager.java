package org.volume.manager;

import android.media.AudioManager;

import org.volume.util.MathUtils;

import java.util.ArrayList;
import java.util.List;

import static android.media.AudioManager.ADJUST_LOWER;
import static android.media.AudioManager.ADJUST_RAISE;
import static android.media.AudioManager.FLAG_PLAY_SOUND;
import static android.media.AudioManager.STREAM_MUSIC;

/**
 * Created by mtkachenko on 06/04/16.
 */
public class VolumeManager {
    public interface OnVolumeChangeListener {
        void onVolumeChange(int oldLevel, int newLevel, int maxLevel);
    }

    private List<Integer> speedThresholds = new ArrayList<>();
    private AudioManager audioManager;
    private OnVolumeChangeListener onVolumeChangeListener;

    public VolumeManager(AudioManager audioManager, List<Integer> speedThresholds) {
        this.audioManager = audioManager;
        setSpeedThresholds(speedThresholds);
    }

    public void onManualAdjust(int direction) {
        adjustVolume(direction);
    }

    public void onSpeedChange(int oldSpeed, int newSpeed) {
        boolean speedPassesThreshold = MathUtils.speedPassesThreshold(oldSpeed, newSpeed, speedThresholds);

        if (!speedPassesThreshold) {
            return;
        }

        boolean speedRaises = oldSpeed < newSpeed;
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

    public void setSpeedThresholds(List<Integer> speedThresholds) {
        this.speedThresholds = speedThresholds;
    }

    public void setVolumePct(float pct) {
        pct = MathUtils.betweenZeroAndOne(pct);

        int maxVolume = audioManager.getStreamMaxVolume(STREAM_MUSIC);
        int newVolume = Math.round(maxVolume * pct);

        audioManager.setStreamVolume(STREAM_MUSIC, newVolume, 0);
    }

    public List<Integer> getSpeedThresholds() {
        return speedThresholds;
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
