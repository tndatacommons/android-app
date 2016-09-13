package org.tndata.android.compass.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;


/**
 * The application's database helper.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
class CompassDbHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "compass.db";

    //Initial version, included the places table and the reminders table
    private static final int V1 = 1;
    //Second version, included the category table
    private static final int V2 = 2;
    //Third version, included the location reminder table and dropped the original reminder table
    private static final int V3 = 3;

    //Current version, V3
    private static final int CURRENT_VERSION = V3;


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
        db.execSQL(LocationReminderTableHandler.CREATE);
        db.execSQL(TDCCategoryTableHandler.CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        for (int i = oldVersion; i < newVersion; i++){
            //From V1 to V2
            if (i == V1){
                db.execSQL(TDCCategoryTableHandler.CREATE);
            }
            //From V2 to V3
            else if (i == V2){
                db.execSQL(LocationReminderTableHandler.CREATE);
                db.execSQL("DROP TABLE IF EXISTS Reminder");
            }
        }
    }
}
