package org.tndata.compass.model;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * Model superclass for anything that can be classified as an action. Abstracts the trigger,
 * next reminder and title of the parent/primary goal.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public abstract class Action extends UserContent implements Comparable<Action>{
    private static final String TAG = "Action";


    @SerializedName("trigger")
    private Trigger mTrigger;
    @SerializedName("next_reminder")
    private String mNextReminder;
    @SerializedName("goal_title")
    private String mGoalTitle;


    //TODO study whether this is actually necessary
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
     * Action title getter.
     *
     * @return the title of the action.
     */
    public abstract String getTitle();

    /**
     * Gets the id of the parent primary Goal.
     *
     * @return the id of the parent primary Goal.
     */
    public abstract long getParentId();

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
        Log.d("Action", "Raw date: " + mNextReminder);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ssZ", Locale.getDefault());
        try{
            return format.parse(mNextReminder);
        }
        catch (ParseException px){
            px.printStackTrace();
            //Returning a date a year from now should dismiss the action where this method is used
            /*Date date = new Date();
            date.setTime(date.getTime() + 365L*24L*60L*60L*1000L);
            return date;*/
            return null;
        }
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
        Date thisReminder = getNextReminderDate();
        Date anotherReminder = another.getNextReminderDate();
        if (thisReminder == null && anotherReminder == null){
            return getTitle().compareTo(another.getTitle());
        }
        else if (thisReminder == null){
            return 1;
        }
        else if (anotherReminder == null){
            return -1;
        }
        else{
            int reminders = thisReminder.compareTo(anotherReminder);
            if (reminders == 0){
                return getTitle().compareTo(another.getTitle());
            }
            else{
                return reminders;
            }
        }
    }

    public boolean happensBefore(Action action){
        return compareTo(action) < 0;
    }


    /**
     * Tells whether a particular action is scheduled to happen between now and the end of the day.
     *
     * @return true if the action is happening today, false otherwise.
     */
    public boolean happensToday(){
        if (getNextReminder().equals("")){
            Log.i(TAG, "happensToday(): next reminder is not set, " + toString());
            return false;
        }

        Calendar tomorrowCalendar = Calendar.getInstance();
        tomorrowCalendar.set(Calendar.DAY_OF_MONTH, tomorrowCalendar.get(Calendar.DAY_OF_MONTH)+1);
        tomorrowCalendar.set(Calendar.HOUR_OF_DAY, 0);
        tomorrowCalendar.set(Calendar.MINUTE, 0);
        tomorrowCalendar.set(Calendar.SECOND, 0);
        tomorrowCalendar.set(Calendar.MILLISECOND, 0);

        Date now = Calendar.getInstance().getTime();
        Date tomorrow = tomorrowCalendar.getTime();
        Date actionDate = getNextReminderDate();

        return actionDate.after(now) && actionDate.before(tomorrow);
    }


    /*------------*
     * PARCELABLE *
     *------------*/

    @Override
    public void writeToParcel(Parcel dest, int flags){
        super.writeToParcel(dest, flags);
        dest.writeParcelable(getTrigger(), flags);
        dest.writeString(getNextReminder());
        dest.writeString(getGoalTitle());
    }

    /**
     * Creates an action from a Parcel.
     *
     * @param src the Parcel from where the content is to be extracted.
     */
    protected Action(Parcel src){
        super(src);
        mTrigger = src.readParcelable(Trigger.class.getClassLoader());
        mNextReminder = src.readString();
        mGoalTitle = src.readString();
    }
}
