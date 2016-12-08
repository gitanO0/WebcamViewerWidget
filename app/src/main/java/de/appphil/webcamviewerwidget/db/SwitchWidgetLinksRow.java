package de.appphil.webcamviewerwidget.db;



public class SwitchWidgetLinksRow {

    private int switchWidgetId;
    private int linkId;
    private int pos;

    public SwitchWidgetLinksRow(int switchWidgetId, int linkId, int pos) {
        this.switchWidgetId = switchWidgetId;
        this.linkId = linkId;
        this.pos = pos;
    }

    public int getSwitchWidgetId() {
        return switchWidgetId;
    }

    public int getLinkId() {
        return linkId;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
