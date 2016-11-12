package de.appphil.webcamviewerwidget;

import java.io.Serializable;

public class Link implements Serializable {

    /***
     * Name of the link.
     */
    private String name;

    /***
     * Link to the image.
     */
    private String link;

    /***
     * Only needed for ExportActivity.
     */
    private transient boolean checked = false;

    /***
     * Constructor for Link.
     * @param name Name of the link.
     * @param link Link.
     */
    public Link(String name, String link) {
        this.name = name;
        this.link = link;
    }

    /***
     *
     * @return Name of the link.
     */
    public String getName() {
        return name;
    }

    /***
     *
     * @return Link.
     */
    public String getLink() {
        return link;
    }

    /***
     * Changes the checked state.
     * @param checked
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    /***
     * Returns if the link is checked.
     * @return
     */
    public boolean isChecked() {
        return checked;
    }
}
