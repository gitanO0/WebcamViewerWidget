package de.appphil.webcamviewerwidget.services;

import android.app.IntentService;
import android.content.Intent;

import java.io.IOException;
import java.util.ArrayList;

import de.appphil.webcamviewerwidget.Link;
import de.appphil.webcamviewerwidget.LinkListIO;
import de.appphil.webcamviewerwidget.utils.CurrentLink;

public class WidgetSwitchLinkService extends IntentService{

    public WidgetSwitchLinkService() {
        super("WidgetSwitchLinkService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("Running WidgetSwitchLinkService now!");
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

        // get the position where the next link can be found in list
        int nextLinkPosition = getPositionOfNextLink(linklist, currentLinkPosition);

        // set new current link
        CurrentLink.saveCurrentLinkName(getApplicationContext(), linklist.get(nextLinkPosition).getName());

        // update the widget image (done by WidgetUpdateService)
        Intent updateService = new Intent(this, WidgetUpdateService.class);
        startService(updateService);
    }

    /***
     * Gets the position of the next link (the link that will should be shown after switch).
     * @param linklist ArrayList with Link objects.
     * @param currentLinkPosition Position of the current link in the list.
     * @return Position where the next link can be found in list.
     */
    private int getPositionOfNextLink(ArrayList<Link> linklist, int currentLinkPosition) {
        // get the position of the new name
        int positionNew = 0;
        if(currentLinkPosition == linklist.size()-1) {
            positionNew = 0;
        } else {
            positionNew = currentLinkPosition + 1;
        }
        return positionNew;
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
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
        ArrayList<Link> linklist = null;
        try {
            linklist = LinkListIO.loadLinklist(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return linklist;
    }
}
