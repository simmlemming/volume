package org.volume;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.media.AudioManager.ADJUST_LOWER;
import static android.media.AudioManager.ADJUST_RAISE;

public class MainActivity extends AppCompatActivity implements VolumeManager.OnVolumeChangeListener, SpeedManager.OnSpeedUpdateListener {
    private TextView speedView, timeView, volLevelView;
    private View volUpView, volDownView, volChangeIndicatorView;

    private VolumeManager volumeManager;
    private SpeedManager speedManager;

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
        volChangeIndicatorView = findViewById(R.id.vol_change_indicator);

        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volumeManager = new VolumeManager(audioManager);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        speedManager = new SpeedManager(locationManager);

        volumeManager.setOnVolumeChangeListener(this);
        speedManager.setOnSpeedUpdateListener(this);

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

        int indicatorColor = oldLevel > newLevel ? R.color.vol_down_indicator : R.color.vol_up_indicator;
        volChangeIndicatorView.setBackgroundColor(getResources().getColor(indicatorColor));

        ObjectAnimator show = ObjectAnimator.ofFloat(volChangeIndicatorView, "alpha", 0, 1);
        show.setInterpolator(new AccelerateInterpolator());
        show.setDuration(150);

        ObjectAnimator hide = ObjectAnimator.ofFloat(volChangeIndicatorView, "alpha", 1, 0);
        hide.setInterpolator(new DecelerateInterpolator());
        hide.setDuration(350);

        volChangeIndicatorView.setVisibility(View.VISIBLE);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(show, hide);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                volChangeIndicatorView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animatorSet.start();
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
