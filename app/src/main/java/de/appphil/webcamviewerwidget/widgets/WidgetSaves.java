package de.appphil.webcamviewerwidget.widgets;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

import de.appphil.webcamviewerwidget.widgets.switchwidget.SwitchWidgetSave;

@Root(name="widgetsaves")
public class WidgetSaves {

    @ElementList(name="switchwidgetlist")
    private ArrayList<SwitchWidgetSave> switchWidgetSaves;

    public WidgetSaves(@ElementList(name="switchwidgetlist") ArrayList<SwitchWidgetSave>switchWidgetSaves) {
        this.switchWidgetSaves = switchWidgetSaves;
    }

    public WidgetSaves() {
        switchWidgetSaves = new ArrayList<SwitchWidgetSave>();
    }

    public ArrayList<SwitchWidgetSave> getSwitchWidgetSaves() {
        return switchWidgetSaves;
    }

    public void setSwitchWidgetSaves(ArrayList<SwitchWidgetSave> saves) {
        this.switchWidgetSaves = saves;
    }

}
