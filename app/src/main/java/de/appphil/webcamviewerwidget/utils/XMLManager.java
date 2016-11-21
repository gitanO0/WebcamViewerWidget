package de.appphil.webcamviewerwidget.utils;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;


import de.appphil.webcamviewerwidget.link.Link;

public class XMLManager {

    private static final int versionCode = 3;

    /***
     * Returns the xml string for the given link.
     * @param link
     * @return XML data as string.
     */
    public static String linkToXML(Link link) {
        return "<versioncode>" + versionCode + "</versioncode>" +
                "<linkobj>" +
                  "<name>" + link.getName() + "</name>" +
                  "<link>" + link.getLink() + "</link>" +
                  "<activated>" + link.isActivated() + "</activated>" +
                "</linkobj>";
    }

    /***
     * Creates an arraylist with Link objects from the given xml string.
     * @param xmlString
     * @return
     */
    public static ArrayList<Link> getLinklistFromXML(String xmlString) {
        ArrayList<Link> linklist = new ArrayList<Link>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            String lastTag = "";
            String name = "";
            String link = "";
            int versionCode = -1; // -1 -> there's no version code
            boolean activated = true;

            xpp.setInput(new StringReader(xmlString));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_DOCUMENT) {

                } else if(eventType == XmlPullParser.START_TAG) {
                    if(xpp.getName().equals("versioncode")) {
                        lastTag = "versioncode";
                    } else if(xpp.getName().equals("name")) {
                        lastTag = "name";
                    } else if(xpp.getName().equals("link")) {
                        lastTag = "link";
                    } else if(xpp.getName().equals("activated")) {
                        lastTag = "activated";
                    }
                } else if(eventType == XmlPullParser.END_TAG) {
                    if(xpp.getName().equals("linkobj")) {
                        if(versionCode == -1) {
                            // "update" to new
                            linklist.add(new Link(name, link, true));
                        } else {
                            linklist.add(new Link(name, link, activated));
                        }
                    }
                } else if(eventType == XmlPullParser.TEXT) {
                    if(lastTag.equals("versioncode")) {
                        versionCode = Integer.parseInt(xpp.getText());
                    } else if(lastTag.equals("name")) {
                        name = xpp.getText();
                    } else if(lastTag.equals("link")) {
                        link = xpp.getText();
                    } else if(lastTag.equals("activated")) {
                        if(xpp.getText().equals("true")) {
                            activated = true;
                        } else {
                            activated = false;
                        }
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return linklist;
    }
}
