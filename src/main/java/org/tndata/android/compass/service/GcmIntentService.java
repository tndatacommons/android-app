package org.tndata.android.compass.service;

import android.app.IntentService;
import android.app.Notification;
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
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.activity.ActionActivity;
import org.tndata.android.compass.activity.BehaviorProgressActivity;
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
        Log.d(TAG, "object_id = " + object_id);
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        String activity;

        if (object_type.equals(Constants.ACTION_TYPE)){
            try{
                Intent intent = new Intent(getApplicationContext(), ActionActivity.class);
                intent.putExtra(ActionActivity.ACTION_ID_KEY, Integer.valueOf(object_id));
                //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                Context ctx = getApplicationContext();
                PendingIntent contentIntent = PendingIntent.getActivity(ctx,
                        (int) System.currentTimeMillis(), intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                Bundle args = new Bundle();
                args.putSerializable("objectType", Constants.ACTION_TYPE);

                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
                Notification notification = new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.drawable.ic_action_compass_white)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg)
                        .setLargeIcon(icon)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .addExtras(args)
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true)
                        .build();

                mNotificationManager.notify(NOTIFICATION_ID, notification);
            }
            catch (NumberFormatException nfx){
                nfx.printStackTrace();
            }
        }
        else{
            // We're launching the BehaviorProgressActivity
            CompassApplication application = (CompassApplication) getApplication();
            Context ctx = getApplicationContext();
            Intent intent = new Intent(ctx, BehaviorProgressActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(ctx,
                    (int) System.currentTimeMillis(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Bundle args = new Bundle();
            args.putSerializable("objectType", Constants.BEHAVIOR_TYPE);

            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            Notification notification = new NotificationCompat.Builder(ctx)
                    .setSmallIcon(R.drawable.ic_action_compass_white)
                    .setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setContentText(msg)
                    .setLargeIcon(icon)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .addExtras(args)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .build();

            mNotificationManager.notify(NOTIFICATION_ID, notification);
        }
    }
}