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
import org.tndata.android.grow.model.Category;
import org.tndata.android.grow.util.Constants;
import org.tndata.android.grow.util.NetworkHelper;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

public class AddCategoryTask extends AsyncTask<Void, Void, ArrayList<Category>> {
    private Context mContext;
    private static Gson gson = new GsonBuilder().setFieldNamingPolicy(
            FieldNamingPolicy.IDENTITY).create();
    private AddCategoryTaskListener mCallback;
    private ArrayList<String> mCategoryIds;

    public interface AddCategoryTaskListener {
        void categoriesAdded(ArrayList<Category> categories);
    }

    public AddCategoryTask(Context context, AddCategoryTaskListener callback,
            ArrayList<String> categoryIds) {
        mContext = context;
        mCallback = callback;
        mCategoryIds = categoryIds;
    }

    @Override
    protected ArrayList<Category> doInBackground(Void... params) {
        String token = ((GrowApplication) ((Activity) mContext)
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
                postId.put("category", mCategoryIds.get(i));
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
            ArrayList<Category> categories = new ArrayList<Category>();
            Log.d("user categories response", jArray.toString(2));
            for (int i = 0; i < jArray.length(); i++) {
                Category category = gson.fromJson(jArray.getString(i),
                        Category.class);
                categories.add(category);
            }
            return categories;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Category> categories) {
        mCallback.categoriesAdded(categories);
    }

}
