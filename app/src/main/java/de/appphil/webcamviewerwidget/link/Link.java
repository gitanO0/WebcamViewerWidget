package de.appphil.webcamviewerwidget.link;

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
     * Needed to check if the link is enabled or disabled.
     */
    private boolean enabled;

    /***
     * Constructor for Link.
     * @param name Name of the link.
     * @param link Link.
     * @param enabled If the link is enabled or not.
     */
    public Link(String name, String link, boolean enabled) {
        this.name = name;
        this.link = link;
        this.enabled = enabled;
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
     * @param checked If the link is checked.
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    /***
     * Returns if the link is checked.
     * @return If the link is checked.
     */
    public boolean isChecked() {
        return checked;
    }

    /***
     * Returns if the link is enabled or not.
     * @return If the link is enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }
}
