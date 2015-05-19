package org.tndata.android.compass.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.os.AsyncTask;
import android.text.Html;

public class BehaviorLoaderTask extends
        AsyncTask<String, Void, ArrayList<Behavior>> {
    private BehaviorLoaderListener mCallback;
    private static Gson gson = new GsonBuilder().setFieldNamingPolicy(
            FieldNamingPolicy.IDENTITY).create();

    public interface BehaviorLoaderListener {
        public void behaviorsLoaded(ArrayList<Behavior> behaviors);
    }

    public BehaviorLoaderTask(BehaviorLoaderListener callback) {
        mCallback = callback;
    }

    @Override
    protected ArrayList<Behavior> doInBackground(String... params) {
        String token = params[0];
        String goalId = null;
        if (params.length > 1) {
            goalId = params[1];
        }
        String url = Constants.BASE_URL + "behaviors/";
        if (goalId != null) {
            url += "?goal=" + goalId;
        }
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + token);
        InputStream stream = NetworkHelper.httpGetStream(url, headers);
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
            ArrayList<Behavior> behaviors = new ArrayList<Behavior>();

            // Else this was a get for all categories
            JSONArray behaviorArray = jObject.optJSONArray("results");
            if (behaviorArray != null) {
                for (int i = 0; i < behaviorArray.length(); i++) {
                    Behavior behavior = gson.fromJson(
                            behaviorArray.getString(i), Behavior.class);

                    behaviors.add(behavior);
                }
            }
            return behaviors;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Behavior> behaviors) {
        mCallback.behaviorsLoaded(behaviors);
    }

}
