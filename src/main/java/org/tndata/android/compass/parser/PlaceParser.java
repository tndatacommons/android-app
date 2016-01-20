package org.tndata.android.compass.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.model.Place;
import org.tndata.android.compass.model.UserPlace;

import java.util.ArrayList;
import java.util.List;


/**
 * Class containing the methods to parse place lists.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public final class PlaceParser extends ParserMethods{
    /**
     * Parses the list of primary places.
     *
     * @param src the source string containing a list of Places.
     * @return a List of Places.
     */
    public static List<Place> parsePrimaryPlaces(String src){
        return sGson.fromJson(src, PlaceList.class).results;
    }

    /**
     * This class is a holder to parse the results from the API endpoint that
     * returns the list of primary places.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private static class PlaceList{
        private List<Place> results = null;
    }
}
