package org.volume.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.volume.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        initPreferences();

        return view;
    }

    private void initPreferences() {
        String keySpeedThresholds = getString(R.string.pref_key_speed_thresholds);
        String keyBtDevice = getString(R.string.pref_key_bt_device);

        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            Preference preference = getPreferenceScreen().getPreference(i);
            if (keySpeedThresholds.equals(preference.getKey())) {
                String summary = ((EditTextPreference)preference).getText();
                preference.setSummary(summary);
            }

            if (keyBtDevice.equals(preference.getKey())) {
                initBtDevicePreference((ListPreference)preference);
            }
        }
    }

    private void initBtDevicePreference(ListPreference preference) {
        List<String> entries = new ArrayList<>();
        List<String> entryValues = new ArrayList<>();

        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> devices = bluetooth.getBondedDevices();

        if (devices.isEmpty()) {
            preference.setEnabled(false);
            return;
        }

        for (BluetoothDevice device : devices) {
            entries.add(device.getName());
            entryValues.add(device.getAddress());
        }

        entries.add("<none>");
        entryValues.add("<none>");

        String[] entriesArray = new String[entries.size()];
        entries.toArray(entriesArray);
        preference.setEntries(entriesArray);

        String[] entryValuesArray = new String[entryValues.size()];
        entryValues.toArray(entryValuesArray);
        preference.setEntryValues(entryValuesArray);

        preference.setSummary(preference.getEntry());
    }

    @Override
    public void onDestroyView() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);

        super.onDestroyView();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        initPreferences();
    }
}
