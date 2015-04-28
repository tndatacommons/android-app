package org.tndata.android.grow.task;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.tndata.android.grow.util.Constants;
import org.tndata.android.grow.util.NetworkHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class GetUserProfileTask extends AsyncTask<String, Void, Void> {
    private UserProfileTaskInterface mCallback;

    public interface UserProfileTaskInterface {
        public void userProfileFound();
    }

    public GetUserProfileTask(UserProfileTaskInterface callback) {
        mCallback = callback;
    }

    @Override
    protected Void doInBackground(String... params) {
        String token = params[0];
        String url = Constants.BASE_URL + "userprofiles/";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + token);
        InputStream stream = NetworkHelper.httpGetStream(url, headers);
        if (stream == null) {
            return null;
        }
        String result = "";
        String profileResponse = "";
        try {

            BufferedReader bReader = new BufferedReader(new InputStreamReader(
                    stream, "UTF-8"));

            String line = null;
            while ((line = bReader.readLine()) != null) {
                result += line;
            }
            bReader.close();

            profileResponse = Html.fromHtml(result).toString();

            JSONObject response = new JSONObject(profileResponse);
            JSONArray jArray = response.getJSONArray("results");
            Log.d("User Profile", jArray.toString(2));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        mCallback.userProfileFound();
    }
}
