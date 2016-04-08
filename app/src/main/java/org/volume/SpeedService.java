package org.volume;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.ToneGenerator;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import static android.media.AudioManager.STREAM_MUSIC;

/**
 * Created by mtkachenko on 09/04/16.
 */
public class SpeedService extends Service implements SpeedManager.OnSpeedUpdateListener {
    private SpeedManager speedManager;
    private SpeedManager.OnSpeedUpdateListener onSpeedUpdateListener;

    private Handler handler = new Handler();
    private ToneGenerator beeper;

    @Override
    public void onCreate() {
        super.onCreate();

        beeper = new ToneGenerator(STREAM_MUSIC, 100);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        speedManager = new SpeedManager(locationManager);
        speedManager.setOnSpeedUpdateListener(this);
    }

    public SpeedManager getSpeedManager() {
        return speedManager;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        speedManager.stopListening();
        speedManager.setOnSpeedUpdateListener(null);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    public void setOnSpeedUpdateListener(SpeedManager.OnSpeedUpdateListener onSpeedUpdateListener) {
        this.onSpeedUpdateListener = onSpeedUpdateListener;
    }

    @Override
    public void onSpeedUpdate(int newSpeed, long time) {
        if (onSpeedUpdateListener != null) {
            onSpeedUpdateListener.onSpeedUpdate(newSpeed, time);
        }
    }

    @Override
    public void onSpeedChange(int oldSpeed, int newSpeed, long time) {
        if (onSpeedUpdateListener != null) {
            onSpeedUpdateListener.onSpeedChange(oldSpeed, newSpeed, time);
        }
    }

    @Override
    public void onStartListening() {
        beeper.startTone(MainActivity.TONE_VOLUME_LOWER, 150);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                beeper.startTone(MainActivity.TONE_VOLUME_RAISE, 150);
            }
        }, 300);

        if (onSpeedUpdateListener != null) {
            onSpeedUpdateListener.onStartListening();
        }
    }

    @Override
    public void onStopListening() {
        beeper.startTone(MainActivity.TONE_VOLUME_RAISE, 150);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                beeper.startTone(MainActivity.TONE_VOLUME_LOWER, 150);
            }
        }, 300);

        if (onSpeedUpdateListener != null) {
            onSpeedUpdateListener.onStopListening();
        }
    }

    public class LocalBinder extends Binder {
        public SpeedService getService() {
            return SpeedService.this;
        }
    }
}
