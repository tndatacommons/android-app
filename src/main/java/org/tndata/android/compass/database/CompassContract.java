package org.tndata.android.compass.database;

import android.provider.BaseColumns;


/**
 * Compass' database contract.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CompassContract{
    /**
     * The definition of all the Place tables.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public static abstract class PlaceEntry implements BaseColumns{
        //Table name information
        public static final String TABLE = "Places";

        //Column information
        public static final String ID = _ID;
        public static final String NAME = "name";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
    }
}
