package de.appphil.webcamviewerwidget;


import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import de.appphil.webcamviewerwidget.utils.XMLManager;

public class LinkListIO {

    private static final String linklistFilename = "linklist.xml";

    /***
     * Saves the linklist to file.
     * @param context
     * @param linklist ArrayList with Link objects.
     * @throws IOException
     */
    public static void saveLinklist(Context context, ArrayList<Link> linklist) throws IOException {
        // linklist to xml string
        String xmlString = "<linklist>";
        for(Link link : linklist) {
            xmlString = xmlString + XMLManager.linkToXML(link);
        }
        xmlString = xmlString + "</linklist>";

        // save string to file
        FileOutputStream outputStream = context.openFileOutput(linklistFilename, Context.MODE_PRIVATE);
        outputStream.write(xmlString.getBytes());
        outputStream.close();
    }

    /***
     * Loads the linklist from file.
     * @param context
     * @return ArrayList with Link objects.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static ArrayList<Link> loadLinklist(Context context) throws IOException, ClassNotFoundException {
        ArrayList<Link> linklist = new ArrayList<Link>();

        // return empty linklist when there's no file
        if(!linklistFileExists(context)) return linklist;

        String xmlString = "";
        FileInputStream inputStream = context.openFileInput(linklistFilename);
        byte[] input = new byte[inputStream.available()];
        while (inputStream.read(input) != -1) {
        }
        xmlString = xmlString + new String(input);

        return XMLManager.getLinklistFromXML(xmlString);
    }


    /***
     *
     * @return If the linklist file exists.
     */
    public static boolean linklistFileExists(Context context){
        return context.getFileStreamPath(linklistFilename).exists();
    }

    /***
     * Gets the link by the given name.
     * @param context
     * @param name Name of the link.
     * @return Link or empty string if no link with the given name was found.
     */
    public static String getLinkByName(Context context, String name) {
        ArrayList<Link> linklist = null;
        try {
            linklist = loadLinklist(context);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return "";
        }
        for(Link link : linklist) {
            if(link.getName().equals(name)) {
                return link.getLink();
            }
        }
        return "";
    }
}
