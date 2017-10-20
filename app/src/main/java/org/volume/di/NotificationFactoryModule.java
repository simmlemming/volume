package org.volume.di;

import org.volume.di.scope.OnePerAppComponent;
import org.volume.util.NotificationFactory;

import dagger.Module;
import dagger.Provides;

/**
 * Created by mtkachenko on 20/10/17.
 */

@Module
public class NotificationFactoryModule {

    @Provides
    @OnePerAppComponent
    NotificationFactory provideNotificationFactory() {
        return new NotificationFactory();
    }
}
