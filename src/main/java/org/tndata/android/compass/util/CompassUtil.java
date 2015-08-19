package org.tndata.android.compass.util;

import android.content.Context;
import android.util.TypedValue;

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
}
