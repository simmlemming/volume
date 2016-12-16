package org.volume.di;

import android.content.Context;

import org.volume.Preferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by mtkachenko on 16/12/16.
 */

@Module
public class PreferencesModule {
    private final Context context;

    public PreferencesModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    Preferences providePreferences() {
        return new Preferences(context);
    }
}
