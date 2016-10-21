package org.volume.util;

import java.util.List;

/**
 * Created by mtkachenko on 21/10/16.
 */
public class MathUtils {
    public static int maxAbs(short... array) {
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

    public static long rawNoiseLevelToDb(int raw) {
        return Math.round(20 * Math.log10(raw / 32768.0));
    }

    /**
     * Meters per second to kilometers per hour
     */
    public static int mpsToKmh(float ms) {
        return Math.round((ms * 18) / 5);
    }

    public static boolean speedPassesThreshold(int oldSpeed, int newSpeed, List<Integer> speedThresholds) {
        for (int threshold : speedThresholds) {

            // This "times-ten" trick helps dealing with corner cases
            int os = oldSpeed * 10;
            int ns = newSpeed * 10;
            int th = threshold * 10 - 5;

            if (os < th && th < ns) {
                return true;
            }

            if (os > th && th > ns) {
                return true;
            }
        }

        return false;
    }

    public static float betweenZeroAndOne(float pct) {
        if (pct > 1) {
            return 1f;
        }

        if (pct < 0) {
            return  0f;
        }

        return pct;
    }
}
