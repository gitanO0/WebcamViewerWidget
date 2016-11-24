package de.appphil.webcamviewerwidget.widgets;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import java.util.Calendar;

import de.appphil.webcamviewerwidget.R;
import de.appphil.webcamviewerwidget.widgets.services.WidgetSwitchLinkService;
import de.appphil.webcamviewerwidget.widgets.services.WidgetUpdateService;
import de.appphil.webcamviewerwidget.activities.ViewImageActivity;
import de.appphil.webcamviewerwidget.utils.PendingIntents;
import de.appphil.webcamviewerwidget.utils.Vars;

public class WVWidgetProvider extends AppWidgetProvider {

    @Override
    public void onEnabled(Context context) {
        System.out.println("Widget enabled!");

        // check if auto update widget is enabled
        SharedPreferences sharedPref = context.getSharedPreferences(Vars.PREFS, Context.MODE_PRIVATE);
        if(sharedPref.getBoolean(Vars.AUTO_UPDATE_WIDGET, false)) {
            // widget should be updated automatically
            // get interval
            int interval = sharedPref.getInt(Vars.AUTO_UPDATE_INTERVAL, 1);
            setAlarm(context, interval);
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        // cancel alarm
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(PendingIntents.getUpdateWidgetPendingIntent(context, true));
    }

    private void setAlarm(Context context, int intervalInMin) {
        System.out.println("WVWidgetProvider sets alarm now.");
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * intervalInMin, PendingIntents.getUpdateWidgetPendingIntent(context, true));
    }

    public void onUpdate(final Context context, AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        System.out.println("onUpdate called");
        final int n = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i = 0; i < n; i++) {
            int appWidgetId = appWidgetIds[i];

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_wv);

            /*
            Reload Button
             */
            Intent intentReload = new Intent(context, WidgetUpdateService.class);
            PendingIntent piReload = PendingIntent.getService(context, 0, intentReload, PendingIntent.FLAG_CANCEL_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_wv_btn_reload, piReload);

            /*
            Left
             */
            Intent intentLeft = new Intent(context, WidgetSwitchLinkService.class);
            intentLeft.putExtra("left", true);
            PendingIntent piLeft = PendingIntent.getService(context, 0, intentLeft, PendingIntent.FLAG_CANCEL_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_wv_btn_left, piLeft);

            /*
            Right
             */
            Intent intentRight = new Intent(context, WidgetSwitchLinkService.class);
            intentRight.putExtra("left", false);
            PendingIntent piRight = PendingIntent.getService(context, 1, intentRight, PendingIntent.FLAG_CANCEL_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_wv_btn_right, piRight);

            /*
            ImageView
             */
            //
            //ViewImageActivity should be started when image view is clicked
            Intent intentViewImage = new Intent(context, ViewImageActivity.class);
            PendingIntent piViewImage = PendingIntent.getActivity(context, 0, intentViewImage, 0);
            views.setOnClickPendingIntent(R.id.widget_wv_iv, piViewImage);


            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
