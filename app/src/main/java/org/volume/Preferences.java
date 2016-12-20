package org.volume;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mtkachenko on 16/12/16.
 */

public class Preferences {
    private final SharedPreferences sharedPreferences;
    private final Resources resources;

    public Preferences(SharedPreferences sharedPreferences, Resources resources) {
        this.sharedPreferences = sharedPreferences;
        this.resources = resources;
    }

    public List<Integer> getSpeedThresholds() {
        ArrayList<Integer> thresholds = new ArrayList<>();

        String thresholdsFromPrefs = getSharedPreferences().getString(resources.getString(R.string.pref_key_speed_thresholds), "");
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

    public boolean beepOnSpeedChange() {
        String beep = resources.getString(R.string.pref_key_beep);
        return sharedPreferences.getBoolean(beep, true);
    }

    public void setBeepOnSpeedChange(boolean beep) {
        String key = resources.getString(R.string.pref_key_beep);
        sharedPreferences.edit()
                .putBoolean(key, beep)
                .apply();
    }

    public boolean isLoggingEnabled() {
        String log = resources.getString(R.string.pref_key_log);
        return getSharedPreferences().getBoolean(log, false);
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
    }

    private SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
}
