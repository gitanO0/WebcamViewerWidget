package de.appphil.webcamviewerwidget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import de.appphil.webcamviewerwidget.services.WidgetSwitchLinkService;
import de.appphil.webcamviewerwidget.services.WidgetUpdateService;

public class WVWidgetProvider extends AppWidgetProvider {

    @Override
    public void onEnabled(Context context) {
        System.out.println("Widget enabled!");
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
            PendingIntent piReload = PendingIntent.getService(context, 0, intentReload, 0);
            views.setOnClickPendingIntent(R.id.widget_wv_btn_reload, piReload);

            /*
            Switch Button
             */
            Intent intentSwitch = new Intent(context, WidgetSwitchLinkService.class);
            PendingIntent piSwitch = PendingIntent.getService(context, 0, intentSwitch, 0);
            views.setOnClickPendingIntent(R.id.widget_wv_btn_switch, piSwitch);

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
