package org.tndata.android.compass.parser;

import android.os.AsyncTask;

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

import java.util.HashMap;


/**
 * Worker that does all the parsing in the background.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public final class ParserWorker extends AsyncTask<Void, Void, Void>{
    private int mRequestCode;
    private String mSrc;
    private ParserCallback mCallback;

    private ParseResults mResults;
    private Gson gson;

    /**
     * Constructor.
     *
     * @param requestCode the request code, needed for the callback.
     * @param src the string to parse.
     * @param callback the callback object.
     */
    ParserWorker(int requestCode, String src, ParserCallback callback){
        mRequestCode = requestCode;
        mSrc = src;
        mCallback = callback;

        mResults = new ParseResults();
        gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
    }

    @Override
    protected Void doInBackground(Void... params){
        try{
            parse(new JSONObject(mSrc));
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return null;
    }

    /**
     * Method used to parse an object whose structure is unknown.
     *
     * @param src the source object.
     * @throws JSONException
     */
    private void parse(JSONObject src) throws JSONException{
        switch (src.getString("object_type")){
            case "user_data":
                parseUserData(src);
                break;
        }
    }

    private void parseUserData(JSONObject src) throws JSONException{
        //First, parse out the user content
        mResults.mCategories = new HashMap<>();
        JSONArray categoryArray = src.getJSONArray("categories");
        for (int i = 0; i < categoryArray.length(); i++){
            JSONObject userCategory = categoryArray.getJSONObject(i);
            Category category = gson.fromJson(userCategory.getString("category"), Category.class);
            category.setMappingId(userCategory.getInt("id"));
            category.setEditable(userCategory.getBoolean("editable"));
            mResults.mCategories.put(category.getId(), category);
        }

        mResults.mGoals = new HashMap<>();
        JSONArray goalArray = src.getJSONArray("goals");
        for (int i = 0; i < goalArray.length(); i++){
            JSONObject userGoal = goalArray.getJSONObject(i);
            Goal goal = gson.fromJson(goalArray.getString(i), Goal.class);
            goal.setMappingId(userGoal.getInt("id"));
            goal.setEditable(userGoal.getBoolean("editable"));
            mResults.mGoals.put(goal.getId(), goal);
        }

        mResults.mBehaviors = new HashMap<>();
        JSONArray behaviorArray = src.getJSONArray("behaviors");
        for (int i = 0; i < behaviorArray.length(); i++){
            JSONObject userBehavior = behaviorArray.getJSONObject(i);
            Behavior behavior = gson.fromJson(behaviorArray.getString(i), Behavior.class);
            behavior.setMappingId(userBehavior.getInt("id"));
            behavior.setEditable(userBehavior.getBoolean("editable"));
            mResults.mBehaviors.put(behavior.getId(), behavior);
        }

        mResults.mActions = new HashMap<>();
        JSONArray actionArray = src.getJSONArray("actions");
        for (int i = 0; i < actionArray.length(); i++){
            JSONObject userAction = behaviorArray.getJSONObject(i);
            Action action = gson.fromJson(actionArray.getString(i), Action.class);
            action.setMappingId(userAction.getInt("id"));
            action.setEditable(userAction.getBoolean("editable"));
            mResults.mActions.put(action.getId(), action);
        }

        //Start the linking
        JSONObject dataGraph = src.getJSONObject("data_graph");
        categoryArray = dataGraph.getJSONArray("categories");
        for (int i = 0; i < categoryArray.length(); i++){
            JSONArray catGoalRelationship = categoryArray.getJSONArray(i);
            Category category = mResults.mCategories.get(catGoalRelationship.getInt(0));
            Goal goal = mResults.mGoals.get(catGoalRelationship.getInt(1));
            category.addGoal(goal);
            goal.addCategory(category);
        }

        goalArray = dataGraph.getJSONArray("goals");
        for (int i = 0; i < goalArray.length(); i++){
            JSONArray goalBehaviorRelationship = goalArray.getJSONArray(i);
            Goal goal = mResults.mGoals.get(goalBehaviorRelationship.getInt(0));
            Behavior behavior = mResults.mBehaviors.get(goalBehaviorRelationship.getInt(1));
            goal.addBehavior(behavior);
            behavior.addGoal(goal);
        }

        behaviorArray = dataGraph.getJSONArray("behaviors");
        for (int i = 0; i < behaviorArray.length(); i++){
            JSONArray behaviorActionRelationship = behaviorArray.getJSONArray(i);
            Behavior behavior = mResults.mBehaviors.get(behaviorActionRelationship.getInt(0));
            Action action = mResults.mActions.get(behaviorActionRelationship.getInt(1));
            behavior.addAction(action);
            //TODO, check with Brad
            action.setBehavior(behavior);
        }
    }

    @Override
    protected void onPostExecute(Void result){
        mCallback.onParseSuccess(mRequestCode, mResults);
    }
}
