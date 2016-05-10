package org.tndata.android.compass.model;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Date;


/**
 * Model superclass for anything that can be classified as an action. Abstracts the trigger,
 * next reminder and title of the parent/primary goal.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public abstract class Action extends UserContent implements Comparable<Action>{
    @SerializedName("trigger")
    private Trigger mTrigger;
    @SerializedName("next_reminder")
    private String mNextReminder;
    @SerializedName("goal_title")
    private String mGoalTitle;


    protected Action(){

    }


    /*---------*
     * SETTERS *
     *---------*/

    /**
     * Trigger setter.
     *
     * @param trigger the new trigger.
     */
    public void setTrigger(Trigger trigger){
        mTrigger = trigger;
    }


    /*---------*
     * GETTERS *
     *---------*/

    /**
     * Trigger getter.
     *
     * @return the trigger.
     */
    public Trigger getTrigger(){
        return mTrigger != null ? mTrigger : new Trigger();
    }

    /**
     * Gets the raw next reminder string.
     *
     * @return a string containing a formatted date with the next reminder time.
     */
    public String getNextReminder(){
        return mNextReminder != null ? mNextReminder : "";
    }

    /**
     * Gets the next reminder date.
     *
     * @return a Date object with the date of the next reminder.
     */
    public Date getNextReminderDate(){
        String year = mNextReminder.substring(0, mNextReminder.indexOf("-"));
        String temp = mNextReminder.substring(mNextReminder.indexOf("-")+1);
        String month = temp.substring(0, temp.indexOf("-"));
        temp = temp.substring(temp.indexOf("-")+1);
        String day = temp.substring(0, temp.indexOf(" "));

        String time = mNextReminder.substring(mNextReminder.indexOf(' ')+1);
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

    /**
     * Goal title getter.
     *
     * @return the goal title.
     */
    public String getGoalTitle(){
        return mGoalTitle != null ? mGoalTitle : "";
    }


    /*-------------*
     * CONVENIENCE *
     *-------------*/

    /**
     * Tells whether the action has a trigger.
     *
     * @return true if the action has a trigger, false otherwise.
     */
    public boolean hasTrigger(){
        return mTrigger != null;
    }

    /**
     * Tells whether the acton's trigger (if any) is enabled.
     *
     * @return true if the action has a trigger and it is enabled, false otherwise.
     */
    public boolean isTriggerEnabled(){
        return hasTrigger() && mTrigger.isEnabled();
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

    @Override
    public void writeToParcel(Parcel dest, int flags){
        super.writeToParcel(dest, flags);
        dest.writeParcelable(getTrigger(), flags);
        dest.writeString(getNextReminder());
        dest.writeString(getGoalTitle());
    }

    protected Action(Parcel src){
        super(src);
        mTrigger = src.readParcelable(Trigger.class.getClassLoader());
        mNextReminder = src.readString();
        mGoalTitle = src.readString();
    }

    public abstract String getTitle();
}
