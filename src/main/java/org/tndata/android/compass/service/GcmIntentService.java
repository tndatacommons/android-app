package org.tndata.android.compass.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
 *
 * NOTE: Messages received from GCM will have a format like the following:
 *
 * {
 *    "title":"Demo"
 *    "message":"Don't forget to review your Notifications for today",
 *    "object_id":32,
 *    "object_type":"action",  // an Action
 * }
 *
 * A single, daily Behavior notification will arrive, but it does not contain object info:
 *
 * {
 *    "title":"Stay on Course"
 *    "message":"some message here...",
 *    "object_id": null,
 *    "object_type": null,
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
        String activity;

        switch (object_type) {
            case Constants.ACTION_TYPE:
                // TODO: We can't launch these activities directly without the full app being
                // TODO: initialized (e.g. some of the data required for model.Goal.getBehaviors()
                // TODO: will be null)
                activity = Constants.GCM_ACTION_ACTIVITY;
                break;
            case Constants.BEHAVIOR_TYPE:
                activity = Constants.GCM_BEHAVIOR_ACTIVITY;
                break;
            default:
                activity = Constants.GCM_BEHAVIOR_ACTIVITY;
                break;
        }

        Class<?> cls;
        try {
            cls = Class.forName(activity);
        }
        catch (Exception e) {
            cls = LoginActivity.class;
        }

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, cls), 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_action_compass_white)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg)
                        .setLargeIcon(icon)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if(object_id != null) {
            // Bundle the object_type/object_id arguments with the intent
            // http://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html
            Bundle args = new Bundle();
            args.putSerializable("objectType", object_type);
            args.putSerializable("objectId", object_id);
            mBuilder.addExtras(args);
        }

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}