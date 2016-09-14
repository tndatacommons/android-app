package org.tndata.android.compass.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;


/**
 * Superclass of all table handlers. Manages the connection to the database.
 *
 * @author Ismael Alonso
 * @version 1.1.0
 */
abstract class CompassTableHandler{
    private static final String TAG = "CompassTableHandler";


    private static CompassDbHelper sDbHelper = null;
    private static SQLiteDatabase sDatabase = null;
    private static int sOpenConnections = 0;


    /**
     * Initializes a connection to the database. This step is absolutely required.
     *
     * @param context a reference to the context.
     */
    protected void init(Context context){
        Log.i(TAG, "Initializing database connection...");
        if (sDbHelper == null){
            Log.i(TAG, "No preexisting connection found, creating...");
            sDbHelper = new CompassDbHelper(context);
            sDatabase = sDbHelper.getWritableDatabase();
            sOpenConnections = 0;
        }
        sOpenConnections++;
        Log.i(TAG, "Connection initialized, that makes " + sOpenConnections + ".");
    }

    /**
     * Returns an instance of the database connection.
     *
     * @return an instance of the database connection.
     */
    @NonNull
    protected SQLiteDatabase getDatabase(){
        if (sOpenConnections == 0){
            throw new IllegalStateException("The handler needs to be initialized before used");
        }
        return sDatabase;
    }

    /**
     * Gets the int in the requested column from the provided cursor.
     *
     * @param cursor the cursor from where the data should be extracted.
     * @param columnName the name of the column storing the int.
     * @return an int.
     */
    protected int getInt(Cursor cursor, String columnName){
        return cursor.getInt(cursor.getColumnIndex(columnName));
    }

    /**
     * Gets the long in the requested column from the provided cursor.
     *
     * @param cursor the cursor from where the data should be extracted.
     * @param columnName the name of the column storing the long.
     * @return a long.
     */
    protected long getLong(Cursor cursor, String columnName){
        return cursor.getLong(cursor.getColumnIndex(columnName));
    }

    /**
     * Gets the String in the requested column from the provided cursor.
     *
     * @param cursor the cursor from where the data should be extracted.
     * @param columnName the name of the column storing the String.
     * @return a String.
     */
    protected String getString(Cursor cursor, String columnName){
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    /**
     * Gets the boolean in the requested column from the provided cursor.
     *
     * @param cursor the cursor from where the data should be extracted.
     * @param columnName the name of the column storing the boolean.
     * @return a boolean.
     */
    protected boolean getBoolean(Cursor cursor, String columnName){
        return cursor.getInt(cursor.getColumnIndex(columnName)) != 0;
    }

    /**
     * Closes a connection to the database
     */
    public void close(){
        //If the number of open connections is already zero, then something went south
        //  wherever this class' children are used, this is an illegal state.
        if (sOpenConnections == 0){
            throw new IllegalStateException("There aren't any connections currently opened");
        }

        Log.i(TAG, "Closing database connection...");
        if (--sOpenConnections == 0){
            Log.i(TAG, "No connections remaining, closing database and helper.");
            sDatabase.close();
            sDatabase = null;
            sDbHelper.close();
            sDbHelper = null;
        }
        else{
            Log.i(TAG, sOpenConnections + " remaining.");
        }
    }
}
