package de.appphil.webcamviewerwidget.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LinkReaderDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "LinkDB1.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + LinkReaderContract.LinkEntry.TABLE_NAME + " (" +
                    LinkReaderContract.LinkEntry._ID + " INTEGER PRIMARY KEY," +
                    LinkReaderContract.LinkEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    LinkReaderContract.LinkEntry.COLUMN_NAME_LINK + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + LinkReaderContract.LinkEntry.TABLE_NAME;

    private static final String SQL_CREATE_ENTRIES_2 =
            "CREATE TABLE " + LinkReaderContract.SwitchWidgetLinksEntry.TABLE_NAME + " (" +
                    LinkReaderContract.SwitchWidgetLinksEntry.COLUMN_NAME_SWITCH_WIDGET_ID + INTEGER_TYPE + COMMA_SEP +
                    LinkReaderContract.SwitchWidgetLinksEntry.COLUMN_NAME_LINK_ID + INTEGER_TYPE + COMMA_SEP +
                    LinkReaderContract.SwitchWidgetLinksEntry.COLUMN_NAME_POS + INTEGER_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES_2 =
            "DROP TABLE IF EXISTS " + LinkReaderContract.SwitchWidgetLinksEntry.TABLE_NAME;

    private static final String SQL_CREATE_ENTRIES_3 =
            "CREATE TABLE " + LinkReaderContract.SwitchWidgetEntry.TABLE_NAME + " (" +
                    LinkReaderContract.SwitchWidgetEntry.COLUMN_NAME_WIDGET_ID + INTEGER_TYPE + COMMA_SEP +
                    LinkReaderContract.SwitchWidgetEntry.COLUMN_NAME_CURRENT_LINK_POS + INTEGER_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES_3 =
            "DROP TABLE IF EXISTS " + LinkReaderContract.SwitchWidgetEntry.TABLE_NAME;

    private static final String SQL_CREATE_ENTRIES_4 =
            "CREATE TABLE " + LinkReaderContract.SingleAutoUpdateWidgetLinks.TABLE_NAME + " (" +
                    LinkReaderContract.SingleAutoUpdateWidgetLinks.COLUMN_NAME_WIDGET_ID + INTEGER_TYPE + COMMA_SEP +
                    LinkReaderContract.SingleAutoUpdateWidgetLinks.COLUMN_NAME_LINK_ID + INTEGER_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES_4 =
            "DROP TABLE IF EXISTS " + LinkReaderContract.SingleAutoUpdateWidgetLinks.TABLE_NAME;


    public LinkReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES_2);
        db.execSQL(SQL_CREATE_ENTRIES_3);
        db.execSQL(SQL_CREATE_ENTRIES_4);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_DELETE_ENTRIES_2);
        db.execSQL(SQL_DELETE_ENTRIES_3);
        db.execSQL(SQL_DELETE_ENTRIES_4);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}