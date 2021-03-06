package de.appphil.webcamviewerwidget.widgets.switchwidget.services;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.appphil.webcamviewerwidget.R;
import de.appphil.webcamviewerwidget.db.LinkDbManager;
import de.appphil.webcamviewerwidget.link.Link;
import de.appphil.webcamviewerwidget.utils.Vars;
import de.appphil.webcamviewerwidget.widgets.switchwidget.SwitchWidgetProvider;

public class WidgetUpdateService extends IntentService {

    private static final String TAG = WidgetUpdateService.class.getSimpleName();

    public WidgetUpdateService() {
        super("WidgetUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Running WidgetUpdateService now!");

        String info = null;

        int id = intent.getIntExtra("id", 0);
        Log.d(TAG, "Updating widget with id: " + id);

        // show progress bar so that the users knows something gets loaded
        showProgressBar(id);

        LinkDbManager linkDbManager = new LinkDbManager(this);

        // get the current link
        Link currentLink = linkDbManager.getCurrentLinkBySwitchWidget(id);
        if(currentLink == null) {
            Log.d(TAG, "LinkDbManager returned a null object as currentLink.");
            updateFailed(id);
            return;
        }
        String currentLinkLink = currentLink.getLink();
        if(currentLinkLink.isEmpty())  {
            Log.d(TAG, "Link object has no link string.");
            updateFailed(id);
            return;
        }

        Log.d(TAG, "Current Link is: " + currentLinkLink);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        RemoteViews remoteViews = new RemoteViews(getApplication().getPackageName(), R.layout.widget_switch);
        updateWidgetInfoText(currentLink.getName() + ":", id);

        // download image from link and save it to internal storage
        Picasso picasso = Picasso.with(getApplicationContext());
        picasso.setLoggingEnabled(true);
        Bitmap bitmap = null;
        try {
            bitmap = picasso.load(currentLinkLink).get();
        } catch (IOException e) {
            info = getResources().getString(R.string.download_failed);
            updateWidgetInfoText(info, id);
            e.printStackTrace();
        }

        if(bitmap == null) {
            Log.d(TAG, "Bitmap is null.");
            updateFailed(id);
            return;
        }

        try {
            File folder = new File(getFilesDir() + "/" + id);
            if(!folder.exists()) folder.mkdir();
            File file = new File(getFilesDir() + "/" + id + "/" + Vars.IMAGE_FILENAME);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);

            out.flush();
            out.close();
        } catch(Exception e){
            info = getResources().getString(R.string.loading_failed);
            updateWidgetInfoText(info, id);
            e.printStackTrace();
        }

        // progress bar is not needed anymore
        hideProgressBar(id);

        int bytes = bitmap.getByteCount();
        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        double maxBytes = width * height * 4 * 1.5;
        while(bytes > maxBytes) {
            bitmap = Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth()*0.9), (int)(bitmap.getHeight()*0.9), false);
            bytes = bitmap.getByteCount();
        }

        remoteViews.setImageViewBitmap(R.id.widget_wv_iv, bitmap);

        SwitchWidgetProvider.setOnClickPendingIntents(getApplicationContext(), remoteViews, id);

        appWidgetManager.updateAppWidget(id, remoteViews);
    }

    /***
     * Changes the info text in the widget.
     * @param text Text that should be shown.
     * @param id Id of the widget.
     */
    private void updateWidgetInfoText(String text, int id) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        RemoteViews remoteViews = new RemoteViews(getApplication().getPackageName(), R.layout.widget_switch);
        remoteViews.setTextViewText(R.id.widget_wv_tv_info, text);
        appWidgetManager.updateAppWidget(id, remoteViews);
    }

    /***
     * Shows the progress bar.
     * @param id
     */
    private void showProgressBar(int id) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        RemoteViews remoteViews = new RemoteViews(getApplication().getPackageName(), R.layout.widget_switch);
        remoteViews.setViewVisibility(R.id.widget_wv_pb, View.VISIBLE);
        appWidgetManager.updateAppWidget(id, remoteViews);
    }

    /***
     * Hides the progress bar again.
     * @param id
     */
    private void hideProgressBar(int id) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        RemoteViews remoteViews = new RemoteViews(getApplication().getPackageName(), R.layout.widget_switch);
        remoteViews.setViewVisibility(R.id.widget_wv_pb, View.GONE);
        appWidgetManager.updateAppWidget(id, remoteViews);
    }


    /***
     * Hides the progress bar of the widget with the given id
     * and changes the info text to "Download failed".
     * @param id
     */
    private void updateFailed(int id) {
        hideProgressBar(id);
        updateWidgetInfoText(getResources().getString(R.string.download_failed), id);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        RemoteViews remoteViews = new RemoteViews(getApplication().getPackageName(), R.layout.widget_switch);

        SwitchWidgetProvider.setOnClickPendingIntents(getApplicationContext(), remoteViews, id);

        appWidgetManager.updateAppWidget(id, remoteViews);
    }

}
