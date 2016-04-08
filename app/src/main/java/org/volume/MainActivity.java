package org.volume;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.media.AudioManager.ADJUST_LOWER;
import static android.media.AudioManager.ADJUST_RAISE;
import static android.media.AudioManager.STREAM_MUSIC;

public class MainActivity extends AppCompatActivity implements VolumeManager.OnVolumeChangeListener, SpeedManager.OnSpeedUpdateListener {
    public static final int TONE_VOLUME_RAISE = ToneGenerator.TONE_DTMF_B;
    public static final int TONE_VOLUME_LOWER = ToneGenerator.TONE_DTMF_1;

    private TextView speedView, timeView, volLevelView;
    private View volUpView, volDownView;
    private CheckBox beepView;
    private Button startStopView;

    private VolumeManager volumeManager;
    private SpeedService speedService;

    private ToneGenerator beeper;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private String logFileName;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            speedService = ((SpeedService.LocalBinder)binder).getService();
            speedService.setOnSpeedUpdateListener(MainActivity.this);
            speedService.getSpeedManager().startListening();
            updateStartStopView();
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

            SpeedManager speedManager = speedService.getSpeedManager();

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

    private View.OnClickListener startStopClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (speedService == null) {
                return;
            }

            SpeedManager speedManager = speedService.getSpeedManager();

            if (speedManager.isListening()) {
                speedManager.stopListening();
            } else {
                speedManager.startListening();
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
        startStopView = (Button) findViewById(R.id.stop);

        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volumeManager = new VolumeManager(audioManager);

        SimpleDateFormat logFileNameFormat = new SimpleDateFormat("'volume'-MM-dd'.log'", Locale.getDefault());
        logFileName = logFileNameFormat.format(new Date());

        beeper = new ToneGenerator(STREAM_MUSIC, 100);

        volumeManager.setOnVolumeChangeListener(this);
        startStopView.setOnClickListener(startStopClickListener);

        volUpView.setOnClickListener(volumeClickListener);
        volDownView.setOnClickListener(volumeClickListener);

        int currentVolume = volumeManager.getCurrentVolume();
        volLevelView.setText(String.valueOf(currentVolume));

        Intent speedService = new Intent(this, SpeedService.class);
        startService(speedService);
        bindService(speedService, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if (speedService != null) {
            speedService.setOnSpeedUpdateListener(null);
        }

        unbindService(serviceConnection);
        super.onDestroy();
    }

    @Override
    public void onVolumeChange(int oldLevel, int newLevel, int maxLevel) {
        volLevelView.setText(String.valueOf(newLevel));

        if (beepView.isChecked()) {
            boolean volumeIncreased = newLevel > oldLevel;
            int beep = volumeIncreased ? TONE_VOLUME_RAISE : TONE_VOLUME_LOWER;
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

        int volume = volumeManager.getCurrentVolume();
        logChange(oldSpeed, newSpeed, volume, time);
    }

    @Override
    public void onStartListening() {
        updateStartStopView();
    }

    @Override
    public void onStopListening() {
        updateStartStopView();
    }

    private void updateStartStopView() {
        if (speedService == null) {
            startStopView.setText("-");
        } else {
            startStopView.setText(speedService.getSpeedManager().isListening() ? "STOP" : "START");
        }
    }

    private void logChange(int oldSpeed, int newSpeed, int volume, long time) {
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
}
