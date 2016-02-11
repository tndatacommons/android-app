package org.tndata.android.compass.parser;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.model.*;
import org.tndata.android.compass.model.Package;

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
}
