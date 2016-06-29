package org.tndata.android.compass.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.tndata.android.compass.database.CompassContract.PlaceEntry;
import org.tndata.android.compass.model.Place;
import org.tndata.android.compass.model.UserPlace;

import java.util.ArrayList;
import java.util.List;


/**
 * Handler for the Places table.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class PlaceTableHandler extends CompassTableHandler{

    /*---------*
     * QUERIES *
     *---------*/

    static final String CREATE = "CREATE TABLE " + PlaceEntry.TABLE + " ("
            + CompassContract.PlaceEntry.LOCAL_ID + " INTEGER PRIMARY KEY, "
            + CompassContract.PlaceEntry.CLOUD_ID + " INTEGER, "
            + CompassContract.PlaceEntry.NAME + " TEXT, "
            + CompassContract.PlaceEntry.LATITUDE + " REAL, "
            + CompassContract.PlaceEntry.LONGITUDE + " REAL)";

    private static final String INSERT = "INSERT INTO " + PlaceEntry.TABLE + " ("
            + CompassContract.PlaceEntry.CLOUD_ID + ", "
            + CompassContract.PlaceEntry.NAME + ", "
            + CompassContract.PlaceEntry.LATITUDE + ", "
            + CompassContract.PlaceEntry.LONGITUDE + ") "
            + "VALUES (?, ?, ?, ?)";

    private static final String UPDATE = "UPDATE " + PlaceEntry.TABLE + " SET "
            + CompassContract.PlaceEntry.NAME + "=?, "
            + CompassContract.PlaceEntry.LATITUDE + "=?, "
            + CompassContract.PlaceEntry.LONGITUDE + "=? "
            + "WHERE " + CompassContract.PlaceEntry.CLOUD_ID + "=?";

    private static final String SELECT = "SELECT * FROM " + PlaceEntry.TABLE + " "
            + "ORDER BY " + CompassContract.PlaceEntry.NAME + " ASC";

    private static final String DELETE = "DELETE FROM " + PlaceEntry.TABLE;


    /**
     * Constructor.
     *
     * @param context a reference to the context.
     */
    public PlaceTableHandler(Context context){
        super(context);
    }


    /*---------*
     * METHODS *
     *---------*/

    /**
     * Saves a place in the database.
     *
     * @param userPlace the place to be saved.
     */
    public void savePlace(@NonNull UserPlace userPlace){
        //Open a connection to the database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //Prepare the statement
        SQLiteStatement stmt = db.compileStatement(INSERT);
        stmt.bindLong(1, userPlace.getId());
        stmt.bindString(2, userPlace.getName());
        stmt.bindDouble(3, userPlace.getLatitude());
        stmt.bindDouble(4, userPlace.getLongitude());

        //Execute the query
        stmt.executeInsert();

        //Close up
        stmt.close();
        db.close();
    }

    /**
     * Saves a list of places in the database using a single transaction.
     *
     * @param userPlaces the list of places to be saved.
     */
    public void savePlaces(@NonNull List<UserPlace> userPlaces){
        //Retrieve a database, begin the transaction, and compile the query
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement(INSERT);

        for (UserPlace userPlace:userPlaces){
            //Previous bindings (if any) are emptied
            stmt.clearBindings();

            //Bindings
            stmt.bindLong(1, userPlace.getId());
            stmt.bindString(2, userPlace.getName());
            stmt.bindDouble(3, userPlace.getLatitude());
            stmt.bindDouble(4, userPlace.getLongitude());

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
     * @param userPlace the place to be updated.
     */
    public void updatePlace(@NonNull UserPlace userPlace){
        //Open a connection to the database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //Prepare the statement
        SQLiteStatement stmt = db.compileStatement(UPDATE);
        stmt.bindString(1, userPlace.getName());
        stmt.bindDouble(2, userPlace.getLatitude());
        stmt.bindDouble(3, userPlace.getLongitude());
        stmt.bindLong(4, userPlace.getId());

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
    public List<UserPlace> getPlaces(){
        //Open a readable database and execute the query
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT, null);

        List<UserPlace> userPlaces = new ArrayList<>();

        //If there are rows in the cursor returned by the query
        if (cursor.moveToFirst()){
            //For each item
            do{
                //Create the Place and populate it
                Place place = new Place(
                        cursor.getString(cursor.getColumnIndex(CompassContract.PlaceEntry.NAME))
                );

                UserPlace userPlace = new UserPlace(
                        place,
                        cursor.getInt(cursor.getColumnIndex(CompassContract.PlaceEntry.CLOUD_ID)),
                        cursor.getDouble(cursor.getColumnIndex(CompassContract.PlaceEntry.LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(CompassContract.PlaceEntry.LONGITUDE))
                );

                //Add the place to the target list
                userPlaces.add(userPlace);
            }
            //Move on until the cursor is empty
            while (cursor.moveToNext());
        }

        //Close both, the cursor and the database
        cursor.close();
        db.close();

        return userPlaces;
    }

    /**
     * Truncates the places table.
     */
    public void emptyPlacesTable(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(DELETE);
        db.close();
    }
}
