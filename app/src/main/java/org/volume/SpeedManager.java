package org.volume;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by mtkachenko on 06/04/16.
 */
public class SpeedManager {
    public interface OnSpeedUpdateListener {
        void onSpeedUpdate(int newSpeed, long time);
        void onSpeedChange(int oldSpeed, int newSpeed, long time);
    }

    public static final int SPEED_UNKNOWN = -1;

    private final LocationManager locationManager;
    private int currentSpeed = SPEED_UNKNOWN;

    public SpeedManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    private OnSpeedUpdateListener onSpeedUpdateListener;

    public void startListening() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, locationListener);
    }

    public void stopListening() {
        locationManager.removeUpdates(locationListener);
    }

    public int getCurrentSpeed() {
        return currentSpeed;
    }

    public void setOnSpeedUpdateListener(OnSpeedUpdateListener onSpeedUpdateListener) {
        this.onSpeedUpdateListener = onSpeedUpdateListener;
    }

    /** Meters per second to kilometers per hour */
    private int mpsToKmh(float ms) {
        return Math.round((ms * 18) / 5);
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

    private LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (!location.hasSpeed()) {
                onSpeedUpdate(SPEED_UNKNOWN, location.getTime());
            }

            float speedMps = location.getSpeed();
            onSpeedUpdate(mpsToKmh(speedMps), location.getTime());
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        public void onProviderEnabled(String provider) {

        }

        public void onProviderDisabled(String provider) {

        }
    };
}
