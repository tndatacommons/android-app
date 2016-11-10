package org.tndata.android.compass.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Class to manage all shared preferences.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class SharedPreferencesManager{
    private static final String LOCATION_PERMISSION_REQUESTED_KEY = "location_permission_requested";


    public static boolean isFirstLocationPermissionRequest(Context context){
        Context appContext = context.getApplicationContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        return !preferences.getBoolean(LOCATION_PERMISSION_REQUESTED_KEY, false);
    }

    public static void locationPermissionRequested(Context context){
        Context appContext = context.getApplicationContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        preferences.edit().putBoolean(LOCATION_PERMISSION_REQUESTED_KEY, true).apply();
    }
}
