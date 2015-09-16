package org.tndata.android.compass.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.tndata.android.compass.database.CompassContract.PlaceEntry;
import org.tndata.android.compass.database.CompassContract.ReminderEntry;
import org.tndata.android.compass.model.Place;
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
    private static final int CURRENT_VERSION = 1;


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
        String createPlaces = "CREATE TABLE " + PlaceEntry.TABLE + " ("
                + PlaceEntry.LOCAL_ID + " INTEGER PRIMARY KEY, "
                + PlaceEntry.CLOUD_ID + " INTEGER, "
                + PlaceEntry.NAME + " TEXT, "
                + PlaceEntry.LATITUDE + " REAL, "
                + PlaceEntry.LONGITUDE + " REAL)";
        db.execSQL(createPlaces);

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //Nothing to do here yet, as this is the first version
    }

    /**
     * Saves a place in the database.
     *
     * @param place the place to be saved.
     */
    public void savePlace(@NonNull Place place){
        //Open a connection to the database
        SQLiteDatabase db = getWritableDatabase();

        String query = "INSERT INTO " + PlaceEntry.TABLE + " ("
                + PlaceEntry.CLOUD_ID + ", "
                + PlaceEntry.NAME + ", "
                + PlaceEntry.LATITUDE + ", "
                + PlaceEntry.LONGITUDE + ") "
                + "VALUES (?, ?, ?, ?)";

        //Prepare the statement
        SQLiteStatement stmt = db.compileStatement(query);
        stmt.bindLong(1, place.getId());
        stmt.bindString(2, place.getName());
        stmt.bindDouble(3, place.getLatitude());
        stmt.bindDouble(4, place.getLongitude());

        //Execute the query
        stmt.executeInsert();

        //Close up
        stmt.close();
        db.close();
    }

    /**
     * Saves a list of places in the database using a single transaction.
     *
     * @param places the list of places to be saved.
     */
    public void savePlaces(@NonNull List<Place> places){
        //Retrieve a database, begin the transaction, and compile the query
        SQLiteDatabase db = getWritableDatabase();

        String query = "INSERT INTO " + PlaceEntry.TABLE + " ("
                + PlaceEntry.CLOUD_ID + ", "
                + PlaceEntry.NAME + ", "
                + PlaceEntry.LATITUDE + ", "
                + PlaceEntry.LONGITUDE + ") "
                + "VALUES (?, ?, ?, ?)";

        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement(query);

        for (Place place:places){
            //Previous bindings (if any) are emptied
            stmt.clearBindings();

            //Bindings
            stmt.bindLong(1, place.getId());
            stmt.bindString(2, place.getName());
            stmt.bindDouble(3, place.getLatitude());
            stmt.bindDouble(4, place.getLongitude());

            //Execution
            stmt.executeInsert();
        }

        //Close the transaction
        db.setTransactionSuccessful();
        db.endTransaction();

        //Close the database
        stmt.close();
        db.close();
    }

    /**
     * Updates a place in the database.
     *
     * @param place the place to be updated.
     */
    public void updatePlace(@NonNull Place place){
        //Open a connection to the database
        SQLiteDatabase db = getWritableDatabase();

        String query = "UPDATE " + PlaceEntry.TABLE + " SET "
                + PlaceEntry.NAME + "=?, "
                + PlaceEntry.LATITUDE + "=?, "
                + PlaceEntry.LONGITUDE + "=? "
                + "WHERE " + PlaceEntry.CLOUD_ID + "=?";

        //Prepare the statement
        SQLiteStatement stmt = db.compileStatement(query);
        stmt.bindString(1, place.getName());
        stmt.bindDouble(2, place.getLatitude());
        stmt.bindDouble(3, place.getLongitude());
        stmt.bindLong(4, place.getId());

        //Execute the query
        stmt.execute();

        //Close up
        stmt.close();
        db.close();
    }

    /**
     * Retrieves all the places from the database.
     *
     * @return the list of places currently stored in the database.
     */
    public List<Place> getPlaces(){
        //Open a readable database and execute the query
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + PlaceEntry.TABLE + " ORDER BY " + PlaceEntry.NAME + " ASC", null);

        List<Place> places = new ArrayList<>();

        //If there are rows in the cursor returned by the query
        if (cursor.moveToFirst()){
            //For each item
            do{
                //Create the Place and populate it
                Place place = new Place();
                place.setId(cursor.getInt(cursor.getColumnIndex(PlaceEntry.CLOUD_ID)));
                place.setName(cursor.getString(cursor.getColumnIndex(PlaceEntry.NAME)));
                place.setLatitude(cursor.getDouble(cursor.getColumnIndex(PlaceEntry.LATITUDE)));
                place.setLongitude(cursor.getDouble(cursor.getColumnIndex(PlaceEntry.LONGITUDE)));

                //Add the place to the target list
                places.add(place);
            }
            //Move on until the cursor is empty
            while(cursor.moveToNext());
        }

        //Close both, the cursor and the database
        cursor.close();
        db.close();

        return places;
    }

    /**
     * Truncates the places table.
     */
    public void emptyPlacesTable(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + PlaceEntry.TABLE);
        db.close();
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
                + " WHERE "
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
     * Truncates the reminder table.
     */
    public void emptyReminderTable(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + ReminderEntry.TABLE);
        db.close();
    }
}
