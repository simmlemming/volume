package org.volume.di;

import org.volume.util.LogUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by mtkachenko on 16/12/16.
 */

@Module
public class LogUtilsModule {

    @Provides
    @Singleton
    LogUtils provideLogUtils() {
        return new LogUtils();
    }
}
