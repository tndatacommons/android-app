package org.tndata.android.compass.task;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


/**
 * Sets the time zone to the current timezone in the background.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class UpdateProfileTask extends AsyncTask<User, Void, Boolean>{
    private OnProfileUpdateCallback mCallback;


    /**
     * Constructor.
     *
     * @param callback the callback implementing object.
     */
    public UpdateProfileTask(@Nullable OnProfileUpdateCallback callback){
        mCallback = callback;
    }

    @Override
    protected Boolean doInBackground(User... params){
        //If no parameters were passed
        if (params.length == 0){
            //No profile is updated, therefore failure.
            return false;
        }

        User user = params[0];
        //If the user has no profile to be updated
        if (user.getUserprofileId() == -1){
            //Failure
            return false;
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + user.getToken());

        String url = Constants.BASE_URL + "userprofiles/" + user.getUserprofileId() + "/";
        JSONObject body = new JSONObject();
        try{
            body.put("timezone", TimeZone.getDefault().getID());
            body.put("needs_onboarding", user.needsOnBoarding());
        }
        catch (JSONException jx){
            jx.printStackTrace();
            return false;
        }

        InputStream stream = NetworkHelper.httpPutStream(url, headers, body.toString());
        if (stream != null){
            try{
                stream.close();
            }
            catch (IOException iox){
                iox.printStackTrace();
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean success){
        Log.d("Profile Update", "success: " + success);
        if (mCallback != null){
            mCallback.onProfileUpdated(success);
        }
    }


    /**
     * Callback interface for profile update events.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface OnProfileUpdateCallback{
        /**
         * Called when the update process is complete.
         *
         * @param success true if the update succeeded, false otherwise.
         */
        void onProfileUpdated(boolean success);
    }
}
