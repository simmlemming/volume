package org.volume;

import android.content.Context;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.media.AudioManager.ADJUST_LOWER;
import static android.media.AudioManager.ADJUST_RAISE;
import static android.media.AudioManager.STREAM_MUSIC;

public class MainActivity extends AppCompatActivity implements VolumeManager.OnVolumeChangeListener, SpeedManager.OnSpeedUpdateListener {
    private TextView speedView, timeView, volLevelView;
    private View volUpView, volDownView;
    private CheckBox beepView;

    private VolumeManager volumeManager;
    private SpeedManager speedManager;

    private ToneGenerator beeper;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    private View.OnClickListener volumeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.vol_up:
                    volumeManager.onManualAdjust(speedManager.getCurrentSpeed(), ADJUST_RAISE);
                    break;

                case R.id.vol_down:
                    volumeManager.onManualAdjust(speedManager.getCurrentSpeed(), ADJUST_LOWER);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speedView = (TextView) findViewById(R.id.speed);
        timeView = (TextView) findViewById(R.id.time);
        volUpView = findViewById(R.id.vol_up);
        volDownView = findViewById(R.id.vol_down);
        volLevelView = (TextView) findViewById(R.id.vol_level);
        beepView = (CheckBox) findViewById(R.id.beep_ckeckbox);

        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volumeManager = new VolumeManager(audioManager);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        speedManager = new SpeedManager(locationManager);

        volumeManager.setOnVolumeChangeListener(this);
        speedManager.setOnSpeedUpdateListener(this);

        beeper = new ToneGenerator(STREAM_MUSIC, 100);

        volUpView.setOnClickListener(volumeClickListener);
        volDownView.setOnClickListener(volumeClickListener);

        int currentVolume = volumeManager.getCurrentVolume();
        volLevelView.setText(String.valueOf(currentVolume));
    }

    @Override
    protected void onResume() {
        super.onResume();
        speedManager.startListening();
    }

    @Override
    protected void onPause() {
        speedManager.stopListening();
        super.onPause();
    }

    @Override
    public void onVolumeChange(int oldLevel, int newLevel, int maxLevel) {
        volLevelView.setText(String.valueOf(newLevel));

        if (beepView.isChecked()) {
            boolean volumeIncreased = newLevel > oldLevel;
            int beep = volumeIncreased ? ToneGenerator.TONE_DTMF_B : ToneGenerator.TONE_DTMF_1;
            beeper.startTone(beep, 150);
        }
    }

    @Override
    public void onSpeedUpdate(int newSpeed, long time) {
        if (newSpeed == SpeedManager.SPEED_UNKNOWN) {
            speedView.setText("- km/h");
        } else {
            speedView.setText(newSpeed + " km/h");
        }

        timeView.setText(dateFormat.format(new Date(time)));
    }

    @Override
    public void onSpeedChange(int oldSpeed, int newSpeed, long time) {
        volumeManager.onSpeedChange(oldSpeed, newSpeed);
    }
}
