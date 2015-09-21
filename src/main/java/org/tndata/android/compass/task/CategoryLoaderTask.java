package org.tndata.android.compass.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;
import org.tndata.android.compass.util.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CategoryLoaderTask extends AsyncTask<String, Void, List<Category>>{
    private CategoryLoaderListener mCallback;


    public CategoryLoaderTask(@NonNull CategoryLoaderListener callback){
        mCallback = callback;
    }

    @Override
    protected List<Category> doInBackground(String... params){
        String categoryId = null;
        if (params.length > 1){
            categoryId = params[1];
        }

        String url = Constants.BASE_URL + "categories/";
        if (categoryId != null) {
            url += categoryId + "/";
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + params[0]);

        InputStream stream = NetworkHelper.httpGetStream(url, headers);
        if (stream == null){
            return null;
        }

        try{
            BufferedReader bReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

            String line, result = "";
            while ((line = bReader.readLine()) != null){
                result += line;
            }
            bReader.close();

            JSONObject jObject = new JSONObject(result);
            List<Category> categories = new ArrayList<>();

            //First, if this was just one category get request
            if (categoryId != null){
                Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
                categories.add(gson.fromJson(result, Category.class));
                return categories;
            }

            //Else this was a get for all categories
            return new Parser().parseCategories(jObject.getJSONArray("results"), false);
        }
        catch (IOException|JSONException x){
            x.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Category> result){
        mCallback.categoryLoaderFinished(result);
    }


    public interface CategoryLoaderListener{
        void categoryLoaderFinished(List<Category> categories);
    }
}
