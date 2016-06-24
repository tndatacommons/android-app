package org.tndata.android.compass.database;

import android.content.Context;
import android.database.Cursor;


/**
 * A generic class for table handlers containing utilities.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
class CompassTableHandler{
    protected CompassDbHelper mDbHelper;


    CompassTableHandler(Context context){
        mDbHelper = new CompassDbHelper(context);
    }

    protected int getInt(Cursor cursor, String columnName){
        return cursor.getInt(cursor.getColumnIndex(columnName));
    }

    protected long getLong(Cursor cursor, String columnName){
        return cursor.getLong(cursor.getColumnIndex(columnName));
    }

    protected String getString(Cursor cursor, String columnName){
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    protected boolean getBoolean(Cursor cursor, String columnName){
        return cursor.getInt(cursor.getColumnIndex(columnName)) != 0;
    }

    public void close(){
        mDbHelper.close();
    }
}
