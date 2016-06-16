package org.tndata.android.compass.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.NumberPicker;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.util.API;

import es.sandwatch.httprequests.HttpRequest;


/**
 * Number picking dialog preference. Only meant to be used for the number of notifications yet.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class NumberPickerPreference extends DialogPreference implements NumberPicker.OnValueChangeListener{
    private static final String TAG = "QuietHoursPreference";


    //Data holders
    private int mValue;


    /**
     * Constructor.
     *
     * @param context the context.
     * @param attrs the attribute set.
     */
    public NumberPickerPreference(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    /**
     * Constructor.
     *
     * @param context the context.
     * @param attrs the attribute set.
     * @param defStyleAttr the style id.
     */
    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Initialises the preference.
     */
    private void init(){
        setDialogLayoutResource(R.layout.dialog_number_picker);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue){
        if (restorePersistedValue){
            mValue = parsePreference(getKey(), getContext());
        }
    }

    @Override
    protected void showDialog(Bundle state){
        super.showDialog(state);
        User user = ((CompassApplication)getContext().getApplicationContext()).getUser();
        mValue = user.getDailyNotifications();
        Dialog dialog = getDialog();
        NumberPicker picker = (NumberPicker)dialog.findViewById(R.id.number_picker_picker);
        picker.setOnValueChangedListener(this);
        picker.setMinValue(0);
        picker.setMaxValue(20);
        picker.setValue(mValue);
        picker.setWrapSelectorWheel(false);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal){
        mValue = newVal;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult){
        if (positiveResult){
            persistInt(mValue);
            User user = ((CompassApplication)getContext().getApplicationContext()).getUser();
            user.setDailyNotifications(mValue);
            HttpRequest.put(null, API.getPutUserProfileUrl(user), API.getPutUserProfileBody(user));
        }
        else{
            mValue = parsePreference(getKey(), getContext());
        }
    }

    public static int parsePreference(String key, Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, 5);
    }
}
