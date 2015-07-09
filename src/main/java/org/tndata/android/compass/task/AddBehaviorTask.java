package org.tndata.android.compass.task;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.CompassApplication;
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

public class AddBehaviorTask extends AsyncTask<Void, Void, ArrayList<Behavior>> {
    private Context mContext;
    private static Gson gson = new GsonBuilder().setFieldNamingPolicy(
            FieldNamingPolicy.IDENTITY).create();
    private ArrayList<String> mBehaviorIds;
    private AddBehaviorsTaskListener mCallback;

    public interface AddBehaviorsTaskListener {
        public void behaviorsAdded(ArrayList<Behavior> behaviors);
    }

    public AddBehaviorTask(Context context, AddBehaviorsTaskListener callback,
                           ArrayList<String> behaviorIds) {
        mContext = context;
        mCallback = callback;
        mBehaviorIds = behaviorIds;
    }

    @Override
    protected ArrayList<Behavior> doInBackground(Void... params) {
        String token = ((CompassApplication) ((Activity) mContext)
                .getApplication()).getToken();

        String url = Constants.BASE_URL + "users/behaviors/";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + token);
        JSONArray postArray = new JSONArray();
        for (int i = 0; i < mBehaviorIds.size(); i++) {
            JSONObject postId = new JSONObject();
            try {
                postId.put("behavior", mBehaviorIds.get(i));
                postArray.put(postId);
            } catch (JSONException e1) {
                e1.printStackTrace();
                return null;
            }
        }
        InputStream stream = NetworkHelper.httpPostStream(url, headers,
                postArray.toString());
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

            createResponse = Html.fromHtml(result).toString();

            JSONArray jArray = new JSONArray(createResponse);
            ArrayList<Behavior> behaviors = new ArrayList<Behavior>();
            Log.d("user behavior", jArray.toString(2));
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject userBehavior = jArray.getJSONObject(i);
                Behavior behavior = gson.fromJson(userBehavior.getString("behavior"),
                        Behavior.class);
                behavior.setMappingId(userBehavior.getInt("id"));

                // Include the Behavior's Parent goals that have been selected by the user
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
        mCallback.behaviorsAdded(behaviors);
    }

}
