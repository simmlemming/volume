package org.volume.di;

import org.volume.util.SpeedLogger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by mtkachenko on 16/12/16.
 */

@Module
public class SpeedLoggerModule {

    @Provides
    @Singleton
    SpeedLogger provideSpeedLogger() {
        return new SpeedLogger();
    }
}
