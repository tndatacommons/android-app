package org.tndata.android.compass.task;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DeleteBehaviorTask extends AsyncTask<Void, Void, Void> {
    private Context mContext;
    private static Gson gson = new GsonBuilder().setFieldNamingPolicy(
            FieldNamingPolicy.IDENTITY).create();
    private DeleteBehaviorTaskListener mCallback;
    private ArrayList<String> mBehaviorIds;

    public interface DeleteBehaviorTaskListener {
        void behaviorsDeleted();
    }

    public DeleteBehaviorTask(Context context, DeleteBehaviorTaskListener callback,
                              ArrayList<String> behaviorIds) {
        mContext = context;
        mCallback = callback;
        mBehaviorIds = behaviorIds;
    }

    @Override
    protected Void doInBackground(Void... params) {
        String token = ((CompassApplication) ((Activity) mContext)
                .getApplication()).getToken();

        String url = Constants.BASE_URL + "users/behaviors/";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + token);
        JSONArray postArray = new JSONArray();
        for (int i = 0; i < mBehaviorIds.size(); i++) {
            JSONObject postId = new JSONObject();
            try {
                postId.put("userbehavior", mBehaviorIds.get(i));
                postArray.put(postId);
            } catch (JSONException e1) {
                e1.printStackTrace();
                return null;
            }
        }
        try {
            Log.d("delete behaviors", postArray.toString(2));
        } catch (Exception e) {
            e.printStackTrace();
        }

        NetworkHelper.httpDeleteStream(url, headers,
                postArray.toString());

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        mCallback.behaviorsDeleted();
    }

}
