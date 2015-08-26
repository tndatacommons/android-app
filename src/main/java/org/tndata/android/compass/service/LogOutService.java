package org.tndata.android.compass.service;

import android.app.IntentService;
import android.content.Intent;

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
        String regId = ((CompassApplication)getApplication()).getGcmRegistrationId();
        if (regId != null && !regId.isEmpty()){
            Map<String, String> headers = new HashMap<>();
            headers.put("Accept", "application/json");
            headers.put("Content-type", "application/json");
            headers.put("Authorization", "Token " + ((CompassApplication)getApplication()).getToken());

            String url = Constants.BASE_URL + "auth/logout/";
            JSONObject body = new JSONObject();
            try{
                body.put("registration_id", regId);
            }
            catch (JSONException jx){
                jx.printStackTrace();
            }

            InputStream stream = NetworkHelper.httpPostStream(url, headers, body.toString());
            if (stream != null){
                try{
                    stream.close();
                }
                catch (IOException iox){
                    iox.printStackTrace();
                }
            }
        }
    }
}
