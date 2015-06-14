package org.tndata.android.compass.task;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.model.Action;
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
public class AddActionTriggerTask extends AsyncTask<Void, Void, Void> {
    private String mToken;
    private String mActionMappingId;
    private String mRrule;
    private String mTime;

    public AddActionTriggerTask(String token, String rrule, String time, String actionMappingId) {
        mToken = token;
        mRrule = rrule;
        mTime = time;
        mActionMappingId = actionMappingId;
    }

    @Override
    protected Void doInBackground(Void... params) {

        String url = Constants.BASE_URL + "users/actions/" + mActionMappingId + "/";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + mToken);
        Log.d("user action trigger", url);

        JSONObject body = new JSONObject();
        try {
            body.put("custom_trigger_time", mTime);
            body.put("custom_trigger_rrule", mRrule);
        } catch (JSONException e1) {
            e1.printStackTrace();
            return null;
        }
        InputStream stream = NetworkHelper.httpPutStream(url, headers, body.toString());
        if (stream == null) {
            return null;
        }
        String result = "";
        String triggerResponse = "";
        try {

            BufferedReader bReader = new BufferedReader(new InputStreamReader(
                    stream, "UTF-8"));

            String line = null;
            while ((line = bReader.readLine()) != null) {
                result += line;
            }
            bReader.close();

            triggerResponse = Html.fromHtml(result).toString();

            JSONObject response = new JSONObject(triggerResponse);
            Log.d("trigger action", response.toString(2));


        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
