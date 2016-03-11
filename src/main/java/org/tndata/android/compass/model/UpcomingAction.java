package org.tndata.android.compass.model;

import com.google.gson.annotations.SerializedName;


/**
 * Created by isma on 3/10/16.
 */
public class UpcomingAction{
    public static final String API_TYPE = "upcoming_item";

    @SerializedName("acton_id")
    private long mId;
    @SerializedName("action")
    private String mTitle;
    @SerializedName("goal")
    private String mGoalTitle;
    @SerializedName("trigger")
    private String mTrigger;
    @SerializedName("category_color")
    private String mColor;
    @SerializedName("editable")
    private boolean mEditable;
    @SerializedName("goal_id")
    private long mGoalId;
    @SerializedName("type")
    private String mType;


    public long getId(){
        return mId;
    }

    public long getGoalId(){
        return mGoalId;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getGoalTitle(){
        return mGoalTitle;
    }

    public String getTriggerDisplay(){
        String time = mTrigger.substring(mTrigger.indexOf(' ')+1, mTrigger.lastIndexOf('-'));
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

    public boolean isEditable(){
        return true;
    }

    public boolean isUserAction(){
        return mType.equals("useraction");
    }

    public boolean isCustomAction(){
        return mType.equals("customaction");
    }
}
