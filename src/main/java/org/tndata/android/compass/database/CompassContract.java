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
        public static final String LOCAL_ID = _ID;
        public static final String CLOUD_ID = "cloud_id";
        public static final String NAME = "name";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
    }

    public static abstract class ReminderEntry implements BaseColumns{
        //Table name information
        public static final String TABLE = "Reminder";

        //Column information
        public static final String ID = _ID;
        public static final String PLACE_ID = "place_id";
        public static final String TITLE = "title";
        public static final String MESSAGE = "message";
        public static final String OBJECT_ID = "object_id";
        public static final String USER_MAPPING_ID = "user_mapping_id";
    }
}
