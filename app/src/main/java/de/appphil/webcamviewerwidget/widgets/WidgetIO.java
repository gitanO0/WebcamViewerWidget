package de.appphil.webcamviewerwidget.widgets;


import android.content.Context;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import de.appphil.webcamviewerwidget.widgets.switchwidget.SwitchWidgetSave;

public class WidgetIO {

    private static final String widgetSavesFilename = "widgetsaves.xml";

    /***
     * Returns if a widget with the given id is saved.
     * @param context Context.
     * @param id Id of the widget.
     * @return If the widget is saved.
     * @throws Exception
     */
    public static boolean isSwitchWidgetSaved(Context context, int id) throws Exception {
        ArrayList<SwitchWidgetSave> saves = getWidgetSaves(context).getSwitchWidgetSaves();

        for(SwitchWidgetSave save : saves) {
            if(save.getId() == id) {
                return true;
            }
        }
        return false;
    }

    /***
     * Returns the current link name of the switch widget with the given id.
     * @param context Context.
     * @param id Id of the SwitchWidget.
     * @return Name of the current link.
     * @throws Exception
     */
    public static String getCurrentLinkNameOfSwitchWidgetById(Context context, int id) throws Exception {
        ArrayList<SwitchWidgetSave> saves = getWidgetSaves(context).getSwitchWidgetSaves();

        for(SwitchWidgetSave save : saves) {
            if(save.getId() == id) {
                return save.getCurrentLinkName();
            }
        }
        return null;
    }

    /***
     * Updates the current link name of the widget with the given id.
     * @param context Context.
     * @param id Id of the widget that should be updated.
     * @param name Current link name that should be saved.
     * @throws Exception
     */
    public static void updateSwitchWidgetSaveCurrentLinkName(Context context, int id, String name) throws Exception {
        WidgetSaves widgetSaves = getWidgetSaves(context);
        ArrayList<SwitchWidgetSave> saves = widgetSaves.getSwitchWidgetSaves();

        int i = 0;
        for(SwitchWidgetSave s : saves) {
            if(s.getId() == id) {
                break;
            }
            i++;
        }
        SwitchWidgetSave oldSave = saves.get(i);
        SwitchWidgetSave newSave = new SwitchWidgetSave(id, name, oldSave.getAutoUpdate(), oldSave.getAutoUpdateInterval());
        saves.set(i, newSave);
        widgetSaves.setSwitchWidgetSaves(saves);
        saveWidgetSaves(context, widgetSaves);
    }

    /***
     * Adds a SwitchWidgetSave object to file.
     * @param context
     * @param save
     * @throws Exception
     */
    public static void addSwitchWidgetSave(Context context, SwitchWidgetSave save) throws Exception {
        WidgetSaves widgetSaves = getWidgetSaves(context);
        ArrayList<SwitchWidgetSave> saves = widgetSaves.getSwitchWidgetSaves();
        saves.add(save);
        widgetSaves.setSwitchWidgetSaves(saves);
        saveWidgetSaves(context, widgetSaves);
    }

    /***
     * Deletes the SwitchWidgetSave object with the given id from file.
     * This can be called when the widget gets removed from screen.
     * @param context
     * @param id
     * @throws Exception
     */
    public static void deleteSwitchWidgetSave(Context context, int id) throws Exception {
        WidgetSaves widgetSaves = getWidgetSaves(context);
        ArrayList<SwitchWidgetSave> saves = widgetSaves.getSwitchWidgetSaves();
        SwitchWidgetSave delete = null;
        for(SwitchWidgetSave save : saves) {
            if(save.getId() == id) {
                delete = save;
                break;
            }
        }
        if(delete != null) saves.remove(delete);
        widgetSaves.setSwitchWidgetSaves(saves);
        saveWidgetSaves(context, widgetSaves);
    }

    /***
     * Loads the WidgetSave object from file.
     * @param context
     * @return
     * @throws Exception
     */
    public static WidgetSaves getWidgetSaves(Context context) throws Exception {
        // return empty object when there's no file
        if(!widgetSavesFileExists(context)) return new WidgetSaves();

        FileInputStream inputStream = context.openFileInput(widgetSavesFilename);
        Serializer serializer = new Persister();
        WidgetSaves saves = serializer.read(WidgetSaves.class, inputStream);

        return saves;
    }

    /***
     * Saves the WidgetSaves object to file.
     * @param context
     * @param widgetSaves
     * @throws Exception
     */
    public static void saveWidgetSaves(Context context, WidgetSaves widgetSaves) throws Exception {
        Serializer serializer = new Persister();
        FileOutputStream outputStream = context.openFileOutput(widgetSavesFilename, Context.MODE_PRIVATE);
        serializer.write(widgetSaves, outputStream);
        outputStream.close();
    }


    /***
     *
     * @return If the widgetsaves file exists.
     */
    private static boolean widgetSavesFileExists(Context context){
        return context.getFileStreamPath(widgetSavesFilename).exists();
    }
}
