package org.tndata.android.compass.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.model.*;

import java.util.ArrayList;
import java.util.List;


/**
 * A centralised parser. All the code that does parsing should go here. At the moment it
 * includes category, goal, behavior, action, and place parsing.
 *
 * @author Ismael Alonso
 * @version 1.0.0 (WIP)
 */
@Deprecated
public class Parser{
    private Gson gson;


    /**
     * Constructor.
     */
    public Parser(){
        gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
    }

    public Goal parseAddedGoal(String src){
        try{
            JSONObject userGoal = new JSONObject(src);
            Goal goal = gson.fromJson(userGoal.getString("goal"), Goal.class);
            goal.setMappingId(userGoal.getInt("id"));
            JSONArray categoryArray = userGoal.getJSONArray("user_categories");
            List<Category> categories = goal.getCategories();
            for (int x = 0; x < categoryArray.length(); x++){
                Category category = gson.fromJson(categoryArray.getString(x), Category.class);
                categories.add(category);
            }
            goal.setCategories(categories);
            return goal;
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }
    }

    public Behavior parseAddedBehavior(String src){
        try{
            JSONObject userBehavior = new JSONObject(src);
            Behavior behavior = gson.fromJson(userBehavior.getString("behavior"), Behavior.class);
            behavior.setMappingId(userBehavior.getInt("id"));

            // Include the Behavior's Parent goals that have been selected by the user
            JSONArray goalArray = userBehavior.getJSONArray("user_goals");
            List<Goal> goals = behavior.getGoals();
            for (int x = 0; x < goalArray.length(); x++){
                Goal goal = gson.fromJson(goalArray.getString(x), Goal.class);
                goals.add(goal);
            }
            behavior.setGoals(goals);
            return behavior;
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }
    }

    public Action parseAddedAction(String src){
        try{
            JSONObject userAction = new JSONObject(src);
            Action action = gson.fromJson(userAction.getString("action"), Action.class);

            if (action != null){
                action.setMappingId(userAction.getInt("id"));
                return action;
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return null;
    }

    public Action parseActionWithTrigger(String src){
        try{
            JSONObject response = new JSONObject(src);
            Action action = gson.fromJson(response.getString("action"), Action.class);
            action.setMappingId(response.getInt("id"));
            if (!response.isNull("custom_trigger")){
                action.setCustomTrigger(gson.fromJson(response.getString("custom_trigger"), Trigger.class));
            }
            action.setNextReminderDate(response.getString("next_reminder"));
            return action;
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }
    }

    public Goal parseGoal(String src){
        Goal goal = gson.fromJson(src, Goal.class);
        try{
            List<Category> categories = new ArrayList<>();
            goal.setCategories(categories);
            JSONArray categoriesArray = new JSONObject(src).getJSONArray("categories");
            for (int i = 0; i < categoriesArray.length(); i++){
                categories.add(gson.fromJson(categoriesArray.getString(i), Category.class));
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return goal;
    }

    public Gson getGson(){
        return gson;
    }
}
