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


    public static abstract class LocationReminderEntry implements BaseColumns{
        public static final String TABLE = "LocationReminder";

        //Column information
        public static final String ID = _ID;
        public static final String PLACE_ID = "place_id";
        public static final String GCM_MESSAGE = "gcm_message";
    }


    /**
     * Definition of TDCCategory tables.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public static abstract class TDCCategoryEntry implements BaseColumns{
        //Table name information
        public static final String TABLE = "TDCCategory";

        //Column information
        public static final String LOCAL_ID = _ID;
        public static final String CLOUD_ID = "cloud_id";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String HTML_DESCRIPTION = "html_description";
        public static final String ICON_URL = "icon_url";
        //'group' is a reserved keyword in SQLite
        public static final String GROUP = "group_id";
        public static final String GROUP_NAME = "group_name";
        //'order' is a reserved keyword as well
        public static final String ORDER = "order_value";
        public static final String IMAGE_URL = "image_url";
        public static final String COLOR = "color";
        public static final String SECONDARY_COLOR = "secondary_color";
        public static final String PACKAGED_CONTENT = "packaged_content";
        public static final String SELECTED_BY_DEFAULT = "selected_by_default";
    }
}
