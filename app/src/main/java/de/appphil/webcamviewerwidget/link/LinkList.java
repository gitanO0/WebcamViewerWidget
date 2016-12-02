package de.appphil.webcamviewerwidget.link;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

@Root(name="linklist")
public class LinkList {

    @ElementList(name="list", entry="linkobj")
    private ArrayList<Link> list;

    @Element(name="versioncode")
    private int versionCode;

    public LinkList(@ElementList(name="list", entry="linkobj") ArrayList<Link> l, @Element(name="versioncode") int versionCode) {
        this.list = l;
        this.versionCode = versionCode;
    }

    public ArrayList<Link> getList() {
        return list;
    }
}
