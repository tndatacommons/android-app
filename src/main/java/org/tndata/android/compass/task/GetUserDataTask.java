package org.tndata.android.compass.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.database.CompassDbHelper;
import org.tndata.android.compass.model.UserData;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;
import org.tndata.android.compass.util.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


/**
 * A task that retrieves all of the user's selected data from the REST API, and returns
 * a populated instance of UserData.
 *
 * See: https://app.tndata.org/api/users/
 */
public class GetUserDataTask extends AsyncTask<String, Void, UserData>{
    private static final String TAG = "GetUserDataTask";

    private Context mContext;
    private GetUserDataListener mCallback;


    public GetUserDataTask(@NonNull Context context, @NonNull GetUserDataListener callback){
        mContext = context;
        mCallback = callback;
    }

    @Override
    protected UserData doInBackground(String... params){
        String url = Constants.BASE_URL + "users/";
        UserData userData = new UserData();

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + params[0]);

        InputStream stream = NetworkHelper.httpGetStream(url, headers);
        if (stream == null){
            return null;
        }

        try{
            BufferedReader bReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line, result = "";
            while ((line = bReader.readLine()) != null){
                result += line;
            }
            bReader.close();

            JSONArray jArray = new JSONObject(result).getJSONArray("results");
            JSONObject userJson = jArray.getJSONObject(0); // 1 user, so 1 result

            Parser parser = new Parser();

            // Parse the user-selected content, store in userData; wait till all data is set
            // before syncing parent/child relationships.
            userData.setCategories(parser.parseCategories(userJson.getJSONArray("categories"), true), false);
            userData.setGoals(parser.parseGoals(userJson.getJSONArray("goals"), true), false);
            userData.setBehaviors(parser.parseBehaviors(userJson.getJSONArray("behaviors"), true), false);
            userData.setActions(parser.parseActions(userJson.getJSONArray("actions"), true), false);
            userData.sync();

            userData.setPlaces(parser.parsePlaces(userJson.getJSONArray("places")));
            CompassDbHelper dbHelper = new CompassDbHelper(mContext);
            dbHelper.emptyPlacesTable();
            dbHelper.savePlaces(userData.getPlaces());
            dbHelper.close();

            Log.d(TAG, "... finishing up GetUserDataTask.");
            userData.logData();
            
            return userData;

        }
        catch (IOException|JSONException x){
            x.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(UserData userData){
        Log.d(TAG, "Finished");
        userData.logSelectedData("FROM UserDataTask.onPostExecute", false);
        mCallback.userDataLoaded(userData);
    }


    public interface GetUserDataListener{
        void userDataLoaded(UserData userData);
    }
}
