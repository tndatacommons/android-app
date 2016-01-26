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


    /*------------*
     * CATEGORIES *
     *------------*/

    /**
     * Populates a Category object with values from a JSON string.
     *
     * @param src the JSON string from which the category will be created.
     * @return a category.
     */
    public static Category parseCategory(String src){
        return sGson.fromJson(src, Category.class);
    }

    /**
     * Parses a user category.
     *
     * @param src a JSON string containing a user category.
     * @return a parsed user category.
     */
    public static UserCategory parseUserCategory(String src){
        return sGson.fromJson(src, UserCategory.class);
    }

    /**
     * Parses a list of categories into an id -> Category map
     *
     * @param src a JSON string containing a list of categories.
     * @return a map of categories.
     */
    public static List<Category> parseCategoryArray(String src){
        JSONArray categoriesArray;
        try{
            categoriesArray = new JSONArray(src);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }

        List<Category> categories = new ArrayList<>();
        for (int i = 0; i < categoriesArray.length(); i++){
            try{
                Category category = parseCategory(categoriesArray.getString(i));
                if (category != null){
                    categories.add(category);
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

    public static List<Category> parseCategories(String src){
        try{
            return parseCategoryArray(new JSONObject(src).getString("results"));
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
    public static Map<Integer, UserCategory> parseUserCategoryArray(String src){
        JSONArray categoriesArray;
        try{
            categoriesArray = new JSONArray(src);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }

        Map<Integer, UserCategory> categories = new HashMap<>();
        for (int i = 0; i < categoriesArray.length(); i++){
            try{
                UserCategory category = parseUserCategory(categoriesArray.getString(i));
                if (category != null){
                    categories.put(category.getId(), category);
                }
                else{
                    Log.d("UserCategoryParser", "UserCategory #" + i + " is null.");
                }
            }
            catch (JSONException jsonx){
                jsonx.printStackTrace();
            }
        }
        return categories;
    }

    public static Map<Integer, UserCategory> parseUserCategories(String src){
        try{
            return parseUserCategoryArray(new JSONObject(src).getString("results"));
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


    /*-------*
     * GOALS *
     *-------*/

    public static Goal parseGoal(String src){
        return sGson.fromJson(src, Goal.class);
    }

    public static UserGoal parseUserGoal(String src){
        return sGson.fromJson(src, UserGoal.class);
    }

    public static List<Goal> parseGoalArray(String src){
        JSONArray goalArray;
        try{
            goalArray = new JSONArray(src);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }

        List<Goal> goals = new ArrayList<>();
        for (int i = 0; i < goalArray.length(); i++){
            try{
                Goal goal = parseGoal(goalArray.getString(i));
                if (goal != null){
                    goals.add(goal);
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

    public static List<Goal> parseGoals(String src){
        try{
            return parseGoalArray(new JSONObject(src).getString("results"));
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }
    }

    public static Map<Integer, UserGoal> parseUserGoalArray(String src){
        JSONArray goalArray;
        try{
            goalArray = new JSONArray(src);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }

        Map<Integer, UserGoal> goals = new HashMap<>();
        for (int i = 0; i < goalArray.length(); i++){
            try{
                UserGoal goal = parseUserGoal(goalArray.getString(i));
                if (goal != null){
                    Log.d("UserGoalParser", goal.toString());
                    goals.put(goal.getId(), goal);
                }
                else{
                    Log.d("UserGoalParser", "UserGoal #" + i + " is null.");
                }
            }
            catch (JSONException jsonx){
                jsonx.printStackTrace();
            }
        }
        return goals;
    }

    public static Map<Integer, UserGoal> parseUserGoals(String src){
        try{
            return parseUserGoalArray(new JSONObject(src).getString("results"));
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }
    }


    /*-----------*
     * BEHAVIORS *
     *-----------*/

    public static BehaviorContent parseBehavior(String src){
        return sGson.fromJson(src, BehaviorContent.class);
    }

    public static UserBehavior parseUserBehavior(String src){
        return sGson.fromJson(src, UserBehavior.class);
    }

    public static List<BehaviorContent> parseBehaviorArray(String src){
        JSONArray behaviorArray;
        try{
            behaviorArray = new JSONArray(src);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }

        List<BehaviorContent> behaviors = new ArrayList<>();
        for (int i = 0; i < behaviorArray.length(); i++){
            try{
                BehaviorContent behavior = parseBehavior(behaviorArray.getString(i));
                if (behavior != null){
                    behaviors.add(behavior);
                }
            }
            catch (JSONException jsonx){
                jsonx.printStackTrace();
            }
        }
        return behaviors;
    }

    public static List<BehaviorContent> parseBehaviors(String src){
        try{
            return parseBehaviorArray(new JSONObject(src).getString("results"));
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }
    }

    public static Map<Integer, UserBehavior> parseUserBehaviorArray(String src){
        JSONArray behaviorArray;
        try{
            behaviorArray = new JSONArray(src);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }

        Map<Integer, UserBehavior> behaviors = new HashMap<>();
        for (int i = 0; i < behaviorArray.length(); i++){
            try{
                UserBehavior behavior = parseUserBehavior(behaviorArray.getString(i));
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

    public static Map<Integer, UserBehavior> parseUserBehaviors(String src){
        try{
            return parseUserBehaviorArray(new JSONObject(src).getString("results"));
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }
    }


    /*---------*
     * ACTIONS *
     *---------*/

    public static ActionContent parseAction(String src){
        return sGson.fromJson(src, ActionContent.class);
    }

    public static UserAction parseUserAction(String src){
        return sGson.fromJson(src, UserAction.class);
    }

    public static List<ActionContent> parseActionArray(String src){
        JSONArray actionArray;
        try{
            actionArray = new JSONArray(src);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }

        List<ActionContent> actions = new ArrayList<>();
        //For each action in the array
        for (int i = 0; i < actionArray.length(); i++){
            try{
                ActionContent action = parseAction(actionArray.getString(i));
                if (action != null){
                    actions.add(action);
                }
            }
            catch (JSONException jsonx){
                jsonx.printStackTrace();
            }
        }
        return actions;
    }

    public static Map<Integer, UserAction> parseUserActionArray(String src, @Nullable List<UserAction> target){
        JSONArray actionArray;
        try{
            actionArray = new JSONArray(src);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }

        Map<Integer, UserAction> actions = new HashMap<>();
        //For each action in the array
        for (int i = 0; i < actionArray.length(); i++){
            try{
                UserAction action = parseUserAction(actionArray.getString(i));
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

    public static List<ActionContent> parseActionsFromResultSet(String src){
        try{
            return parseActionArray(new JSONObject(src).getString("results"));
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }
    }

    public static Map<Integer, UserAction> parseUserActions(String src, @Nullable List<UserAction> target){
        return parseUserActionArray(src, target);
    }

    public static Map<Integer, UserAction> parseUserActionsFromResultSet(String src, @Nullable List<UserAction> target){
        try{
            return parseUserActionArray(new JSONObject(src).getString("results"), target);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }
    }


    public static List<UserAction> parseUserActions(String src){
        return sGson.fromJson(src, ActionList.class).results;
    }

    public static class ActionList{
        private List<UserAction> results = null;
    }
}
