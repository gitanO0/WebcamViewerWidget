package de.appphil.webcamviewerwidget.widgets.singleautoupdatewidget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.appphil.webcamviewerwidget.R;
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

        // update widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        RemoteViews remoteViews = new RemoteViews(getApplication().getPackageName(), R.layout.widget_singleautoupdate);
        remoteViews.setImageViewBitmap(R.id.widget_sau_iv, bitmap);
        appWidgetManager.updateAppWidget(id, remoteViews);
    }
}
