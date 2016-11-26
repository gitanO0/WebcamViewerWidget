package de.appphil.webcamviewerwidget.widgets.services;

import android.app.IntentService;
import android.content.Intent;

import java.io.IOException;
import java.util.ArrayList;

import de.appphil.webcamviewerwidget.link.Link;
import de.appphil.webcamviewerwidget.link.LinkListIO;
import de.appphil.webcamviewerwidget.utils.CurrentLink;

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

        // get list with all links
        ArrayList<Link> linklist = getLinkList();
        if(linklist == null) return;

        // if there's only one link in the list the current link can't be switched
        if(linklist.size() < 2) return;

        // get the current link name
        String currentLinkName = getCurrentLinkName();
        if(currentLinkName.isEmpty()) return;

        // get the position of the link in the list
        int currentLinkPosition = getPositionOfLinkName(linklist, currentLinkName);

        if(containsEnabledLink(linklist)) {
            String nextLinkName = getNextLinkName(linklist, currentLinkPosition, left);
            // set new current link
            CurrentLink.saveCurrentLinkName(getApplicationContext(), nextLinkName);
        } else {
            // there's no activated link so the current link can't be switched
            return;
        }

        // update the widget image (done by WidgetUpdateService)
        Intent updateService = new Intent(this, WidgetUpdateService.class);
        startService(updateService);
    }

    /***
     * Gets the position of the given link name in the given list.
     * @param linklist Array with Link objects.
     * @param currentLinkName Name of the current link.
     * @return Position of the link name in the list as int.
     */
    private int getPositionOfLinkName(ArrayList<Link> linklist, String currentLinkName) {
        int position = 0;
        for(Link link : linklist) {
            if(link.getName().equals(currentLinkName)) {
                break;
            }
            position++;
        }
        return position;
    }

    /***
     * Tries to get the current link name.
     * @return Name as String or empty String.
     */
    private String getCurrentLinkName() {
        String currentLinkName = "";
        try {
            currentLinkName = CurrentLink.getCurrentLinkName(getApplicationContext());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return currentLinkName;
    }

    /***
     * Tries to get the linklist from file.
     * If there's a linklist saved it returns it.
     * If there's no linklist saved it returns null.
     * @return ArrayList with Link objects or null.
     */
    private ArrayList<Link> getLinkList() {
        try {
            return LinkListIO.loadLinklist(getApplicationContext());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * Returns the name of the next enabled link.
     * @param linklist ArrayList containing all the Link objects.
     * @param currentLinkPosition Position of the current link in the ArrayList.
     * @param moveLeft If the user clicked the left or right button.
     * @return Name of the next link as string.
     */
    private String getNextLinkName(ArrayList<Link> linklist, int currentLinkPosition, boolean moveLeft) {
        int positionNew;
        if((currentLinkPosition == linklist.size()-1 && !moveLeft)) {
            positionNew = 0;
        } else if(currentLinkPosition == 0 && moveLeft) {
            positionNew = linklist.size() - 1;
        } else {
            if(!moveLeft) {
                positionNew = currentLinkPosition + 1;
            } else {
                positionNew = currentLinkPosition - 1;
            }
        }
        // check if that link is disabled
        if(!linklist.get(positionNew).isEnabled()) {
            return getNextLinkName(linklist, positionNew, moveLeft);
        } else {
            // link is enabled
            return linklist.get(positionNew).getName();
        }
    }

    /***
     * Checks if the given list contains an enabled link.
     * @param linklist ArrayList containing all the Link objects.
     * @return If the given linklist contains one or more enabled links.
     */
    private boolean containsEnabledLink(ArrayList<Link> linklist) {
        for(Link link : linklist) {
            if(link.isEnabled()) return true;
        }
        return false;
    }
}
