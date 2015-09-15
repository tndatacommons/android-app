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
import android.util.Log;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Service that checks the user's location and creates reminders accordingly.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class LocationNotificationService
        extends Service
        implements LocationRequest.OnLocationAcquiredCallback{

    //Constants
    private static final int UPDATE_INTERVAL = 60*1000; //60 seconds
    private static final int CHECKING_INTERVAL = 31*1000; //31 seconds
    private static final int GEOFENCE_RADIUS = 40; //40 meters

    //Cancellation boolean, mainly for testing purposes
    private static boolean cancelled;

    /**
     * Shuts down the service if it is running.
     */
    public static void cancel(){
        cancelled = true;
    }


    //Running boolean, to work around some LocationRequest edge cases
    private boolean mRunning;

    //The location request
    private LocationRequest mLocationRequest;
    private boolean mRequestInProgress;

    //Map of places and list of reminders
    private Map<Integer, Place> mPlaces;
    private List<Reminder> mReminders;

    //Next update time
    private long mNextUpdateTime;


    @Override
    public void onCreate(){
        super.onCreate();
        Log.d("LocationNotification", "Service fired");
        cancelled = false;
        mRunning = false;
        mLocationRequest = new LocationRequest(this, this, 0);
        mRequestInProgress = false;
    }

    @Override
    public synchronized int onStartCommand(Intent intent, int flags, int startId){
        CompassDbHelper dbHelper = new CompassDbHelper(this);
        mPlaces = new HashMap<>();
        for (Place place:dbHelper.getPlaces()){
            mPlaces.put(place.getId(), place);
        }
        mReminders = dbHelper.getReminders();
        dbHelper.close();

        if (!mRunning){
            mRunning = true;
            mLocationRequest.onStart();
        }
        requestLocation();
        return START_STICKY;
    }

    /**
     * Requests a location from the LocationRequest, but checks if the service should
     * shut down first.
     */
    private void requestLocation(){
        //If the service was cancelled, clean up and stop
        if (cancelled){
            mLocationRequest.onStop();
            stopSelf();
        }
        //Otherwise, request a location
        else{
            if (!mRequestInProgress){
                mRequestInProgress = true;
                mLocationRequest.requestLocation();
            }
        }
    }

    @Override
    public synchronized void onLocationAcquired(Location location){
        mRequestInProgress = false;
        NotificationManager mNotificationManager =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //The maximum surface distance between two points in the planet is about 20Mm
        double minDistance = 20*1000*1000;
        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());

        //To avoid concurrent modification exceptions, the list of reminders to be
        //  removed are put into a separate list and removed from the main list
        //  once the iterator is done iterating
        List<Reminder> remindersToRemove = new ArrayList<>();

        //For every location based reminder in sight
        for (Reminder reminder:mReminders){
            //The place is retrieved from the map and the distance calculated
            Place place = mPlaces.get(reminder.getPlaceId());
            double distance = CompassUtil.getDistance(place.getLocation(), current);

            //If the pone is within the geofence a notification is created
            if (distance < GEOFENCE_RADIUS){
                //NOTE: I am doing this here to avoid conflicts, once the PR is merged
                //  I'll fix all the notification related code.
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

                //The reminder is removed from the database and added to the removal list
                CompassDbHelper dbHelper = new CompassDbHelper(this);
                dbHelper.deleteReminder(reminder);
                dbHelper.close();
                remindersToRemove.add(reminder);
            }
            //If the phone is not in the fence but the distance is the minimum seen so far
            else if (distance < minDistance){
                //The distance is recorded as the minimum
                minDistance = distance;
            }
        }

        //The reminders that have been dealt with are removed from the main list
        for (Reminder reminder:remindersToRemove){
            mReminders.remove(reminder);
        }

        //If there are no more reminders, shut down the service
        if (mReminders.size() == 0){
            stopSelf();
        }
        //Otherwise, schedule a timing check
        else{
            setNextUpdateTime(minDistance);
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    checkTiming();
                }
            }, CHECKING_INTERVAL);
        }
    }

    /**
     * Sets the next update time given the distance to the nearest place for which the user has
     * a reminder set. It calculates the time it would take the user to travel that distance at
     * about 50mph and divides that time by 2.
     *
     * @param distance the distance for which a time estimate needs to be calculated.
     */
    private void setNextUpdateTime(double distance){
        //25m/s is a bit over 50mph, calculate the time to cover that distance and divide by 2
        int estimate = (int)((distance/25)*1000/2);
        if (estimate < UPDATE_INTERVAL){
            estimate = UPDATE_INTERVAL;
        }
        mNextUpdateTime = System.currentTimeMillis() + estimate;
        Toast.makeText(this, distance + ", " + estimate, Toast.LENGTH_SHORT).show();
    }

    /**
     * Checks whether it is time to request a location update, if not it goes to sleep.
     */
    private void checkTiming(){
        //If there is a location update scheduled, request a location
        if (mNextUpdateTime < System.currentTimeMillis()){
            requestLocation();
        }
        //Otherwise, schedule another check in 30 seconds
        else{
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    checkTiming();
                }
            }, CHECKING_INTERVAL);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }
}
