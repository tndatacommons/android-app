package org.tndata.android.grow.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.tndata.android.grow.R;
import org.tndata.android.grow.model.Category;
import org.tndata.android.grow.util.Constants;
import org.tndata.android.grow.util.NetworkHelper;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

public class CategoryLoaderTask extends
        AsyncTask<String, Void, ArrayList<Category>> {
    private CategoryLoaderListener mCallback;
    private Context mContext;
    private static Gson gson = new GsonBuilder().setFieldNamingPolicy(
            FieldNamingPolicy.IDENTITY).create();

    public interface CategoryLoaderListener {
        public void categoryLoaderFinished(ArrayList<Category> categories);
    }

    public CategoryLoaderTask(Context context, CategoryLoaderListener callback) {
        mContext = context;
        mCallback = callback;
    }

    @Override
    protected ArrayList<Category> doInBackground(String... params) {
        String token = params[0];
        String categoryId = null;
        if (params.length > 1) {
            categoryId = params[1];
        }
        String url = Constants.BASE_URL + "categories/";
        if (categoryId != null) {
            url += categoryId + "/";
        }
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

            JSONObject jObject = new JSONObject(createResponse);
            Log.d("category response", jObject.toString(2));
            ArrayList<Category> categories = new ArrayList<Category>();

            // First, if this was just one category get request
            if (categoryId != null) {
                Category category = gson.fromJson(createResponse,
                        Category.class);
                categories.add(category);
                return categories;
            }

            // Else this was a get for all categories
            JSONArray categoryArray = jObject.optJSONArray("results");
            TypedArray ta = mContext.getResources().obtainTypedArray(
                    R.array.category_colors);
            int[] colorIds = new int[ta.length()];
            for (int i = 0; i < ta.length(); i++) {
                colorIds[i] = ta.getResourceId(i, 0);
            }
            ta.recycle();
            int colorCounter = 0;
            if (categoryArray != null) {
                for (int i = 0; i < categoryArray.length(); i++) {
                    Category category = gson.fromJson(
                            categoryArray.getString(i), Category.class);
                    category.setColor(colorIds[colorCounter]);
                    colorCounter++;
                    if (colorCounter >= colorIds.length) {
                        colorCounter = 0;
                    }
                    categories.add(category);
                }
            }
            Collections.sort(categories, new Comparator<Category>() {
                @Override
                public int compare(Category cat1, Category cat2) {

                    return (cat1.getOrder() < cat2.getOrder()) ? 0 : 1;
                }
            });
            return categories;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Category> result) {
        mCallback.categoryLoaderFinished(result);
    }

}
