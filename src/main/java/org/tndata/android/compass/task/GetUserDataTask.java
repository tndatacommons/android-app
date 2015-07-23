package org.tndata.android.compass.task;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.Trigger;
import org.tndata.android.compass.model.UserData;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A task that retrieves all of the user's selected data from the REST API, and returns
 * a populated instance of UserData.
 *
 * See: https://app.tndata.org/api/users/
 */
public class GetUserDataTask extends AsyncTask<String, Void, UserData> {

    private static final String TAG = "GetUserDataTask";
    private GetUserDataListener mCallback;
    private static Gson gson = new GsonBuilder().setFieldNamingPolicy(
            FieldNamingPolicy.IDENTITY).create();

    public interface GetUserDataListener {
        public void userDataLoaded(UserData userData);
    }

    public GetUserDataTask(GetUserDataListener callback) {
        mCallback = callback;
    }

    @Override
    protected UserData doInBackground(String... params) {
        String token = params[0];
        String url = Constants.BASE_URL + "users/";
        UserData userData = new UserData();
        String result = ""; // result of http request

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + token);
        InputStream stream = NetworkHelper.httpGetStream(url, headers);

        if (stream == null) {
            return null;
        }

        try {

            BufferedReader bReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line = null;
            while ((line = bReader.readLine()) != null) {
                result += line;
            }
            bReader.close();

            JSONObject response = new JSONObject(result);
            JSONArray jArray = response.getJSONArray("results");
            JSONObject userJson = jArray.getJSONObject(0); // 1 user, so 1 result

            // Parse the user-selected content, store in userData; wait till all data is set
            // before syncing parent/child relationships.
            userData.setCategories(parseCategories(userJson.getJSONArray("categories")), false);
            userData.setGoals(parseGoals(userJson.getJSONArray("goals")), false);
            userData.setBehaviors(parseBehaviors(userJson.getJSONArray("behaviors")), false);
            userData.setActions(parseActions(userJson.getJSONArray("actions")), false);
            userData.sync();

            Log.e(TAG, "... finishing up GetUserDataTask.");
            userData.logData();
            
            return userData;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected ArrayList<Category> parseCategories(JSONArray categoryArray) {
        ArrayList<Category> categories = new ArrayList<Category>();

        try {
            for (int i = 0; i < categoryArray.length(); i++) {
                JSONObject categoryJson = categoryArray.getJSONObject(i);
                Category category = gson.fromJson(categoryJson.getString("category"), Category.class);
                category.setMappingId(categoryJson.getInt("id"));
                categories.add(category);

                Log.d(TAG, "Created UserCategory (" +
                        category.getMappingId() + ") with Category (" +
                        category.getId() + ")" + category.getTitle());

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return categories;
    }

    protected ArrayList<Goal> parseGoals(JSONArray goalArray) {
        ArrayList<Goal> goals = new ArrayList<Goal>();

        try {
            for (int i = 0; i < goalArray.length(); i++) {
                JSONObject goalJson = goalArray.getJSONObject(i);
                Goal goal = gson.fromJson(goalJson.getString("goal"), Goal.class);
                goal.setMappingId(goalJson.getInt("id"));
                goals.add(goal);

                Log.d(TAG, "Created UserGoal (" +
                        goal.getMappingId() + ") with Goal (" +
                        goal.getId() + ")" + goal.getTitle());

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return goals;
    }

    protected ArrayList<Behavior> parseBehaviors(JSONArray behaviorArray) {
        ArrayList<Behavior> behaviors = new ArrayList<Behavior>();

        try {
            for (int i = 0; i < behaviorArray.length(); i++) {
                JSONObject behaviorJson = behaviorArray.getJSONObject(i);
                Behavior behavior = gson.fromJson(behaviorJson.getString("behavior"), Behavior.class);
                behavior.setMappingId(behaviorJson.getInt("id"));
                behaviors.add(behavior);

                Log.d(TAG, "Created UserBehavior (" +
                        behavior.getMappingId() + ") with Behavior (" +
                        behavior.getId() + ")" + behavior.getTitle());

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return behaviors;
    }

    protected ArrayList<Action> parseActions(JSONArray actionArray) {
        ArrayList<Action> actions = new ArrayList<Action>();

        try {
            for (int i = 0; i < actionArray.length(); i++) {
                JSONObject actionJson = actionArray.getJSONObject(i);
                Action action = gson.fromJson(actionJson.getString("action"), Action.class);
                action.setMappingId(actionJson.getInt("id"));
                actions.add(action);

                Log.d(TAG, "Created UserAction (" +
                        action.getMappingId() + ") with Action (" +
                        action.getId() + ")" + action.getTitle());

                Log.d(TAG, "Custom Trigger: " + actionJson.getString("custom_trigger"));

                if (!actionJson.isNull("custom_trigger")) {
                    action.setCustomTrigger(
                            gson.fromJson(actionJson.getString("custom_trigger"), Trigger.class));

                    Log.d(TAG, "loaded trigger: " + action.getTrigger().getName());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return actions;
    }

    @Override
    protected void onPostExecute(UserData userData) {
        Log.e(TAG, "Finished");
        userData.logSelectedData("FROM UserDataTask.onPostExecute");
        mCallback.userDataLoaded(userData);
    }

}
