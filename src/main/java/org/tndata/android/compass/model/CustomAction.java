package org.tndata.android.compass.model;

import com.google.gson.annotations.SerializedName;


/**
 * Model object for custom actions.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CustomAction extends TDCBase{
    //API delivered values
    @SerializedName("customgoal")
    private int mCustomGoalId;

    @SerializedName("notification_text")
    private String mNotificationText;

    @SerializedName("custom_trigger")
    private Trigger mTrigger;

    @SerializedName("next_trigger_date")
    private String mNextTriggerDate;


    //Post processing set values
    private CustomGoal mGoal;


    public int getCustomGoalId(){
        return mCustomGoalId;
    }

    public String getNotificationText(){
        return mNotificationText;
    }

    public Trigger getCustomTrigger(){
        return mTrigger;
    }

    public String getRawNextReminderDate(){
        return mNextTriggerDate;
    }

    public String getNextReminderDate(){
        if (mNextTriggerDate == null){
            return "";
        }

        String time = mNextTriggerDate.substring(mNextTriggerDate.indexOf('T')+1);
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

    public void setGoal(CustomGoal goal){
        mGoal = goal;
    }

    public CustomGoal getGoal(){
        return mGoal;
    }
}
