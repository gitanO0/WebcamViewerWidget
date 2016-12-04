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
}