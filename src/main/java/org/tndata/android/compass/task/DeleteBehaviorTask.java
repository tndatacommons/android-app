package org.tndata.android.compass.task;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteBehaviorTask extends AsyncTask<Void, Void, Void> {
    private String mToken;
    private DeleteBehaviorCallback mCallback;
    private List<String> mBehaviorIds;


    public DeleteBehaviorTask(String token, @Nullable DeleteBehaviorCallback callback,
                              List<String> behaviorIds){
        mToken = token;
        mCallback = callback;
        mBehaviorIds = behaviorIds;
    }

    @Override
    protected Void doInBackground(Void... params){
        String url = Constants.BASE_URL + "users/behaviors/";

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + mToken);

        JSONArray postArray = new JSONArray();
        for (int i = 0; i < mBehaviorIds.size(); i++){
            JSONObject postId = new JSONObject();
            try{
                postId.put("userbehavior", mBehaviorIds.get(i));
                postArray.put(postId);
            }
            catch (JSONException jsonx){
                jsonx.printStackTrace();
                return null;
            }
        }
        try{
            Log.d("delete behaviors", postArray.toString(2));
        }
        catch (Exception e){
            e.printStackTrace();
        }

        NetworkHelper.httpDeleteStream(url, headers, postArray.toString());

        return null;
    }

    @Override
    protected void onPostExecute(Void result){
        if (mCallback != null){
            mCallback.behaviorsDeleted();
        }
    }


    public interface DeleteBehaviorCallback{
        void behaviorsDeleted();
    }
}
