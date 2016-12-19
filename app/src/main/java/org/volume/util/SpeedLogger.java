package org.volume.util;

import android.os.Environment;
import android.util.Log;

import org.volume.VolumeApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by mtkachenko on 21/10/16.
 */
public class SpeedLogger {
    private static final String DEFAULT_LOGFILE_NAME = "volume-should_never_be_used.log";
    private static final SimpleDateFormat logFileNameFormat = new SimpleDateFormat("'volume'-MM-dd-HH-mm'.log'", Locale.getDefault());
    private String logFileName = DEFAULT_LOGFILE_NAME;

    private boolean isEnabled = false;

    public void startSession() {
        if (!isEnabled) {
            Log.w(VolumeApplication.TAG, "Speed logging is disabled");
            return;
        }

        Date currentTime = Calendar.getInstance(TimeZone.getDefault()).getTime();
        logFileName = logFileNameFormat.format(currentTime);
    }

    public void stopSession() {
        logFileName = DEFAULT_LOGFILE_NAME;
    }

    private boolean isSessionStarted() {
        return !DEFAULT_LOGFILE_NAME.equals(logFileName);
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public void logSpeedChange(int oldSpeed, int newSpeed, int volume, int noiseLevel, long time) {
        if (!isSessionStarted()) {
            return;
        }

        File file = new File(Environment.getExternalStorageDirectory(), logFileName);
        try {
            FileOutputStream f = new FileOutputStream(file, true);
            PrintWriter pw = new PrintWriter(f);
            String logRecord = time + "," +
                    oldSpeed + "," +
                    newSpeed + "," +
                    volume + "," +
                    noiseLevel + "," +
                    MathUtils.rawNoiseLevelToDb(noiseLevel);
            pw.println(logRecord);
            pw.flush();
            pw.close();
            f.close();
        } catch (IOException e) {
            Log.e(VolumeApplication.TAG, "Cannot log speed change", e);
        }
    }
}
