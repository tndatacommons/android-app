package org.tndata.android.compass.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.model.Place;

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
     * Parses a place list.
     *
     * @param src a string representing a JSON array containing a list of places.
     * @return a list of places.
     */
    public static List<Place> parsePlaces(String src){
        //First, create the JSONArray; if that fails, the method cannot carry on
        JSONArray placeArray;
        try{
            placeArray = new JSONArray(src);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }

        //Once the array is created, the mist is populated
        List<Place> places = new ArrayList<>();
        for (int i = 0; i < placeArray.length(); i++){
            //If an individual place fails to be parsed it is left out of the list
            try{
                JSONObject placeObject = placeArray.getJSONObject(i);
                Place place = sGson.fromJson(placeObject.toString(), Place.class);
                place.setName(placeObject.getJSONObject("place").getString("name"));
                place.setPrimary(placeObject.getJSONObject("place").getBoolean("primary"));
                places.add(place);
            }
            catch (JSONException jsonx){
                jsonx.printStackTrace();
            }
        }

        return places;
    }

    /**
     * Parses a primary place list.
     *
     * @param src a string representing a JSON object containing an array of primary places.
     * @return a list of primary places or {@code null} if unsuccessful.
     */
    public static List<Place> parsePrimaryPlaces(String src){
        JSONArray placeArray;
        try{
            placeArray = new JSONObject(src).optJSONArray("results");
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }

        List<Place> places = new ArrayList<>();
        for (int i = 0; i < placeArray.length(); i++){
            try{
                Place place = sGson.fromJson(placeArray.getString(i), Place.class);
                place.setId(-1);
                places.add(place);
            }
            catch (JSONException jsonx){
                jsonx.printStackTrace();
            }
        }
        return places;
    }
}
