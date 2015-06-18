package org.tndata.android.compass.ui;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupMenu;

public class CompassPopupMenu extends PopupMenu {

    private static final String TAG = "CompassPopupMenu";

    public CompassPopupMenu(Context context, View anchor) {super(context, anchor);}

    public CompassPopupMenu(Context context, View anchor, int gravity) {
        // Android Studio complains about this requiring API level 19 and our
        // minimum being 14, but with the factory method, should be ok.
        super(context, anchor, gravity);
    }

    public static CompassPopupMenu newInstance(Context context, View anchor) {
        CompassPopupMenu popup;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // TODO: This doesn't actually seem to change where the popup menu appears :(
            popup = new CompassPopupMenu(context, anchor, Gravity.CENTER);
        } else {
            popup = new CompassPopupMenu(context, anchor);
        }
        return popup;
    }
}