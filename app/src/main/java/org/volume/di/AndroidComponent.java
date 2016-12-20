package org.volume.di;

import android.location.LocationManager;
import android.media.AudioManager;

import dagger.Component;

/**
 * Created by mtkachenko on 19/12/16.
 */

@Component(modules = AndroidModule.class)
public interface AndroidComponent {
    LocationManager locationManager();
    AudioManager audioManager();
}
