package org.tndata.android.compass.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.v4.content.ContextCompat;

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
import org.tndata.android.compass.util.NotificationUtil;

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
        cancelled = false;
        mRunning = false;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mLocationRequest = new LocationRequest(this, this, 0);
        }
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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationRequest.onStart();
            }
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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationRequest.onStop();
            }
            stopSelf();
        }
        //Otherwise, request a location
        else{
            if (!mRequestInProgress){
                mRequestInProgress = true;
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    mLocationRequest.requestLocation();
                }
            }
        }
    }

    @Override
    public synchronized void onLocationAcquired(Location location){
        mRequestInProgress = false;

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
                NotificationUtil.generateActionNotification(this, reminder.getNotificationId(),
                        reminder.getTitle(), reminder.getMessage(), reminder.getObjectId(),
                        reminder.getUserMappingId());

                //The reminder is removed from the database and added to the removal list
                //NOTE: This is the case because all reminders in the database are snoozed
                //  at the moment, which means that they should be triggered only once.
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
        //Toast.makeText(this, distance + ", " + estimate, Toast.LENGTH_SHORT).show();
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
