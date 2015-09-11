package org.tndata.android.compass.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.model.Place;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


/**
 * Saves a place to the backend.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class SavePlaceTask extends AsyncTask<Place, Void, Integer>{
    private SavePlaceCallback mCallback;
    private String mToken;


    /**
     * Constructor.
     *
     * @param callback the callback interface.
     * @param token the user token.
     */
    public SavePlaceTask(@NonNull SavePlaceCallback callback, @NonNull String token){
        mCallback = callback;
        mToken = token;
    }

    @Override
    protected Integer doInBackground(Place... params){
        //Check and retrieve the place to be saved
        if (params.length == 0){
            return null;
        }
        Place place = params[0];

        //Compose the url
        String url = Constants.BASE_URL + "users/places/";

        //Set up the headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + mToken);

        try{
            //Set up the body
            JSONObject body = new JSONObject();
            body.put("place", place.getName());
            body.put("latitude", place.getLatitude());
            body.put("longitude", place.getLongitude());

            //Retrieve the stream
            InputStream stream = NetworkHelper.httpPostStream(url, headers, body.toString());
            if (stream == null){
                return null;
            }

            //Create a reader and read the reply
            BufferedReader bReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line, result = "";
            while ((line = bReader.readLine()) != null){
                result += line;
            }
            bReader.close();

            //Return the id so it can be set to the place
            return new JSONObject(result).getInt("id");
        }
        catch (JSONException|IOException x){
            x.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Integer integer){
        mCallback.onPlaceSaved(integer == null ? -1 : integer);
    }


    /**
     * Callback interface for place saved events.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface SavePlaceCallback{
        /**
         * Called when the api replies to the save request.
         *
         * @param id the id of the place in the backend or -1 if there was an error.
         */
        void onPlaceSaved(int id);
    }
}
