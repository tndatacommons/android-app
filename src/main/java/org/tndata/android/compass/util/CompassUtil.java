package org.tndata.android.compass.util;

import android.content.Context;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * This class contains all general purpose helper or utility methods used
 * throughout the application but small or rogue enough not to be worthy of
 * having a class of their own.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public final class CompassUtil{
    /**
     * Constructor. Declared private to prevent external instantiation. Furthermore, throws
     * an exception to prevent internal instantiation.
     */
    private CompassUtil() throws IllegalAccessException{
        throw new IllegalAccessException(getClass().toString() + " is not to be instantiated");
    }

    /**
     * Getter for the screen width in pixels.
     *
     * @param context a reference to the context.
     * @return the screen width in pixels.
     */
    public static int getScreenWidth(Context context){
        WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    /**
     * Converts density pixels into pixels.
     *
     * @param context the application context.
     * @param densityPixels the amount of DPs to convert.
     * @return the amount in pixels.
     */
    public static int getPixels(Context context, int densityPixels){
        return (int)Math.ceil(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, densityPixels,
                context.getResources().getDisplayMetrics()));
    }

    public static double getDistance(LatLng point1, LatLng point2){
        //Coordinates
        double lat1 = point1.latitude;
        double lng1 = point1.longitude;
        double lat2 = point2.latitude;
        double lng2 = point2.longitude;

        //Distance
        lat1 *= Math.PI/180;
        lng1 *= Math.PI/180;
        lat2 *= Math.PI/180;
        lng2 *= Math.PI/180;
        double a = Math.sin((lat2-lat1)/2)*Math.sin((lat2-lat1)/2)
                +Math.cos(lat1)*Math.cos(lat2)
                *Math.sin((lng2-lng1)/2)*Math.sin((lng2-lng1)/2);
        double c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return c*6371*1000;
    }
}
