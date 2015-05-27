package org.tndata.android.compass.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;
import org.tndata.android.compass.R;
import org.tndata.android.compass.activity.LoginActivity;
import org.tndata.android.compass.util.Constants;

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
 *    "title":"Demo"
 *    "message":"Don't forget to review your Notifications for today",
 *    "object_id":32,
 *    "object_type":"action",  // either behavior or action
 * }
 *
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
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

        if (!extras.isEmpty()) {  // has effect of un-parcelling Bundle
            Log.d(TAG, "GCM message: " + extras.get("message"));
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.d(TAG, "Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.d(TAG, "Deleted messages on server: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                try {
                    JSONObject jsonObject = new JSONObject(extras.getString("message"));
                    sendNotification(
                            jsonObject.optString("message"),
                            jsonObject.optString("title"),
                            jsonObject.optString("object_type"),
                            jsonObject.optString("object_id")
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    private void sendNotification(String msg, String title, String object_type, String object_id) {
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        String activity = Constants.GCM_DEFAULT_ACTIVITY;
        /*
        // TODO: We can't launch these activities directly without the full app being
        // TODO: initialized (e.g. some of the data required for model.Goal.getBehaviors()
        // TODO: will be null)
        switch (object_type) {
            case Constants.ACTION_TYPE:
                activity = Constants.GCM_ACTION_ACTIVITY;
                break;
            case Constants.BEHAVIOR_TYPE:
                activity = Constants.GCM_BEHAVIOR_ACTIVITY;
                break;
        }
        */

        Class<?> cls;
        try {
            cls = Class.forName(activity);
        }
        catch (Exception e) {
            Log.d(TAG, "Activity " + activity + " not found! Using default!");
            cls = LoginActivity.class;
        }

        // TODO: How can we tell the activity about the object_id (and possibly the object_type)
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