package org.tndata.android.compass.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ToggleButton;

import org.tndata.android.compass.R;


/**
 * Preference used to select a set of hours when the app should go silent.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class QuietHoursPreference extends DialogPreference implements View.OnClickListener{
    private static final String TAG = "QuietHoursPreference";


    //Data holders
    private boolean mAM[];
    private boolean mPM[];


    /**
     * Constructor.
     *
     * @param context the context.
     * @param attrs the attribute set.
     */
    public QuietHoursPreference(Context context, AttributeSet attrs){
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
    public QuietHoursPreference(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Initialises the preference.
     */
    private void init(){
        mAM = new boolean[12];
        mPM = new boolean[12];

        setDialogLayoutResource(R.layout.dialog_quiet_hours);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }

    /**
     * Populates the data holders for the preference.
     */
    private void populateArrays(){
        boolean values[][] = parsePreference(getKey(), getContext());
        mAM = values[0];
        mPM = values[1];
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue){
        if (restorePersistedValue){
            populateArrays();
        }
    }

    @Override
    protected void showDialog(Bundle state){
        super.showDialog(state);
        populateArrays();
        Dialog dialog = getDialog();
        String pkg = getContext().getPackageName();
        String ap = "am";
        for (int i = 0, hour = 0; i < mAM.length + mPM.length; i++, hour++){
            if (i == 12){
                ap = "pm";
                hour = 0;
            }
            @IdRes int id = getContext().getResources().getIdentifier("quiet_"+hour+ap, "id", pkg);
            ToggleButton button = (ToggleButton)dialog.findViewById(id);
            if (i < 12){
                button.setChecked(mAM[hour]);
                if (mAM[hour]){
                    button.setTextColor(0xFFFF0000);
                }
                else{
                    button.setTextColor(0xFF000000);
                }
            }
            else{
                button.setChecked(mPM[hour]);
                if (mPM[hour]){
                    button.setTextColor(0xFFFF0000);
                }
                else{
                    button.setTextColor(0xFF000000);
                }
            }
            button.setTag(R.id.quiet_toggle_tag, i);
            button.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view){
        ToggleButton button = (ToggleButton)view;
        int tag = (int)button.getTag(R.id.quiet_toggle_tag);
        if (tag/12 == 0){
            mAM[tag] = button.isChecked();
            if (mAM[tag]){
                button.setTextColor(0xFFFF0000);
            }
            else{
                button.setTextColor(0xFF000000);
            }
        }
        else{
            mPM[tag%12] = button.isChecked();
            if (mPM[tag%12]){
                button.setTextColor(0xFFFF0000);
            }
            else{
                button.setTextColor(0xFF000000);
            }
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult){
        if (positiveResult){
            String result = "";
            for (int i = 0; i < mAM.length; i++){
                if (mAM[i]){
                    result += i + "a,";
                }
            }
            for (int i = 0; i < mPM.length; i++){
                if (mPM[i]){
                    result += i + "p,";
                }
            }
            persistString(result.substring(0, result.length()-1));
        }
    }

    public static boolean[][] parsePreference(String key, Context context){
        boolean result[][] = new boolean[2][12];
        for (int i = 0; i < 12; i++){
            result[0][i] = false;
            result[1][i] = false;
        }

        //Load the preference
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String quietPreference = preferences.getString(key, "");
        Log.d(TAG, quietPreference);

        //If the preference ain't empty split it and parse the parts
        if (!quietPreference.isEmpty()){
            String quiet[] = quietPreference.split(",");
            for (String time:quiet){
                int hour = Integer.valueOf(time.substring(0, time.length() - 1));
                if (time.endsWith("a")){
                    result[0][hour] = true;
                }
                else{
                    result[1][hour] = true;
                }
            }
        }

        return result;
    }
}
