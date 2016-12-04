package de.appphil.webcamviewerwidget.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LinkReaderDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Link.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + LinkReaderContract.LinkEntry.TABLE_NAME + " (" +
                    LinkReaderContract.LinkEntry._ID + " INTEGER PRIMARY KEY," +
                    LinkReaderContract.LinkEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    LinkReaderContract.LinkEntry.COLUMN_NAME_LINK + TEXT_TYPE + " )";

    public LinkReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}