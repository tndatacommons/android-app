package org.tndata.android.compass.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;
import org.tndata.android.compass.util.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by isma on 9/28/15.
 */
public class GetFeedDataTask extends AsyncTask<Void, Void, FeedData>{
    private GetFeedDataCallback mCallback;
    private String mToken;


    public GetFeedDataTask(@NonNull GetFeedDataCallback callback, @NonNull String token){
        mCallback = callback;
        mToken = token;
    }

    @Override
    protected FeedData doInBackground(Void... params){
        String url = Constants.BASE_URL + "feed/";

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + mToken);

        InputStream stream = NetworkHelper.httpGetStream(url, headers);
        if (stream == null){
            return null;
        }

        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line, result = "";
            while ((line = reader.readLine()) != null){
                result += line;
            }
            reader.close();

            return new Parser().parseFeedData(new JSONObject(result));
        }
        catch (IOException|JSONException x){
            x.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(FeedData feedData){
        mCallback.onFeedDataLoaded(feedData);
    }

    public interface GetFeedDataCallback{
        void onFeedDataLoaded(@Nullable FeedData feedData);
    }
}
