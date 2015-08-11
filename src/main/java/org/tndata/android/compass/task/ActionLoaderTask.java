package org.tndata.android.compass.task;

import android.os.AsyncTask;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ActionLoaderTask extends
        AsyncTask<String, Void, ArrayList<Action>> {
    private ActionLoaderListener mCallback;
    private static Gson gson = new GsonBuilder().setFieldNamingPolicy(
            FieldNamingPolicy.IDENTITY).create();

    public interface ActionLoaderListener {
        public void actionsLoaded(ArrayList<Action> actions);
    }

    public ActionLoaderTask(ActionLoaderListener callback) {
        mCallback = callback;
    }

    @Override
    protected ArrayList<Action> doInBackground(String... params) {
        String token = params[0];
        String behaviorId = null;
        if (params.length > 1) {
            behaviorId = params[1];
        }
        String url = Constants.BASE_URL + "actions/";
        if (behaviorId != null) {
            url += "?behavior=" + behaviorId;
        }
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + token);

        InputStream stream = NetworkHelper.httpGetStream(url, headers);
        if (stream == null) {
            return null;
        }
        String result = "";
        String createResponse = "";
        try {

            BufferedReader bReader = new BufferedReader(new InputStreamReader(
                    stream, "UTF-8"));

            String line = null;
            while ((line = bReader.readLine()) != null) {
                result += line;
            }
            bReader.close();

            createResponse = result;

            JSONObject jObject = new JSONObject(createResponse);
            ArrayList<Action> actions = new ArrayList<Action>();

            // Else this was a get for all categories
            JSONArray actionArray = jObject.optJSONArray("results");
            if (actionArray != null) {
                for (int i = 0; i < actionArray.length(); i++) {
                    Action action = gson.fromJson(actionArray.getString(i),
                            Action.class);  
                    actions.add(action);
                }
            }
            Collections.sort(actions, new Comparator<Action>() {
                @Override
                public int compare(Action act1, Action act2) {

                    return (act1.getSequenceOrder() < act2.getSequenceOrder()) ? 0
                            : 1;
                }
            });
            return actions;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Action> actions) {
        mCallback.actionsLoaded(actions);
    }

}
