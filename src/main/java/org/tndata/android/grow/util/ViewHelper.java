package org.tndata.android.grow.util;

import android.view.View;

public class ViewHelper {
    public final static int SELECTED = 1;
    public final static int ADD = 2;

    /**
     * Helper method to set the visibility for a View, but only if the view is
     * not null.
     */
    public static void setVisibility(View v, int visibility) {
        if(v != null) {
            v.setVisibility(visibility);
        }
    }

}
