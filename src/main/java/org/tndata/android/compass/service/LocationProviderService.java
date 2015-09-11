package org.tndata.android.compass.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.tndata.android.compass.R;
import org.tndata.android.compass.activity.ActionActivity;
import org.tndata.android.compass.activity.SnoozeActivity;
import org.tndata.android.compass.database.CompassDbHelper;
import org.tndata.android.compass.model.Place;
import org.tndata.android.compass.model.Reminder;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.LocationRequest;

import java.util.List;


/**
 * Created by isma on 9/8/15.
 */
public class LocationProviderService extends Service implements LocationRequest.OnLocationAcquiredCallback{
    private static final LatLng COWORK = new LatLng(35.121132, -89.990519);

    private static boolean cancelled;
    private boolean mRunning;

    private LocationRequest mLocationRequest;
    private List<Reminder> mReminders;
    private List<Place> mPlaces;


    public static void cancel(){
        cancelled = true;
    }


    @Override
    public void onCreate(){
        super.onCreate();
        mLocationRequest = new LocationRequest(this);
        mRunning = false;
        cancelled = false;
    }

    @Override
    public synchronized int onStartCommand(Intent intent, int flags, int startId){
        CompassDbHelper dbHelper = new CompassDbHelper(this);
        mReminders = dbHelper.getReminders();
        mPlaces = dbHelper.getPlaces();
        dbHelper.close();

        if (!mRunning){
            mRunning = true;
            mLocationRequest.onStart();
            requestLocation();
        }
        return START_STICKY;
    }

    private void requestLocation(){
        //If the service was cancelled, clean up and stop
        if (cancelled){
            mLocationRequest.onStop();
            stopSelf();
        }
        //Otherwise, request a location
        else{
            mLocationRequest.requestLocation(this);
        }
    }

    @Override
    public synchronized void onLocationAcquired(Location location){
        double distance = CompassUtil.getDistance(COWORK, new LatLng(location.getLatitude(), location.getLongitude()));
        for (Reminder reminder:mReminders){
            for (Place place:mPlaces){
                if (place.getId() == reminder.getPlaceId()){
                    distance = CompassUtil.getDistance(place.getLocation(), new LatLng(location.getLatitude(), location.getLongitude()));
                    Toast.makeText(this, "distance: " + distance, Toast.LENGTH_SHORT).show();
                    if (distance < 50){
                        NotificationManager mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

                        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        try{
                            Intent intent = new Intent(getApplicationContext(), ActionActivity.class);
                            intent.putExtra(ActionActivity.ACTION_ID_KEY, Integer.valueOf(reminder.getObjectId()));

                            int notificationId = Integer.valueOf(reminder.getObjectId());

                            PendingIntent contentIntent = PendingIntent.getActivity(this,
                                    (int)System.currentTimeMillis(), intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);

                            Intent snoozeIntent = new Intent(this, SnoozeActivity.class)
                                    .putExtra(SnoozeActivity.REMINDER_KEY, reminder)
                                    .putExtra(SnoozeActivity.NOTIFICATION_ID_KEY, Integer.valueOf(20))
                                    .putExtra(SnoozeActivity.PUSH_NOTIFICATION_ID_KEY, notificationId);

                            PendingIntent snoozePendingIntent = PendingIntent.getActivity(this,
                                    (int)System.currentTimeMillis(), snoozeIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);

                            Intent didItIntent = new Intent(this, CompleteActionService.class)
                                    .putExtra(CompleteActionService.NOTIFICATION_ID_KEY, notificationId)
                                    .putExtra(CompleteActionService.ACTION_MAPPING_ID_KEY, Integer.valueOf(reminder.getObjectId()));

                            PendingIntent didItPendingIntent = PendingIntent.getService(this,
                                    (int)System.currentTimeMillis(), didItIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);

                            Bundle args = new Bundle();
                            args.putSerializable("objectType", Constants.ACTION_TYPE);

                            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
                            Notification notification = new NotificationCompat.Builder(this)
                                    .setSmallIcon(R.drawable.ic_action_compass_white)
                                    .setContentTitle(reminder.getTitle())
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(reminder.getMessage()))
                                    .setContentText(reminder.getMessage())
                                    .setLargeIcon(icon)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setSound(sound)
                                    .addAction(R.drawable.ic_alarm_black_24dp, getString(R.string.later_title), snoozePendingIntent)
                                    .addAction(R.drawable.ic_check_normal_dark, getString(R.string.did_it_title), didItPendingIntent)
                                    .addExtras(args)
                                    .setContentIntent(contentIntent)
                                    .setAutoCancel(true)
                                    .build();

                            mNotificationManager.notify("org.tndata.compass.ActionNotification",
                                    notificationId,
                                    notification);
                        }
                        catch (NumberFormatException nfx){
                            nfx.printStackTrace();
                        }

                        CompassDbHelper dbHelper = new CompassDbHelper(this);
                        dbHelper.deleteReminder(reminder);
                        dbHelper.close();
                    }
                }
            }
        }
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                requestLocation();
            }
        }, 10000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }
}
