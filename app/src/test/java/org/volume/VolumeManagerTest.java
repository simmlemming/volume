package org.volume;

import android.media.AudioManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.volume.manager.VolumeManager;

import static android.media.AudioManager.ADJUST_LOWER;
import static android.media.AudioManager.ADJUST_RAISE;
import static android.media.AudioManager.STREAM_MUSIC;
import static java.util.Arrays.asList;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by mtkachenko on 07/04/16.
 */
public class VolumeManagerTest {
    private static final int FIRST_THRESHOLD = 20;
    private static final int SECOND_THRESHOLD = 45;

    private VolumeManager volumeManager;
    private AudioManager audioManager;
    private Preferences preferences;
    private VolumeManager.OnVolumeChangeListener listener;

    @Before
    public void setUp() {
        audioManager = mock(AudioManager.class);
        listener = mock(VolumeManager.OnVolumeChangeListener.class);
        preferences = mock(Preferences.class);
        when(preferences.getSpeedThresholds())
                .thenReturn(asList(FIRST_THRESHOLD, SECOND_THRESHOLD));

        volumeManager = new VolumeManager(audioManager, preferences);
        volumeManager.setOnVolumeChangeListener(listener);
    }

    @Test
    public void onSpeedChange_listenerIsCalled() {
        volumeManager.onSpeedChange(SECOND_THRESHOLD - 2, SECOND_THRESHOLD + 2);
        verify(listener).onVolumeChange(anyInt(), anyInt(), anyInt());
    }

    @Test
    public void speedIncreasesPastSecondThreshold() {
        volumeManager.onSpeedChange(SECOND_THRESHOLD - 2, SECOND_THRESHOLD + 2);
        verify(audioManager, times(VolumeManager.VOLUME_ADJUSTMENTS_MULTIPLIER)).adjustStreamVolume(eq(STREAM_MUSIC), eq(ADJUST_RAISE), anyInt());
    }

    @Test
    public void speedIncreasesToThreshold() {
        volumeManager.onSpeedChange(SECOND_THRESHOLD - 2, SECOND_THRESHOLD);
        verify(audioManager, times(VolumeManager.VOLUME_ADJUSTMENTS_MULTIPLIER)).adjustStreamVolume(eq(STREAM_MUSIC), eq(ADJUST_RAISE), anyInt());
    }

    @Test
    public void speedDecreasesFromThreshold() {
        volumeManager.onSpeedChange(SECOND_THRESHOLD, SECOND_THRESHOLD - 2);
        verify(audioManager, times(VolumeManager.VOLUME_ADJUSTMENTS_MULTIPLIER)).adjustStreamVolume(eq(STREAM_MUSIC), eq(ADJUST_LOWER), anyInt());
    }

    @Test
    public void speedIncreasesPastFirstThreshold() {
        volumeManager.onSpeedChange(FIRST_THRESHOLD - 2, FIRST_THRESHOLD + 2);
        verify(audioManager, times(VolumeManager.VOLUME_ADJUSTMENTS_MULTIPLIER)).adjustStreamVolume(eq(STREAM_MUSIC), eq(ADJUST_RAISE), anyInt());
    }

    @Test
    public void speedDecreasesPastSecondThreshold() {
        volumeManager.onSpeedChange(SECOND_THRESHOLD + 2, SECOND_THRESHOLD - 2);
        verify(audioManager, times(VolumeManager.VOLUME_ADJUSTMENTS_MULTIPLIER)).adjustStreamVolume(eq(STREAM_MUSIC), eq(ADJUST_LOWER), anyInt());
    }

    @Test
    public void speedDecreasesPastFirstThreshold() {
        volumeManager.onSpeedChange(FIRST_THRESHOLD + 2, FIRST_THRESHOLD - 2);
        verify(audioManager, times(VolumeManager.VOLUME_ADJUSTMENTS_MULTIPLIER)).adjustStreamVolume(eq(STREAM_MUSIC), eq(ADJUST_LOWER), anyInt());
    }

    @After
    public void tearDown() {
        audioManager = null;
        volumeManager = null;
    }
}