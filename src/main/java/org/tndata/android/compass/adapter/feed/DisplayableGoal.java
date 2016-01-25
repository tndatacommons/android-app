package org.tndata.android.compass.adapter.feed;

import android.content.Context;


/**
 * Allows the retrieval of data from different kinds of goal objects in an homogeneous way.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public interface DisplayableGoal{
    /**
     * Getter for titles.
     *
     * @return the title of the goal.
     */
    String getTitle();

    /**
     * Getter for the icon url.
     *
     * @return the icon url of the goal or the empty string if a default icon is to be used.
     */
    String getIconUrl();

    /**
     * Returns the background color of the icon container for the goal.
     *
     * @param context a reference to the context.
     * @return a background color as a hex value string.
     */
    String getColor(Context context);
}
