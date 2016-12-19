package org.volume.util;

import android.media.ToneGenerator;
import android.os.Handler;

import static android.media.AudioManager.STREAM_MUSIC;

/**
 * Created by mtkachenko on 19/12/16.
 */

public class Beeper {
    private static final int TONE_VOLUME_RAISE = ToneGenerator.TONE_DTMF_B;
    private static final int TONE_VOLUME_LOWER = ToneGenerator.TONE_DTMF_1;

    private final ToneGenerator toneGenerator;
    private final Handler handler;

    public Beeper(Handler handler) {
        this.handler = handler;
        toneGenerator = new ToneGenerator(STREAM_MUSIC, 75);
    }

    public void beepStartTone() {
        beep(TONE_VOLUME_LOWER, 0);
        beep(TONE_VOLUME_RAISE, 300);
    }


    public void beepStopTone() {
        beep(TONE_VOLUME_RAISE, 0);
        beep(TONE_VOLUME_LOWER, 300);
    }

    public void beepVolumeChangeTone(boolean volumeRaised) {
        beep(volumeRaised ? TONE_VOLUME_RAISE : TONE_VOLUME_LOWER, 0);
    }

    private void beep(final int tone, int delay) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toneGenerator.startTone(tone, 150);
            }
        }, delay);
    }
}
