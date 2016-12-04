package de.appphil.webcamviewerwidget.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import de.appphil.webcamviewerwidget.link.Link;

public class LinkDbManager {

    private LinkReaderDbHelper dbHelper;

    private String[] fullLinkProjection = {
            LinkReaderContract.LinkEntry._ID,
            LinkReaderContract.LinkEntry.COLUMN_NAME_NAME,
            LinkReaderContract.LinkEntry.COLUMN_NAME_LINK
    };

    public LinkDbManager(Context context) {
        dbHelper = new LinkReaderDbHelper(context);
    }

    /***
     * Adds a row to the database table.
     * @param name
     * @param link
     */
    public void addLink(String name, String link) {
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LinkReaderContract.LinkEntry.COLUMN_NAME_NAME, name);
        values.put(LinkReaderContract.LinkEntry.COLUMN_NAME_LINK, link);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(LinkReaderContract.LinkEntry.TABLE_NAME, null, values);

        db.close();
    }

    /***
     * Reads the database table rows and creates Link objects from them.
     * @return ArrayList containing link objects.
     */
    public ArrayList<Link> getAllLinks() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // How you want the results sorted in the resulting Cursor
        String sortOrder = LinkReaderContract.LinkEntry.COLUMN_NAME_NAME + " ASC";

        Cursor cursor = db.query(
                LinkReaderContract.LinkEntry.TABLE_NAME,  // The table to query
                fullLinkProjection,                       // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        ArrayList<Link> allLinks = new ArrayList<>();

        cursor.moveToFirst();
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(LinkReaderContract.LinkEntry._ID));
            String itemName = cursor.getString(cursor.getColumnIndexOrThrow(LinkReaderContract.LinkEntry.COLUMN_NAME_NAME));
            String itemLink = cursor.getString(cursor.getColumnIndexOrThrow(LinkReaderContract.LinkEntry.COLUMN_NAME_LINK));

            // add to allLinks list
            allLinks.add(new Link(itemId, itemName, itemLink, true));
        }
        cursor.close();

        db.close();
        return allLinks;
    }

    /***
     * Gets the link object with the given id from the database table.
     * @param id Id of the item in the database table.
     * @return Link object.
     */
    public Link getLinkById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Filter results WHERE "id" = id
        String selection = LinkReaderContract.LinkEntry._ID + " = ?";
        String[] selectionArgs = { "" + id };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = LinkReaderContract.LinkEntry.COLUMN_NAME_NAME + " ASC";

        Cursor cursor = db.query(
                LinkReaderContract.LinkEntry.TABLE_NAME,  // The table to query
                fullLinkProjection,                       // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        cursor.moveToFirst();

        String itemName = cursor.getString(cursor.getColumnIndexOrThrow(LinkReaderContract.LinkEntry.COLUMN_NAME_NAME));
        String itemLink = cursor.getString(cursor.getColumnIndexOrThrow(LinkReaderContract.LinkEntry.COLUMN_NAME_LINK));

        cursor.close();
        db.close();
        return new Link(id, itemName, itemLink, true);
    }

    /***
     * Gets the id by the given name from the database table.
     * @param name
     * @return
     */
    public long getIdByName(String name) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                LinkReaderContract.LinkEntry._ID
        };

        // Filter results WHERE "name" = name
        String selection = LinkReaderContract.LinkEntry.COLUMN_NAME_NAME + " =?";
        String[] selectionArgs = { name };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = LinkReaderContract.LinkEntry.COLUMN_NAME_NAME + " ASC";

        Cursor cursor = db.query(
                LinkReaderContract.LinkEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        cursor.moveToFirst();

        long id = cursor.getLong(cursor.getColumnIndexOrThrow(LinkReaderContract.LinkEntry._ID));

        cursor.close();
        db.close();
        return id;
    }

    /***
     * Removes the row with the given id from the database table.
     * @param id
     */
    public void deleteLinkById(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Define 'where' part of query.
        String selection = LinkReaderContract.LinkEntry._ID + " =?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { "" + id };
        // Issue SQL statement.
        db.delete(LinkReaderContract.LinkEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }

    /***
     * Updates the row with the given id in the database table.
     * @param id
     * @param name
     * @param link
     */
    public void updateLinkById(long id, String name, String link) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Define 'where' part of query.
        String selection = LinkReaderContract.LinkEntry._ID + " =?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { "" + id };

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LinkReaderContract.LinkEntry.COLUMN_NAME_NAME, name);
        values.put(LinkReaderContract.LinkEntry.COLUMN_NAME_LINK, link);

        db.update(LinkReaderContract.LinkEntry.TABLE_NAME, values, selection, selectionArgs);
        db.close();
    }
}
