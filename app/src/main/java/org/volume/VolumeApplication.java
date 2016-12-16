package org.volume;

import android.app.Application;

import org.volume.di.AudioManagerModule;
import org.volume.di.DaggerSpeedServiceComponent;
import org.volume.di.LogUtilsModule;
import org.volume.di.PreferencesModule;
import org.volume.di.SpeedServiceComponent;
import org.volume.di.SpeedManagerModule;

/**
 * Created by mtkachenko on 16/12/16.
 */

public class VolumeApplication extends Application {
    private SpeedServiceComponent speedManagerComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        speedManagerComponent = DaggerSpeedServiceComponent.builder()
                .speedManagerModule(new SpeedManagerModule(this))
                .preferencesModule(new PreferencesModule(this))
                .audioManagerModule(new AudioManagerModule(this))
                .logUtilsModule(new LogUtilsModule())
                .build();
    }

    public SpeedServiceComponent getSpeedManagerComponent() {
        return speedManagerComponent;
    }
}
