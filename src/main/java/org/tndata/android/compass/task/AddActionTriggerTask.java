package org.tndata.android.compass.task;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Trigger;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kevin on 6/14/15.
 */
public class AddActionTriggerTask extends AsyncTask<Void, Void, Action> {
    private String mToken;
    private String mActionMappingId;
    private String mRrule;
    private String mTime;
    private String mDate;

    private static final String TAG = "AddActionTriggerTask";

    private AddActionTriggerTaskListener mCallback;
    private static Gson gson = new GsonBuilder().setFieldNamingPolicy(
            FieldNamingPolicy.IDENTITY).create();

    public AddActionTriggerTask(AddActionTriggerTaskListener callback,
                                String token,
                                String rrule,
                                String time,
                                String date,
                                String actionMappingId) {
        mCallback = callback;
        mToken = token;
        mRrule = rrule;
        mTime = time;
        mDate = date;
        mActionMappingId = actionMappingId;
    }

    @Override
    protected Action doInBackground(Void... params) {

        String url = Constants.BASE_URL + "users/actions/" + mActionMappingId + "/";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + mToken);

        Log.d(TAG, "PUT: " + url);
        Log.d(TAG, "custom_trigger_time: '" + mTime + "'");
        Log.d(TAG, "custom_trigger_date: '" + mDate + "'");
        Log.d(TAG, "custom_trigger_rrule: '" + mRrule + "'");

        JSONObject body = new JSONObject();
        try {
            body.put("custom_trigger_time", mTime);
            body.put("custom_trigger_rrule", mRrule);
            body.put("custom_trigger_date", mDate);
        } catch (JSONException e1) {
            e1.printStackTrace();
            return null;
        }
        InputStream stream = NetworkHelper.httpPutStream(url, headers, body.toString());
        if (stream == null) {
            return null;
        }
        String result = "";
        try {

            BufferedReader bReader = new BufferedReader(new InputStreamReader(
                    stream, "UTF-8"));

            String line = null;
            while ((line = bReader.readLine()) != null) {
                result += line;
            }
            bReader.close();

            Log.d(TAG, new JSONObject(result).toString(2));

            // create an Action object from the result.
            JSONObject response = new JSONObject(result);
            Action action = gson.fromJson(response.getString("action"), Action.class);
            action.setMappingId(response.getInt("id"));
            if(!response.isNull("custom_trigger")) {
                action.setCustomTrigger(
                        gson.fromJson(response.getString("custom_trigger"), Trigger.class));
            }
            action.setNextReminderDate(response.getString("next_reminder"));
            return action;

        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Action action){
        // Send an Action with the populated Trigger (or null)
        mCallback.actionTriggerAdded(action);
    }


    public interface AddActionTriggerTaskListener{
        boolean actionTriggerAdded(Action action);
    }
}
