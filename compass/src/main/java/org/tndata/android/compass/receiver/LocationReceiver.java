package org.tndata.android.compass.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.tndata.android.compass.service.LocationNotificationService;
import org.tndata.android.compass.util.CompassUtil;


/**
 * Receives location provider state changes.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class LocationReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent){
        if (CompassUtil.isLocationEnabled(context)){
            //Fire up location
            LocationNotificationService.start(context.getApplicationContext());
        }
        else{
            //Kill location
            LocationNotificationService.kill(context.getApplicationContext());
        }
    }
}
