package org.tndata.compass.model;

import com.google.gson.annotations.SerializedName;

import org.tndata.compass.model.TDCBase;


/**
 * Daily progress model class. Basic for now.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class DailyProgress extends TDCBase{
    //Types
    public static final String API_TYPE = "dailyprogress";
    public static final String TYPE = "daily_progress";


    @SerializedName("actions_total")
    private int mTotalActions;
    @SerializedName("actions_completed")
    private int mCompletedActions;
    @SerializedName("actions_snoozed")
    private int mSnoozedActions;
    @SerializedName("actions_dismissed")
    private int mDismissedActions;

    @SerializedName("customactions_total")
    private int mTotalCustomActions;
    @SerializedName("customactions_completed")
    private int mCompletedCustomActions;
    @SerializedName("customactions_snoozed")
    private int mSnoozedCustomActions;
    @SerializedName("customactions_dismissed")
    private int mDismissedCustomActions;


    public float getCompletedFraction(){
        int total = mTotalActions + mTotalCustomActions;
        if (total == 0){
            return 0;
        }
        return (float)(mCompletedActions+mSnoozedCustomActions)/total;
    }

    public float getSnoozedFraction(){
        int total = mTotalActions + mTotalCustomActions;
        if (total == 0){
            return 0;
        }
        return (float)(mSnoozedActions+mSnoozedCustomActions)/total;
    }

    public float getDismissedFraction(){
        int total = mTotalActions + mTotalCustomActions;
        if (total == 0){
            return 0;
        }
        return (float)(mDismissedActions+mDismissedCustomActions)/total;
    }

    @Override
    protected String getType(){
        return TYPE;
    }
}
