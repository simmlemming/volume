package org.volume;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by mtkachenko on 12/04/16.
 */
public class HeadphonesUnpluggedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SpeedService.requestStopListening(context);
    }
}
