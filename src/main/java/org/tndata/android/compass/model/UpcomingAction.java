package org.tndata.android.compass.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


/**
 * Created by isma on 3/10/16.
 */
public class UpcomingAction implements Parcelable{
    public static final String API_TYPE = "upcoming_item";

    @SerializedName("action_id")
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
    @SerializedName("category_id")
    private long mCategoryId;
    @SerializedName("type")
    private String mType;


    public long getId(){
        return mId;
    }

    public long getGoalId(){
        return mGoalId;
    }

    public long getCategoryId(){
        return mCategoryId;
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

    @Override
    public String toString(){
        String result = "";
        if (isUserAction()){
            result = "UserAction ";
        }
        else if (isCustomAction()){
            result = "CustomAction ";
        }
        result += "#" + mId + ": " + mTitle;
        return result;
    }

    @Override
    public boolean equals(Object o){
        if (o == null || !(o instanceof UpcomingAction)){
            return false;
        }
        UpcomingAction action = (UpcomingAction)o;
        return mType.equals(action.mType) && mId == action.mId;
    }

    //TODO is the editor going to nag me if I combine this with equals()?
    @SuppressWarnings("RedundantIfStatement")
    public boolean is(Action action){
        if (isUserAction() && action instanceof UserAction){
            return mId == action.getId();
        }
        if (isCustomAction() && action instanceof CustomAction){
            return mId == action.getId();
        }
        return false;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeLong(mId);
        dest.writeString(mTitle);
        dest.writeString(mGoalTitle);
        dest.writeString(mTrigger);
        dest.writeString(mColor);
        dest.writeByte((byte)(mEditable ? 1 : 0));
        dest.writeLong(mGoalId);
        dest.writeLong(mCategoryId);
        dest.writeString(mType);
    }

    public static final Parcelable.Creator<UpcomingAction> CREATOR = new Parcelable.Creator<UpcomingAction>(){
        @Override
        public UpcomingAction createFromParcel(Parcel in){
            return new UpcomingAction(in);
        }

        @Override
        public UpcomingAction[] newArray(int size){
            return new UpcomingAction[size];
        }
    };

    /**
     * Constructor to create from parcel.
     *
     * @param in the parcel where the object is stored.
     */
    private UpcomingAction(Parcel in){
        mId = in.readLong();
        mTitle = in.readString();
        mGoalTitle = in.readString();
        mTrigger = in.readString();
        mColor = in.readString();
        mEditable = in.readByte() == 1;
        mGoalId = in.readLong();
        mCategoryId = in.readLong();
        mType = in.readString();
    }
}
