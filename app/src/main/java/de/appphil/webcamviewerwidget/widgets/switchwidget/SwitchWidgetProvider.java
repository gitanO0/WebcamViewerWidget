package de.appphil.webcamviewerwidget.widgets.switchwidget;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.Calendar;

import de.appphil.webcamviewerwidget.R;
import de.appphil.webcamviewerwidget.link.Link;
import de.appphil.webcamviewerwidget.widgets.WidgetIO;
import de.appphil.webcamviewerwidget.widgets.switchwidget.services.WidgetSwitchLinkService;
import de.appphil.webcamviewerwidget.widgets.switchwidget.services.WidgetUpdateService;
import de.appphil.webcamviewerwidget.activities.ViewImageActivity;
import de.appphil.webcamviewerwidget.utils.PendingIntents;
import de.appphil.webcamviewerwidget.utils.Vars;

public class SwitchWidgetProvider extends AppWidgetProvider {

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

        // Perform this loop procedure for each App Widget that belongs to this provider
        for(int appWidgetId : appWidgetIds) {
            System.out.println("AppWidgetId: " + appWidgetId);

            // check if a widget with this id is saved already
            try {
                if(WidgetIO.isSwitchWidgetSaved(context, appWidgetId)) {
                    // a widget with this id is already saved
                    System.out.println("Switch widget is already saved.");
                } else {
                    System.out.println("Switch widget is not saved yet.");
                    // this widget needs to be saved
                    // get a link to set as current link
                    String linkName = getEnabledLinkNameToSetAsCurrentLink(context, appWidgetId);
                    if(linkName.isEmpty()) return;
                    System.out.println("Link name: " + linkName);
                    WidgetIO.addSwitchWidgetSave(context, new SwitchWidgetSave(appWidgetId, linkName, false, 0));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_switch);

            /*
            Reload Button
             */
            Intent intentReload = new Intent(context, WidgetUpdateService.class);
            intentReload.putExtra("id", appWidgetId);
            PendingIntent piReload = PendingIntent.getService(context, appWidgetId, intentReload, PendingIntent.FLAG_CANCEL_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_wv_btn_reload, piReload);

            /*
            Left
             */
            Intent intentLeft = new Intent(context, WidgetSwitchLinkService.class);
            intentLeft.putExtra("left", true);
            intentLeft.putExtra("id", appWidgetId);
            PendingIntent piLeft = PendingIntent.getService(context, -appWidgetId, intentLeft, PendingIntent.FLAG_CANCEL_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_wv_btn_left, piLeft);

            /*
            Right
             */
            Intent intentRight = new Intent(context, WidgetSwitchLinkService.class);
            intentRight.putExtra("left", false);
            intentRight.putExtra("id", appWidgetId);
            PendingIntent piRight = PendingIntent.getService(context, appWidgetId, intentRight, PendingIntent.FLAG_CANCEL_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_wv_btn_right, piRight);

            /*
            ImageView
             */
            //
            //ViewImageActivity should be started when image view is clicked
            Intent intentViewImage = new Intent(context, ViewImageActivity.class);
            intentViewImage.putExtra("id", appWidgetId);
            PendingIntent piViewImage = PendingIntent.getActivity(context, appWidgetId, intentViewImage, PendingIntent.FLAG_CANCEL_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_wv_iv, piViewImage);


            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

        for(int id : appWidgetIds) {
            // remove the SwitchWidgetSave object with the given id from file
            try {
                WidgetIO.deleteSwitchWidgetSave(context, id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * Needed when a widget gets added to get a link to set as first current link.
     * Returns empty string when there's no enabled link.
     * @param context
     * @param id Id of the widget.
     * @return
     */
    private String getEnabledLinkNameToSetAsCurrentLink(Context context, int id) {
        // check if user added a link to the list yet
        if(LinkListIO.linklistFileExists(context, id)) {
            try {
                ArrayList<Link> linklist = LinkListIO.loadLinklist(context);
                for(Link link : linklist) {
                    if(link.isEnabled()) {
                        return link.getName();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
