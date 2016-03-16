package org.tndata.android.compass.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.ui.ContentContainer;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;


/**
 * Model superclass for anything that can be classified as an action.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public abstract class Action
        extends UserContent
        implements Serializable, ContentContainer.ContainerAction, Comparable<Action>{

    private static final long serialVersionUID = 2919447142568751923L;


    @SerializedName("trigger")
    private Trigger mTrigger;
    @SerializedName("next_reminder")
    private String mNextReminder;


    public void setTrigger(Trigger trigger){
        mTrigger = trigger;
    }

    public Trigger getTrigger(){
        return mTrigger != null ? mTrigger : new Trigger();
    }

    public boolean hasTrigger(){
        return mTrigger != null;
    }

    @Override
    public boolean isTriggerEnabled(){
        return !mTrigger.isDisabled();
    }

    public void setNextReminder(String nextReminder){
        mNextReminder = nextReminder;
    }

    public String getNextReminder(){
        return mNextReminder;
    }

    public Date getNextReminderDate(){
        String year = mNextReminder.substring(0, mNextReminder.indexOf("-"));
        String temp = mNextReminder.substring(mNextReminder.indexOf("-")+1);
        String month = temp.substring(0, temp.indexOf("-"));
        temp = temp.substring(temp.indexOf("-")+1);
        String day = temp.substring(0, temp.indexOf("T"));

        String time = mNextReminder.substring(mNextReminder.indexOf('T')+1);
        String hour = time.substring(0, time.indexOf(':'));
        time = time.substring(time.indexOf(':')+1);
        String minute = time.substring(0, time.indexOf(':'));

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.valueOf(year));
        calendar.set(Calendar.MONTH, Integer.valueOf(month)-1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(day));
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

    @Override
    public int compareTo(@NonNull Action another){
        if (mTrigger == null && another.mTrigger == null){
            return getTitle().compareTo(another.getTitle());
        }
        else if (mTrigger == null){
            return 1;
        }
        else if (another.mTrigger == null){
            return -1;
        }
        else{
            int trigger = mTrigger.compareTo(another.mTrigger);
            if (trigger == 0){
                return getTitle().compareTo(another.getTitle());
            }
            else{
                return trigger;
            }
        }
    }

    public abstract String getTitle();
    public abstract Goal getGoal();
    public abstract String getGoalTitle();
}
