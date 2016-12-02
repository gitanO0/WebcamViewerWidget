package de.appphil.webcamviewerwidget.widgets.switchwidget;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="switchwidget")
public class SwitchWidgetSave {

    /***
     * AppWidgetId of the widget.
     */
    @Attribute(name="id")
    private int id;

    /***
     * Name of the link currently shown.
     */
    @Element(name="currentlinkname", required=false)
    private String currentLinkName;

    /***
     * If the widget should be automatically updated.
     */
    @Element(name="autoupdate")
    private boolean autoUpdate;

    /***
     * The interval in seconds after which the widget should be updated.
     */
    @Element(name="autoupdateinterval")
    private int autoUpdateInterval;

    /***
     * Constructor for SwitchWidgetSave.
     * @param id AppWidgetId of the widget.
     * @param currentLinkName Name of the link currently shown.
     * @param autoUpdate If the widget should be automatically updated.
     * @param autoUpdateInterval The interval in seconds after which the widget should be updated.
     */
    public SwitchWidgetSave(@Attribute(name="id")int id, @Element(name="currentlinkname", required=false)String currentLinkName,
                            @Element(name="autoupdate")boolean autoUpdate, @Element(name="autoupdateinterval")int autoUpdateInterval) {
        this.id = id;
        this.currentLinkName = currentLinkName;
        this.autoUpdate = autoUpdate;
        this.autoUpdateInterval = autoUpdateInterval;
    }

    /***
     * Returns the AppWidgetId of the widget.
     * @return AppWidgetId.
     */
    public int getId() {
        return id;
    }

    /***
     * Returns the name of the currently shown link.
     * @return Name of the link.
     */
    public String getCurrentLinkName() {
        return currentLinkName;
    }

    /***
     * Returns if the widget should be updated automatically.
     * @return If widget should be updated automatically.
     */
    public boolean getAutoUpdate() {
        return autoUpdate;
    }

    /***
     * Returns the interval after which the widget should be updated.
     * @return Update interval in seconds.
     */
    public int getAutoUpdateInterval() {
        return autoUpdateInterval;
    }

}
