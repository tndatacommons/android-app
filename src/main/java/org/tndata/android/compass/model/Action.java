package org.tndata.android.compass.model;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Date;


/**
 * Model superclass for anything that can be classified as an action.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public abstract class Action extends UserContent implements Comparable<Action>{
    @SerializedName("trigger")
    private Trigger mTrigger;
    @SerializedName("next_reminder")
    private String mNextReminder;


    protected Action(){

    }

    public void setTrigger(Trigger trigger){
        mTrigger = trigger;
    }

    public Trigger getTrigger(){
        return mTrigger != null ? mTrigger : new Trigger();
    }

    public boolean hasTrigger(){
        return mTrigger != null;
    }

    public boolean isTriggerEnabled(){
        return mTrigger.isEnabled();
    }

    public String getNextReminder(){
        return mNextReminder != null ? mNextReminder : "";
    }

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
    }

    protected Action(Parcel src){
        super(src);
        mTrigger = src.readParcelable(Trigger.class.getClassLoader());
        mNextReminder = src.readString();
    }

    public abstract String getTitle();
}
