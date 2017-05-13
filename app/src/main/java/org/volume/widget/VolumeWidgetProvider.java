package org.volume.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import org.volume.activity.MainActivity;
import org.volume.R;

import static org.volume.service.SpeedService.intentToStartManagingVolume;
import static org.volume.service.SpeedService.intentToStopManagingVolume;

/**
 * Created by mtkachenko on 12/04/16.
 */
public class VolumeWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = getRemoteViews(context, false, -1);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @NonNull
    public static RemoteViews getRemoteViews(Context context, boolean isActive, int volumeLevel) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

        views.setOnClickPendingIntent(R.id.start_stop, isActive ? intentToStopManagingVolume(context) : intentToStartManagingVolume(context));
        views.setImageViewResource(R.id.start_stop, isActive ? R.drawable.ic_on : R.drawable.ic_off);

        int textColor = isActive ? R.color.text_state_active : R.color.text_state_passive;
        views.setTextColor(R.id.level, context.getResources().getColor(textColor));
        views.setOnClickPendingIntent(R.id.level, MainActivity.intentToOpen(context));
        views.setTextViewText(R.id.level, isActive ? String.valueOf(volumeLevel) : "-");

        return views;
    }
}
