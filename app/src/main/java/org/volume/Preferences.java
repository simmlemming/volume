package org.volume;

import android.content.Context;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mtkachenko on 16/12/16.
 */

public class Preferences {
    private final Context context;

    public Preferences(Context context) {
        this.context = context;
    }

    public List<Integer> getSpeedThresholds() {
        ArrayList<Integer> thresholds = new ArrayList<>();

        String thresholdsFromPrefs = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_key_speed_thresholds), "");
        if (TextUtils.isEmpty(thresholdsFromPrefs)) {
            return thresholds;
        }

        String[] split = thresholdsFromPrefs.split(",");
        for (String threshold : split) {
            try {
                thresholds.add(Integer.parseInt(threshold.trim()));
            } catch (NumberFormatException e) {
                Log.e("Volume", "", e);
            }
        }

        return thresholds;
    }
}
