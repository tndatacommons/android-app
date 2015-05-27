package org.tndata.android.compass.task;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GoalLoaderTask extends
        AsyncTask<String, Void, ArrayList<Goal>> {
    private GoalLoaderListener mCallback;
    private Context mContext;
    private static Gson gson = new GsonBuilder().setFieldNamingPolicy(
            FieldNamingPolicy.IDENTITY).create();

    public interface GoalLoaderListener {
        public void goalLoaderFinished(ArrayList<Goal> goals);
    }

    public GoalLoaderTask(Context context, GoalLoaderListener callback) {
        mContext = context;
        mCallback = callback;
    }

    @Override
    protected ArrayList<Goal> doInBackground(String... params) {
        String token = params[0];
        String categoryId = params[1];

        String url = Constants.BASE_URL + "goals/?category=" + categoryId;

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
            Log.d("goal response", jObject.toString(2));
            ArrayList<Goal> goals = new ArrayList<Goal>();

            JSONArray resultArray = jObject.optJSONArray("results");
            for (int i = 0; i < resultArray.length(); i++) {
                Goal goal = gson.fromJson(
                        resultArray.getString(i), Goal.class);
                goals.add(goal);
            }

            return goals;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Goal> result) {
        mCallback.goalLoaderFinished(result);
    }

}
