package org.volume.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.volume.service.SpeedService;

/**
 * Created by mtkachenko on 12/04/16.
 */
public class HeadphonesUnpluggedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            SpeedService.intentToStopManagingVolume(context).send();
        } catch (PendingIntent.CanceledException e) {
            Log.e("Volume", "Cannot stop managing volume", e);
        }
    }
}
