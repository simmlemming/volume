package org.volume.di;

import org.volume.di.scope.OnePerAppComponent;
import org.volume.util.SpeedLogger;

import dagger.Module;
import dagger.Provides;

/**
 * Created by mtkachenko on 16/12/16.
 */

@Module
public class SpeedLoggerModule {

    @Provides
    @OnePerAppComponent
    SpeedLogger provideSpeedLogger() {
        return new SpeedLogger();
    }
}
