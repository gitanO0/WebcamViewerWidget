package de.appphil.webcamviewerwidget.link;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

public class Link {

    /***
     * Id of the link in database.
     */
    private long id;

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
     * @param id Id.
     * @param name Name of the link.
     * @param link Link.
     * @param enabled If the link is enabled or not.
     */
    public Link(long id, String name, String link, boolean enabled) {
        this.id = id;
        this.name = name;
        this.link = link;
        this.enabled = enabled;
    }

    /***
     *
     * @return Id of the link.
     */
    public long getId() {
        return id;
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
