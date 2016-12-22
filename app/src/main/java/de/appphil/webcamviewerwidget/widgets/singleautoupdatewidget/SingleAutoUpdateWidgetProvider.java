package de.appphil.webcamviewerwidget.widgets.singleautoupdatewidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


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

            // reload image
            Intent intent = new Intent(context, SAUWidgetUpdateService.class);
            intent.putExtra("id", appWidgetId);
            context.startService(intent);
        }
    }
}
