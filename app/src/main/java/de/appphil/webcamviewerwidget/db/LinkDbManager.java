package de.appphil.webcamviewerwidget.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import de.appphil.webcamviewerwidget.link.Link;

public class LinkDbManager {

    private static final String TAG = LinkDbManager.class.getSimpleName();

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
        Log.d(TAG, "addLink in LinkDbManager got name: " + name + " and link: " + link);
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LinkReaderContract.LinkEntry.COLUMN_NAME_NAME, name);
        values.put(LinkReaderContract.LinkEntry.COLUMN_NAME_LINK, link);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(LinkReaderContract.LinkEntry.TABLE_NAME, null, values);
        Log.d(TAG, "This link gets the id: " + newRowId);

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
        for(int i = 0; i < cursor.getCount(); i++) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(LinkReaderContract.LinkEntry._ID));
            String itemName = cursor.getString(cursor.getColumnIndexOrThrow(LinkReaderContract.LinkEntry.COLUMN_NAME_NAME));
            String itemLink = cursor.getString(cursor.getColumnIndexOrThrow(LinkReaderContract.LinkEntry.COLUMN_NAME_LINK));
            Log.d(TAG, "Found link with id: " + itemId + " name: " + itemName + " link: " + itemLink);
            // add to allLinks list
            allLinks.add(new Link(itemId, itemName, itemLink));
            cursor.moveToNext();
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

        if(cursor.getCount() == 0) return null;

        cursor.moveToFirst();

        String itemName = cursor.getString(cursor.getColumnIndexOrThrow(LinkReaderContract.LinkEntry.COLUMN_NAME_NAME));
        String itemLink = cursor.getString(cursor.getColumnIndexOrThrow(LinkReaderContract.LinkEntry.COLUMN_NAME_LINK));

        cursor.close();
        db.close();
        return new Link(id, itemName, itemLink);
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


    public ArrayList<SwitchWidgetLinksRow> getSwitchWidgetLinksRowsByWidgetId(int switchWidgetId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                LinkReaderContract.SwitchWidgetLinksEntry.COLUMN_NAME_SWITCH_WIDGET_ID,
                LinkReaderContract.SwitchWidgetLinksEntry.COLUMN_NAME_LINK_ID,
                LinkReaderContract.SwitchWidgetLinksEntry.COLUMN_NAME_POS
        };

        // Filter results
        String selection = LinkReaderContract.SwitchWidgetLinksEntry.COLUMN_NAME_SWITCH_WIDGET_ID + " =?";
        String[] selectionArgs = { "" + switchWidgetId };

        Cursor cursor = db.query(
                LinkReaderContract.SwitchWidgetLinksEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        cursor.moveToFirst();
        ArrayList<SwitchWidgetLinksRow> rows = new ArrayList<>();
        if(cursor.getCount() == 0) return rows;

        for(int i = 0; i < cursor.getCount(); i++) {
            int linkId = cursor.getInt(cursor.getColumnIndexOrThrow(LinkReaderContract.SwitchWidgetLinksEntry.COLUMN_NAME_LINK_ID));
            int pos = cursor.getInt(cursor.getColumnIndexOrThrow(LinkReaderContract.SwitchWidgetLinksEntry.COLUMN_NAME_POS));
            rows.add(new SwitchWidgetLinksRow(switchWidgetId, linkId, pos));
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return rows;
    }

    /***
     * Adds a link at the given position to the switch widget linklist with the given switch widget id.
     * @param switchWidgetId
     * @param linkId
     * @param pos
     */
    public void addLinkToSwitchWidgetLinklist(int switchWidgetId, int linkId, int pos) {
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LinkReaderContract.SwitchWidgetLinksEntry.COLUMN_NAME_SWITCH_WIDGET_ID, switchWidgetId);
        values.put(LinkReaderContract.SwitchWidgetLinksEntry.COLUMN_NAME_LINK_ID, linkId);
        values.put(LinkReaderContract.SwitchWidgetLinksEntry.COLUMN_NAME_POS, pos);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(LinkReaderContract.SwitchWidgetLinksEntry.TABLE_NAME, null, values);

        db.close();
    }

    /***
     * Adds a link to the end of the switch widget linklist with the given switch widget id.
     * @param switchWidgetId
     * @param linkId
     */
    public void addLinkToSwitchWidgetLinklist(int switchWidgetId, int linkId) {
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // get count of links that are currently in the list

        String[] posProjection = {
                LinkReaderContract.SwitchWidgetLinksEntry.COLUMN_NAME_POS
        };

        String selection = LinkReaderContract.SwitchWidgetLinksEntry.COLUMN_NAME_SWITCH_WIDGET_ID + " = ?";
        String[] selectionArgs = { "" + switchWidgetId };

        Cursor cursor = db.query(
                LinkReaderContract.SwitchWidgetLinksEntry.TABLE_NAME,// The table to query
                posProjection,                            // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        int countOfLinks = cursor.getCount();
        cursor.close();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LinkReaderContract.SwitchWidgetLinksEntry.COLUMN_NAME_SWITCH_WIDGET_ID, switchWidgetId);
        values.put(LinkReaderContract.SwitchWidgetLinksEntry.COLUMN_NAME_LINK_ID, linkId);
        values.put(LinkReaderContract.SwitchWidgetLinksEntry.COLUMN_NAME_POS, countOfLinks+1);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(LinkReaderContract.SwitchWidgetLinksEntry.TABLE_NAME, null, values);

        db.close();
    }

    public void deleteLinkFromSwitchWidgetLinklist(int switchWidgetId, int linkId) {
        // get all links from the switch widget linklist
        ArrayList<SwitchWidgetLinksRow> rowsBeforeDelete = getSwitchWidgetLinksRowsByWidgetId(switchWidgetId);
        // remove all the rows from the db table
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Define 'where' part of query.
        String selection = LinkReaderContract.SwitchWidgetLinksEntry.COLUMN_NAME_SWITCH_WIDGET_ID + " =?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { "" + switchWidgetId };
        // Issue SQL statement.
        db.delete(LinkReaderContract.SwitchWidgetLinksEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
        // now remove the link with the given linkId from the arraylist
        ArrayList<SwitchWidgetLinksRow> rowsAfterDelete = new ArrayList<>();
        int posOfDeletedLink = 1;
        for(SwitchWidgetLinksRow row : rowsBeforeDelete) {
            if(row.getLinkId() != linkId) {
                rowsAfterDelete.add(row);
            } else {
                posOfDeletedLink = row.getPos();
            }
        }
        if(posOfDeletedLink == rowsBeforeDelete.size()) {
            // no position have to be changed
        } else {
            // now there's a problem because the position of the deleted link is missing in the list
            for(SwitchWidgetLinksRow row : rowsAfterDelete) {
                if(row.getPos() > posOfDeletedLink) {
                    row.setPos(row.getPos()-1);
                }
            }
        }
        // add the links to the database table again
        for(SwitchWidgetLinksRow row : rowsAfterDelete) {
            addLinkToSwitchWidgetLinklist(switchWidgetId, row.getLinkId(), row.getPos());
        }
    }

    /***
     * Updates the position value of the current link in the SwitchWidget table.
     * @param switchWidgetId
     * @param position
     */
    public void updateSwitchWidgetCurrentLinkPosition(int switchWidgetId, int position) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Define 'where' part of query.
        String selection = LinkReaderContract.SwitchWidgetEntry.COLUMN_NAME_WIDGET_ID + " =?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { "" + switchWidgetId };

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LinkReaderContract.SwitchWidgetEntry.COLUMN_NAME_WIDGET_ID, switchWidgetId);
        values.put(LinkReaderContract.SwitchWidgetEntry.COLUMN_NAME_CURRENT_LINK_POS, position);

        db.update(LinkReaderContract.SwitchWidgetEntry.TABLE_NAME, values, selection, selectionArgs);
        db.close();
    }

    /***
     * Tries to get the current link position.
     * If there are no widget entries it returns -1.
     * Tries to set a link to current if there's one.
     * @param switchWidgetId
     * @return
     */
    public int getSwitchWidgetCurrentLinkPosition(int switchWidgetId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                LinkReaderContract.SwitchWidgetEntry.COLUMN_NAME_CURRENT_LINK_POS
        };

        // Filter results
        String selection = LinkReaderContract.SwitchWidgetEntry.COLUMN_NAME_WIDGET_ID + " =?";
        String[] selectionArgs = { "" + switchWidgetId };

        Cursor cursor = db.query(
                LinkReaderContract.SwitchWidgetEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        if(cursor.getCount() == 0) return -1;
        cursor.moveToFirst();
        int currentLinkPos = cursor.getInt(cursor.getColumnIndexOrThrow(LinkReaderContract.SwitchWidgetEntry.COLUMN_NAME_CURRENT_LINK_POS));
        cursor.close();
        db.close();

        if(currentLinkPos == -1) {
            // check if there are some links in the widgets linklist
            ArrayList<SwitchWidgetLinksRow> rows = getSwitchWidgetLinksRowsByWidgetId(switchWidgetId);
            if (!rows.isEmpty()) {
                // set link with pos 1 to current
                updateSwitchWidgetCurrentLinkPosition(switchWidgetId, 1);
                return 1;
            }
        }
        return currentLinkPos;
    }

    public Link getCurrentLinkBySwitchWidget(int switchWidgetId) {
        int currentLinkPosition = getSwitchWidgetCurrentLinkPosition(switchWidgetId);
        Log.d(TAG, "CurrentLinkPosition: " + currentLinkPosition);
        if(currentLinkPosition == -1) {
            // there is no link
            return null;
        }
        ArrayList<SwitchWidgetLinksRow> rows = getSwitchWidgetLinksRowsByWidgetId(switchWidgetId);
        int linkId = 0;
        for(SwitchWidgetLinksRow row : rows) {
            Log.d(TAG, "Row: " + row.getSwitchWidgetId() + " " + row.getLinkId() + " " + row.getPos());
            if(row.getPos() == currentLinkPosition) {
                linkId = row.getLinkId();
                Log.d(TAG, "LinkId is: " + linkId);
                break;
            }
        }
        // get link by linkId
        return getLinkById(linkId);
    }

    public boolean isSwitchWidgetSaved(int switchWidgetId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                LinkReaderContract.SwitchWidgetEntry.COLUMN_NAME_WIDGET_ID
        };

        // Filter results
        String selection = LinkReaderContract.SwitchWidgetEntry.COLUMN_NAME_WIDGET_ID + " =?";
        String[] selectionArgs = { "" + switchWidgetId };

        Cursor cursor = db.query(
                LinkReaderContract.SwitchWidgetEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        if(cursor.getCount() == 0) return false;
        return true;
    }

    public void deleteSwitchWidget(int switchWidgetId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Define 'where' part of query.
        String selection = LinkReaderContract.SwitchWidgetEntry.COLUMN_NAME_WIDGET_ID + " =?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { "" + switchWidgetId };
        // Issue SQL statement.
        db.delete(LinkReaderContract.SwitchWidgetEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }

    public void addSwitchWidget(int widgetId, int currentLinkPos) {
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LinkReaderContract.SwitchWidgetEntry.COLUMN_NAME_WIDGET_ID, widgetId);
        values.put(LinkReaderContract.SwitchWidgetEntry.COLUMN_NAME_CURRENT_LINK_POS, currentLinkPos);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(LinkReaderContract.SwitchWidgetEntry.TABLE_NAME, null, values);

        db.close();
    }


}
