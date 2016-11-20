package de.appphil.webcamviewerwidget.utils;



import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;


import de.appphil.webcamviewerwidget.Link;

public class XMLManager {

    /***
     * Returns the xml string for the given link.
     * @param link
     * @return XML data as string.
     */
    public static String linkToXML(Link link) {
        return "<linkobj>" +
                  "<name>" + link.getName() + "</name>" +
                  "<link>" + link.getLink() + "</link>" +
               "</linkobj>";
    }

    public static ArrayList<Link> getLinklistFromXML(String xmlString) {
        ArrayList<Link> linklist = new ArrayList<Link>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            String lastTag = "";
            String name = "";
            String link = "";

            xpp.setInput(new StringReader(xmlString));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_DOCUMENT) {

                } else if(eventType == XmlPullParser.START_TAG) {
                    if(xpp.getName().equals("name")) {
                        lastTag = "name";
                    } else if(xpp.getName().equals("link")) {
                        lastTag = "link";
                    }
                } else if(eventType == XmlPullParser.END_TAG) {
                    if(xpp.getName().equals("linkobj")) {
                        linklist.add(new Link(name, link));
                    }
                } else if(eventType == XmlPullParser.TEXT) {
                    if(lastTag.equals("name")) {
                        name = xpp.getText();
                    } else if(lastTag.equals("link")) {
                        link = xpp.getText();
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
