package org.tndata.android.compass.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

public class LoginTask extends AsyncTask<User, Void, User> {
    private LoginTaskListener mCallback;

    public interface LoginTaskListener {
        void loginResult(User result);
    }

    public LoginTask(LoginTaskListener callback) {
        mCallback = callback;
    }

    @Override
    protected User doInBackground(User... params) {
        User user = params[0];
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        String url = Constants.BASE_URL + "auth/token/";
        JSONObject holder = new JSONObject();
        try {
            holder.put("password", user.getPassword());
            holder.put("email", user.getEmail());
        } catch (JSONException e1) {
            e1.printStackTrace();
            return null;
        }
        InputStream stream = NetworkHelper.httpPostStream(url, headers,
                holder.toString());
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
            Log.d("user response", jObject.toString(2));
            JSONArray errorArray = jObject.optJSONArray("non_field_errors");
            if (errorArray != null) {
                user.setError(errorArray.optString(0));
            }

            user.setToken(jObject.optString("token"));
            user.setFullName(jObject.optString("full_name"));
            user.setLastName(jObject.optString("last_name", user.getLastName()));
            user.setFirstName(jObject.optString("first_name",
                    user.getFirstName()));
            user.setEmail(jObject.optString("email", user.getEmail()));
            user.setId(jObject.optInt("id", -1));
            user.setUserprofileId(jObject.optInt("userprofile_id", -1));
            user.setDateJoined(jObject.optString("date_joined"));
            if (jObject.optBoolean("needs_onboarding", false)){
                user.needsOnBoarding();
            }
            return user;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(User result) {
        mCallback.loginResult(result);
    }

}
