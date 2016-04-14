package org.volume;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

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

    public boolean isStarted() {
        return noiseMeter != null;
    }

    public int getCurrentNoiseLevel() {
        return currentNoiseLevel;
    }

    public long getCurrentNoiseLevelDb() {
        return rawToDb(currentNoiseLevel);
    }

    private class NoiseMeter extends Thread {

        @Override
        public void run() {
            int minSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minSize);
            short[] buffer = new short[minSize];

            record.startRecording();

            while (!interrupted()) {
                int read = record.read(buffer, 0, minSize);
                currentNoiseLevel = maxAbs(buffer);
//                Log.i("Volume", read + " : " + currentNoiseLevel + " : " + rawToDb(currentNoiseLevel));
            }

            record.stop();
            currentNoiseLevel = 0;
        }

        private int maxAbs(short... array) {
            if (array.length == 0) {
                return 0;
            }

            int max = Math.abs(array[0]);

            for (int i = 1; i < array.length; i++) {
                int absI = Math.abs(array[i]);
                if (absI > max) {
                    max = absI;
                }
            }

            return max;
        }
    }

    public static long rawToDb(int raw) {
        return Math.round(20 * Math.log10(raw / 32768.0));
    }
}
