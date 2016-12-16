package org.volume.di;

import org.volume.service.SpeedService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by mtkachenko on 16/12/16.
 */

@Singleton
@Component(modules = {SpeedManagerModule.class, PreferencesModule.class, AudioManagerModule.class, LogUtilsModule.class})
public interface SpeedServiceComponent {
    void inject(SpeedService service);
}
