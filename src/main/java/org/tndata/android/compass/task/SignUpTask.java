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
 * Task to perform sign up operations.
 *
 * @author Edited by Ismael Alonso.
 * @version 1.0.0
 */
public class SignUpTask extends AsyncTask<Void, Void, User>{
    private static final String TAG = "SignUpTask";


    private SignUpTaskCallback mCallback;
    private String mEmail;
    private String mPassword;
    private String mFirstName;
    private String mLastName;


    /**
     * Constructor.
     *
     * @param callback the callback object.
     * @param email the email.
     * @param pass the password.
     * @param firstName the first name.
     * @param lastName the last name.
     */
    public SignUpTask(@NonNull SignUpTaskCallback callback, @NonNull String email, @NonNull String pass,
                      @NonNull String firstName, @NonNull String lastName){
        mCallback = callback;
        mEmail = email;
        mPassword = pass;
        mFirstName = firstName;
        mLastName = lastName;
    }

    @Override
    protected User doInBackground(Void... params){
        String url = Constants.BASE_URL + "users/";

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");

        JSONObject holder = new JSONObject();
        try{
            holder.put("email", mEmail);
            holder.put("password", mPassword);
            holder.put("first_name", mFirstName);
            holder.put("last_name", mLastName);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }

        //Create the stream and check errors
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

            //Parse the user
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
            Log.e(TAG, "Couldn't sign up");
        }
        else{
            Log.d(TAG, result.toString());
            result.setPassword(mPassword);
        }
        mCallback.signUpResult(result);
    }


    /**
     * Sign up event callback interface.
     *
     * @author Edited by Ismael Alonso
     * @version 1.0.0
     */
    public interface SignUpTaskCallback{
        /**
         * Called when an event associated with sign up is triggered.
         *
         * @param user the user if the sign up operation succeeded or null if it failed.
         */
        void signUpResult(@Nullable User user);
    }
}
