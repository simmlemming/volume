package org.volume.di;

import android.media.AudioManager;

import org.volume.Preferences;
import org.volume.di.scope.OnePerAppComponent;
import org.volume.manager.NoiseManager;
import org.volume.manager.VolumeManager;

import dagger.Module;
import dagger.Provides;

/**
 * Created by mtkachenko on 16/12/16.
 */

@Module
public class AudioManagerModule {

    @Provides
    @OnePerAppComponent
    VolumeManager provideAudioManager(AudioManager audioManager, Preferences preferences) {
        return new VolumeManager(audioManager, preferences);
    }

    @Provides
    @OnePerAppComponent
    NoiseManager provideNoiseManager() {
        return new NoiseManager();
    }
}
