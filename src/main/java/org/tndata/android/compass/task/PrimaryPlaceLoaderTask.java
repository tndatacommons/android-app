package org.tndata.android.compass.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.model.Place;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Retrieves the list of primary places from the backend.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class PrimaryPlaceLoaderTask extends AsyncTask<Void, Void, List<Place>>{
    private PrimaryPlaceLoaderCallback mCallback;


    public PrimaryPlaceLoaderTask(@NonNull PrimaryPlaceLoaderCallback callback){
        mCallback = callback;
    }

    @Override
    protected List<Place> doInBackground(Void... params){
        //Create the headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");

        //Create a stream, if that fails, return null to signal failure
        InputStream stream = NetworkHelper.httpGetStream(Constants.BASE_URL + "places/", headers);
        if (stream == null){
            return null;
        }

        try{
            //Create a reader and read the reply
            BufferedReader bReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line, result = "";
            while ((line = bReader.readLine()) != null){
                result += line;
            }
            bReader.close();

            //Retrieve the array and read it using GSON
            JSONArray placeArray = new JSONObject(result).optJSONArray("results");
            if (placeArray != null){
                List<Place> places = new ArrayList<>();
                Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
                for (int i = 0; i < placeArray.length(); i++){
                    places.add(gson.fromJson(placeArray.getString(i), Place.class));
                }
                return places;
            }
        }
        catch (IOException|JSONException x){
            x.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Place> places){
        mCallback.onPlacesLoaded(places);
    }


    /**
     * Callback interface for places loaded events.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface PrimaryPlaceLoaderCallback{
        /**
         * Called when the loader has finished loading the places.
         *
         * @param primaryPlaces the list of primary places.
         */
        void onPlacesLoaded(List<Place> primaryPlaces);
    }
}
