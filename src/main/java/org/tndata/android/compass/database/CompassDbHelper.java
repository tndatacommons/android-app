package org.tndata.android.compass.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.tndata.android.compass.database.CompassContract.PlaceEntry;
import org.tndata.android.compass.model.Place;

import java.util.ArrayList;
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
    public void updatePlace(Place place){
        //Open a connection to the database
        SQLiteDatabase db = getWritableDatabase();

        String query = "UPDATE " + PlaceEntry.TABLE + " SET "
                + PlaceEntry.NAME + "=?, "
                + PlaceEntry.LATITUDE + "=?, "
                + PlaceEntry.LONGITUDE + "=?, "
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
                //Create the Spot and populate it
                Place place = new Place();
                place.setId(cursor.getInt(cursor.getColumnIndex(PlaceEntry.CLOUD_ID)));
                place.setName(cursor.getString(cursor.getColumnIndex(PlaceEntry.NAME)));
                place.setLatitude(cursor.getDouble(cursor.getColumnIndex(PlaceEntry.LATITUDE)));
                place.setLongitude(cursor.getDouble(cursor.getColumnIndex(PlaceEntry.LONGITUDE)));

                //Add the spot to the target list
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
}
