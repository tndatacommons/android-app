package org.tndata.android.compass.model;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Date;


/**
 * Model superclass for anything that can be classified as an action.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public abstract class Action extends UserContent{
    @SerializedName("trigger")
    private Trigger mTrigger;
    @SerializedName("next_reminder")
    private String mNextReminder;


    public Trigger getTrigger(){
        return mTrigger;
    }

    public boolean hasTrigger(){
        return mTrigger != null;
    }

    public String getNextReminder(){
        return mNextReminder;
    }

    public Date getNextReminderDate(){
        String time = mNextReminder.substring(mNextReminder.indexOf('T')+1);
        String hour = time.substring(0, time.indexOf(':'));
        time = time.substring(time.indexOf(':')+1);
        String minute = time.substring(0, time.indexOf(':'));

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hour));
        calendar.set(Calendar.MINUTE, Integer.valueOf(minute));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public String getNextReminderDisplay(){
        if (mNextReminder == null){
            return "";
        }

        String time = mNextReminder.substring(mNextReminder.indexOf('T')+1);
        String hourStr = time.substring(0, time.indexOf(':'));
        time = time.substring(time.indexOf(':')+1);
        try{
            boolean am = true;
            int hour = Integer.valueOf(hourStr);
            if (hour > 12){
                hour -= 12;
                am = false;
            }

            return hour + ":" + time.substring(0, time.indexOf(":")) + (am ? " am" : " pm");
        }
        catch (NumberFormatException nfx){
            nfx.printStackTrace();
            return "";
        }
    }

    public abstract String getTitle();
    public abstract Goal getGoal();
    public abstract String getGoalTitle();
}
