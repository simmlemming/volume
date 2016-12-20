package org.volume.di;

import org.volume.activity.MainActivity;
import org.volume.di.scope.OnePerAppComponent;

import dagger.Component;

/**
 * Created by mtkachenko on 19/12/16.
 */

@OnePerAppComponent
@Component(dependencies = ApplicationComponent.class)
public interface MainActivityComponent {
    void inject(MainActivity activity);
}
