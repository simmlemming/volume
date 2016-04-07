package org.volume;

import android.media.AudioManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static android.media.AudioManager.ADJUST_LOWER;
import static android.media.AudioManager.ADJUST_RAISE;
import static android.media.AudioManager.STREAM_MUSIC;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.volume.VolumeManager.FIRST_THRESHOLD;
import static org.volume.VolumeManager.SECOND_THRESHOLD;

/**
 * Created by mtkachenko on 07/04/16.
 */
public class VolumeManagerTest {
    private VolumeManager volumeManager;
    private AudioManager audioManager;
    private VolumeManager.OnVolumeChangeListener listener;

    @Before
    public void setUp() {
        audioManager = mock(AudioManager.class);
        listener = mock(VolumeManager.OnVolumeChangeListener.class);
        volumeManager = new VolumeManager(audioManager);
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
        verify(audioManager).adjustStreamVolume(eq(STREAM_MUSIC), eq(ADJUST_RAISE), anyInt());
    }

    @Test
    public void speedIncreasesPastFirstThreshold() {
        volumeManager.onSpeedChange(FIRST_THRESHOLD - 2, FIRST_THRESHOLD + 2);
        verify(audioManager).adjustStreamVolume(eq(STREAM_MUSIC), eq(ADJUST_RAISE), anyInt());
    }

    @Test
    public void speedDecreasesPastSecondThreshold() {
        volumeManager.onSpeedChange(SECOND_THRESHOLD + 2, SECOND_THRESHOLD - 2);
        verify(audioManager).adjustStreamVolume(eq(STREAM_MUSIC), eq(ADJUST_LOWER), anyInt());
    }

    @Test
    public void speedDecreasesPastFirstThreshold() {
        volumeManager.onSpeedChange(FIRST_THRESHOLD + 2, FIRST_THRESHOLD - 2);
        verify(audioManager).adjustStreamVolume(eq(STREAM_MUSIC), eq(ADJUST_LOWER), anyInt());
    }

    @After
    public void tearDown() {
        audioManager = null;
        volumeManager = null;
    }
}