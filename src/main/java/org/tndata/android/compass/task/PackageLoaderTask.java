package org.tndata.android.compass.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.model.Package;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


/**
 * Created by isma on 9/17/15.
 */
public class PackageLoaderTask extends AsyncTask<Integer, Void, List<Package>>{
    private static final String TAG = "PackageLoaderTask";


    private String mToken;
    private PackageLoaderCallback mCallback;


    public PackageLoaderTask(@NonNull String token, @NonNull PackageLoaderCallback callback){
        mToken = token;
        mCallback = callback;
    }

    @Override
    protected List<Package> doInBackground(Integer... params){
        //If there is nothing to fetch, finish
        if (params.length == 0){
            return null;
        }
        //Put all the ids into a hash set to improve lookup complexity
        HashSet<Integer> packageSet = new HashSet<>();
        packageSet.addAll(Arrays.asList(params));

        //Set the url
        String url = Constants.BASE_URL + "users/packages/";
        if (params.length == 1){
            url += params[0] + "/";
        }

        //Create the headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + mToken);

        //Create a stream, if that fails, return null to signal failure
        InputStream stream = NetworkHelper.httpGetStream(url, headers);
        if (stream == null){
            Log.d(TAG, "Bad stream");
            return null;
        }

        try{
            //Create a reader and read the reply
            BufferedReader bReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line, result = "";
            while ((line = bReader.readLine()) != null){
                result += line;
            }
            bReader.close();

            Log.d(TAG, result);

            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
            List<Package> packages = new ArrayList<>();
            //If there is only one element, parse it out
            if (params.length == 1){
                Log.d(TAG, "One parameter");
                Package newPackage = gson.fromJson(new JSONObject(result).getString("category"), Package.class);
                newPackage.setId(new JSONObject(result).getInt("id"));
                packages.add(newPackage);
                return packages;
            }
            //Otherwise, parse and filter using the hash set.
            else{
                JSONArray packageArray = new JSONObject(result).optJSONArray("results");
                if (packageArray != null){
                    for (int i = 0; i < packageArray.length(); i++){
                        int id = packageArray.getJSONObject(i).getInt("id");
                        if (packageSet.contains(id)){
                            Package newPackage = gson.fromJson(packageArray.getJSONObject(i).getString("category"), Package.class);
                            newPackage.setId(id);
                            packages.add(newPackage);
                        }
                    }
                    return packages;
                }
            }
        }
        catch (IOException |JSONException x){
            x.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Package> packages){
        mCallback.onPackagesLoaded(packages);
    }


    public interface PackageLoaderCallback{
        void onPackagesLoaded(List<Package> packages);
    }
}
