package org.volume.di;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import org.volume.Preferences;
import org.volume.VolumeApplication;
import org.volume.di.scope.OnePerApplication;

import dagger.Module;
import dagger.Provides;

/**
 * Created by mtkachenko on 20/12/16.
 */

@Module
public class ApplicationModule {
    private final VolumeApplication application;

    public ApplicationModule(VolumeApplication application) {
        this.application = application;
    }

    @Provides
    @OnePerApplication
    VolumeApplication provideApplication() {
        return application;
    }

    @Provides
    @OnePerApplication
    SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @OnePerApplication
    Resources provideResources() {
        return application.getResources();
    }

    @Provides
    @OnePerApplication
    Preferences providePreferences(SharedPreferences preferences, Resources resources) {
        return new Preferences(preferences, resources);
    }

}
