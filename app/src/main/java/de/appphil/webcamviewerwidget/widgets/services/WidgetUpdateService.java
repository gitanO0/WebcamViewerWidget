package de.appphil.webcamviewerwidget.widgets.services;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.appphil.webcamviewerwidget.R;
import de.appphil.webcamviewerwidget.link.LinkListIO;
import de.appphil.webcamviewerwidget.widgets.WVWidgetProvider;
import de.appphil.webcamviewerwidget.utils.CurrentLink;
import de.appphil.webcamviewerwidget.utils.Vars;

public class WidgetUpdateService extends IntentService {

    public WidgetUpdateService() {
        super("WidgetUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("Running WidgetUpdateService now!");

        String info = null;

        // check if there's an internet connection
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(isConnected) {
            // check if update was sent by an alarm
            if(intent.hasExtra("sentByAlarm")) {
                // check if widget should only be updated with wifi connection
                SharedPreferences sharedPref = this.getSharedPreferences(Vars.PREFS, Context.MODE_PRIVATE);
                boolean onlyUpdateWithWifi = sharedPref.getBoolean(Vars.ONLY_UPDATE_WITH_WIFI, true);
                if(onlyUpdateWithWifi) {
                    // check if wifi is connected
                    boolean isWifi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
                    if(isWifi) {
                        // update
                    } else {
                        // don't update
                        System.out.println("WidgetUpdateService: Not updated widget because it should only update automatically with a wifi connection and there's none now.");
                        return;
                    }
                }
            } else {
                System.out.println("WidgetUpdateService: intent has't got sentByAlarm intent.");
                // update sent by user clicking on "reload" button so also update without wifi
            }
            boolean isWifi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
            if(isWifi) {
                // can update
            } else {

            }
        } else {
            System.out.println("WidgetUpdateService: Not updated widget because there's no internet connection available.");
            return;
        }

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
            info = getResources().getString(R.string.download_failed);
            updateWidgetInfoText(info);
            e.printStackTrace();
        }

        if(bitmap == null) return;

        try {
            File file = new File(getFilesDir() + "/" + Vars.IMAGE_FILENAME);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);

            out.flush();
            out.close();
        } catch(Exception e){
            info = getResources().getString(R.string.loading_failed);
            updateWidgetInfoText(info);
            e.printStackTrace();
        }

        if(info == null) {
            info = getCurrentLinkName() + ":";
        }

        // update widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        RemoteViews remoteViews = new RemoteViews(getApplication().getPackageName(), R.layout.widget_wv);
        ComponentName thisWidget = new ComponentName(getApplication(), WVWidgetProvider.class);
        remoteViews.setImageViewBitmap(R.id.widget_wv_iv, bitmap);
        remoteViews.setTextViewText(R.id.widget_wv_tv_info, info);
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);

    }

    /***
     * Changes the info text in the widget.
     * @param text
     */
    private void updateWidgetInfoText(String text) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        RemoteViews remoteViews = new RemoteViews(getApplication().getPackageName(), R.layout.widget_wv);
        ComponentName thisWidget = new ComponentName(getApplication(), WVWidgetProvider.class);
        remoteViews.setTextViewText(R.id.widget_wv_tv_info, text);
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
        String currentLinkName = getCurrentLinkName();

        if(currentLinkName.isEmpty()) return currentLinkName;

        // get the link by the name and return it
        return LinkListIO.getLinkByName(getApplicationContext(), currentLinkName);
    }

    /***
     * Tries to get the current link name.
     * If there's no current link saved it returns an empty string.
     * @return
     */
    private String getCurrentLinkName() {
        String currentLinkName = "";
        try {
            currentLinkName = CurrentLink.getCurrentLinkName(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return currentLinkName;
    }
}
