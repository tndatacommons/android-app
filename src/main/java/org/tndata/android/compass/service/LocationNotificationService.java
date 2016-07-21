package org.tndata.android.compass.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.tndata.android.compass.database.CompassDbHelper;
import org.tndata.android.compass.database.PlaceTableHandler;
import org.tndata.android.compass.model.Reminder;
import org.tndata.android.compass.model.UserPlace;
import org.tndata.android.compass.util.CompassUtil;
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

    private static final String TAG = "LocationNotificationSvc";

    //Commands
    private static final String COMMAND_KEY = "org.tndata.compass.LocationNotificationSvc.Command";
    private static final String START = "start";
    private static final String UPDATE = "update";
    private static final String KILL = "kill";

    //Defaults
    private static final int UPDATE_INTERVAL = 60*1000; //60 seconds
    private static final int CHECKING_INTERVAL = 31*1000; //31 seconds
    private static final int GEOFENCE_RADIUS = 40; //40 meters


    /**
     * Starts the service for scan.
     *
     * @param context a reference to the context.
     */
    public static void start(@NonNull Context context){
        Log.i(TAG, "Start request. Service will start shortly.");
        context.startService(new Intent(context, LocationNotificationService.class)
                .putExtra(COMMAND_KEY, START));
    }

    public static void updateDataSet(@NonNull Context context){
        Log.i(TAG, "Update request. Service's data set will update shortly.");
        context.startService(new Intent(context, LocationNotificationService.class)
                .putExtra(COMMAND_KEY, UPDATE));
    }

    public static void kill(@NonNull Context context){
        Log.i(TAG, "Kill request. Service will stop shortly.");
        context.startService(new Intent(context, LocationNotificationService.class)
                .putExtra(COMMAND_KEY, KILL));
    }


    //Running boolean, to work around some LocationRequest edge cases
    private boolean mRunning;

    //The location request
    private LocationRequest mLocationRequest;
    private boolean mRequestInProgress;

    //Map of places and list of reminders
    private Map<Long, UserPlace> mPlaces;
    private List<Reminder> mReminders;

    //Next update time
    private long mNextUpdateTime;


    @Override
    public void onCreate(){
        super.onCreate();
        mRunning = false;
        mRequestInProgress = false;
    }

    @Override
    public synchronized int onStartCommand(Intent intent, int flags, int startId){
        //Check permission, from this point on, this needn't be checked, as the app restarts when
        //  permissions change. Check if location is enabled. Checking if location is enabled is
        //  a safety measure for whenever the service is started from the BootReceiver,
        //  SnoozeActivity, or CompassApplication
        if (CompassUtil.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                && CompassUtil.isLocationEnabled(this)){

            //Evaluate the action
            if (intent.getExtras() != null){
                switch (intent.getExtras().getString(COMMAND_KEY, "")){
                    case START:
                        //For start actions, if the service is already started, do nothing,
                        //  otherwise, start it
                        if (!mRunning){
                            Log.i(TAG, "Starting service");
                            loadData();
                            //If there are no reminders, starting the service makes no sense
                            if (mReminders.isEmpty()){
                                Log.i(TAG, "There are no reminders, killing service");
                                stopSelf();
                            }
                            else{
                                mRunning = true;
                                mLocationRequest = new LocationRequest(this, this, 0);
                                mLocationRequest.onStart();
                                mLocationRequest.requestLocation();
                            }
                        }
                        else{
                            Log.i(TAG, "Service was already running");
                        }
                        break;

                    case UPDATE:
                        //For update actions, if the service was running, load the data,
                        //  otherwise assume there was a good reason for it to be stopped
                        //  and stop it.
                        if (mRunning){
                            Log.i(TAG, "Updating service's data set");
                            loadData();
                        }
                        else{
                            Log.e(TAG, "Can't update, service is not running");
                            Log.i(TAG, "Trying to start the service");
                            start(getApplicationContext());
                        }
                        break;

                    case KILL:
                        //If the action is kill, stop the service
                        if (mRunning){
                            Log.i(TAG, "Killing service");
                            mLocationRequest.onStop();
                            mRunning = false;
                        }
                        else{
                            Log.i(TAG, "Service was not running");
                        }
                        stopSelf();
                        break;

                    default:
                        Log.w(TAG, "Unrecognized command");
                        if (!mRunning){
                            stopSelf();
                        }
                }
            }
            else{
                if (!mRunning){
                    stopSelf();
                }
            }
        }
        else{
            Log.e(TAG, "Permissions not granted. Aborting.");
            stopSelf();
        }

        return START_STICKY;
    }

    /**
     * Reloads the data set.
     */
    private void loadData(){
        Log.i(TAG, "Loading data");
        PlaceTableHandler handler = new PlaceTableHandler(this);
        mPlaces = new HashMap<>();
        for (UserPlace userPlace:handler.getPlaces()){
            mPlaces.put(userPlace.getId(), userPlace);
        }
        handler.close();
        CompassDbHelper dbHelper = new CompassDbHelper(this);
        mReminders = dbHelper.getReminders();
        dbHelper.close();
        Log.i(TAG, mPlaces.size() + " places found");
        for (UserPlace place:mPlaces.values()){
            Log.i(TAG, place.toString());
        }
        Log.i(TAG, mReminders.size() + " reminders found");
        for (Reminder reminder:mReminders){
            Log.i(TAG, reminder.getTitle());
        }
    }

    /**
     * Requests a location from the LocationRequest, but checks if the service should
     * shut down first.
     */
    private void requestLocation(){
        if (mRunning && !mRequestInProgress){
            Log.i(TAG, "Requesting location");
            mRequestInProgress = true;
            mLocationRequest.requestLocation();
        }
    }

    @Override
    public synchronized void onLocationAcquired(Location location){
        mRequestInProgress = false;

        //The maximum surface distance between two points in the planet is about 20Mm
        double minDistance = 20*1000*1000;
        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
        Log.i(TAG, "Geofence radius: " + GEOFENCE_RADIUS);
        Log.d(TAG, "Location acquired: " + current.toString());

        //To avoid concurrent modification exceptions, the list of reminders to be
        //  removed are put into a separate list and removed from the main list
        //  once the iterator is done iterating
        List<Reminder> remindersToRemove = new ArrayList<>();

        //For every location based reminder in sight
        for (Reminder reminder:mReminders){
            Log.i(TAG, "Reminder: " + reminder.getTitle());
            //The place is retrieved from the map
            UserPlace place = mPlaces.get(reminder.getPlaceId());
            //If the place was removed or this is me switching from staging to production
            //  or vice-versa, remove the reminder, because otherwise the service won't
            //  operate as expected
            if (place == null){
                Log.e(TAG, "The reminder is not associated with an existing place, removing");
                CompassDbHelper dbHelper = new CompassDbHelper(this);
                dbHelper.deleteReminder(reminder);
                dbHelper.close();
                remindersToRemove.add(reminder);
            }
            else{
                //Calculate distance and log data
                double distance = CompassUtil.getDistance(place.getLocation(), current);
                Log.i(TAG, place.toString());
                Log.i(TAG, "Current location: " + current);
                Log.i(TAG, "Distance: " + distance);

                //If the pone is within the geofence a notification is created
                if (distance < GEOFENCE_RADIUS){
                    NotificationUtil.putActionNotification(this, reminder.getNotificationId(),
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
        }

        //The reminders that have been dealt with are removed from the main list
        for (Reminder reminder:remindersToRemove){
            mReminders.remove(reminder);
        }

        //If there are no more reminders, shut down the service
        if (mReminders.size() == 0){
            Log.d(TAG, "There are no more reminders, killing service");
            mLocationRequest.onStop();
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
        Log.d(TAG, "The next location update will happen in " + (estimate/1000) + " seconds");
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
            if (mRunning){
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        checkTiming();
                    }
                }, CHECKING_INTERVAL);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }
}
