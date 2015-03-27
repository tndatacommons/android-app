package org.tndata.android.grow.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.grow.GrowApplication;
import org.tndata.android.grow.util.Constants;
import org.tndata.android.grow.util.NetworkHelper;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

public class AddBehaviorTask extends AsyncTask<String, Void, Void> {
    private Context mContext;

    public AddBehaviorTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        String token = ((GrowApplication) ((Activity) mContext)
                .getApplication()).getToken();
        String userId = String.valueOf(((GrowApplication) ((Activity) mContext)
                .getApplication()).getUser().getId());
        ArrayList<String> behaviorIds = new ArrayList<String>();
        for (int i = 0; i < params.length; i++) {
            behaviorIds.add(params[i]);
        }
        String url = Constants.BASE_URL + "users/behaviors/";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + token);
        JSONObject body = new JSONObject();
        try {
            body.put("user", userId);
            body.put("behavior", behaviorIds.get(0));
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

            JSONObject jObject = new JSONObject(createResponse);
            Log.d("user categories response", jObject.toString(2));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
