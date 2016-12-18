package org.volume.manager;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import org.volume.util.MathUtils;

/**
 * Created by mtkachenko on 14/04/16.
 */
public class NoiseManager {
    private NoiseMeter noiseMeter;
    private volatile int currentNoiseLevel = 0;

    public void start() {
        if (noiseMeter != null) {
            return;
        }

        noiseMeter = new NoiseMeter();
        noiseMeter.start();
    }

    public void stop() {
        if (noiseMeter == null) {
            return;
        }

        noiseMeter.interrupt();
        noiseMeter = null;
    }

    public int getCurrentNoiseLevel() {
        return currentNoiseLevel;
    }

    public long getCurrentNoiseLevelDb() {
        return MathUtils.rawNoiseLevelToDb(currentNoiseLevel);
    }

    private class NoiseMeter extends Thread {

        @Override
        public void run() {
            int minSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minSize);
            short[] buffer = new short[minSize];

            record.startRecording();

            while (!interrupted()) {
                record.read(buffer, 0, minSize);
                currentNoiseLevel = MathUtils.maxAbs(buffer);
            }

            record.stop();
            currentNoiseLevel = 0;
        }
    }
}
