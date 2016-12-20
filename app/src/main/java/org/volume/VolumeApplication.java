package org.volume;

import android.app.Application;

import org.volume.di.AndroidComponent;
import org.volume.di.AndroidModule;
import org.volume.di.ApplicationComponent;
import org.volume.di.ApplicationModule;
import org.volume.di.DaggerAndroidComponent;
import org.volume.di.DaggerApplicationComponent;

/**
 * Created by mtkachenko on 16/12/16.
 */

public class VolumeApplication extends Application {
    public static String TAG = "Volume";

    private ApplicationComponent applicationComponent;
    private AndroidComponent androidComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        androidComponent = DaggerAndroidComponent.builder()
                .androidModule(new AndroidModule(this))
                .build();

        applicationComponent = DaggerApplicationComponent.builder()
                .androidComponent(androidComponent)
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
