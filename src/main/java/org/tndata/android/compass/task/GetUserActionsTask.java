package org.tndata.android.compass.task;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GetUserActionsTask extends AsyncTask<String, Void, ArrayList<Action>> {
    private GetUserActionsListener mCallback;
    private static Gson gson = new GsonBuilder().setFieldNamingPolicy(
            FieldNamingPolicy.IDENTITY).create();

    public interface GetUserActionsListener {
        public void actionsLoaded(ArrayList<Action> actions);
    }

    public GetUserActionsTask(GetUserActionsListener callback) {
        mCallback = callback;
    }

    @Override
    protected ArrayList<Action> doInBackground(String... params) {
        String token = params[0];
        String url = Constants.BASE_URL + "users/actions/";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + token);
        InputStream stream = NetworkHelper.httpGetStream(url, headers);
        if (stream == null) {
            return null;
        }
        String result = "";
        String actionResponse = "";
        try {

            BufferedReader bReader = new BufferedReader(new InputStreamReader(
                    stream, "UTF-8"));

            String line = null;
            while ((line = bReader.readLine()) != null) {
                result += line;
            }
            bReader.close();

            actionResponse = Html.fromHtml(result).toString();

            JSONObject response = new JSONObject(actionResponse);
            JSONArray jArray = response.getJSONArray("results");
            ArrayList<Action> actions = new ArrayList<Action>();
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject userAction = jArray.getJSONObject(i);
                Log.d("USER ACTION", userAction.toString(2));
                Action action = gson.fromJson(userAction.getString("action"), Action.class);
                action.setMappingId(userAction.getInt("id"));
                actions.add(action);
            }
            return actions;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Action> actions) {
        mCallback.actionsLoaded(actions);
    }

}
