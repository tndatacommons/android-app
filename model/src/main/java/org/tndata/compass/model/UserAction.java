package org.tndata.compass.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;


/**
 * Model class for user actions.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class UserAction extends Action{
    public static final String TYPE = "useraction";


    //Values retrieved from the API
    @SerializedName("action")
    private TDCAction mAction;

    @SerializedName("primary_goal")
    private long mPrimaryGoalId;
    @SerializedName("primary_usergoal")
    private long mPrimaryUserGoalId;
    @SerializedName("goal_icon")
    private String mGoalIconUrl;

    @SerializedName("primary_category")
    private long mPrimaryCategoryId;


    /*---------*
     * GETTERS *
     *---------*/

    public TDCAction getAction(){
        return mAction;
    }

    @Override
    public long getContentId(){
        return mAction.getId();
    }

    @Override
    public String getTitle(){
        return mAction.getTitle();
    }

    @Override
    public long getParentId(){
        return mPrimaryGoalId;
    }

    public String getDescription(){
        return mAction.getDescription();
    }

    public String getMoreInfo(){
        return mAction.getMoreInfo();
    }

    public String getExternalResource(){
        return mAction.getExternalResource();
    }

    public String getExternalResourceName(){
        return mAction.getExternalResourceName();
    }

    public String getExternalResourceType() { return mAction.getExternalResourceType(); }

    public String getIconUrl(){
        return mAction.getIconUrl();
    }

    public long getPrimaryGoalId(){
        return mPrimaryGoalId;
    }

    public long getPrimaryUserGoalId(){
        return mPrimaryUserGoalId;
    }

    public String getPrimaryGoalIconUrl(){
        return mGoalIconUrl;
    }

    public long getPrimaryCategoryId(){
        return mPrimaryCategoryId;
    }

    @Override
    protected String getType(){
        return TYPE;
    }


    /*---------*
     * UTILITY *
     *---------*/

    @Override
    /* no-op */
    public void init(){
        //This method is not necessary here
    }

    @Override
    public String toString(){
        return "UserAction #" + getId() + " (" + mAction + "): " + getNextReminder();
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        super.writeToParcel(dest, flags);
        dest.writeParcelable(mAction, flags);
        dest.writeLong(mPrimaryGoalId);
        dest.writeLong(mPrimaryUserGoalId);
        dest.writeString(mGoalIconUrl);
        dest.writeLong(mPrimaryCategoryId);
    }

    public static final Creator<UserAction> CREATOR = new Creator<UserAction>(){
        @Override
        public UserAction createFromParcel(Parcel source){
            return new UserAction(source);
        }

        @Override
        public UserAction[] newArray(int size){
            return new UserAction[size];
        }
    };

    /**
     * Constructor to create from parcel.
     *
     * @param src the parcel where the object is stored.
     */
    private UserAction(Parcel src){
        super(src);
        mAction = src.readParcelable(TDCAction.class.getClassLoader());
        mPrimaryGoalId = src.readLong();
        mPrimaryUserGoalId = src.readLong();
        mGoalIconUrl = src.readString();
        mPrimaryCategoryId = src.readLong();
    }
}
