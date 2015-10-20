package org.tndata.android.compass.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;
import org.tndata.android.compass.util.Parser;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;


/**
 * Task to perform login operations.
 *
 * @author Edited by Ismael Alonso
 * @version 1.0.0
 */
public class LogInTask extends AsyncTask<Void, Void, User>{
    private static final String TAG = "LogInTask";


    private LogInTaskCallback mCallback;
    private String mEmail;
    private String mPassword;


    /**
     * Constructor.
     *
     * @param callback the callback object.
     * @param email the email.
     * @param pass the password.
     */
    public LogInTask(@NonNull LogInTaskCallback callback, @NonNull String email, @NonNull String pass){
        mCallback = callback;
        mEmail = email;
        mPassword = pass;
    }

    @Override
    protected User doInBackground(Void... params){
        String url = Constants.BASE_URL + "auth/token/";

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");

        JSONObject holder = new JSONObject();
        try{
            holder.put("email", mEmail);
            holder.put("password", mPassword);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }

        //Create the stream and check for failure
        InputStream stream = NetworkHelper.httpPostStream(url, headers, holder.toString());
        if (stream == null){
            Log.d(TAG, "Bad stream");
            return null;
        }

        try{
            //Read the stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line, result = "";
            while ((line = reader.readLine()) != null){
                result += line;
            }
            reader.close();

            //Parse out the new user
            return new Parser().parseUser(result);
        }
        catch (IOException iox){
            iox.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(User result){
        if (result == null){
            Log.e(TAG, "Couldn't log in");
        }
        else{
            Log.d(TAG, result.toString());
            result.setPassword(mPassword);
        }
        mCallback.logInResult(result);
    }


    /**
     * Login event callback interface.
     *
     * @author Edited by Ismael Alonso
     * @version 1.0.0
     */
    public interface LogInTaskCallback{
        /**
         * Called when an event associated with login is triggered.
         *
         * @param result the user if the login operation succeeded or null if it failed.
         */
        void logInResult(@Nullable User result);
    }
}
