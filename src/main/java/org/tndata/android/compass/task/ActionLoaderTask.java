package org.tndata.android.compass.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionLoaderTask extends AsyncTask<String, Void, List<Action>>{
    private ActionLoaderListener mCallback;


    public ActionLoaderTask(@NonNull ActionLoaderListener callback){
        mCallback = callback;
    }

    @Override
    protected List<Action> doInBackground(String... params){
        String token = params[0];
        String behaviorId = null;
        if (params.length > 1) {
            behaviorId = params[1];
        }

        String url = Constants.BASE_URL + "actions/";
        if (behaviorId != null) {
            url += "?behavior=" + behaviorId;
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

            List<Action> actions = new Parser().parseActions(new JSONObject(result).optJSONArray("results"), false);

            Collections.sort(actions, new Comparator<Action>(){
                @Override
                public int compare(Action act1, Action act2){
                    return (act1.getSequenceOrder() < act2.getSequenceOrder()) ? 0 : 1;
                }
            });
            return actions;
        }
        catch (IOException|JSONException x){
            x.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Action> actions){
        mCallback.actionsLoaded(actions);
    }


    public interface ActionLoaderListener{
        void actionsLoaded(List<Action> actions);
    }
}
