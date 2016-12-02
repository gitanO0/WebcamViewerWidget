package de.appphil.webcamviewerwidget.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import de.appphil.webcamviewerwidget.widgets.switchwidget.services.WidgetUpdateService;

public class PendingIntents {

    public static PendingIntent getUpdateWidgetPendingIntent(Context context, boolean sentByAlarm) {
        Intent intentReload = new Intent(context, WidgetUpdateService.class);
        intentReload.putExtra("sentByAlarm", sentByAlarm);
        return PendingIntent.getService(context, 1, intentReload, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
