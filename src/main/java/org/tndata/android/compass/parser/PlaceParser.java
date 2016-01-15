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
     * Parses a place list.
     *
     * @param src a string representing a JSON array containing a list of places.
     * @return a list of places.
     */
    public static List<UserPlace> parsePlaces(String src){
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
        List<UserPlace> places = new ArrayList<>();
        for (int i = 0; i < placeArray.length(); i++){
            //If an individual place fails to be parsed it is left out of the list
            try{
                JSONObject placeObject = placeArray.getJSONObject(i);
                UserPlace place = sGson.fromJson(placeObject.toString(), UserPlace.class);
                //place.setName(placeObject.getJSONObject("place").getString("name"));
                //place.setPrimary(placeObject.getJSONObject("place").getBoolean("primary"));
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
                //place.setId(-1);
                places.add(place);
            }
            catch (JSONException jsonx){
                jsonx.printStackTrace();
            }
        }
        return places;
    }

    /**
     * Parses the list of primary places. TODO this is a test.
     *
     * @param src the source string containing a list of Places.
     * @return a List of Places.
     */
    public static List<Place> parsePrimaryPlaces2(String src){
        return sGson.fromJson(src, PlaceList.class).results;
    }

    /**
     * This class is a holder to parse the results from the API endpoint that
     * returns the list of primary places.
     *
     * TODO a class with a generic type can be created to contemplate all the result
     * TODO possibilities: class ResultSet<T>{List<T>} NOTE: DO NOT STICK TO THIS IF
     * TODO IT IS JUST TOO MUCH TROUBLE.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private static class PlaceList{
        private List<Place> results = null;
    }
}
