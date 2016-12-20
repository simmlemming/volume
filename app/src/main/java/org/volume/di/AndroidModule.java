package org.volume.di;

import android.content.Context;
import android.location.LocationManager;
import android.media.AudioManager;

import dagger.Module;
import dagger.Provides;

/**
 * Created by mtkachenko on 19/12/16.
 */

@Module
public class AndroidModule {
    private final Context context;

    public AndroidModule(Context context) {
        this.context = context;
    }

    @Provides
    LocationManager provideLocationManager() {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Provides
    AudioManager provideAudioManager() {
        return (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }
}
