package org.volume.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.ToneGenerator;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import org.volume.Preferences;
import org.volume.R;
import org.volume.VolumeApplication;
import org.volume.manager.NoiseManager;
import org.volume.manager.SpeedManager;
import org.volume.manager.VolumeManager;
import org.volume.util.SpeedLogger;
import org.volume.widget.VolumeWidgetProvider;

import javax.inject.Inject;

import static android.media.AudioManager.STREAM_MUSIC;

/**
 * Created by mtkachenko on 09/04/16.
 */
public class SpeedService extends Service implements SpeedManager.OnSpeedUpdateListener, VolumeManager.OnVolumeChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {
    public interface SpeedServiceListener {
        void onSpeedUpdate(int newSpeed);
        void onVolumeUpdate(int newVolume);
        void onStateUpdate(boolean isListening);
    }

    private static final String ACTION_STOP_LISTENING = "stop_listening";
    private static final String ACTION_START_LISTENING = "start_listening";

    private enum Part {
        VOLUME,
        SPEED,
        STATE
    }

    private static final int TONE_VOLUME_RAISE = ToneGenerator.TONE_DTMF_B;
    private static final int TONE_VOLUME_LOWER = ToneGenerator.TONE_DTMF_1;

    @Inject SpeedManager speedManager;
    @Inject Preferences preferences;
    @Inject VolumeManager volumeManager;
    @Inject NoiseManager noiseManager;
    @Inject SpeedLogger speedLogger;

    private Handler handler = new Handler();
    private ToneGenerator beeper;

    @Nullable
    private SpeedServiceListener listener;

    @Override
    public void onCreate() {
        super.onCreate();

        beeper = new ToneGenerator(STREAM_MUSIC, 75);

        getVolumeApplicationContext().getSpeedManagerComponent().inject(this);

        speedLogger.setEnabled(preferences.isLoggingEnabled());

        speedManager.setOnSpeedUpdateListener(this);
        volumeManager.setOnVolumeChangeListener(this);
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    public void startManagingVolume() {
        speedLogger.startSession();
        speedManager.startListening();
        noiseManager.start();
    }

    public void stopManagingVolume() {
        speedManager.stopListening();
        noiseManager.stop();
        speedLogger.stopSession();
    }

    public boolean isManagingVolume() {
        return speedManager.isListening();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }

        if (ACTION_STOP_LISTENING.equals(intent.getAction())) {
            stopManagingVolume();
        }

        if (ACTION_START_LISTENING.equals(intent.getAction())) {
            startManagingVolume();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        speedManager.stopListening();
        speedManager.setOnSpeedUpdateListener(null);
        volumeManager.setOnVolumeChangeListener(null);
        preferences.unregisterOnSharedPreferenceChangeListener(this);

        super.onDestroy();
    }

    @Override
    public void onSpeedUpdate(int newSpeed, long time) {
        notifyUpdated(Part.SPEED);
    }

    @Override
    public void onSpeedChange(int oldSpeed, int newSpeed, long time) {
        volumeManager.onSpeedChange(oldSpeed, newSpeed);
        speedLogger.logSpeedChange(oldSpeed, newSpeed, volumeManager.getCurrentVolume(), noiseManager.getCurrentNoiseLevel(), time);
    }

    @Override
    public void onVolumeChange(int oldLevel, int newLevel, int maxLevel) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean(getString(R.string.pref_key_beep), true)) {
            boolean volumeIncreased = newLevel > oldLevel;
            beep(volumeIncreased ? TONE_VOLUME_RAISE : TONE_VOLUME_LOWER, 0);
        }

        notifyUpdated(Part.VOLUME);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_key_speed_thresholds))) {
            volumeManager.setSpeedThresholds(preferences.getSpeedThresholds());
        }

        if (key.equals(getString(R.string.pref_key_log))) {
            boolean isLoggingEnabled = preferences.isLoggingEnabled();
            speedLogger.setEnabled(isLoggingEnabled);
        }
    }

    public void setListener(@Nullable SpeedServiceListener listener) {
        this.listener = listener;
    }

    public SpeedManager getSpeedManager() {
        return speedManager;
    }

    public VolumeManager getVolumeManager() {
        return volumeManager;
    }

    public NoiseManager getNoiseManager() {
        return noiseManager;
    }

    @Override
    public void onStartListening() {
        beep(TONE_VOLUME_LOWER, 0);
        beep(TONE_VOLUME_RAISE, 300);
        notifyUpdated(Part.STATE);

        volumeManager.setVolumePct(0.6666666f);
        notifyUpdated(Part.VOLUME);
    }

    @Override
    public void onStopListening() {
        beep(TONE_VOLUME_RAISE, 0);
        beep(TONE_VOLUME_LOWER, 300);

        notifyUpdated(Part.STATE);
    }

    private void notifyUpdated(Part state) {
        updateWidget();

        if (listener == null) {
            return;
        }

        switch (state) {
            case STATE:
                listener.onStateUpdate(speedManager.isListening());
                break;

            case VOLUME:
                listener.onVolumeUpdate(volumeManager.getCurrentVolume());
                break;

            case SPEED:
                listener.onSpeedUpdate(speedManager.getCurrentSpeed());
                break;

            default:
                break;
        }
    }

    private void updateWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName componentName = new ComponentName(getPackageName(), VolumeWidgetProvider.class.getName());

        appWidgetManager.updateAppWidget(componentName, VolumeWidgetProvider.getRemoteViews(this, isManagingVolume(), volumeManager.getCurrentVolume()));
    }

    public void requestUpdate() {
        for (Part part : Part.values()) {
            notifyUpdated(part);
        }
    }

    private void beep(final int tone, int delay) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                beeper.startTone(tone, 150);
            }
        }, delay);
    }

    private VolumeApplication getVolumeApplicationContext() {
        return (VolumeApplication) getApplicationContext();
    }

    public static PendingIntent intentToStartManagingVolume(Context context) {
        Intent speedService = new Intent(context, SpeedService.class);
        speedService.setAction(ACTION_START_LISTENING);
        return PendingIntent.getService(context, 0, speedService, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public static PendingIntent intentToStopManagingVolume(Context context) {
        Intent speedService = new Intent(context, SpeedService.class);
        speedService.setAction(ACTION_STOP_LISTENING);
        return PendingIntent.getService(context, 0, speedService, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    public class LocalBinder extends Binder {
        public SpeedService getService() {
            return SpeedService.this;
        }
    }
}
