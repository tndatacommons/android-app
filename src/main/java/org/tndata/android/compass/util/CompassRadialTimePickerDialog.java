package org.tndata.android.compass.util;

import android.content.DialogInterface;

import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;

/**
 * Created by isma on 8/25/15.
 */
public class CompassRadialTimePickerDialog extends RadialTimePickerDialog{
    private DialogInterface.OnKeyListener mListener;


    public void setOnKeyListener(DialogInterface.OnKeyListener listener){
        mListener = listener;
    }

    @Override
    public void onResume(){
        super.onResume();
        getDialog().setOnKeyListener(mListener);
    }
}
