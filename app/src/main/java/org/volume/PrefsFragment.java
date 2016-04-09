package org.volume;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mtkachenko on 09/04/16.
 */
public class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        updateSummary();

        return view;
    }

    private void updateSummary() {
        String keySpeedThresholds = getString(R.string.pref_key_speed_thresholds);
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            Preference preference = getPreferenceScreen().getPreference(i);
            if (keySpeedThresholds.equals(preference.getKey())) {
                String summary = ((EditTextPreference)preference).getText();
                preference.setSummary(summary);
            }
        }
    }

    @Override
    public void onDestroyView() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);

        super.onDestroyView();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateSummary();
    }
}
