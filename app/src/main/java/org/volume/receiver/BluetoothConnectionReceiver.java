package org.volume.receiver;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import org.volume.R;
import org.volume.VolumeApplication;
import org.volume.service.SpeedService;

/**
 * Created by mtkachenko on 17/04/16.
 */
public class BluetoothConnectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, -1);

        String address = getBtDeviceFromPreferences(context);
        if (state == BluetoothAdapter.STATE_CONNECTED && address.equals(device.getAddress())) {
            try {
                SpeedService.intentToStartManagingVolume(context).send();
            } catch (PendingIntent.CanceledException e) {
                Log.e(VolumeApplication.TAG, "Cannot start managing volume", e);
            }
        }
    }

    private String getBtDeviceFromPreferences(Context context) {
        String keyBtDevice = context.getString(R.string.pref_key_bt_device);
        return PreferenceManager.getDefaultSharedPreferences(context).getString(keyBtDevice, "does_not_exist");
    }
}
