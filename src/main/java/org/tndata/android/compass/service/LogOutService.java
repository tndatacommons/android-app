package org.tndata.android.compass.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * Service that notifies the webapp that a user logged out.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class LogOutService extends IntentService{
    /**
     * Creates an IntentService. Invoked by your subclass's constructor.
     */
    public LogOutService(){
        super("LogOutService");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        CompassApplication app = (CompassApplication)getApplication();
        String regId = app.getGcmRegistrationId();
        if (regId != null && !regId.isEmpty()){
            String url = Constants.BASE_URL + "auth/logout/";

            Map<String, String> headers = new HashMap<>();
            headers.put("Accept", "application/json");
            headers.put("Content-type", "application/json");
            headers.put("Authorization", "Token " + app.getToken());

            //We do this after retrieving the token but before any network operation is
            //  carried out to avoid a possible bug where the user opens the application
            //  before the API replies
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("auth_token", "");
            editor.putString("first_name", "");
            editor.putString("last_name", "");
            editor.putString("email", "");
            editor.putString("username", "");
            editor.putString("password", "");
            editor.putInt("id", -1);
            editor.apply();

            JSONObject body = new JSONObject();
            try{
                body.put("registration_id", regId);

                InputStream stream = NetworkHelper.httpPostStream(url, headers, body.toString());
                if (stream != null){
                    stream.close();
                }
            }
            catch (JSONException|IOException x){
                x.printStackTrace();
            }
        }
    }
}
