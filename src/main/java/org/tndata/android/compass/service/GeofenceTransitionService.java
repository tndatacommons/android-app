package org.tndata.android.compass.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.tndata.android.compass.R;


/**
 * Created by isma on 9/4/15.
 */
public class GeofenceTransitionService extends IntentService{
    public GeofenceTransitionService(){
        super("GeofenceTransitionService");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        Toast.makeText(this, "Yo, Geofence transition", Toast.LENGTH_SHORT).show();

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_action_compass_white)
                .setContentTitle("GeofenceTransition")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Transition test"))
                .setContentText("Transition test")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build();

        notificationManager.notify("GEOFENCE",
                (int)System.currentTimeMillis(),
                notification);
    }
}
