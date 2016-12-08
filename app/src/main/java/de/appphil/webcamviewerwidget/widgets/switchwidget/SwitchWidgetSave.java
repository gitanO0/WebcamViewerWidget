package de.appphil.webcamviewerwidget.widgets.switchwidget;


public class SwitchWidgetSave {

    /***
     * AppWidgetId of the widget.
     */
    private int id;

    /***
     * Id of the link currently shown.
     */
    private int currentLinkId;

    /***
     * Constructor for SwitchWidgetSave.
     * @param id AppWidgetId of the widget.
     * @param currentLinkId Id of the link currently shown.
     */
    public SwitchWidgetSave(int id, int currentLinkId) {
        this.id = id;
        this.currentLinkId = currentLinkId;
    }

    /***
     * Returns the AppWidgetId of the widget.
     * @return AppWidgetId.
     */
    public int getId() {
        return id;
    }

    /***
     * Returns the Id of the currently shown link.
     * @return Id of the link.
     */
    public int getCurrentLinkId() {
        return currentLinkId;
    }

}
