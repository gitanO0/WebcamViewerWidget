package de.appphil.webcamviewerwidget.db;


import android.provider.BaseColumns;

public final class LinkReaderContract {
    // To prevent someone from accidentally instantiating the contract class the constructor is private
    private LinkReaderContract() {}

    // Inner class that defines the table contents
    public static class LinkEntry implements BaseColumns {
        public static final String TABLE_NAME = "all_links";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_LINK = "link";
    }

    public static class SwitchWidgetLinksEntry implements BaseColumns {
        public static final String TABLE_NAME = "switchwidget_links";
        public static final String COLUMN_NAME_SWITCH_WIDGET_ID = "switch_widget_id";
        public static final String COLUMN_NAME_LINK_ID = "link_id";
        public static final String COLUMN_NAME_POS = "pos";
    }

    public static class SwitchWidgetEntry implements BaseColumns {
        public static final String TABLE_NAME = "switch_widgets";
        public static final String COLUMN_NAME_WIDGET_ID = "widget_id";
        public static final String COLUMN_NAME_CURRENT_LINK_ID = "current_link_id";
    }
}