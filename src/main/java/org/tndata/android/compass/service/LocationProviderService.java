package org.tndata.android.compass.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.LocationRequest;


/**
 * Created by isma on 9/8/15.
 */
public class LocationProviderService extends Service implements LocationRequest.OnLocationAcquiredCallback{
    private static final LatLng COWORK = new LatLng(35.121132, -89.990519);

    private static boolean cancelled;
    private boolean mRunning;

    private LocationRequest mLocationRequest;


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
    public int onStartCommand(Intent intent, int flags, int startId){
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
    public void onLocationAcquired(Location location){
        double distance = CompassUtil.getDistance(COWORK, new LatLng(location.getLatitude(), location.getLongitude()));
        Toast.makeText(this, "distance: " + distance, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                requestLocation();
            }
        }, (int)(distance*100));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }
}
