package org.tndata.android.compass.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.database.CompassDbHelper;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.Place;
import org.tndata.android.compass.model.UserData;
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

    private static Gson gson = new GsonBuilder().setFieldNamingPolicy(
            FieldNamingPolicy.IDENTITY).create();


    public GetUserDataTask(Context context, GetUserDataListener callback){
        mContext = context;
        mCallback = callback;
    }

    @Override
    protected UserData doInBackground(String... params){
        String token = params[0];
        String url = Constants.BASE_URL + "users/";
        UserData userData = new UserData();

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + token);
        InputStream stream = NetworkHelper.httpGetStream(url, headers);

        if (stream == null) {
            return null;
        }

        try {

            BufferedReader bReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line, result = "";
            while ((line = bReader.readLine()) != null) {
                result += line;
            }
            bReader.close();

            JSONObject response = new JSONObject(result);
            JSONArray jArray = response.getJSONArray("results");
            JSONObject userJson = jArray.getJSONObject(0); // 1 user, so 1 result

            Parser parser = new Parser();

            // Parse the user-selected content, store in userData; wait till all data is set
            // before syncing parent/child relationships.
            userData.setCategories(parser.parseCategories(userJson.getJSONArray("categories"), true), false);
            userData.setGoals(parser.parseGoals(userJson.getJSONArray("goals"), true), false);
            userData.setBehaviors(parseUserBehaviors(userJson.getJSONArray("behaviors")), false);
            userData.setActions(parser.parseActions(userJson.getJSONArray("actions"), true), false);
            userData.sync();

            userData.setPlaces(parseUserPlaces(userJson.getJSONArray("places")));
            CompassDbHelper dbHelper = new CompassDbHelper(mContext);
            dbHelper.emptyPlacesTable();
            dbHelper.savePlaces(userData.getPlaces());
            dbHelper.close();

            Log.d(TAG, "... finishing up GetUserDataTask.");
            userData.logData();
            
            return userData;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected ArrayList<Goal> parseUserGoals(JSONArray goalArray) {
        ArrayList<Goal> goals = new ArrayList<Goal>();

        /*try {/*
            for (int i = 0; i < goalArray.length(); i++) {
                JSONObject goalJson = goalArray.getJSONObject(i);
                Goal goal = gson.fromJson(goalJson.getString("goal"), Goal.class);
                goal.setProgressValue(goalJson.getDouble("progress_value"));
                goal.setMappingId(goalJson.getInt("id"));
                goal.setCustomTriggersAllowed(goalJson.getBoolean("custom_triggers_allowed"));
                goals.add(goal);

                // Set the Goal's parent categories
                // parse these into Category objects and set on the Goal
                ArrayList<Category> goalCategories = goal.getCategories();
                JSONArray user_categories = goalJson.getJSONArray("user_categories");
                Log.d(TAG, "Goal.user_categories JSON: " + user_categories.toString(2));
                for(int x = 0; x < user_categories.length(); x++) {
                    JSONObject categoryJson = user_categories.getJSONObject(x);
                    Category c = gson.fromJson(categoryJson.toString(), Category.class);
                    goalCategories.add(c);
                }
                goal.setCategories(goalCategories);

                // Set the Goal's child behaviors
                // parse these into Behavior objects and set on the Goal
                ArrayList<Behavior> goalBehaviors = goal.getBehaviors();
                JSONArray user_behaviors = goalJson.getJSONArray("user_behaviors");
                Log.d(TAG, "Goal.user_behaviors JSON: " + user_behaviors.toString(2));
                for(int x = 0; x < user_behaviors.length(); x++) {
                    JSONObject behaviorJson = user_behaviors.getJSONObject(x);
                    Behavior b = gson.fromJson(behaviorJson.toString(), Behavior.class);
                    goalBehaviors.add(b);
                }
                goal.setBehaviors(goalBehaviors);

                Log.d(TAG, "Created UserGoal (" +
                        goal.getMappingId() + ") with Goal (" +
                        goal.getId() + ")" + goal.getTitle());

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        return goals;
    }

    protected ArrayList<Behavior> parseUserBehaviors(JSONArray behaviorArray) {
        ArrayList<Behavior> behaviors = new ArrayList<Behavior>();

        try {
            for (int i = 0; i < behaviorArray.length(); i++) {
                JSONObject behaviorJson = behaviorArray.getJSONObject(i);
                Behavior behavior = gson.fromJson(behaviorJson.getString("behavior"), Behavior.class);
                behavior.setMappingId(behaviorJson.getInt("id"));
                behavior.setCustomTriggersAllowed(behaviorJson.getBoolean("custom_triggers_allowed"));
                behaviors.add(behavior);

                // Set the Behavior's parent goals
                // parse these into Goal objects an set on the Behavior
                ArrayList<Goal> behaviorGoals = behavior.getGoals();
                JSONArray user_goals = behaviorJson.getJSONArray("user_goals");
                Log.d(TAG, "Behavior.user_goals JSON: " + user_goals.toString(2));
                for (int x = 0; x < user_goals.length(); x++) {
                    JSONObject goalJson = user_goals.getJSONObject(x);
                    Goal g = gson.fromJson(goalJson.toString(), Goal.class);
                    behaviorGoals.add(g);
                }
                behavior.setGoals(behaviorGoals);

                // Set the Behavior's child Actions
                // parse these into Action objects an set on the Behavior
                ArrayList<Action> behaviorActions = behavior.getActions();
                JSONArray user_actions = behaviorJson.getJSONArray("user_actions");
                Log.d(TAG, "Behavior.user_actions JSON: " + user_actions.toString(2));
                for (int x = 0; x < user_actions.length(); x++) {
                    JSONObject actionJson = user_actions.getJSONObject(x);
                    Action a = gson.fromJson(actionJson.toString(), Action.class);
                    behaviorActions.add(a);
                }
                behavior.setActions(behaviorActions);

                Log.d(TAG, "Created UserBehavior (" +
                        behavior.getMappingId() + ") with Behavior (" +
                        behavior.getId() + ")" + behavior.getTitle());

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return behaviors;
    }

    private List<Place> parseUserPlaces(JSONArray placeArray){
        List<Place> places = new ArrayList<>();

        try{
            for (int i = 0; i < placeArray.length(); i++){
                JSONObject placeObject = placeArray.getJSONObject(i);
                Place place = gson.fromJson(placeObject.toString(), Place.class);
                place.setName(placeObject.getJSONObject("place").getString("name"));
                place.setPrimary(placeObject.getJSONObject("place").getBoolean("primary"));
                places.add(place);
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }

        return places;
    }

    @Override
    protected void onPostExecute(UserData userData) {
        Log.d(TAG, "Finished");
        userData.logSelectedData("FROM UserDataTask.onPostExecute", false);
        mCallback.userDataLoaded(userData);
    }


    public interface GetUserDataListener{
        void userDataLoaded(UserData userData);
    }
}
