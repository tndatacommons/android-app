package org.tndata.android.compass.parser;

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.model.*;
import org.tndata.android.compass.model.Package;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Parser for the classes that make up the core of the model (Category, Goal, Behavior, Action).
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public final class ContentParser extends ParserMethods{
    /**
     * Populates a Category object with values from a JSON string.
     *
     * @param src the JSON string from which the category will be created.
     * @return a category.
     */
    public static Category parseCategoryModel(String src){
        return sGson.fromJson(src, Category.class);
    }

    /**
     * Parses a category or a user category, depending on the object_type value.
     *
     * @param src a JSON string containing a category.
     * @return a parsed category.
     */
    public static Category parseCategory(String src){
        try{
            //Create the JSON object and determine whether this is a user category
            JSONObject categoryObject = new JSONObject(src);
            boolean userContent = categoryObject.getString("object_type").equals("usercategory");

            //Parse the category object
            Category category = userContent ?
                    parseCategoryModel(categoryObject.getString("category"))
                    :
                    parseCategoryModel(src);

            String goalArrayName;
            if (userContent){
                //If this is a user category, set the user related values
                category.setMappingId(categoryObject.getInt("id"));
                category.setProgressValue(categoryObject.getDouble("progress_value"));
                category.setEditable(categoryObject.getBoolean("editable"));
                goalArrayName = "user_goals";
            }
            else{
                goalArrayName = "goals";
            }

            //Set the category's goals
            try{
                List<Goal> goals = new ArrayList<>();
                JSONArray goalArray = categoryObject.getJSONArray(goalArrayName);
                for (int i = 0; i < goalArray.length(); i++){
                    goals.add(parseGoalModel(goalArray.getString(i)));
                }
                category.setGoals(goals);
            }
            catch (JSONException jsonx){
                category.setGoals(new ArrayList<Goal>());
            }

            return category;
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }
    }

    /**
     * Parses a list of categories into an id -> Category map
     *
     * @param src a JSON string containing a list of categories.
     * @return a map of categories.
     */
    public static Map<Integer, Category> parseCategoryArray(String src){
        JSONArray categoriesArray;
        try{
            categoriesArray = new JSONArray(src);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }

        Map<Integer, Category> categories = new HashMap<>();
        for (int i = 0; i < categoriesArray.length(); i++){
            try{
                Category category = parseCategory(categoriesArray.getString(i));
                if (category != null){
                    categories.put(category.getId(), category);
                }
                else{
                    Log.d("CategoryParser", "Category #" + i + " is null.");
                }
            }
            catch (JSONException jsonx){
                jsonx.printStackTrace();
            }
        }
        return categories;
    }

    public static Map<Integer, Category> parseCategories(String src){
        try{
            return parseCategoryArray(new JSONObject(src).getString("results"));
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }
    }

    public static Package parsePackage(String src){
        try{
            Package myPackage = sGson.fromJson(new JSONObject(src).getString("category"), Package.class);
            myPackage.setId(new JSONObject(src).getInt("id"));
            return myPackage;
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }
    }

    public static Goal parseGoalModel(String src){
        return sGson.fromJson(src, Goal.class);
    }

    public static Goal parseGoal(String src){
        try{
            JSONObject goalObject = new JSONObject(src);
            boolean userContent = goalObject.getString("object_type").equals("usergoal");

            //Parse the goal object
            Goal goal = userContent ?
                    parseGoalModel(goalObject.getString("goal"))
                    :
                    parseGoalModel(src);

            String categoryArrayName;
            if (userContent){
                goal.setProgressValue(goalObject.getDouble("progress_value"));
                goal.setMappingId(goalObject.getInt("id"));
                goal.setEditable(goalObject.getBoolean("editable"));

                //Set the goal's child behaviors
                List<Behavior> behaviors = new ArrayList<>();
                try{
                    JSONArray behaviorArray = goalObject.getJSONArray("user_behaviors");
                    for (int j = 0; j < behaviorArray.length(); j++){
                        behaviors.add(parseBehaviorModel(behaviorArray.getString(j)));
                    }
                    goal.setBehaviors(behaviors);
                }
                //This happens when there is n array in user_behaviors
                catch (JSONException jsonx){
                    goal.setBehaviors(new ArrayList<Behavior>());
                }

                //Set the primary category
                goal.setPrimaryCategory(parseCategoryModel(goalObject.getString("primary_category")));

                //Set the progress
                goal.setProgress(sGson.fromJson(goalObject.getString("goal_progress"), Progress.class));

                categoryArrayName = "user_categories";
            }
            else{
                categoryArrayName = "categories";
            }

            //Set the goal's parent categories
            List<Category> categories = new ArrayList<>();
            JSONArray categoryArray = goalObject.getJSONArray(categoryArrayName);
            for (int j = 0; j < categoryArray.length(); j++){
                categories.add(sGson.fromJson(categoryArray.getString(j), Category.class));
            }
            goal.setCategories(categories);

            return goal;
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }
    }

    public static Map<Integer, Goal> parseGoalArray(String src){
        JSONArray goalArray;
        try{
            goalArray = new JSONArray(src);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }

        Map<Integer, Goal> goals = new HashMap<>();
        for (int i = 0; i < goalArray.length(); i++){
            try{
                Goal goal = parseGoal(goalArray.getString(i));
                if (goal != null){
                    goals.put(goal.getId(), goal);
                }
                else{
                    Log.d("GoalParser", "Goal #" + i + " is null.");
                }
            }
            catch (JSONException jsonx){
                jsonx.printStackTrace();
            }
        }
        return goals;
    }

    public static Map<Integer, Goal> parseGoals(String src){
        try{
            return parseGoalArray(new JSONObject(src).getString("results"));
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }
    }

    public static Behavior parseBehaviorModel(String src){
        return sGson.fromJson(src, Behavior.class);
    }

    public static Behavior parseBehavior(String src){
        try{
            JSONObject behaviorObject = new JSONObject(src);
            boolean userContent = behaviorObject.getString("object_type").equals("userbehavior");

            //Parse the behavior object
            Behavior behavior = userContent ?
                    parseBehaviorModel(behaviorObject.getString("behavior"))
                    :
                    parseBehaviorModel(src);

            String goalArrayName;
            if (userContent){
                behavior.setMappingId(behaviorObject.getInt("id"));
                behavior.setEditable(behaviorObject.getBoolean("editable"));

                //Set the behavior's child actions
                List<Action> actions = new ArrayList<>();
                try{
                    JSONArray actionArray = behaviorObject.getJSONArray("user_actions");
                    for (int i = 0; i < actionArray.length(); i++){
                        actions.add(parseActionModel(actionArray.getString(i)));
                    }
                    behavior.setActions(actions);
                }
                //This happens when there is no array in user_actions
                catch (JSONException jsonx){
                    jsonx.printStackTrace();
                    behavior.setActions(new ArrayList<Action>());
                }

                try{
                    JSONArray categoryArray = behaviorObject.getJSONArray("user_categories");
                    List<Category> categories = behavior.getUserCategories();
                    for (int i = 0; i < categoryArray.length(); i++){
                        categories.add(parseCategoryModel(categoryArray.getString(i)));
                    }
                    behavior.setUserCategories(categories);
                }
                catch (JSONException jsonx){
                    behavior.setUserCategories(new ArrayList<Category>());
                }

                behavior.setProgress(sGson.fromJson(behaviorObject.getString("behavior_progress"), Progress.class));

                goalArrayName = "user_goals";
            }
            else{
                goalArrayName = "goals";
            }

            //Set the behavior's parent goals
            List<Goal> goals = behavior.getGoals();
            try{
                JSONArray goalArray = behaviorObject.getJSONArray(goalArrayName);
                for (int i = 0; i < goalArray.length(); i++){
                    goals.add(parseGoalModel(goalArray.getString(i)));
                }
                behavior.setGoals(goals);
            }
            catch (JSONException jsonx){
                behavior.setGoals(new ArrayList<Goal>());
            }

            return behavior;
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }
    }

    public static Map<Integer, Behavior> parseBehaviorArray(String src){
        JSONArray behaviorArray;
        try{
            behaviorArray = new JSONArray(src);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }

        Map<Integer, Behavior> behaviors = new HashMap<>();
        for (int i = 0; i < behaviorArray.length(); i++){
            try{
                Behavior behavior = parseBehavior(behaviorArray.getString(i));
                if (behavior != null){
                    behaviors.put(behavior.getId(), behavior);
                }
            }
            catch (JSONException jsonx){
                jsonx.printStackTrace();
            }
        }
        return behaviors;
    }

    public static Map<Integer, Behavior> parseBehaviors(String src){
        try{
            return parseBehaviorArray(new JSONObject(src).getString("results"));
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }
    }

    public static Action parseActionModel(String src){
        return sGson.fromJson(src, Action.class);
    }

    public static Action parseAction(String src){
        try{
            JSONObject actionObject = new JSONObject(src);
            boolean userContent = actionObject.getString("object_type").equals("useraction");

            //Parse the action object
            Action action = userContent ?
                    parseActionModel(actionObject.getString("action"))
                    :
                    parseActionModel(src);

            //There are some other things that need to be parsed, but only if this is a user action
            if (userContent){
                //Set the user mapping id and the editable flag
                action.setMappingId(actionObject.getInt("id"));
                action.setEditable(actionObject.getBoolean("editable"));

                //Parse the custom trigger if there is one
                if (!actionObject.isNull("custom_trigger")){
                    String triggerString = actionObject.getString("custom_trigger");
                    action.setCustomTrigger(sGson.fromJson(triggerString, Trigger.class));
                }

                action.setPrimaryGoal(parseGoalModel(actionObject.getString("primary_goal")));
                action.setNextReminderDate(actionObject.getString("next_reminder"));

                action.setPrimaryCategory(parseCategoryModel(actionObject.getString("primary_category")));
            }
            action.setBehavior(parseBehaviorModel(actionObject.getString("behavior")));

            return action;
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }
    }

    public static Map<Integer, Action> parseActionArray(String src, @Nullable List<Action> target){
        JSONArray actionArray;
        try{
            actionArray = new JSONArray(src);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }

        Map<Integer, Action> actions = new HashMap<>();
        //For each action in the array
        for (int i = 0; i < actionArray.length(); i++){
            try{
                Action action = parseAction(actionArray.getString(i));
                if (action != null){
                    actions.put(action.getId(), action);
                    if (target != null){
                        target.add(action);
                    }
                }
            }
            catch (JSONException jsonx){
                jsonx.printStackTrace();
            }
        }
        return actions;
    }

    public static Map<Integer, Action> parseActions(String src){
        return parseActionArray(src, null);
    }

    public static Map<Integer, Action> parseActionsFromResultSet(String src, @Nullable List<Action> target){
        try{
            return parseActionArray(new JSONObject(src).getString("results"), target);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }
    }

    public static Map<Integer, Action> parseActions(String src, @Nullable List<Action> target){
        return parseActionArray(src, target);
    }
}
