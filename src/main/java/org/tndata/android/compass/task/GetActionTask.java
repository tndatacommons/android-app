package org.tndata.android.compass.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


/**
 * Retrieves a single action from the API. This class can be easily modified to get several
 * actions and return them in a List.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class GetActionTask extends AsyncTask<Integer, Void, Action>{
    private static final String URL = Constants.BASE_URL + "actions/";
    private OnActionRetrievedCallback mCallback;


    /**
     * Constructor.
     *
     * @param callback the callback object.
     */
    public GetActionTask(@NonNull OnActionRetrievedCallback callback){
        mCallback = callback;
    }

    @Override
    protected Action doInBackground(Integer... params){
        if (params.length == 0){
            return null;
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        InputStream stream = NetworkHelper.httpGetStream(URL + "/" + params[0] + "/", headers);
        if (stream == null){
            return null;
        }

        String result = "";
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null){
                result += line;
            }
            reader.close();

            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
            return gson.fromJson(result, Action.class);
        }
        catch (Exception x){
            x.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Action action){
        mCallback.onActionRetrieved(action);
    }

    /**
     * Callback interface for action retrieved events.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface OnActionRetrievedCallback{
        void onActionRetrieved(Action action);
    }
}
