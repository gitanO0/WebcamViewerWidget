package de.appphil.webcamviewerwidget.widgets.singleautoupdatewidget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.appphil.webcamviewerwidget.R;
import de.appphil.webcamviewerwidget.activities.ViewImageActivity;
import de.appphil.webcamviewerwidget.db.LinkDbManager;
import de.appphil.webcamviewerwidget.link.Link;
import de.appphil.webcamviewerwidget.utils.Vars;


/**
 * Created by Philipp on 22.12.2016.
 */

public class SAUWidgetUpdateService extends IntentService {

    private static final String TAG = SAUWidgetUpdateService.class.getSimpleName();

    public SAUWidgetUpdateService() {
        super("SAUWidgetUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Running SAUWidgetUpdateService now!");

        int id = intent.getIntExtra("id", 0);
        Log.d(TAG, "Updating widget with id: " + id);

        LinkDbManager linkDbManager = new LinkDbManager(this);
        // get link id of this widget
        int linkId = linkDbManager.getLinkIdBySingleAutoUpdateWidgetId(id);
        if(linkId == -1) return;
        // get link by id
        Link l = linkDbManager.getLinkById(linkId);
        if(l == null) return;
        String link = l.getLink();

        Log.d(TAG, "Link is: " + link);

        showProgressBar(id);

        // download image from link and save it to internal storage
        Picasso picasso = Picasso.with(getApplicationContext());
        picasso.setLoggingEnabled(true);
        Bitmap bitmap = null;
        try {
            bitmap = picasso.load(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(bitmap == null) {
            Log.d(TAG, "Bitmap is null.");
            hideProgressBar(id);
            return;
        }

        // save
        try {
            File folder = new File(getFilesDir() + "/" + id);
            if(!folder.exists()) folder.mkdir();
            File file = new File(getFilesDir() + "/" + id + "/" + Vars.SAU_IMAGE_FILENAME);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);

            out.flush();
            out.close();
        } catch(Exception e){
            e.printStackTrace();
        }

        hideProgressBar(id);

        // update widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        RemoteViews remoteViews = new RemoteViews(getApplication().getPackageName(), R.layout.widget_singleautoupdate);
        remoteViews.setImageViewBitmap(R.id.widget_sau_iv, bitmap);

        Intent intentViewImage = new Intent(getApplicationContext(), ViewImageActivity.class);
        intentViewImage.putExtra(ViewImageActivity.EXTRA_IMAGE_PATH, id + "/" + Vars.SAU_IMAGE_FILENAME);
        PendingIntent piViewImage = PendingIntent.getActivity(getApplicationContext(), id, intentViewImage, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_sau_rl, piViewImage);

        appWidgetManager.updateAppWidget(id, remoteViews);
    }

    /***
     * Shows the progress bar.
     * @param id
     */
    private void showProgressBar(int id) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        RemoteViews remoteViews = new RemoteViews(getApplication().getPackageName(), R.layout.widget_singleautoupdate);
        remoteViews.setViewVisibility(R.id.widget_sau_pb, View.VISIBLE);
        appWidgetManager.updateAppWidget(id, remoteViews);
    }

    /***
     * Hides the progress bar again.
     * @param id
     */
    private void hideProgressBar(int id) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        RemoteViews remoteViews = new RemoteViews(getApplication().getPackageName(), R.layout.widget_singleautoupdate);
        remoteViews.setViewVisibility(R.id.widget_sau_pb, View.GONE);
        appWidgetManager.updateAppWidget(id, remoteViews);
    }
}
