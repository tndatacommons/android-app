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

public class DeleteCategoryTask extends AsyncTask<Void, Void, Void> {
    private Context mContext;
    private static Gson gson = new GsonBuilder().setFieldNamingPolicy(
            FieldNamingPolicy.IDENTITY).create();
    private DeleteCategoryTaskListener mCallback;
    private ArrayList<String> mCategoryIds;

    public interface DeleteCategoryTaskListener {
        void categoriesDeleted();
    }

    public DeleteCategoryTask(Context context, DeleteCategoryTaskListener callback,
                              ArrayList<String> categoryIds) {
        mContext = context;
        mCallback = callback;
        mCategoryIds = categoryIds;
    }

    @Override
    protected Void doInBackground(Void... params) {
        String token = ((CompassApplication) ((Activity) mContext)
                .getApplication()).getToken();

        String url = Constants.BASE_URL + "users/categories/";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + token);
        JSONArray postArray = new JSONArray();
        for (int i = 0; i < mCategoryIds.size(); i++) {
            JSONObject postId = new JSONObject();
            try {
                postId.put("usercategory", mCategoryIds.get(i));
                postArray.put(postId);
            } catch (JSONException e1) {
                e1.printStackTrace();
                return null;
            }
        }
        try {
            Log.d("delete cats", postArray.toString(2));
        } catch (Exception e) {
            e.printStackTrace();
        }

        NetworkHelper.httpDeleteStream(url, headers,
                postArray.toString());

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        mCallback.categoriesDeleted();
    }

}
