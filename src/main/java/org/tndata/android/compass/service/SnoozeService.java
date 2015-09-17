package org.tndata.android.compass.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;
import org.tndata.android.compass.util.NotificationUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * Intent service that snoozes a notification for an hour.
 *
 * @author Ismael Alonso
 * @version 1.0.1
 */
public class SnoozeService extends IntentService{
    public static final String NOTIFICATION_ID_KEY = "org.tndata.compass.Snooze.NotificationId";
    public static final String PUSH_NOTIFICATION_ID_KEY = "org.tndata.compass.Snooze.PushNotificationId";

    public static final String DATE_KEY = "org.tndata.compass.Snooze.Date";
    public static final String TIME_KEY = "org.tndata.compass.Snooze.Time";


    /**
     * Creates an IntentService. Invoked by your subclass's constructor.
     */
    public SnoozeService(){
        super("SnoozeService");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        //Retrieve the bundled data
        int pushNotificationId = intent.getIntExtra(PUSH_NOTIFICATION_ID_KEY, -1);
        int notificationId = intent.getIntExtra(NOTIFICATION_ID_KEY, -1);
        String date = intent.getStringExtra(DATE_KEY);
        String time = intent.getStringExtra(TIME_KEY);

        //Cancel the notification
        NotificationManager manager = ((NotificationManager)getSystemService(NOTIFICATION_SERVICE));
        manager.cancel(NotificationUtil.NOTIFICATION_TYPE_ACTION_TAG, pushNotificationId);

        //If the notification id is not -1, create the request.
        if (notificationId != -1){
            Map<String, String> headers = new HashMap<>();
            headers.put("Accept", "application/json");
            headers.put("Content-type", "application/json");
            headers.put("Authorization", "Token " + ((CompassApplication)getApplication()).getToken());

            String url = Constants.BASE_URL + "notifications/" + notificationId + "/";
            JSONObject body = new JSONObject();
            try{
                body.put("date", date);
                body.put("time", time);
            }
            catch (JSONException jx){
                jx.printStackTrace();
            }

            //Send the request, and if that succeeds, close it up
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
