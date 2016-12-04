package de.appphil.webcamviewerwidget.link;


import android.content.Context;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LinkListIO {

    private static final String linklistFilename = "linklist.xml";
    private static final int versionCode = 6;

    /***
     * Saves the linklist to file.
     * @param context Context
     * @param linklist ArrayList with Link objects.
     * @param widgetId Id of the widget.
     * @throws IOException
     */
    public static void saveLinklist(Context context, ArrayList<Link> linklist, int widgetId) throws Exception {
        LinkList list = new LinkList(linklist, versionCode);

        Serializer serializer = new Persister();
        File folder = new File(context.getFilesDir() + "/" + widgetId);
        if(!folder.exists()) folder.mkdir();
        FileOutputStream outputStream = new FileOutputStream(new File(context.getFilesDir() + "/" + widgetId + "/" + linklistFilename));
        serializer.write(list, outputStream);
        outputStream.close();
    }

    /***
     * Saves the default app linklist to file.
     * @param context
     * @param linklist
     * @throws Exception
     */
    public static void saveLinklist(Context context, ArrayList<Link> linklist) throws Exception {
        saveLinklist(context, linklist, -1);
    }

    /***
     * Loads the linklist from file.
     * @param context Context
     * @param widgetId Id of the widget.
     * @return ArrayList with Link objects.
     * @throws Exception
     */
    public static ArrayList<Link> loadLinklist(Context context, int widgetId) throws Exception {
        // return empty linklist when there's no file
        if(!linklistFileExists(context, widgetId)) return new ArrayList<>();

        File folder = new File(context.getFilesDir() + "/" + widgetId);
        if(!folder.exists()) folder.mkdir();
        FileInputStream inputStream = new FileInputStream(new File(context.getFilesDir() + "/" + widgetId + "/" + linklistFilename));
        Serializer serializer = new Persister();
        LinkList list = serializer.read(LinkList.class, inputStream);

        return list.getList();
    }

    /***
     * Loads the default app linklist from file.
     * @param context
     * @return
     * @throws Exception
     */
    public static ArrayList<Link> loadLinklist(Context context) throws Exception {
        return loadLinklist(context, -1);
    }


    /***
     *
     * @param widgetId Id of the widget.
     * @return If the linklist file exists.
     */
    public static boolean linklistFileExists(Context context, int widgetId){
        return new File(context.getFilesDir() + "/" + widgetId + "/" + linklistFilename).exists();
    }

    /***
     * Gets the link by the given name.
     * @param context Context
     * @param name Name of the link.
     * @param widgetId Id of the widget.
     * @return Link or empty string if no link with the given name was found.
     */
    public static String getLinkByName(Context context, String name, int widgetId) {
        ArrayList<Link> linklist;
        try {
            linklist = loadLinklist(context, widgetId);
        } catch (Exception e) {
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
