package org.volume.di;

import android.content.res.Resources;
import android.location.LocationManager;
import android.media.AudioManager;

import org.volume.Preferences;
import org.volume.VolumeApplication;
import org.volume.di.scope.OnePerApplication;

import dagger.Component;

/**
 * Created by mtkachenko on 20/12/16.
 */

@OnePerApplication
@Component(dependencies = AndroidComponent.class, modules = ApplicationModule.class)
public interface ApplicationComponent {
    VolumeApplication application();
    Preferences preferences();
    Resources resources();

    /* These are from Android component */
    LocationManager locationManager();
    AudioManager audioManager();
}
