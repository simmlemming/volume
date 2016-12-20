package org.volume.di;

import org.volume.di.scope.OnePerAppComponent;
import org.volume.service.SpeedService;

import dagger.Component;

/**
 * Created by mtkachenko on 16/12/16.
 */

@OnePerAppComponent
@Component(modules = {SpeedManagerModule.class, AudioManagerModule.class, SpeedLoggerModule.class, BeeperModule.class},
            dependencies = {ApplicationComponent.class})

public interface SpeedServiceComponent {
    void inject(SpeedService service);
}
