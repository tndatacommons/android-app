package org.tndata.android.compass.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.tndata.android.compass.database.CompassContract.ReminderEntry;
import org.tndata.android.compass.model.Reminder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * The application's database helper.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CompassDbHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "compass.db";

    //Initial version, included the places table and the reminders table
    private static final int V1 = 1;
    //Second version, included the category table
    private static final int V2 = 2;

    //Current version, V2
    private static final int CURRENT_VERSION = V2;


    /**
     * Constructor.
     *
     * @param context a reference to the context.
     */
    public CompassDbHelper(@NonNull Context context){
        super(context, DATABASE_NAME, null, CURRENT_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(PlaceTableHandler.CREATE);

        String createReminders = "CREATE TABLE " + ReminderEntry.TABLE + " ("
                + ReminderEntry.ID + " INTEGER PRIMARY KEY, "
                + ReminderEntry.PLACE_ID + " INTEGER, "
                + ReminderEntry.NOTIFICATION_ID + " INTEGER, "
                + ReminderEntry.TITLE + " TEXT, "
                + ReminderEntry.MESSAGE + " TEXT, "
                + ReminderEntry.OBJECT_ID + " INTEGER, "
                + ReminderEntry.USER_MAPPING_ID + " INTEGER, "
                + ReminderEntry.SNOOZED + " TINYINT, "
                + ReminderEntry.LAST_DELIVERED + " INTEGER)";
        db.execSQL(createReminders);

        db.execSQL(TDCCategoryTableHandler.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        for (int i = oldVersion; i < newVersion; i++){
            //From V1 to V2
            if (i == V1){
                db.execSQL(TDCCategoryTableHandler.CREATE_TABLE);
            }
        }
    }

    /**
     * Saves a reminder to the database.
     *
     * @param reminder the reminder to be saved.
     */
    public void saveReminder(Reminder reminder){
        //Open a connection to the database
        SQLiteDatabase db = getWritableDatabase();

        String query = "INSERT INTO " + ReminderEntry.TABLE + " ("
                + ReminderEntry.NOTIFICATION_ID + ", "
                + ReminderEntry.PLACE_ID + ", "
                + ReminderEntry.TITLE + ", "
                + ReminderEntry.MESSAGE + ", "
                + ReminderEntry.OBJECT_ID + ", "
                + ReminderEntry.USER_MAPPING_ID + ", "
                + ReminderEntry.SNOOZED + ", "
                + ReminderEntry.LAST_DELIVERED + ") "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        //Prepare the statement
        SQLiteStatement stmt = db.compileStatement(query);
        stmt.bindLong(1, reminder.getNotificationId());
        stmt.bindLong(2, reminder.getPlaceId());
        stmt.bindString(3, reminder.getTitle());
        stmt.bindString(4, reminder.getMessage());
        stmt.bindLong(5, reminder.getObjectId());
        stmt.bindLong(6, reminder.getUserMappingId());
        stmt.bindLong(7, reminder.isSnoozed() ? 1 : 0);
        stmt.bindLong(8, reminder.getLastDelivered());

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
    public List<Reminder> getReminders(){
        List<Reminder> reminders = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

        String query = "SELECT * FROM " + ReminderEntry.TABLE
                +       " WHERE "
                +       ReminderEntry.SNOOZED + "<>0"
                +       " OR "
                +       ReminderEntry.LAST_DELIVERED + "<" + calendar.getTimeInMillis();

        //Open a readable database and execute the query
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        //If there are rows in the cursor returned by the query
        if (cursor.moveToFirst()){
            //For each item
            do{
                //Create the Reminder and populate it
                Reminder place = new Reminder(
                        cursor.getInt(cursor.getColumnIndex(ReminderEntry.NOTIFICATION_ID)),
                        cursor.getInt(cursor.getColumnIndex(ReminderEntry.PLACE_ID)),
                        cursor.getString(cursor.getColumnIndex(ReminderEntry.TITLE)),
                        cursor.getString(cursor.getColumnIndex(ReminderEntry.MESSAGE)),
                        cursor.getInt(cursor.getColumnIndex(ReminderEntry.OBJECT_ID)),
                        cursor.getInt(cursor.getColumnIndex(ReminderEntry.USER_MAPPING_ID)));
                place.setId(cursor.getInt(cursor.getColumnIndex(ReminderEntry.ID)));
                place.setSnoozed(cursor.getLong(cursor.getColumnIndex(ReminderEntry.SNOOZED)) != 0);
                place.setLastDelivered(cursor.getLong(cursor.getColumnIndex(ReminderEntry.LAST_DELIVERED)));

                //Add the reminder to the target list
                reminders.add(place);
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
    public void deleteReminder(Reminder reminder){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + ReminderEntry.TABLE + " WHERE " + ReminderEntry.ID + "=" + reminder.getId());
        db.close();
        reminder.setId(-1);
    }

    /**
     * Tells whether the reminder table is empty.
     *
     * @return true if it is empty, false otherwise.
     */
    public boolean isReminderTableEmpty(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + ReminderEntry.TABLE, null);
        cursor.moveToFirst();

        int count = cursor.getInt(0);

        cursor.close();
        db.close();

        return count == 0;
    }

    /**
     * Truncates the reminder table.
     */
    public void emptyReminderTable(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + ReminderEntry.TABLE);
        db.close();
    }
}
