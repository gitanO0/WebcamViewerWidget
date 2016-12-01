package de.appphil.webcamviewerwidget.link;


import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

@Root(name="linklist")
public class LinkList {

    @ElementList(name="list")
    private ArrayList<Link> list;

    public LinkList(@ElementList(name="list") ArrayList<Link> l) {
        this.list = l;
    }

    public ArrayList<Link> getList() {
        return list;
    }
}
