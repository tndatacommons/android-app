package org.tndata.android.compass.task;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;
import org.tndata.android.compass.util.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GetUserActionsTask extends AsyncTask<String, Void, List<Action>>{
    private static final String TAG = "GetUserActionsTask";


    private GetUserActionsCallback mCallback;


    public GetUserActionsTask(GetUserActionsCallback callback){
        mCallback = callback;
    }

    @Override
    protected List<Action> doInBackground(String... params){
        String token = params[0];

        String url = Constants.BASE_URL + "users/actions/";
        if(params.length == 2){
            if (params[1].contains("action:")){
                url += "?action=" + params[1].substring(params[1].indexOf(":")+1);
            }
            else if (params[1].contains("today")){
                url += "?today=1";
            }
            else{
                url = url + "?goal=" + params[1];
            }
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + token);

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

            return new Parser().parseActions(new JSONObject(result).getJSONArray("results"), true);

        }
        catch (IOException|JSONException x){
            x.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Action> actions){
        Log.e("GetUserActionsTask", "Finished");
        if (actions != null){
            for (Action action:actions){
                Log.d(TAG, action.toString());
            }
            mCallback.onActionsLoaded(actions);
        }
        else{
            mCallback.onActionsLoaded(new ArrayList<Action>());
        }
    }


    public interface GetUserActionsCallback{
        void onActionsLoaded(List<Action> actions);
    }
}
