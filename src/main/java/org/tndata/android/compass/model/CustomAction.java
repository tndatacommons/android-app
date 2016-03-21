package org.tndata.android.compass.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


/**
 * Model object for custom actions.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CustomAction extends Action{
    public static final String TYPE = "custom_action";


    //API delivered values
    @SerializedName("title")
    private String mTitle;
    @SerializedName("customgoal")
    private long mCustomGoalId;
    @SerializedName("notification_text")
    private String mNotificationText;


    //Post processing set values
    private CustomGoal mGoal;


    public CustomAction(String title, CustomGoal goal){
        mTitle = title;
        mNotificationText = title;
        mGoal = goal;
    }

    public void setTitle(String title){
        mTitle = title;
    }

    @Override
    public long getContentId(){
        return getId();
    }

    @Override
    public void init(){
        //Unused
    }

    public long getCustomGoalId(){
        return mCustomGoalId;
    }

    public String getNotificationText(){
        return mNotificationText;
    }

    @Override
    public String getTitle(){
        return mTitle;
    }

    @Override
    public String getGoalTitle(){
        return mGoal.getTitle();
    }

    public void setGoal(CustomGoal goal){
        mGoal = goal;
    }

    public CustomGoal getGoal(){
        return mGoal;
    }

    @Override
    protected String getType(){
        return TYPE;
    }

    @Override
    public boolean isEditable(){
        return true;
    }

    @Override
    public String toString(){
        return "CustomAction #" + getId() + ": " + mTitle;
    }


    /*------------*
     * PARCELABLE *
     *------------*/

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeLong(getId());
        dest.writeString(mTitle);
        dest.writeLong(mCustomGoalId);
        dest.writeString(mNotificationText);
        dest.writeParcelable(getTrigger(), flags);
        dest.writeByte((byte)(getNextReminder() != null ? 1 : 0));
        if (getNextReminder() != null){
            dest.writeString(getNextReminder());
        }
    }

    public static final Parcelable.Creator<CustomAction> CREATOR = new Parcelable.Creator<CustomAction>(){
        @Override
        public CustomAction createFromParcel(Parcel in){
            return new CustomAction(in);
        }

        @Override
        public CustomAction[] newArray(int size){
            return new CustomAction[size];
        }
    };

    /**
     * Constructor to create from parcel.
     *
     * @param in the parcel where the object is stored.
     */
    private CustomAction(Parcel in){
        setId(in.readLong());
        mTitle = in.readString();
        mCustomGoalId = in.readLong();
        mNotificationText = in.readString();
        setTrigger((Trigger)in.readParcelable(Trigger.class.getClassLoader()));
        if (in.readByte() == 1){
            setNextReminder(in.readString());
        }
    }
}
