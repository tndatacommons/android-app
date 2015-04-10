package org.tndata.android.grow.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.tndata.android.grow.model.Goal;
import org.tndata.android.grow.util.Constants;
import org.tndata.android.grow.util.NetworkHelper;

import android.os.AsyncTask;
import android.text.Html;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GetUserGoalsTask extends AsyncTask<String, Void, ArrayList<Goal>> {
    private GetUserGoalsListener mCallback;
    private static Gson gson = new GsonBuilder().setFieldNamingPolicy(
            FieldNamingPolicy.IDENTITY).create();

    public interface GetUserGoalsListener {
        public void goalsLoaded(ArrayList<Goal> goals);
    }

    public GetUserGoalsTask(GetUserGoalsListener callback) {
        mCallback = callback;
    }

    @Override
    protected ArrayList<Goal> doInBackground(String... params) {
        String token = params[0];
        String url = Constants.BASE_URL + "users/goals/";
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

            JSONObject response = new JSONObject(createResponse);
            JSONArray jArray = response.getJSONArray("results");
            ArrayList<Goal> goals = new ArrayList<Goal>();
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject userGoal = jArray.getJSONObject(i);
                Goal goal = gson.fromJson(userGoal.getString("goal"), Goal.class);
                goal.setMappingId(userGoal.getInt("id"));
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
    protected void onPostExecute(ArrayList<Goal> goals) {
        mCallback.goalsLoaded(goals);
    }

}
