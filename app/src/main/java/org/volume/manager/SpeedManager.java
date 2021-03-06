package org.volume.manager;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import org.volume.util.MathUtils;

/**
 * Created by mtkachenko on 06/04/16.
 */
public class SpeedManager implements LocationListener {
    private static final int LOCATION_UPDATES_INTERVAL_MS = 2000;

    public interface OnSpeedUpdateListener {
        void onSpeedUpdate(int newSpeed, long time);

        void onSpeedChange(int oldSpeed, int newSpeed, long time);

        void onStartListening();

        void onStopListening();
    }

    public static final int SPEED_UNKNOWN = -1;

    private final LocationManager locationManager;
    private int currentSpeed = SPEED_UNKNOWN;
    private boolean isListening = false;

    public SpeedManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    private OnSpeedUpdateListener onSpeedUpdateListener;

    public void startListening() {
        if (isListening) {
            return;
        }

        isListening = true;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATES_INTERVAL_MS, 0, this);

        if (onSpeedUpdateListener != null) {
            onSpeedUpdateListener.onStartListening();
        }
    }

    public void stopListening() {
        if (!isListening) {
            return;
        }

        isListening = false;
        locationManager.removeUpdates(this);

        if (onSpeedUpdateListener != null) {
            onSpeedUpdateListener.onStopListening();
        }
    }

    public boolean isListening() {
        return isListening;
    }

    public int getCurrentSpeed() {
        return currentSpeed;
    }

    public void setOnSpeedUpdateListener(OnSpeedUpdateListener onSpeedUpdateListener) {
        this.onSpeedUpdateListener = onSpeedUpdateListener;
    }

    private void onSpeedUpdate(int newSpeed, long time) {
        int oldSpeed = currentSpeed;
        currentSpeed = newSpeed;

        if (onSpeedUpdateListener != null) {
            onSpeedUpdateListener.onSpeedUpdate(currentSpeed, time);

            if (oldSpeed != currentSpeed) {
                onSpeedUpdateListener.onSpeedChange(oldSpeed, currentSpeed, time);
            }
        }
    }

    public void onLocationChanged(Location location) {
        if (!location.hasSpeed()) {
            onSpeedUpdate(SPEED_UNKNOWN, location.getTime());
        }

        float speedMps = location.getSpeed();
        onSpeedUpdate(MathUtils.mpsToKmh(speedMps), location.getTime());
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void onProviderEnabled(String provider) {

    }

    public void onProviderDisabled(String provider) {

    }
}
