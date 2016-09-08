package org.tndata.android.compass.service;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.model.GcmMessage;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.parser.ParserMethods;
import org.tndata.android.compass.receiver.GcmBroadcastReceiver;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.NotificationUtil;


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
 */
public class GcmIntentService extends IntentService{
    private static final String TAG = "GcmIntentService";

    public static final String FROM_GCM_KEY = "org.tndata.Compass.GcmIntentService.FromGcm";
    public static final String MESSAGE_KEY = "org.tndata.Compass.GcmIntentService.Message";


    public GcmIntentService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent){
        boolean isFromGcm = intent.getBooleanExtra(FROM_GCM_KEY, false);
        String gcmMessage = intent.getStringExtra(MESSAGE_KEY);

        //IntentServices are executed in the background, so it is safe to do this
        GcmMessage message = ParserMethods.sGson.fromJson(gcmMessage, GcmMessage.class);
        if (message.isProduction() == !API.STAGING){
            User user = ((CompassApplication)getApplicationContext()).getUser();
            if (user.getId() == message.getRecipient()){
                message.setGcmMessage(gcmMessage);
                NotificationUtil.generateNotification(this, message);
            }
            else{
                long recipient = message.getRecipient();
                long receiver = user.getId();
                Log.e(TAG, "The message was intended for " + recipient + ", received by " + receiver);
            }
        }
        else{
            String sender = message.isProduction() ? "production" : "staging";
            String running = API.STAGING ? "staging" : "production";
            Log.e(TAG, "The message was delivered from " + sender + ", running " + running);
        }

        if (isFromGcm){
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        }
    }


    public static Intent getIntent(Context context, String message){
        return new Intent(context, GcmIntentService.class)
                .putExtra(MESSAGE_KEY, message)
                .putExtra(FROM_GCM_KEY, false);
    }

    public static Intent populateIntent(Context context, Intent intent, String message){
        return intent.putExtra(MESSAGE_KEY, message)
                .putExtra(FROM_GCM_KEY, true)
                .setComponent(
                        new ComponentName(
                                context.getPackageName(),
                                GcmIntentService.class.getName()
                        )
                );
    }
}
