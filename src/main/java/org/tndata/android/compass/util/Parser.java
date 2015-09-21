package org.tndata.android.compass.util;

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

import java.util.ArrayList;
import java.util.List;


/**
 * Created by isma on 9/18/15.
 */
public class Parser{
    private Gson gson;


    public Parser(){
        gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
    }

    public List<Category> parseCategories(JSONArray categoryArray, boolean userCategories){
        List<Category> categories = new ArrayList<>();

        try{
            //For each category in the array
            for (int i = 0; i < categoryArray.length(); i++){
                //The string to be parsed by GSON is extracted from the array
                String categoryString;
                if (userCategories){
                    //If it is a user category, it will come as a nested object
                    categoryString = categoryArray.getJSONObject(i).getString("category");
                }
                else{
                    //If it is not, it will come as the object itself
                    categoryString = categoryArray.getString(i);
                }
                Category category = gson.fromJson(categoryString, Category.class);

                //The JSONObject is extracted, since it will be needed later on
                JSONObject categoryJson = categoryArray.getJSONObject(i);
                String goalArrayName;
                if (userCategories){
                    category.setMappingId(categoryJson.getInt("id"));
                    category.setProgressValue(categoryJson.getDouble("progress_value"));
                    category.setCustomTriggersAllowed(categoryJson.getBoolean("custom_triggers_allowed"));
                    goalArrayName = "user_goals";
                }
                else{
                    goalArrayName = "goals";
                }

                Log.d("CategoryParser", category.toString());

                //Set the Category's goals
                List<Goal> categoryGoals = new ArrayList<>();
                JSONArray goalArray = categoryJson.getJSONArray(goalArrayName);
                for (int j = 0; j < goalArray.length(); j++){
                    categoryGoals.add(gson.fromJson(goalArray.getString(j), Goal.class));
                    Log.d("CategoryParser", categoryGoals.get(j).toString());
                }
                category.setGoals(categoryGoals);
                categories.add(category);
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }

        return categories;
    }

    public List<Action> parseActions(JSONArray actionArray, boolean userActions){
        List<Action> actions = new ArrayList<>();

        try{
            //For each action in the array
            for (int i = 0; i < actionArray.length(); i++){
                //The string to be parsed by GSON is extracted from the array
                String actionString;
                if (userActions){
                    //If it is a user action, it will come as a nested object
                    actionString = actionArray.getJSONObject(i).getString("action");
                }
                else{
                    //If it is not, it will come as the object itself
                    actionString = actionArray.getString(i);
                }
                Action action = gson.fromJson(actionString, Action.class);
                //There are some other things that need to be parsed, but only if
                //  this is a user action.
                if (userActions){
                    //Extract the relevant object from the array
                    JSONObject actionObject = actionArray.getJSONObject(i);

                    //Set the user mapping id and the trigger allowance flag
                    action.setMappingId(actionObject.getInt("id"));
                    action.setCustomTriggersAllowed(actionObject.getBoolean("custom_triggers_allowed"));

                    //Parse the custom trigger if there is one
                    if (!actionObject.isNull("custom_trigger")){
                        String triggerString = actionObject.getString("custom_trigger");
                        action.setCustomTrigger(gson.fromJson(triggerString, Trigger.class));
                    }
                }
                Log.d("ActionParser", action.toString());
                actions.add(action);
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }

        return actions;
    }

    public List<Goal> parseGoals(JSONArray goalArray, boolean userGoals){
        List<Goal> goals = new ArrayList<>();



        return goals;
    }

    public List<Behavior> parseBehaviors(JSONArray behaviorArray, boolean userBehaviors){
        List<Behavior> behaviors = new ArrayList<>();
/*
        try{
            for (int i = 0; i < behaviorArray.length(); i++){
                //The string to be parsed by GSON is extracted from the array
                String behaviorString;
                if (userBehaviors){
                    //If it is a user behavior, it will come as a nested object
                    behaviorString = behaviorArray.getJSONObject(i).getString("behavior");
                }
                else{
                    //If it is not, it will come as the object itself
                    behaviorString = behaviorArray.getString(i);
                }
                Behavior behavior = gson.fromJson(behaviorString, Behavior.class);


                JSONObject behaviorJson = behaviorArray.getJSONObject(i);
                behavior.setMappingId(behaviorJson.getInt("id"));
                behavior.setCustomTriggersAllowed(behaviorJson.getBoolean("custom_triggers_allowed"));
                behaviors.add(behavior);

                // Set the Behavior's parent goals
                // parse these into Goal objects an set on the Behavior
                ArrayList<Goal> behaviorGoals = behavior.getGoals();
                JSONArray user_goals = behaviorJson.getJSONArray("user_goals");
                Log.d(TAG, "Behavior.user_goals JSON: " + user_goals.toString(2));
                for (int x = 0; x < user_goals.length(); x++){
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
                for (int x = 0; x < user_actions.length(); x++){
                    JSONObject actionJson = user_actions.getJSONObject(x);
                    Action a = gson.fromJson(actionJson.toString(), Action.class);
                    behaviorActions.add(a);
                }
                behavior.setActions(behaviorActions);

                Log.d(TAG, "Created UserBehavior (" +
                        behavior.getMappingId() + ") with Behavior (" +
                        behavior.getId() + ")" + behavior.getTitle());

            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }*/

        return behaviors;
    }
}
