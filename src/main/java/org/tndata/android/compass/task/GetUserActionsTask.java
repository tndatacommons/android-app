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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GetUserActionsTask extends AsyncTask<String, Void, List<Action>>{
    private GetUserActionsListener mCallback;


    public GetUserActionsTask(GetUserActionsListener callback){
        mCallback = callback;
    }

    @Override
    protected List<Action> doInBackground(String... params){
        String token = params[0];
        String goalFilter = null;
        String actionFilter = null;

        if(params.length == 2){
            if (params[1].contains("action:")){
                actionFilter = params[1].substring(params[1].indexOf(":")+1);
            }
            else{
                goalFilter = params[1];
            }
        }
        String url = Constants.BASE_URL + "users/actions/";
        if(goalFilter != null){
            url = url + "?goal=" + goalFilter;
        }
        else if (actionFilter != null){
            url = url + "?action=" + actionFilter;
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
    protected void onPostExecute(List<Action> actions) {
        Log.e("GetUserActionsTask", "Finished");
        if (actions != null){
            for (Action a:actions){
                Log.d("GetUserActionsTask", "- (" + a.getId() + ") " + a.getTitle());
            }
        }
        mCallback.actionsLoaded(actions);
    }


    public interface GetUserActionsListener{
        void actionsLoaded(List<Action> actions);
    }
}
