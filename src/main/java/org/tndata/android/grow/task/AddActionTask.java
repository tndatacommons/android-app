package org.tndata.android.grow.task;

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
import org.tndata.android.grow.GrowApplication;
import org.tndata.android.grow.model.Action;
import org.tndata.android.grow.model.Behavior;
import org.tndata.android.grow.util.Constants;
import org.tndata.android.grow.util.NetworkHelper;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AddActionTask extends AsyncTask<Void, Void, Action> {
    private Context mContext;
    private Action mAction;
    private AddActionTaskListener mCallback;
    private static Gson gson = new GsonBuilder().setFieldNamingPolicy(
            FieldNamingPolicy.IDENTITY).create();

    public interface AddActionTaskListener {
        public void actionAdded(Action action);
    }

    public AddActionTask(Context context, AddActionTaskListener callback, Action action) {
        mContext = context;
        mCallback = callback;
        mAction = action;
    }

    @Override
    protected Action doInBackground(Void... params) {
        String token = ((GrowApplication) ((Activity) mContext)
                .getApplication()).getToken();

        String url = Constants.BASE_URL + "users/actions/";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + token);
        JSONObject body = new JSONObject();
        try {
            body.put("action", mAction.getId());
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

            JSONObject userAction = new JSONObject(createResponse);
            Log.d("user action", userAction.toString(2));
            Action action = gson.fromJson(userAction.getString("action"),
                    Action.class);
            if (action.getId() == mAction.getId()) {
                mAction.setMappingId(userAction.getInt("id"));
                return mAction;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Action action) {
        mCallback.actionAdded(action);
    }

}
