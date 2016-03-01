package org.tndata.android.compass.model;

import com.google.gson.annotations.SerializedName;


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


    public float getCompletedFraction(){
        if (mTotalActions == 0){
            return 0;
        }
        return (float)mCompletedActions/mTotalActions;
    }

    public float getSnoozedFraction(){
        if (mTotalActions == 0){
            return 0;
        }
        return (float)mSnoozedActions/mTotalActions;
    }

    public float getDismissedFraction(){
        if (mTotalActions == 0){
            return 0;
        }
        return (float)mDismissedActions/mTotalActions;
    }

    @Override
    protected String getType(){
        return TYPE;
    }
}
