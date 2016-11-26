package de.appphil.webcamviewerwidget.utils;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;


import de.appphil.webcamviewerwidget.link.Link;

public class XMLManager {

    private static final int versionCode = 5;

    /***
     * Returns the xml string for the given link.
     * @param link Link object that should be parsed
     * @return XML data as string.
     */
    public static String linkToXML(Link link) {
        return "<versioncode>" + versionCode + "</versioncode>" +
                "<linkobj>" +
                  "<name>" + link.getName() + "</name>" +
                  "<link>" + link.getLink() + "</link>" +
                  "<enabled>" + link.isEnabled() + "</enabled>" +
                "</linkobj>";
    }

    /***
     * Creates an arraylist with Link objects from the given xml string.
     * @param xmlString String read from the file.
     * @return ArrayList with Link objects read from xmlString.
     */
    public static ArrayList<Link> getLinklistFromXML(String xmlString) {
        ArrayList<Link> linklist = new ArrayList<>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            String lastTag = "";
            String name = "";
            String link = "";
            boolean enabled = true;

            // get versionCode first
            int versionCode = Integer.parseInt(xmlString.split("<versioncode>")[1].split("</versioncode>")[0]);

            // update old xml files
            if(versionCode <= 3) {
                // replace "activated" with "enabled"
                xmlString = xmlString.replaceAll("activated", "enabled");
            }

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
                    } else if(xpp.getName().equals("enabled")) {
                        lastTag = "enabled";
                    }
                } else if(eventType == XmlPullParser.END_TAG) {
                    if(xpp.getName().equals("linkobj")) {
                        if(versionCode == -1) {
                            // "update" to new
                            linklist.add(new Link(name, link, true));
                        } else {
                            linklist.add(new Link(name, link, enabled));
                        }
                    }
                } else if(eventType == XmlPullParser.TEXT) {
                    if(lastTag.equals("versioncode")) {

                    } else if(lastTag.equals("name")) {
                        name = xpp.getText();
                    } else if(lastTag.equals("link")) {
                        link = xpp.getText();
                    } else if(lastTag.equals("enabled")) {
                        if(xpp.getText().equals("true")) {
                            enabled = true;
                        } else {
                            enabled = false;
                        }
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return linklist;
    }
}
