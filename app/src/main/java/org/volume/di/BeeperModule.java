package org.volume.di;

import android.os.Handler;
import android.support.annotation.NonNull;

import org.volume.di.scope.OnePerAppComponent;
import org.volume.util.Beeper;

import dagger.Module;
import dagger.Provides;

/**
 * Created by mtkachenko on 19/12/16.
 */

@Module
public class BeeperModule {

    @NonNull
    private final Handler handler;

    // Handler must be created in "prepared" thread,
    // so better have it explicitly provided rather than
    // created by Dagger
    public BeeperModule(@NonNull Handler handler) {
        this.handler = handler;
    }

    @Provides
    @OnePerAppComponent
    Beeper provideBeeper() {
        return new Beeper(handler);
    }
}
