package org.volume.di.scope;

import javax.inject.Scope;

/**
 * Created by mtkachenko on 19/12/16.
 * <br><br>
 *
 * Suitable for anything with lifecycle shorter that Application's lifecycle:
 * Activities, Services, etc...
 *
 */

@Scope
public @interface OnePerAppComponent {

}
