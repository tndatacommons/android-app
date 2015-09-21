package org.tndata.android.compass.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.model.Behavior;
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


public class BehaviorLoaderTask extends AsyncTask<String, Void, List<Behavior>>{
    private BehaviorLoaderListener mCallback;


    public BehaviorLoaderTask(@NonNull BehaviorLoaderListener callback){
        mCallback = callback;
    }

    @Override
    protected List<Behavior> doInBackground(String... params){
        String goalId = null;
        if (params.length > 1){
            goalId = params[1];
        }

        String url = Constants.BASE_URL + "behaviors/";
        if (goalId != null){
            url += "?goal=" + goalId;
        }

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

            return new Parser().parseBehaviors(new JSONObject(result).getJSONArray("results"), false);
        }
        catch (IOException|JSONException x){
            x.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Behavior> behaviors){
        mCallback.behaviorsLoaded(behaviors);
    }


    public interface BehaviorLoaderListener{
        void behaviorsLoaded(List<Behavior> behaviors);
    }
}
