package org.volume.di;

import android.location.LocationManager;

import org.volume.di.scope.OnePerAppComponent;
import org.volume.manager.SpeedManager;

import dagger.Module;
import dagger.Provides;

/**
 * Created by mtkachenko on 16/12/16.
 */

@Module
public class SpeedManagerModule {
    @Provides
    @OnePerAppComponent
    SpeedManager provideSpeedManager(LocationManager locationManager) {
        return new SpeedManager(locationManager);
    }
}
