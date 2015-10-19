package org.tndata.android.compass.task;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddActionTask extends AsyncTask<Void, Void, Action> {
    private Context mContext;
    private Action mAction;
    private Goal mGoal;
    private AddActionTaskListener mCallback;
    private static Gson gson = new GsonBuilder().setFieldNamingPolicy(
            FieldNamingPolicy.IDENTITY).create();

    public interface AddActionTaskListener {
        public void actionAdded(Action action);
    }

    public AddActionTask(Context context, AddActionTaskListener callback, Action action) {
        mContext = context;
        mCallback = callback;
        mGoal = null;
        mAction = action;
    }

    public AddActionTask(Context context, AddActionTaskListener callback, Goal goal, Action action) {
        mContext = context;
        mCallback = callback;
        mGoal = goal;
        mAction = action;
    }

    @Override
    protected Action doInBackground(Void... params) {

        CompassApplication application = (CompassApplication) ((Activity) mContext).getApplication();
        String token = application.getToken();

        // To associate the added Action with the currently selected behavior, we need the
        // applications list of selected Behaviors.
        List<Behavior> selectedBehaviors = application.getBehaviors();

        String url = Constants.BASE_URL + "users/actions/";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + token);
        JSONObject body = new JSONObject();
        try {
            body.put("action", mAction.getId());
            if(mGoal != null) {
                body.put("primary_goal", mGoal.getId());
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
            return null;
        }
        InputStream stream = NetworkHelper.httpPostStream(url, headers,
                body.toString());
        if (stream == null) {
            return null;
        }
        String result = "";
        String createResponse = "";
        try {

            BufferedReader bReader = new BufferedReader(new InputStreamReader(
                    stream, "UTF-8"));

            String line = null;
            while ((line = bReader.readLine()) != null) {
                result += line;
            }
            bReader.close();

            createResponse = Html.fromHtml(result).toString();

            JSONObject userAction = new JSONObject(createResponse);
            Log.d("user action", userAction.toString(2));
            Action action = gson.fromJson(userAction.getString("action"), Action.class);

            if(action != null) {
                action.setMappingId(userAction.getInt("id"));

                // Associate the behavior, as well
                for(Behavior behavior : selectedBehaviors) {
                    if(behavior.getId() == action.getBehavior_id()) {
                        action.setBehavior(behavior);
                    }
                }
                return action;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Action action) {
        if(action != null) {
            mCallback.actionAdded(action);
        }else{
            Log.e("user action", "onPostExecute, action is null!?");
        }
    }

}
