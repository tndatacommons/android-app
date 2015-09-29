package org.tndata.android.compass.util;

import android.support.annotation.Nullable;
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
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.Place;
import org.tndata.android.compass.model.Trigger;

import java.util.ArrayList;
import java.util.List;


/**
 * A centralised parser. All the code that does parsing should go here. At the moment it
 * includes category, goal, behavior, action, and place parsing.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class Parser{
    private Gson gson;


    /**
     * Constructor.
     */
    public Parser(){
        gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
    }

    /**
     * Parses out the category list.
     *
     * @param categoryArray a JSONArray containing categories.
     * @param userCategories true if the data comes from a api/user/ prefixed endpoint.
     * @return A list of categories.
     */
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

                //Set the category's goals
                List<Goal> goals = new ArrayList<>();
                JSONArray goalArray = categoryJson.getJSONArray(goalArrayName);
                for (int j = 0; j < goalArray.length(); j++){
                    goals.add(gson.fromJson(goalArray.getString(j), Goal.class));
                }
                category.setGoals(goals);

                Log.d("CategoryParser", category.toString());
                categories.add(category);
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }

        return categories;
    }

    /**
     * Parses out the goal list.
     *
     * @param goalArray a JSONArray containing goals.
     * @param userGoals true if the data comes from a api/user/ prefixed endpoint.
     * @return A list of goals.
     */
    public List<Goal> parseGoals(JSONArray goalArray, boolean userGoals){
        List<Goal> goals = new ArrayList<>();

        try {
            //For each category in the array
            for (int i = 0; i < goalArray.length(); i++){
                //The string to be parsed by GSON is extracted from the array
                String categoryString;
                if (userGoals){
                    //If it is a user goal, it will come as a nested object
                    categoryString = goalArray.getJSONObject(i).getString("goal");
                }
                else{
                    //If it is not, it will come as the object itself
                    categoryString = goalArray.getString(i);
                }
                Goal goal = gson.fromJson(categoryString, Goal.class);

                JSONObject goalJson = goalArray.getJSONObject(i);
                String categoryArrayName;
                if (userGoals){
                    goal.setProgressValue(goalJson.getDouble("progress_value"));
                    goal.setMappingId(goalJson.getInt("id"));
                    goal.setCustomTriggersAllowed(goalJson.getBoolean("custom_triggers_allowed"));

                    //Set the goal's child behaviors
                    List<Behavior> behaviors = new ArrayList<>();
                    JSONArray behaviorArray = goalJson.getJSONArray("user_behaviors");
                    for (int j = 0; j < behaviorArray.length(); j++){
                        behaviors.add(gson.fromJson(behaviorArray.getString(j), Behavior.class));
                    }
                    goal.setBehaviors(behaviors);

                    categoryArrayName = "user_categories";
                }
                else{
                    categoryArrayName = "categories";
                }

                //Set the goal's parent categories
                List<Category> categories = new ArrayList<>();
                JSONArray categoryArray = goalJson.getJSONArray(categoryArrayName);
                for (int j = 0; j < categoryArray.length(); j++){
                    categories.add(gson.fromJson(categoryArray.getString(j), Category.class));
                }
                goal.setCategories(categories);

                Log.d("GoalParser", goal.toString());
                goals.add(goal);
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }

        return goals;
    }

    /**
     * Parses out the goal list.
     *
     * @param behaviorArray a JSONArray containing behaviors.
     * @param userBehaviors true if the data comes from a api/user/ prefixed endpoint.
     * @return A list of behaviors.
     */
    public List<Behavior> parseBehaviors(JSONArray behaviorArray, boolean userBehaviors){
        List<Behavior> behaviors = new ArrayList<>();

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
                String goalArrayName;
                if (userBehaviors){
                    behavior.setMappingId(behaviorJson.getInt("id"));
                    behavior.setCustomTriggersAllowed(behaviorJson.getBoolean("custom_triggers_allowed"));

                    //Set the behavior's child actions
                    List<Action> actions = new ArrayList<>();
                    JSONArray actionArray = behaviorJson.getJSONArray("user_actions");
                    for (int j = 0; j < actionArray.length(); j++){
                        actions.add(parseAction(actionArray.getJSONObject(j), false));
                    }
                    behavior.setActions(actions);

                    JSONArray categoryArray = behaviorJson.getJSONArray("user_categories");
                    List<Category> categories = behavior.getUserCategories();
                    for (int j = 0; j < categoryArray.length(); j++){
                        categories.add(gson.fromJson(categoryArray.getString(j), Category.class));
                    }
                    behavior.setUserCategories(categories);

                    goalArrayName = "user_goals";
                }
                else{
                    goalArrayName = "goals";
                }

                //Set the behavior's parent goals
                List<Goal> goals = behavior.getGoals();
                JSONArray goalArray = behaviorJson.getJSONArray(goalArrayName);
                for (int j = 0; j < goalArray.length(); j++){
                    goals.add(gson.fromJson(goalArray.getJSONObject(j).toString(), Goal.class));
                }
                behavior.setGoals(goals);

                Log.d("BehaviorParser", behavior.toString());
                behaviors.add(behavior);
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }

        return behaviors;
    }

    /**
     * Parses out the action list.
     *
     * @param actionArray a JSONArray containing actions.
     * @param userActions true if the data comes from a api/user/ prefixed endpoint.
     * @return A list of actions.
     */
    public List<Action> parseActions(JSONArray actionArray, boolean userActions){
        List<Action> actions = new ArrayList<>();

        try{
            //For each action in the array
            for (int i = 0; i < actionArray.length(); i++){
                actions.add(parseAction(actionArray.getJSONObject(i), userActions));
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }

        return actions;
    }

    /**
     * Parses out a single action.
     *
     * @param actionObject the JSONObject containing the action.
     * @param userAction true if the data is un user format.
     * @return the parsed action.
     */
    @Nullable
    private Action parseAction(JSONObject actionObject, boolean userAction){
        //The string to be parsed by GSON is extracted from the array
        try{
            String actionString;
            if (userAction){
                //If it is a user action, it will come as a nested object
                actionString = actionObject.getString("action");
            }
            else{
                //If it is not, it will come as the object itself
                actionString = actionObject.toString();
            }
            Action action = gson.fromJson(actionString, Action.class);
            //There are some other things that need to be parsed, but only if this is a user action
            if (userAction){
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
            return action;
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }

        return null;
    }

    /**
     * Parses out the place list.
     *
     * @param placeArray a JSONArray containing a list of places.
     * @return a list of places.
     */
    public List<Place> parsePlaces(JSONArray placeArray){
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

    public FeedData parseFeedData(JSONObject feedData){
        FeedData data = new FeedData();

        try{
            JSONObject action = feedData.getJSONObject("next_action");
            data.setNextAction(parseAction(action, true));
            data.getNextAction().setPrimaryGoal(gson.fromJson(action.getString("primary_goal"), Goal.class));

            JSONObject progress = feedData.getJSONObject("progress");
            data.setProgressPercentage(progress.getInt("progress"));
            data.setCompletedActions(progress.getInt("completed"));
            data.setTotalActions(progress.getInt("total"));

            data.setUserGoals(parseGoals(feedData.getJSONArray("user_goals"), true));
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }

        return data;
    }
}