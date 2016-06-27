package org.tndata.android.compass.receiver;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.service.GcmIntentService;


/**
 * Processes and dispatches GCM messages. Only delivers actual messages and only if the
 * user is logged in.
 *
 * @author Edited by Ismael Alonso
 * @version 2.0.0
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver{
    private static final String TAG = "GcmBroadcastReceiver";


    @Override
    public void onReceive(Context context, Intent intent){
        if (!((CompassApplication)context.getApplicationContext()).getToken().equals("")){
            Bundle extras = intent.getExtras();

            //Check if there are actually extras
            if (extras != null && !extras.isEmpty()){
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(gcm.getMessageType(intent))){
                    //If this is an actual gcm message, handle it
                    String message = extras.getString("message");
                    Log.d(TAG, "GCM message received: " + message);

                    //Put the extras with my own keys to avoid conflicts
                    intent.putExtra(GcmIntentService.FROM_GCM_KEY, true);
                    intent.putExtra(GcmIntentService.MESSAGE_KEY, message);

                    //Explicitly specify that GcmIntentService will handle the intent
                    ComponentName comp = new ComponentName(context.getPackageName(),
                            GcmIntentService.class.getName());
                    //Start the service, keeping the device awake while it is running
                    startWakefulService(context, intent.setComponent(comp));
                }
                else{
                    //Otherwise, release the lock
                    completeWakefulIntent(intent);
                }
            }
        }
        //This is the only class in the application allowed to receive and process messages
        setResultCode(Activity.RESULT_CANCELED);
    }
}
