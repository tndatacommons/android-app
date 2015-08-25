package org.tndata.android.compass.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.task.SnoozeTask;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * Intent service that snoozes a notification for an hour.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class SnoozeService extends IntentService{
    public static final String NOTIFICATION_ID_KEY = "org.tndata.compass.Snooze.NotificationId";
    public static final String PUSH_NOTIFICATION_ID_KEY = "org.tndata.compass.Snooze.PushNotificationId";


    /**
     * Creates an IntentService. Invoked by your subclass's constructor.
     */
    public SnoozeService(){
        super("SnoozeService");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        int pushNotificationId = intent.getIntExtra(PUSH_NOTIFICATION_ID_KEY, -1);
        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel(pushNotificationId);
        int notificationId = intent.getIntExtra(NOTIFICATION_ID_KEY, -1);
        if (notificationId != -1){
            Map<String, String> headers = new HashMap<>();
            headers.put("Accept", "application/json");
            headers.put("Content-type", "application/json");
            headers.put("Authorization", "Token " + ((CompassApplication)getApplication()).getToken());

            String url = Constants.BASE_URL + "notifications/" + notificationId + "/";
            JSONObject body = new JSONObject();
            try{
                body.put("snooze", "1");
            }
            catch (JSONException jx){
                jx.printStackTrace();
            }

            InputStream stream = NetworkHelper.httpPutStream(url, headers, body.toString());
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
