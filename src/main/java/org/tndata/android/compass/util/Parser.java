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

    public List<Behavior> parseBehaviors(JSONArray behaviorArray, boolean userBehaviors){
        return null;
    }
}
