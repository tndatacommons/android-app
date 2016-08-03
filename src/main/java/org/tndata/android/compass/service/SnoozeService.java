package org.tndata.android.compass.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;

import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.NotificationUtil;

import es.sandwatch.httprequests.HttpRequest;


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
        manager.cancel(NotificationUtil.USER_ACTION_TAG, pushNotificationId);

        //If the notification id is not -1, create the request
        if (notificationId != -1){
            HttpRequest.put(null, API.URL.putSnooze(notificationId), API.BODY.putSnooze(date, time));
        }
    }
}
