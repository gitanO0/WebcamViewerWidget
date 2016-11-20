package de.appphil.webcamviewerwidget;

public class Link {

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
    private boolean checked = false;

    /***
     * Needed to check if the link is activated or deactivated.
     */
    private boolean activated;

    /***
     * Constructor for Link.
     * @param name Name of the link.
     * @param link Link.
     * @param activated If the link is activated or not.
     */
    public Link(String name, String link, boolean activated) {
        this.name = name;
        this.link = link;
        this.activated = activated;
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

    /***
     * Returns if the link is activated or not.
     * @return
     */
    public boolean isActivated() {
        return activated;
    }
}
