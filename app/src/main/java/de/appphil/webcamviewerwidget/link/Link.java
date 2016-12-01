package de.appphil.webcamviewerwidget.link;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="linkobj")
public class Link {

    /***
     * Name of the link.
     */
    @Element(name="name")
    private String name;

    /***
     * Link to the image.
     */
    @Element(name="link")
    private String link;

    /***
     * Only needed for ExportActivity.
     */
    private boolean checked = false;

    /***
     * Needed to check if the link is enabled or disabled.
     */
    @Element(name="enabled")
    private boolean enabled;

    /***
     * Constructor for Link.
     * @param name Name of the link.
     * @param link Link.
     * @param enabled If the link is enabled or not.
     */
    public Link(@Element(name="name")String name, @Element(name="link")String link, @Element(name="enabled")boolean enabled) {
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
