package org.tndata.android.compass.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

public class AddGoalTask extends AsyncTask<Void, Void, ArrayList<Goal>> {
    private Context mContext;
    private static Gson gson = new GsonBuilder().setFieldNamingPolicy(
            FieldNamingPolicy.IDENTITY).create();
    private AddGoalsTaskListener mCallback;
    private ArrayList<String> mGoalIds;
    private Goal mGoal;

    public interface AddGoalsTaskListener {
        public void goalsAdded(ArrayList<Goal> goals, Goal goal);
    }

    public AddGoalTask(Context context, AddGoalsTaskListener callback,
                       ArrayList<String> goalIds, Goal goal) {
        mContext = context;
        mCallback = callback;
        mGoalIds = goalIds;
        mGoal = goal;
    }

    @Override
    protected ArrayList<Goal> doInBackground(Void... params) {
        String token = ((CompassApplication) ((Activity) mContext)
                .getApplication()).getToken();

        String url = Constants.BASE_URL + "users/goals/";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + token);
        JSONArray postArray = new JSONArray();
        for (int i = 0; i < mGoalIds.size(); i++) {
            JSONObject postId = new JSONObject();
            try {
                postId.put("goal", mGoalIds.get(i));
                postArray.put(postId);
            } catch (JSONException e1) {
                e1.printStackTrace();
                return null;
            }
        }
        InputStream stream = NetworkHelper.httpPostStream(url, headers,
                postArray.toString());
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

            JSONArray jArray = new JSONArray(createResponse);
            ArrayList<Goal> goals = new ArrayList<Goal>();
            Log.d("user goals response", jArray.toString(2));
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject userGoal = jArray.getJSONObject(i);
                Goal goal = gson.fromJson(userGoal.getString("goal"), Goal.class);
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
        mCallback.goalsAdded(goals, mGoal);
    }

}
