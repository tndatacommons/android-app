package org.tndata.android.compass.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;


/**
 * A generic class for table handlers containing utilities.
 *
 * @author Ismael Alonso
 * @version 1.1.0
 */
abstract class CompassTableHandler{
    private static CompassDbHelper sDbHelper = null;
    private static SQLiteDatabase sDatabase = null;
    private static int sOpenConnections = 0;


    protected void init(Context context){
        if (sDbHelper == null){
            sDbHelper = new CompassDbHelper(context);
            sDatabase = sDbHelper.getWritableDatabase();
            sOpenConnections = 0;
        }
        sOpenConnections++;
    }

    @NonNull
    protected SQLiteDatabase getDatabase(){
        if (sOpenConnections == 0){
            throw new IllegalStateException("The handler needs to be initialized before used");
        }
        return sDatabase;
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
        if (--sOpenConnections == 0){
            sDatabase.close();
            sDatabase = null;
            sDbHelper.close();
            sDbHelper = null;
        }
    }
}
