package de.appphil.webcamviewerwidget.link;


import android.content.Context;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

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
     * @throws IOException
     */
    public static void saveLinklist(Context context, ArrayList<Link> linklist) throws Exception {
        LinkList list = new LinkList(linklist, versionCode);

        Serializer serializer = new Persister();
        FileOutputStream outputStream = context.openFileOutput(linklistFilename, Context.MODE_PRIVATE);
        serializer.write(list, outputStream);
        outputStream.close();
    }

    /***
     * Loads the linklist from file.
     * @param context Context
     * @return ArrayList with Link objects.
     * @throws Exception
     */
    public static ArrayList<Link> loadLinklist(Context context) throws Exception {
        // return empty linklist when there's no file
        if(!linklistFileExists(context)) return new ArrayList<Link>();

        FileInputStream inputStream = context.openFileInput(linklistFilename);
        Serializer serializer = new Persister();
        LinkList list = serializer.read(LinkList.class, inputStream);

        return list.getList();
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
     * @param context Context
     * @param name Name of the link.
     * @return Link or empty string if no link with the given name was found.
     */
    public static String getLinkByName(Context context, String name) {
        ArrayList<Link> linklist;
        try {
            linklist = loadLinklist(context);
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
