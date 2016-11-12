package de.appphil.webcamviewerwidget.services;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.appphil.webcamviewerwidget.LinkListIO;
import de.appphil.webcamviewerwidget.R;
import de.appphil.webcamviewerwidget.WVWidgetProvider;
import de.appphil.webcamviewerwidget.utils.CurrentLink;

public class WidgetUpdateService extends IntentService {

    private static final String FILENAME = "image.png";

    public WidgetUpdateService() {
        super("WidgetUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("Running WidgetUpdateService now!");
        // get the current link
        String currentLink = getCurrentLink();
        if(currentLink.isEmpty()) return;

        System.out.println("Current Link is: " + currentLink);

        // download image from link and save it to internal storage
        Picasso picasso = Picasso.with(getApplicationContext());
        picasso.setLoggingEnabled(true);
        Bitmap bitmap = null;
        try {
            bitmap = picasso.load(currentLink).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(bitmap == null) return;

        try {
            File file = new File(getFilesDir() + "/" + FILENAME);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);

            out.flush();
            out.close();
        } catch(Exception e){
            e.printStackTrace();
        }

        // update widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        RemoteViews remoteViews = new RemoteViews(getApplication().getPackageName(), R.layout.widget_wv);
        ComponentName thisWidget = new ComponentName(getApplication(), WVWidgetProvider.class);
        remoteViews.setImageViewBitmap(R.id.widget_wv_iv, bitmap);
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);

    }

    /***
     * Tries to get the current link.
     * If there's a current link then it returns that one.
     * If there's no current link saved it returns an empty string.
     * @return String with link or empty when there's no current link saved.
     */
    private String getCurrentLink() {
        // get current link name
        String currentLinkName = "";
        try {
            currentLinkName = CurrentLink.getCurrentLinkName(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if(currentLinkName.isEmpty()) return currentLinkName;

        // get the link by the name and return it
        return LinkListIO.getLinkByName(getApplicationContext(), currentLinkName);
    }
}
