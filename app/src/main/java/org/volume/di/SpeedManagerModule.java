package org.volume.di;

import android.content.Context;
import android.location.LocationManager;

import org.volume.manager.SpeedManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by mtkachenko on 16/12/16.
 */

@Module
public class SpeedManagerModule {
    private final Context context;

    public SpeedManagerModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    SpeedManager provideSpeedManager() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return new SpeedManager(locationManager);
    }
}
