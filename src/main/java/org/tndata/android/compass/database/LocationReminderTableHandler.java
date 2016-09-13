package org.tndata.android.compass.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import org.tndata.android.compass.database.CompassContract.LocationReminderEntry;
import org.tndata.android.compass.model.LocationReminder;

import java.util.ArrayList;
import java.util.List;


/**
 * Table handler for the location reminder table.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class LocationReminderTableHandler extends CompassTableHandler{
    static final String CREATE = "CREATE TABLE " + LocationReminderEntry.TABLE + " ("
            + LocationReminderEntry.ID + " INTEGER PRIMARY KEY, "
            + LocationReminderEntry.PLACE_ID + " INTEGER, "
            + LocationReminderEntry.GCM_MESSAGE + " TEXT)";

    private static final String COUNT = "SELECT COUNT(*) FROM " + LocationReminderEntry.TABLE;
    private static final String SELECT = "SELECT * FROM " + LocationReminderEntry.TABLE;
    private static final String EMPTY = "DELETE FROM " + LocationReminderEntry.TABLE;
    private static final String DELETE = "DELETE FROM " + LocationReminderEntry.TABLE
            + " WHERE " + LocationReminderEntry.ID + "=";


    public LocationReminderTableHandler(Context context){
        super(context);
    }




    /**
     * Saves a reminder to the database.
     *
     * @param reminder the reminder to be saved.
     */
    public void saveReminder(LocationReminder reminder){
        //Open a connection to the database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String query = "INSERT INTO " + LocationReminderEntry.TABLE + " ("
                + LocationReminderEntry.PLACE_ID + ", "
                + LocationReminderEntry.GCM_MESSAGE + ") "
                + "VALUES (?, ?)";

        //Prepare the statement
        SQLiteStatement stmt = db.compileStatement(query);
        stmt.bindLong(1, reminder.getPlaceId());
        stmt.bindString(2, reminder.getGcmMessage());

        //Execute the query
        reminder.setId((int)stmt.executeInsert());

        //Close up
        stmt.close();
        db.close();
    }

    /**
     * Returns all the reminders in the reminder table.
     *
     * @return a list of reminders.
     */
    public List<LocationReminder> getReminders(){
        List<LocationReminder> reminders = new ArrayList<>();

        //Open a readable database and execute the query
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT, null);

        //If there are rows in the cursor returned by the query
        if (cursor.moveToFirst()){
            //For each item
            do{
                //Create the Reminder and populate it
                LocationReminder reminder = new LocationReminder(
                        getLong(cursor, LocationReminderEntry.ID),
                        getLong(cursor, LocationReminderEntry.PLACE_ID),
                        getString(cursor, LocationReminderEntry.GCM_MESSAGE));

                //Add the reminder to the target list
                reminders.add(reminder);
            }
            //Move on until the cursor is empty
            while(cursor.moveToNext());
        }

        //Close both, the cursor and the database
        cursor.close();
        db.close();

        return reminders;
    }

    /**
     * Deletes a reminder from the database.
     *
     * @param reminder the reminder to be deleted.
     */
    public void deleteReminder(LocationReminder reminder){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(DELETE + reminder.getId());
        db.close();
        reminder.setId(-1);
    }

    /**
     * Tells whether the reminder table has reminders.
     *
     * @return true if it has reminders, false otherwise.
     */
    public boolean hasReminders(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(COUNT, null);
        cursor.moveToFirst();

        int count = cursor.getInt(0);

        cursor.close();
        db.close();

        return count != 0;
    }

    /**
     * Truncates the reminder table.
     */
    public void emptyReminderTable(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(EMPTY);
        db.close();
    }
}
