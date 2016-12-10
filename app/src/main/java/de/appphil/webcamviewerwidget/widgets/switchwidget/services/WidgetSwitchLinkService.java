package de.appphil.webcamviewerwidget.widgets.switchwidget.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

import de.appphil.webcamviewerwidget.db.LinkDbManager;
import de.appphil.webcamviewerwidget.db.SwitchWidgetLinksRow;

public class WidgetSwitchLinkService extends IntentService{

    private static final String TAG = WidgetSwitchLinkService.class.getSimpleName();

    public WidgetSwitchLinkService() {
        super("WidgetSwitchLinkService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Running WidgetSwitchLinkService now!");

        boolean left = intent.getBooleanExtra("left", false);
        if (left) {
            Log.d(TAG, "Button left clicked.");
        } else {
            Log.d(TAG, "Button right clicked.");
        }

        int id = intent.getIntExtra("id", 0);
        Log.d(TAG, "Switching on widget with id: " + id);

        LinkDbManager linkDbManager = new LinkDbManager(this);

        // get current link position
        int currentLinkPosition = linkDbManager.getSwitchWidgetCurrentLinkPosition(id);
        Log.d(TAG, "currentLinkPosition: " + currentLinkPosition);
        // get count of links in the widget linklist
        ArrayList<SwitchWidgetLinksRow> links = linkDbManager.getSwitchWidgetLinksRowsByWidgetId(id);
        int countOfLinks = links.size();
        // set new current link
        int newPosition;
        if(left) {
            if(currentLinkPosition == 1) {
                newPosition = countOfLinks;
            } else {
                newPosition = currentLinkPosition-1;
            }
        } else {
            if(currentLinkPosition == countOfLinks) {
                newPosition = 1;
            } else {
                newPosition = currentLinkPosition+1;
            }
        }
        Log.d(TAG, "newPosition=" + newPosition);
        linkDbManager.updateSwitchWidgetCurrentLinkPosition(id, newPosition);


        // update the widget image (done by WidgetUpdateService)
        Intent updateService = new Intent(this, WidgetUpdateService.class);
        updateService.putExtra("id", id);
        startService(updateService);
    }


}
