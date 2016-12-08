package de.appphil.webcamviewerwidget.widgets.switchwidget.services;

import android.app.IntentService;
import android.content.Intent;

import java.util.ArrayList;

import de.appphil.webcamviewerwidget.db.LinkDbManager;
import de.appphil.webcamviewerwidget.db.SwitchWidgetLinksRow;
import de.appphil.webcamviewerwidget.link.Link;

public class WidgetSwitchLinkService extends IntentService{

    public WidgetSwitchLinkService() {
        super("WidgetSwitchLinkService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("Running WidgetSwitchLinkService now!");

        boolean left = intent.getBooleanExtra("left", false);
        if (left) {
            System.out.println("Button left clicked.");
        } else {
            System.out.println("Button right clicked.");
        }

        int id = intent.getIntExtra("id", 0);
        System.out.println("Switching on widget with id: " + id);

        LinkDbManager linkDbManager = new LinkDbManager(this);

        // get current link position
        int currentLinkPosition = linkDbManager.getSwitchWidgetCurrentLinkPosition(id);
        // get count of links in the widget linklist
        ArrayList<SwitchWidgetLinksRow> links = linkDbManager.getSwitchWidgetLinksRowsByWidgetId(id);
        int countOfLinks = links.size();
        // set new current link
        int newPosition;
        if(left) {
            if(currentLinkPosition == 0) {
                newPosition = countOfLinks-1;
            } else {
                newPosition = currentLinkPosition-1;
            }
        } else {
            if(currentLinkPosition == countOfLinks-1) {
                newPosition = 0;
            } else {
                newPosition = currentLinkPosition+1;
            }
        }
        linkDbManager.updateSwitchWidgetCurrentLinkPosition(id, newPosition);


        // update the widget image (done by WidgetUpdateService)
        Intent updateService = new Intent(this, WidgetUpdateService.class);
        updateService.putExtra("id", id);
        startService(updateService);
    }


}
