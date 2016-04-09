package org.volume;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import static android.media.AudioManager.ADJUST_LOWER;
import static android.media.AudioManager.ADJUST_RAISE;

public class MainActivity extends AppCompatActivity implements SpeedService.SpeedServiceListener {
    public static final String PREFERENCES_NAME = "org.volume";
    public static final String PREF_KEY_BEEP = "beep";

    private TextView speedView, volLevelView;
    private Button startStopView;

    private SpeedService speedService;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            speedService = ((SpeedService.LocalBinder)binder).getService();

            speedService.setListener(MainActivity.this);
            speedService.startManagingVolume();

            speedService.requestUpdate();
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
            SharedPreferences.Editor preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).edit();
            preferences.putBoolean(PREF_KEY_BEEP, isChecked);
            preferences.apply();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speedView = (TextView) findViewById(R.id.speed);
        volLevelView = (TextView) findViewById(R.id.vol_level);
        startStopView = (Button) findViewById(R.id.stop);
        View volUpView = findViewById(R.id.vol_up);
        View volDownView = findViewById(R.id.vol_down);
        CheckBox beepView = (CheckBox) findViewById(R.id.beep_ckeckbox);

        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        beepView.setChecked(preferences.getBoolean(PREF_KEY_BEEP, true));

        startStopView.setOnClickListener(startStopClickListener);
        volUpView.setOnClickListener(volumeClickListener);
        volDownView.setOnClickListener(volumeClickListener);
        beepView.setOnCheckedChangeListener(beepCheckedChangeListener);

        Intent speedService = new Intent(this, SpeedService.class);
        startService(speedService);
        bindService(speedService, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onSpeedUpdate(int newSpeed) {
        if (newSpeed == SpeedManager.SPEED_UNKNOWN) {
            speedView.setText("- km/h");
        } else {
            speedView.setText(newSpeed + " km/h");
        }
    }

    @Override
    public void onVolumeUpdate(int newVolume) {
        volLevelView.setText(String.valueOf(newVolume));
    }

    @Override
    public void onStateUpdate(boolean isListening) {
        startStopView.setText(isListening ? "STOP" : "START");
    }

    @Override
    protected void onDestroy() {
        if (speedService != null) {
            speedService.setListener(null);
        }

        unbindService(serviceConnection);
        super.onDestroy();
    }
}
