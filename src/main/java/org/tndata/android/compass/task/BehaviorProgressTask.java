package org.tndata.android.compass.task;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class BehaviorProgressTask extends AsyncTask<String, Void, Void> {

    private Context mContext;
    private BehaviorProgressTaskListener mCallback;
    private String token;

    public interface BehaviorProgressTaskListener {
        public void behaviorProgressSaved();
    }

    public BehaviorProgressTask(Context context, BehaviorProgressTaskListener callback) {
        mContext = context;
        mCallback = callback;
        token = ((CompassApplication) ((Activity) mContext).getApplication()).getToken();
    }

    @Override
    protected Void doInBackground(String... params) {
        String behaviorId = params[0];
        String progressValue = params[1];

        String url = Constants.BASE_URL + "users/behaviors/progress/";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + token);

        JSONObject payload = new JSONObject();
        try {
            payload.put("status", progressValue);
            payload.put("behavior", behaviorId);
        } catch (JSONException e1) {
            e1.printStackTrace();
            return null;
        }

        InputStream stream = NetworkHelper.httpPostStream(
                url, headers, payload.toString());
        if(stream == null) {
            return null;
        }

        String result = "";
        String createResponse;
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(
                    stream, "UTF-8"));

            String line;
            while ((line = bReader.readLine()) != null) {
                result += line;
            }
            bReader.close();
            createResponse = Html.fromHtml(result).toString();
            JSONObject json_result = new JSONObject(createResponse);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // NOTE: We aren't doing anything with the results in the app,
        // so just ignore failures?
        return null;
    }

    @Override
    protected void onPostExecute(Void param) {
        mCallback.behaviorProgressSaved();
    }

}
