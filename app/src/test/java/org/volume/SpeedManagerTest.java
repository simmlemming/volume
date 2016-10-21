package org.volume;

import android.location.Location;
import android.location.LocationManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.volume.manager.SpeedManager;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.volume.manager.SpeedManager.SPEED_UNKNOWN;

/**
 * Created by mtkachenko on 07/04/16.
 */
public class SpeedManagerTest {
    private SpeedManager speedManager;
    private SpeedManager.OnSpeedUpdateListener listener;

    @Before
    public void setUp() {
        listener = Mockito.mock(SpeedManager.OnSpeedUpdateListener.class);
        speedManager = new SpeedManager(Mockito.mock(LocationManager.class));
        speedManager.setOnSpeedUpdateListener(listener);
    }

    @Test
    public void locationUpdatesHaveSameSpeed_onSpeedChangeIsCalledOnce() {
        speedManager.onLocationChanged(newLocation(10));
        speedManager.onLocationChanged(newLocation(10));
        speedManager.onLocationChanged(newLocation(10));

        verify(listener, times(3)).onSpeedUpdate(eq(36), anyLong());
    }

    @Test
    public void locationHasSpeed_onSpeedChangeIsCalled_withSpeeds() {
        speedManager.onLocationChanged(newLocation(10));
        speedManager.onLocationChanged(newLocation(20));
        speedManager.onLocationChanged(newLocation(-1));

        InOrder order = inOrder(listener);
        order.verify(listener).onSpeedChange(eq(SPEED_UNKNOWN), eq(36), anyLong());
        order.verify(listener).onSpeedChange(eq(36), eq(72), anyLong());
        order.verify(listener).onSpeedChange(eq(72), eq(SPEED_UNKNOWN), anyLong());
    }

    @Test
    public void locationHasSpeed_onSpeedUpdateIsCalled_withSpeed() {
        speedManager.onLocationChanged(newLocation(10)); // 10 m/s
        verify(listener).onSpeedUpdate(eq(36), anyLong()); // 36 km/h
    }

    @Test
    public void locationHasNoSpeed_onSpeedUpdateIsCalled_withoutSpeed() {
        speedManager.onLocationChanged(newLocation(-10));
        verify(listener).onSpeedUpdate(eq(SPEED_UNKNOWN), anyLong());
    }

    private Location newLocation(float speed) {
        Location location = Mockito.mock(Location.class);

        when(location.hasSpeed()).thenReturn(speed > 0);
        when(location.getSpeed()).thenReturn(speed);
        when(location.getTime()).thenReturn(System.currentTimeMillis());

        return location;
    }

    @After
    public void tearDown() {
        listener = null;
        speedManager = null;
    }


}
