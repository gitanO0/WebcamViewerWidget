package de.appphil.webcamviewerwidget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import de.appphil.webcamviewerwidget.utils.CurrentLink;

public class WVWidgetProvider extends AppWidgetProvider {

    private static final String SWITCH_CLICKED = "switch_clicked";
    private static final String RELOAD_CLICKED = "reload_clicked";
    private static final String IMAGE_CLICKED = "image_clicked";

    private int[] appWidgetIds;

    @Override
    public void onEnabled(Context context) {
        System.out.println("Widget enabled!");
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        System.out.println("onUpdate called");
        final int n = appWidgetIds.length;
        this.appWidgetIds = appWidgetIds;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i = 0; i < n; i++) {
            int appWidgetId = appWidgetIds[i];

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_wv);

            Picasso picasso = Picasso.with(context);

            // get current link name
            String currentLinkName = "";
            try {
                currentLinkName = CurrentLink.getCurrentLinkName(context);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            // load image from link when currentLinkName is not empty
            if(!currentLinkName.isEmpty()) {
                String link = LinkListIO.getLinkByName(context, currentLinkName);
                picasso.invalidate(link);
                picasso.setLoggingEnabled(true);
                picasso.load(link).into(views, R.id.widget_wv_iv, appWidgetIds);
            }

            views.setOnClickPendingIntent(R.id.widget_wv_btn_switch, getPendingSelfIntent(context, SWITCH_CLICKED));
            views.setOnClickPendingIntent(R.id.widget_wv_btn_reload, getPendingSelfIntent(context, RELOAD_CLICKED));

            Intent intent = new Intent(context, ViewImageActivity.class);
            PendingIntent pendingIntentToViewImage = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget_wv_iv, pendingIntentToViewImage);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    public void onReceive(Context context, Intent intent) {
        switch(intent.getAction()) {
            case SWITCH_CLICKED:
                switchClicked(context);
                break;
            case RELOAD_CLICKED:
                reloadClicked(context);
                break;
        }

        super.onReceive(context, intent);

    }

    /***
     * Gets called when the switch button is clicked.
     * Changes the current link name.
     * Calls onUpdate after that to reload the image.
     * @param context
     */
    private void switchClicked(Context context) {
        System.out.println("Switch clicked");

        // first: try to get the linklist
        ArrayList<Link> linklist = null;
        try {
            linklist = LinkListIO.loadLinklist(context);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if(linklist == null) return;
        // if there's only one link in the list the current link can't be switched
        if(linklist.size() < 2) return;

        // get the current link name
        String currentLinkName = "";
        try {
            currentLinkName = CurrentLink.getCurrentLinkName(context);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // get the position of the name in the array list
        int position = 0;
        for(Link link : linklist) {
            if(link.getName().equals(currentLinkName)) {
                break;
            }
            position++;
        }

        // get the position of the new name
        int positionNew = 0;
        if(position == linklist.size()-1) {
            positionNew = 0;
        } else {
            positionNew = position + 1;
        }

        // set new current link
        CurrentLink.saveCurrentLinkName(context, linklist.get(positionNew).getName());

        // call onUpdate
        ComponentName cn = new ComponentName( context, WVWidgetProvider.class );
        onUpdate(context, AppWidgetManager.getInstance(context), AppWidgetManager.getInstance(context).getAppWidgetIds(cn));
    }

    /***
     * Gets called when the reload button is clicked.
     * Calls onUpdate method which updates the image.
     * @param context
     */
    private void reloadClicked(Context context) {
        System.out.println("Reload clicked");
        ComponentName cn = new ComponentName( context, WVWidgetProvider.class );
        onUpdate(context, AppWidgetManager.getInstance(context), AppWidgetManager.getInstance(context).getAppWidgetIds(cn));
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
