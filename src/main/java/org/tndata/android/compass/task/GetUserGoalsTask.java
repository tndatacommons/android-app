package org.tndata.android.compass.task;

import android.os.AsyncTask;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.tndata.android.compass.model.Category;
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
        try {

            BufferedReader bReader = new BufferedReader(new InputStreamReader(
                    stream, "UTF-8"));

            String line = null;
            while ((line = bReader.readLine()) != null) {
                result += line;
            }
            bReader.close();

            JSONObject response = new JSONObject(result);
            JSONArray jArray = response.getJSONArray("results");
            ArrayList<Goal> goals = new ArrayList<Goal>();
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject userGoal = jArray.getJSONObject(i);
                Goal goal = gson.fromJson(userGoal.getString("goal"), Goal.class);
                goal.setProgressValue(userGoal.getDouble("progress_value"));
                goal.setMappingId(userGoal.getInt("id"));
                JSONArray categoryArray = userGoal.getJSONArray("user_categories");
                ArrayList<Category> categories = goal.getCategories();
                for (int x = 0; x < categoryArray.length(); x++) {
                    Category category = gson.fromJson(categoryArray.getString(x), Category.class);
                    categories.add(category);
                }
                goal.setCategories(categories);
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
