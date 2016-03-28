package org.tndata.android.compass.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;


/**
 * Receives location provider state changes.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class LocationReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent){
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean isLocationEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isLocationEnabled |= lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isLocationEnabled){
            //Fire up location
        }
        else{
            //Kill location
        }
    }
}
