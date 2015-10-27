package org.tndata.android.compass.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;
import org.tndata.android.compass.util.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by isma on 10/27/15.
 */
public class GetTodaysActionsTask extends AsyncTask<Void, Void, List<Action>>{
    private GetTodaysActionsCallback mCallback;
    private String mToken;


    public GetTodaysActionsTask(@NonNull GetTodaysActionsCallback callback, @NonNull String token){
        mCallback = callback;
        mToken = token;
    }

    @Override
    protected List<Action> doInBackground(Void... params){
        String url = Constants.BASE_URL + "users/actions/?today=1";

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + mToken);

        InputStream stream = NetworkHelper.httpGetStream(url, headers);
        if (stream == null){
            return null;
        }
        try{
            BufferedReader bReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line, result = "";
            while ((line = bReader.readLine()) != null){
                result += line;
            }
            bReader.close();

            //TODO it'd be better to have this in the parser at some point
            Parser parser = new Parser();
            JSONArray actionArray = new JSONObject(result).getJSONArray("results");
            List<Action> actions = new ArrayList<>();
            for (int i = 0; i < actionArray.length(); i++){
                JSONObject actionObject = actionArray.getJSONObject(i);
                Action action = parser.parseAction(actionObject, true);
                Goal goal = parser.getGson().fromJson(actionObject.getString("primary_goal"), Goal.class);
                goal.setPrimaryCategory(parser.getGson().fromJson(actionObject.getString("primary_category"), Category.class));
                if (action != null){
                    action.setPrimaryGoal(goal);
                    action.setBehavior(parser.getGson().fromJson(actionObject.getString("behavior"), Behavior.class));
                    actions.add(action);
                }
            }
            return actions;
        }
        catch (IOException|JSONException x){
            x.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Action> actions){
        mCallback.onActionsLoaded(actions);
    }


    public interface GetTodaysActionsCallback{
        void onActionsLoaded(List<Action> actions);
    }
}
