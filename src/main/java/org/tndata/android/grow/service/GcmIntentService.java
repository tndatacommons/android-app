package org.tndata.android.grow.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.tndata.android.grow.R;
import org.tndata.android.grow.activity.MainActivity;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 *
 * NOTE: Messages received from GCM will have a format like the following:
 *
 * {
 *    "id":32,
 *    "message":"Don't forget to review your Notifications for today",
 *    "activity":"org.tndata.android.grow.demoactivity",
 *    "title":"Demo"
 * }
 *
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private static final String DefaultActivity = "org.tndata.android.grow.activity.MainActivity";
    private static final String DefaultTitle = "Grow Notification";
    private String TAG = "GcmIntentService";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            Log.i(TAG, "extra! extra! message " + extras.get("message") + " activity: " + extras.get("activity"));
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                sendNotification(
                        extras.getString("message"),
                        extras.getString("title"),
                        extras.getString("activity")
                );

                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {
        sendNotification(msg, DefaultActivity);
    }

    private void sendNotification(String msg, String activity) {
        sendNotification(msg, DefaultTitle, activity);
    }

    // Put the message into a notification and post it.
    private void sendNotification(String msg, String title, String activity) {
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Class<?> cls;
        try {
            cls = Class.forName(activity);
        }
        catch (Exception e) {
            Log.d(TAG, "Activity " + activity + " not found! Using default!");
            cls = MainActivity.class;
        }

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, cls), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}