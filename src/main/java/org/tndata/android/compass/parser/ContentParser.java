package org.tndata.android.compass.parser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.model.*;
import org.tndata.android.compass.model.Package;

import java.util.ArrayList;
import java.util.List;


/**
 * Parser for the classes that make up the core of the model (Category, Goal, Behavior, Action).
 *
 * TODO try to kill this class at some point. After networking background parsing ain't no overhead.
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
    public static CategoryContent parseCategory(String src){
        return sGson.fromJson(src, CategoryContent.class);
    }

    /**
     * Parses a list of categories into an id -> Category map
     *
     * @param src a JSON string containing a list of categories.
     * @return a map of categories.
     */
    public static List<CategoryContent> parseCategoryArray(String src){
        JSONArray categoriesArray;
        try{
            categoriesArray = new JSONArray(src);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }

        List<CategoryContent> categories = new ArrayList<>();
        for (int i = 0; i < categoriesArray.length(); i++){
            try{
                CategoryContent category = parseCategory(categoriesArray.getString(i));
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

    public static List<CategoryContent> parseCategories(String src){
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


    /*-------*
     * GOALS *
     *-------*/

    public static GoalContent parseGoal(String src){
        return sGson.fromJson(src, GoalContent.class);
    }

    public static UserGoal parseUserGoal(String src){
        return sGson.fromJson(src, UserGoal.class);
    }

    public static List<GoalContent> parseGoalArray(String src){
        JSONArray goalArray;
        try{
            goalArray = new JSONArray(src);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }

        List<GoalContent> goals = new ArrayList<>();
        for (int i = 0; i < goalArray.length(); i++){
            try{
                GoalContent goal = parseGoal(goalArray.getString(i));
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

    public static List<GoalContent> parseGoals(String src){
        try{
            return parseGoalArray(new JSONObject(src).getString("results"));
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


    /*---------*
     * ACTIONS *
     *---------*/

    public static UserAction parseUserAction(String src){
        return sGson.fromJson(src, UserAction.class);
    }


    public static List<UserAction> parseUserActions(String src){
        return sGson.fromJson(src, ActionList.class).results;
    }

    public static class ActionList{
        private List<UserAction> results = null;
    }
}
