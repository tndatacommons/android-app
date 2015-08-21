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
import org.tndata.android.compass.R;
import org.tndata.android.compass.activity.ActionActivity;
import org.tndata.android.compass.activity.BehaviorProgressActivity;
import org.tndata.android.compass.activity.TriggerActivity;
import org.tndata.android.compass.util.Constants;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * <p/>
 * NOTE: Messages received from GCM will have a format like the following:
 * <p/>
 * {
 * "title":"Demo"
 * "message":"Don't forget to review your Notifications for today",
 * "object_id":32,
 * "object_type":"action",  // an Action
 * }
 * <p/>
 * A single, daily Behavior notification will arrive, but it does not contain object info:
 * <p/>
 * {
 * "title":"Stay on Course"
 * "message":"some message here...",
 * "object_id": null,
 * "object_type": null,
 * }
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    public final static String NOTIFICATION_TAG = "COMPASS_ACTION";
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

        if (extras != null && !extras.isEmpty()) {  // has effect of un-parcelling Bundle
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
        Context ctx = getApplicationContext();
        NotificationManager mNotificationManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = (int)(Math.random()*1000000);
        if (object_type.equals(Constants.ACTION_TYPE)){
            try {
                Intent intent = new Intent(getApplicationContext(), ActionActivity.class);
                intent.putExtra(ActionActivity.ACTION_ID_KEY, Integer.valueOf(object_id));
                //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent contentIntent = PendingIntent.getActivity(ctx,
                        (int)System.currentTimeMillis(), intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                Intent laterIntent = new Intent(this, TriggerActivity.class)
                        .putExtra(TriggerActivity.NEEDS_FETCHING_KEY, true)
                        .putExtra(TriggerActivity.NOTIFICATION_ID_KEY, notificationId)
                        .putExtra(TriggerActivity.ACTION_ID_KEY, Integer.valueOf(object_id));

                PendingIntent laterPendingIntent = PendingIntent.getActivity(ctx,
                        (int)System.currentTimeMillis(), laterIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                Intent didItIntent = new Intent(this, CompleteActionService.class)
                        .putExtra(CompleteActionService.NOTIFICATION_ID_KEY, notificationId)
                        .putExtra(CompleteActionService.ACTION_ID_KEY, Integer.valueOf(object_id));

                PendingIntent didItPendingIntent = PendingIntent.getService(ctx,
                        (int)System.currentTimeMillis(), didItIntent,
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
                        .addAction(R.drawable.ic_blue_notifications, "Later", laterPendingIntent)
                        .addAction(R.drawable.ic_check_normal_dark, "I did it", didItPendingIntent)
                        .addExtras(args)
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true)
                        .build();

                mNotificationManager.notify(notificationId, notification);
            }
            catch (NumberFormatException nfx){
                nfx.printStackTrace();
            }
        }
        else{
            // We're launching the BehaviorProgressActivity
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

            mNotificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notification);
        }
    }
}