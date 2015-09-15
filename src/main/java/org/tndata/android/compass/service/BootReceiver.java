package org.tndata.android.compass.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * This receiver receives a broadcast when the system boots up, services that need to be running
 * when the system starts up should be fired here.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class BootReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent){
        Log.d("BootReceiver", "Receiver fired");
        Intent locationNotification = new Intent(context, LocationNotificationService.class);
        context.startService(locationNotification);
    }
}
