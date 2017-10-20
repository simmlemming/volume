package org.volume.util;

import android.app.Notification;
import android.content.Context;
import android.graphics.BitmapFactory;

import org.volume.R;
import org.volume.activity.MainActivity;
import org.volume.service.SpeedService;

/**
 * Created by mtkachenko on 20/10/17.
 */

public class NotificationFactory {

    public Notification newNotification(Context context) {
        Notification.Builder notification = new Notification.Builder(context);

        notification.setSmallIcon(R.drawable.ic_notification_small_icon);
        notification.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification_large_icon));
        notification.setContentTitle(context.getString(R.string.app_name));
        notification.setContentText(context.getString(R.string.notification_text));
        notification.setPriority(Notification.PRIORITY_LOW);
        notification.setCategory(Notification.CATEGORY_SYSTEM);

        notification.setContentIntent(MainActivity.intentToOpen(context));
        Notification.Action.Builder stop = new Notification.Action.Builder(null, context.getString(R.string.stop), SpeedService.intentToStopManagingVolume(context));
        notification.addAction(stop.build());

        return notification.build();
    }
}
