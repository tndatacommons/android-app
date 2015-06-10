package org.tndata.android.compass.task;

import android.os.AsyncTask;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GetUserBehaviorsTask extends AsyncTask<String, Void, ArrayList<Behavior>> {
    private GetUserBehaviorsListener mCallback;
    private static Gson gson = new GsonBuilder().setFieldNamingPolicy(
            FieldNamingPolicy.IDENTITY).create();

    public interface GetUserBehaviorsListener {
        public void behaviorsLoaded(ArrayList<Behavior> behaviors);
    }

    public GetUserBehaviorsTask(GetUserBehaviorsListener callback) {
        mCallback = callback;
    }

    @Override
    protected ArrayList<Behavior> doInBackground(String... params) {
        String token = params[0];
        String goalFilter = null;

        if(params.length == 2) {
            goalFilter = params[1];
        }
        String url = Constants.BASE_URL + "users/behaviors/";
        if(goalFilter != null) {
            url = url + "?goal=" + goalFilter;
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
        try {

            BufferedReader bReader = new BufferedReader(new InputStreamReader(
                    stream, "UTF-8"));

            String line = null;
            while ((line = bReader.readLine()) != null) {
                result += line;
            }
            bReader.close();

            JSONObject response = new JSONObject(result);
            JSONArray jArray = response.getJSONArray("results");
            ArrayList<Behavior> behaviors = new ArrayList<Behavior>();
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject userBehavior = jArray.getJSONObject(i);
                Behavior behavior = gson.fromJson(userBehavior.getString("behavior"),
                        Behavior.class);
                behavior.setMappingId(userBehavior.getInt("id"));
                JSONArray goalArray = userBehavior.getJSONArray("user_goals");
                ArrayList<Goal> goals = behavior.getGoals();
                for (int x = 0; x < goalArray.length(); x++) {
                    Goal goal = gson.fromJson(goalArray.getString(x), Goal.class);
                    goals.add(goal);
                }
                behavior.setGoals(goals);
                behaviors.add(behavior);
            }
            return behaviors;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Behavior> behaviors) {
        mCallback.behaviorsLoaded(behaviors);
    }

}
