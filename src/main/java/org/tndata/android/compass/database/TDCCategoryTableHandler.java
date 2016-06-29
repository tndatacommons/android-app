package org.tndata.android.compass.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import org.tndata.android.compass.database.CompassContract.TDCCategoryEntry;
import org.tndata.android.compass.model.TDCCategory;

import java.util.ArrayList;
import java.util.List;


/**
 * Handler for the TDCCategory table. As the number of things in the database
 * grows, this handlers will help keeping CompassDbHelper concise.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class TDCCategoryTableHandler extends CompassTableHandler{
    //The CREATE query needs to be package-protected to be accessible from CompassDbHandler
    static final String CREATE_TABLE = "CREATE TABLE " + TDCCategoryEntry.TABLE + " ("
            + TDCCategoryEntry.LOCAL_ID + " INTEGER PRIMARY KEY, "
            + TDCCategoryEntry.CLOUD_ID + " INTEGER, "
            + TDCCategoryEntry.TITLE + " TEXT, "
            + TDCCategoryEntry.DESCRIPTION + " TEXT, "
            + TDCCategoryEntry.HTML_DESCRIPTION + " TEXT, "
            + TDCCategoryEntry.ICON_URL + " TEXT, "
            + TDCCategoryEntry.GROUP + " INTEGER, "
            + TDCCategoryEntry.GROUP_NAME + " TEXT, "
            + TDCCategoryEntry.ORDER + " INTEGER, "
            + TDCCategoryEntry.IMAGE_URL + " TEXT, "
            + TDCCategoryEntry.COLOR + " TEXT, "
            + TDCCategoryEntry.SECONDARY_COLOR + " TEXT, "
            + TDCCategoryEntry.PACKAGED_CONTENT + " TINYINT, "
            + TDCCategoryEntry.SELECTED_BY_DEFAULT + " TINYINT)";

    //These other queries are only accessed from within this class
    private static final String CLEAR = "DELETE FROM " + TDCCategoryEntry.TABLE;
    private static final String COUNT = "SELECT COUNT(*) FROM " + TDCCategoryEntry.TABLE;
    private static final String SELECT = "SELECT * FROM " + TDCCategoryEntry.TABLE;
    private static final String INSERT = "INSERT INTO " + TDCCategoryEntry.TABLE + " ("
            + TDCCategoryEntry.CLOUD_ID + ", "
            + TDCCategoryEntry.TITLE + ", "
            + TDCCategoryEntry.DESCRIPTION + ", "
            + TDCCategoryEntry.HTML_DESCRIPTION + ", "
            + TDCCategoryEntry.ICON_URL + ", "
            + TDCCategoryEntry.GROUP + ", "
            + TDCCategoryEntry.GROUP_NAME + ", "
            + TDCCategoryEntry.ORDER + ", "
            + TDCCategoryEntry.IMAGE_URL + ", "
            + TDCCategoryEntry.COLOR + ", "
            + TDCCategoryEntry.SECONDARY_COLOR + ", "
            + TDCCategoryEntry.PACKAGED_CONTENT + ", "
            + TDCCategoryEntry.SELECTED_BY_DEFAULT + ") "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


    /**
     * Constructor.
     *
     * @param context a reference to the context.
     */
    public TDCCategoryTableHandler(Context context){
        super(context);
    }

    /**
     * Replaces all the entries previously in the database with new entries for the
     * provided categories.
     *
     * @param categories the list of categories to be written to the database.
     */
    public void writeCategories(List<TDCCategory> categories){
        //Get the database and clear it
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(CLEAR);

        //Prepare the transaction
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement(INSERT);

        //For every category
        for (TDCCategory category:categories){
            //Previous bindings (if any) are emptied
            stmt.clearBindings();

            //Bindings
            stmt.bindLong(1, category.getId());
            stmt.bindString(2, category.getTitle());
            stmt.bindString(3, category.getDescription());
            stmt.bindString(4, category.getHTMLDescription());
            stmt.bindString(5, category.getIconUrl());
            stmt.bindLong(6, category.getGroup());
            stmt.bindString(7, category.getGroupName());
            stmt.bindLong(8, category.getOrder());
            stmt.bindString(9, category.getImageUrl());
            stmt.bindString(10, category.getColor());
            stmt.bindString(11, category.getSecondaryColor());
            stmt.bindLong(12, category.isPackagedContent() ? 1 : 0);
            stmt.bindLong(13, category.isSelectedByDefault() ? 1 : 0);

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
     * Reads the list of categories currently stored in the database.
     *
     * @return the list of stored categories.
     */
    public List<TDCCategory> readCategories(){
        //Open a readable database and execute the query
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT, null);

        List<TDCCategory> categories = new ArrayList<>();

        //If there are rows in the cursor returned by the query
        if (cursor.moveToFirst()){
            //For each item
            do{
                //Create the TDCCategory and populate it
                TDCCategory category = new TDCCategory();
                category.setId(getLong(cursor, TDCCategoryEntry.CLOUD_ID));
                category.setTitle(getString(cursor, TDCCategoryEntry.TITLE));
                category.setDescription(getString(cursor, TDCCategoryEntry.DESCRIPTION));
                category.setHTMLDescription(getString(cursor, TDCCategoryEntry.HTML_DESCRIPTION));
                category.setIconUrl(getString(cursor, TDCCategoryEntry.ICON_URL));
                category.setGroup(getInt(cursor, TDCCategoryEntry.GROUP));
                category.setGroupName(getString(cursor, TDCCategoryEntry.GROUP_NAME));
                category.setOrder(getInt(cursor, TDCCategoryEntry.ORDER));
                category.setImageUrl(getString(cursor, TDCCategoryEntry.IMAGE_URL));
                category.setColor(getString(cursor, TDCCategoryEntry.COLOR));
                category.setSecondaryColor(getString(cursor, TDCCategoryEntry.SECONDARY_COLOR));
                category.setPackagedContent(getBoolean(cursor, TDCCategoryEntry.PACKAGED_CONTENT));
                category.setSelectedByDefault(getBoolean(cursor, TDCCategoryEntry.SELECTED_BY_DEFAULT));

                //Add the place to the target list
                categories.add(category);
            }
            //Move on until the cursor is empty
            while (cursor.moveToNext());
        }

        //Clean up and return the result
        cursor.close();
        db.close();
        return categories;
    }

    /**
     * Tells whether the category table is empty.
     *
     * @return true if it is empty, false otherwise.
     */
    public boolean isTableEmpty(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(COUNT, null);
        cursor.moveToFirst();

        int count = cursor.getInt(0);

        cursor.close();
        db.close();
        return count == 0;
    }
}
