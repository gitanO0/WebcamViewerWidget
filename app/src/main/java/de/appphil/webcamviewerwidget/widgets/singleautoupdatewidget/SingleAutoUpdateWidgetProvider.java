package de.appphil.webcamviewerwidget.widgets.singleautoupdatewidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import de.appphil.webcamviewerwidget.R;
import de.appphil.webcamviewerwidget.activities.WidgetViewImageActivity;
import de.appphil.webcamviewerwidget.utils.Vars;


/**
 * Created by Philipp on 22.12.2016.
 */

public class SingleAutoUpdateWidgetProvider extends AppWidgetProvider {

    private static final String TAG = SingleAutoUpdateWidgetProvider.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate called");
        // Perform this loop procedure for each App Widget that belongs to this provider
        for(int appWidgetId : appWidgetIds) {
            Log.d(TAG, "AppWidgetId: " + appWidgetId);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_singleautoupdate);

            // reload image
            Intent intent = new Intent(context, SAUWidgetUpdateService.class);
            intent.putExtra("id", appWidgetId);
            context.startService(intent);

            Intent intentViewImage = new Intent(context, WidgetViewImageActivity.class);
            intentViewImage.putExtra(WidgetViewImageActivity.EXTRA_IMAGE_PATH, appWidgetId + "/" + Vars.SAU_IMAGE_FILENAME);
            PendingIntent piViewImage = PendingIntent.getActivity(context, appWidgetId, intentViewImage, PendingIntent.FLAG_CANCEL_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_sau_rl, piViewImage);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
