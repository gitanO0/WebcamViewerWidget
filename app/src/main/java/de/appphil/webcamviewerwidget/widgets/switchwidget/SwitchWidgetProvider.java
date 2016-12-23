package de.appphil.webcamviewerwidget.widgets.switchwidget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import de.appphil.webcamviewerwidget.R;
import de.appphil.webcamviewerwidget.activities.SwitchWidgetConfigActivity;
import de.appphil.webcamviewerwidget.db.LinkDbManager;
import de.appphil.webcamviewerwidget.utils.Vars;
import de.appphil.webcamviewerwidget.widgets.switchwidget.services.WidgetSwitchLinkService;
import de.appphil.webcamviewerwidget.widgets.switchwidget.services.WidgetUpdateService;
import de.appphil.webcamviewerwidget.activities.ViewImageActivity;

public class SwitchWidgetProvider extends AppWidgetProvider {

    private static final String TAG = SwitchWidgetProvider.class.getSimpleName();

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "Widget enabled!");
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    public void onUpdate(final Context context, AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        Log.d(TAG, "onUpdate called");

        // Perform this loop procedure for each App Widget that belongs to this provider
        for(int appWidgetId : appWidgetIds) {
            Log.d(TAG, "AppWidgetId: " + appWidgetId);

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
            Config button
             */
            Intent intentConfig = new Intent(context, SwitchWidgetConfigActivity.class);
            intentConfig.putExtra("id", appWidgetId);
            PendingIntent piConfig = PendingIntent.getActivity(context, appWidgetId, intentConfig, PendingIntent.FLAG_CANCEL_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_wv_btn_settings, piConfig);

            /*
            ImageView
             */
            //
            //ViewImageActivity should be started when image view is clicked
            Intent intentViewImage = new Intent(context, ViewImageActivity.class);
            intentViewImage.putExtra(ViewImageActivity.EXTRA_IMAGE_PATH, appWidgetId + "/" + Vars.IMAGE_FILENAME);
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
            // remove the SwitchWidget from db
            LinkDbManager linkDbManager = new LinkDbManager(context);
            linkDbManager.deleteSwitchWidget(id);
        }
    }
}
