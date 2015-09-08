package org.tndata.android.compass.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


/**
 * Class that handles location requests. Its lifecycle is the same as that of the carrier
 * activity or fragment. It should be constructed on onCreate, started on onStart, and
 * stopped on onStop.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class LocationRequest
        implements
                GoogleApiClient.ConnectionCallbacks,
                GoogleApiClient.OnConnectionFailedListener,
                LocationListener{


    private static final int LOCATION_TIMEOUT = 5000; //5 seconds
    private Context context;
    //Google api client
    private GoogleApiClient googleApiClient;
    private boolean connected, updateQueued;
    //Backup system
    private LocationManager locationManager;
    //Location callback
    private OnLocationAcquiredCallback callback;


    /**
     * Creates the location request object. This object is meant to be constructed on the
     * onCreate() method of the activity/fragment it is used in and kept until the activity
     * is disposed of.
     *
     * @param context the activity/fragment context.
     */
    public LocationRequest(Context context){
        this.context = context;

        //Start the API client up
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //At the beginning, neither the service is connected nor a request is queued
        connected = false;
        updateQueued = false;
    }

    /**
     * Fires the main location provider system. Called on the onStart() method of
     * the activity/fragment it is embedded in.
     */
    public void onStart(){
        googleApiClient.connect();
    }

    /**
     * Shuts down all location provider systems. Called on the onStop() method of
     * the activity/fragment it is embedded in.
     */
    public void onStop(){
        if (googleApiClient != null){
            googleApiClient.disconnect();
        }
        if (locationManager != null){
            locationManager.removeUpdates(this);
            locationManager = null;
        }
    }

    /**
     * Queues a location update.
     *
     * @param callback the callback object.
     */
    public void requestLocation(@NonNull OnLocationAcquiredCallback callback){
        //The callback is set
        this.callback = callback;
        //If play services is connected
        if (connected){
            //Let it provide an update
            setLocation(LocationServices.FusedLocationApi.getLastLocation(googleApiClient));
        }
        //Otherwise
        else{
            //Queue the update
            updateQueued = true;
        }
    }

    /**
     * Makes sure that the location is valid and if not triggers the backup.
     *
     * @param location the last known location
     */
    private void setLocation(Location location){
        //If the location is null or has expired
        if (location == null || location.getTime() + LOCATION_TIMEOUT < System.currentTimeMillis()){
            //Log for debugging purposes
            Log.d("LocationRequest", "The location was not valid");

            //The backup system (system location provider) is fired
            locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

            //Accepted providers are the carrier network and the GPS network
            //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        //Otherwise
        else{
            //Log for debugging purposes
            Log.d("LocationRequest", "(" + location.getLatitude() + ", " + location.getLongitude() + ")");

            //If the update was from the backup system
            if (locationManager != null){
                //The backup system is silenced
                locationManager.removeUpdates(this);
                locationManager = null;
            }

            //The location is provided
            callback.onLocationAcquired(location);
        }
    }


    //Play services connection methods

    @Override
    public void onConnected(Bundle bundle){
        //Log for debugging purposes
        Log.d("PlayConnection", "Connection established");

        //The connected flag is set to true
        connected = true;
        //If an update is queued
        if (updateQueued){
            //It is provided through the provider method
            setLocation(LocationServices.FusedLocationApi.getLastLocation(googleApiClient));
        }
    }

    @Override
    public void onConnectionSuspended(int i){
        //Log for debugging purposes
        Log.d("PlayConnection", "Connection suspended");

        //The connected flag is set to false;
        connected = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){
        //Log for debugging purposes
        Log.d("PlayConnection", "Couldn't establish connection");

        //If an update was queued
        if (updateQueued){
            //A null location is provided to force the backup system to fire
            setLocation(null);
        }
    }


    //System location provider methods

    @Override
    public void onLocationChanged(Location location){
        //Log for debugging purposes
        Log.d("Location", "A location was provided by the system manager");

        Log.d("Location time", location.getTime() + "");
        Log.d("System time", System.currentTimeMillis() + "");

        //If the location providers still alive
        if (locationManager != null){
            //The location providers are silenced
            locationManager.removeUpdates(this);
            locationManager = null;
        }

        //The location is provided
        setLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){
        //Unused
    }

    @Override
    public void onProviderEnabled(String provider){
        //Unused
    }

    @Override
    public void onProviderDisabled(String provider){
        //Unused
    }


    /**
     * An interface with a callback method that gets called when a location
     * is acquired.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface OnLocationAcquiredCallback{
        /**
         * Called when a location is acquired.
         *
         * @param location the most recent known location.
         */
        void onLocationAcquired(Location location);
    }
}
