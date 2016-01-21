package org.tndata.android.compass.util;

import android.content.Context;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.model.UserBehavior;
import org.tndata.android.compass.model.UserGoal;
import org.tndata.android.compass.parser.ContentParser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Created by isma on 1/19/16.
 */
public class RequestResultProcessor{
    private static Set<Integer> sGoalResponseSet;
    private static Map<Integer, UserBehavior> sBehaviorResponseMap;
    private static Map<Integer, UserAction> sActionResponseMap;


    private static void init(){
        if (sGoalResponseSet == null){
            sGoalResponseSet = new HashSet<>();
        }
        if (sBehaviorResponseMap == null){
            sBehaviorResponseMap = new HashMap<>();
        }
        if (sActionResponseMap == null){
            sActionResponseMap = new HashMap<>();
        }
    }

    public static void registerGoal(int id){
        init();
        sGoalResponseSet.add(id);
    }

    public static void registerBehavior(int id){
        init();
        sBehaviorResponseMap.put(id, null);
    }

    public static void registerAction(int id){
        init();
        sActionResponseMap.put(id, null);
    }

    public static void resolveGoal(Context context, String src){
        UserGoal userGoal = ContentParser.parseUserGoal(src);
        sGoalResponseSet.remove(userGoal.getObjectId());
    }

    public static void resolveBehavior(Context context, String src){

    }

    public static void resolveAction(Context context, String src){

    }

    private static CompassApplication getApplication(Context context){
        return (CompassApplication)context.getApplicationContext();
    }
}
