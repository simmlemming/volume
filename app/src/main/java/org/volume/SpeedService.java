package org.volume;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    private SpeedManager speedManager;
    private VolumeManager volumeManager;

    private Handler handler = new Handler();
    private ToneGenerator beeper;

    @Nullable
    private SpeedServiceListener listener;

    @Override
    public void onCreate() {
        super.onCreate();

        beeper = new ToneGenerator(STREAM_MUSIC, 100);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        speedManager = new SpeedManager(locationManager);

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volumeManager = new VolumeManager(audioManager, getSpeedThresholds());

        speedManager.setOnSpeedUpdateListener(this);
        volumeManager.setOnVolumeChangeListener(this);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    public void startManagingVolume() {
        speedManager.startListening();
    }

    public void stopManagingVolume() {
        speedManager.stopListening();
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
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onSpeedUpdate(int newSpeed, long time) {
        notifyUpdated(Part.SPEED);
    }

    @Override
    public void onSpeedChange(int oldSpeed, int newSpeed, long time) {
        volumeManager.onSpeedChange(oldSpeed, newSpeed);
        logChange(oldSpeed, newSpeed, volumeManager.getCurrentVolume(), time);
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
            volumeManager.setSpeedThresholds(getSpeedThresholds());
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

    private List<Integer> getSpeedThresholds() {
        ArrayList<Integer> thresholds = new ArrayList<>();

        String thresholdsFromPrefs = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_key_speed_thresholds), "");
        if (TextUtils.isEmpty(thresholdsFromPrefs)) {
            return thresholds;
        }

        String[] split = thresholdsFromPrefs.split(",");
        for (String threshold : split) {
            try {
                thresholds.add(Integer.parseInt(threshold.trim()));
            } catch (NumberFormatException e) {
                Log.e("Volume", "", e);
            }
        }

        return thresholds;
    }

    private SimpleDateFormat logFileNameFormat = new SimpleDateFormat("'volume'-MM-dd'.log'", Locale.getDefault());
    private void logChange(int oldSpeed, int newSpeed, int volume, long time) {
        String logFileName = logFileNameFormat.format(new Date());

        File file = new File(Environment.getExternalStorageDirectory(), logFileName);
        try {
            FileOutputStream f = new FileOutputStream(file, true);
            PrintWriter pw = new PrintWriter(f);
            String logRecord = time + "," + oldSpeed + "," + newSpeed + "," + volume;
            pw.println(logRecord);
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "File " + file.getAbsolutePath() + " not found", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
