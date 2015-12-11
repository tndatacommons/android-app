package org.tndata.android.compass.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.NetworkRequest;


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

            NetworkRequest.post(this, null, API.getLogOutUrl(), app.getToken(), API.getLogOutBody(regId));
        }
    }
}
