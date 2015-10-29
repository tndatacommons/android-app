package org.tndata.android.compass.ui;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import org.tndata.android.compass.R;


/**
 * Created by isma on 10/29/15.
 */
public class QuietHoursPreference extends DialogPreference{
    public QuietHoursPreference(Context context){
        super(context);
        init();
    }

    public QuietHoursPreference(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    public QuietHoursPreference(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setDialogLayoutResource(R.layout.dialog_quiet_hours);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }
}
