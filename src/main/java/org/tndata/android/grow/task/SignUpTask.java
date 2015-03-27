package org.tndata.android.grow.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.grow.model.User;
import org.tndata.android.grow.util.Constants;
import org.tndata.android.grow.util.NetworkHelper;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

public class SignUpTask extends AsyncTask<User, Void, User> {
    private SignUpTaskListener mCallback;

    public interface SignUpTaskListener {
        public void signUpResult(User result);
    }

    public SignUpTask(SignUpTaskListener callback) {
        mCallback = callback;
    }

    @Override
    protected User doInBackground(User... params) {
        User user = params[0];
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        String url = Constants.BASE_URL + "users/";
        JSONObject holder = new JSONObject();
        try {
            holder.put("password", user.getPassword());
            holder.put("email", user.getEmail());
            holder.put("first_name", user.getFirstName());
            holder.put("last_name", user.getLastName());
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
            user.setToken(jObject.optString("token"));
            user.setFullName(jObject.optString("full_name"));
            user.setLastName(jObject.optString("last_name", user.getLastName()));
            user.setFirstName(jObject.optString("first_name",
                    user.getFirstName()));
            user.setEmail(jObject.optString("email", user.getEmail()));
            user.setId(jObject.optInt("id", -1));
            user.setUserprofileId(jObject.optInt("userprofile_id", -1));
            user.setDateJoined(jObject.optString("date_joined"));
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
        mCallback.signUpResult(result);
    }

}
