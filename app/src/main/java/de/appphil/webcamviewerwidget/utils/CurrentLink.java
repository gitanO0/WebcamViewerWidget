package de.appphil.webcamviewerwidget.utils;


import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import de.appphil.webcamviewerwidget.link.LinkListIO;

public class CurrentLink {

    private static final String PREFS = "prefs";
    private static final String CURRENT_LINK_NAME = "current_link_name";

    /***
     * Gets the current link name from shared preferences.
     * If there's no current link name saved it returns the first of the linklist or nothing.
     * @param context
     * @return Name of current link or empty string if no link added to list.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static String getCurrentLinkName(Context context) throws IOException, ClassNotFoundException {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if(sharedPref.contains(CURRENT_LINK_NAME)) {
            return sharedPref.getString(CURRENT_LINK_NAME, "");
        } else {
            // check if user added a link to the list yet
            if(LinkListIO.linklistFileExists(context)) {
                String currentLinkName = LinkListIO.loadLinklist(context).get(0).getName();
                // save to shared prefs
                saveCurrentLinkName(context, currentLinkName);
                return currentLinkName;
            } else {
                return "";
            }
        }
    }

    /***
     * Saves the given link name to shared preferences.
     * @param context
     * @param name Name of the link.
     */
    public static void saveCurrentLinkName(Context context, String name) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(CURRENT_LINK_NAME, name);
        editor.commit();
    }
}
