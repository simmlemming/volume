package org.volume.activity;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.volume.R;
import org.volume.manager.SpeedManager;
import org.volume.service.SpeedService;
import org.volume.manager.VolumeManager;

import static android.media.AudioManager.ADJUST_LOWER;
import static android.media.AudioManager.ADJUST_RAISE;

public class MainActivity extends AppCompatActivity implements SpeedService.SpeedServiceListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private TextView speedView, noiseView, volLevelView, speedThresholdsView;
    private CheckBox beepView;
    private Button startStopView;

    private SpeedService speedService;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            speedService = ((SpeedService.LocalBinder)binder).getService();

            speedService.setListener(MainActivity.this);
            speedService.requestUpdate();

            updateSpeedThresholdsView();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            speedService = null;
        }
    };

    private View.OnClickListener volumeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (speedService == null) {
                return;
            }

            VolumeManager volumeManager = speedService.getVolumeManager();

            switch (v.getId()) {
                case R.id.vol_up:
                    volumeManager.onManualAdjust(ADJUST_RAISE);
                    break;

                case R.id.vol_down:
                    volumeManager.onManualAdjust(ADJUST_LOWER);
                    break;
            }
        }
    };

    private View.OnClickListener startStopClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (speedService == null) {
                return;
            }

            if (speedService.isManagingVolume()) {
                speedService.stopManagingVolume();
            } else {
                speedService.startManagingVolume();
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener beepCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
            preferences.putBoolean(getString(R.string.pref_key_beep), isChecked);
            preferences.apply();
        }
    };

    private View.OnClickListener speedThresholdsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openPreferences();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speedView = (TextView) findViewById(R.id.speed);
        noiseView = (TextView) findViewById(R.id.noise);
        speedThresholdsView = (TextView) findViewById(R.id.speed_thresholds);
        volLevelView = (TextView) findViewById(R.id.vol_level);
        startStopView = (Button) findViewById(R.id.stop);
        View volUpView = findViewById(R.id.vol_up);
        View volDownView = findViewById(R.id.vol_down);
        beepView = (CheckBox) findViewById(R.id.beep_ckeckbox);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        beepView.setChecked(preferences.getBoolean(getString(R.string.pref_key_beep), true));

        startStopView.setOnClickListener(startStopClickListener);
        volUpView.setOnClickListener(volumeClickListener);
        volDownView.setOnClickListener(volumeClickListener);
        beepView.setOnCheckedChangeListener(beepCheckedChangeListener);
        speedThresholdsView.setOnClickListener(speedThresholdsClickListener);

        preferences.registerOnSharedPreferenceChangeListener(this);

        Intent speedService = new Intent(this, SpeedService.class);
        startService(speedService);
        bindService(speedService, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.prefs) {
            openPreferences();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openPreferences() {
        Intent prefs = new Intent(this, PrefsActivity.class);
        startActivity(prefs);
    }

    @Override
    public void onSpeedUpdate(int newSpeed) {
        if (newSpeed == SpeedManager.SPEED_UNKNOWN) {
            speedView.setText(getString(R.string.speed_unknown));
            noiseView.setText(getString(R.string.noise_level_unknown));
        } else {
            speedView.setText(getString(R.string.speed, newSpeed));
            int noiseRaw = speedService.getNoiseManager().getCurrentNoiseLevel();
            long noiseDb = speedService.getNoiseManager().getCurrentNoiseLevelDb();
            noiseView.setText(getString(R.string.noise, noiseRaw, noiseDb));
        }
    }

    @Override
    public void onVolumeUpdate(int newVolume) {
        volLevelView.setText(String.valueOf(newVolume));
    }

    @Override
    public void onStateUpdate(boolean isListening) {
        startStopView.setText(isListening ? getString(R.string.stop) : getString(R.string.start));

        if (!isListening) {
            speedView.setText(getString(R.string.speed_unknown));
            noiseView.setText(getString(R.string.noise_level_unknown));
        }
    }

    @Override
    protected void onDestroy() {
        if (speedService != null) {
            speedService.setListener(null);
        }

        unbindService(serviceConnection);
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (getString(R.string.pref_key_beep).equals(key)) {
            beepView.setChecked(sharedPreferences.getBoolean(key, true));
        }

        if (getString(R.string.pref_key_speed_thresholds).equals(key)) {
            updateSpeedThresholdsView();
        }
    }

    private void updateSpeedThresholdsView() {
        String thresholds = String.valueOf(speedService.getVolumeManager().getSpeedThresholds());
        speedThresholdsView.setText(thresholds);
    }

    public static PendingIntent intentToOpen(Context context) {
        Intent activity = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context, 0, activity, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
