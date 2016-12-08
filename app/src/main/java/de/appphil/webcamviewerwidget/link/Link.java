package de.appphil.webcamviewerwidget.link;

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
     * Constructor for Link.
     * @param id Id.
     * @param name Name of the link.
     * @param link Link.
     */
    public Link(long id, String name, String link) {
        this.id = id;
        this.name = name;
        this.link = link;
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
}
