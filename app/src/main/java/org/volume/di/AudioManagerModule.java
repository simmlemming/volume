package org.volume.di;

import android.content.Context;
import android.media.AudioManager;

import org.volume.Preferences;
import org.volume.manager.NoiseManager;
import org.volume.manager.VolumeManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by mtkachenko on 16/12/16.
 */

@Module
public class AudioManagerModule {
    private final Context context;

    public AudioManagerModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    VolumeManager provideAudioManager(Preferences preferences) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return new VolumeManager(audioManager, preferences);
    }

    @Singleton
    @Provides
    NoiseManager provideNoiseManagerPreferences() {
        return new NoiseManager();
    }
}
