package org.tndata.android.compass.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.parser.ParserModels;


/**
 * Model class for user actions.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class UserAction extends Action implements ParserModels.ResultSet, UserSelectedContent{
    public static final String TYPE = "useraction";


    //Values retrieved from the API
    @SerializedName("action")
    private ActionContent mAction;
    @SerializedName("primary_goal")
    private long mPrimaryGoalId;
    @SerializedName("primary_category")
    private long mPrimaryCategoryId;

    //These values are retrieved from the API when an Action is POSTed
    @SerializedName("parent_userbehavior")
    private UserBehavior mParentUserBehavior;
    @SerializedName("parent_usergoal")
    private UserGoal mParentUserGoal;
    @SerializedName("parent_usercategory")
    private UserCategory mParentUserCategory;

    //Values set during post-processing
    private UserBehavior mBehavior;
    private UserGoal mPrimaryGoal;
    private UserCategory mPrimaryCategory;


    public UserAction(ActionContent action, UserGoal primaryGoal, UserCategory primaryCategory){
        this.mAction = action;
        this.mPrimaryGoal = primaryGoal;
        this.mPrimaryCategory = primaryCategory;
    }


    /*---------*
     * SETTERS *
     *---------*/

    public void setBehavior(UserBehavior behavior){
        this.mBehavior = behavior;
    }

    public void setPrimaryCategory(UserCategory primaryCategory){
        this.mPrimaryCategory = primaryCategory;
    }

    public void setPrimaryGoal(UserGoal primaryGoal){
        this.mPrimaryGoal = primaryGoal;
    }


    /*---------*
     * GETTERS *
     *---------*/

    public ActionContent getAction(){
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
    public String getGoalTitle(){
        return mPrimaryGoal.getTitle();
    }

    @Override
    public String getDescription(){
        return mAction.getDescription();
    }

    @Override
    public String getHTMLDescription(){
        return mAction.getHTMLDescription();
    }

    public String getMoreInfo(){
        return mAction.getMoreInfo();
    }

    public String getHTMLMoreInfo(){
        return mAction.getHTMLMoreInfo();
    }

    public String getExternalResource(){
        return mAction.getExternalResource();
    }

    public String getExternalResourceName(){
        return mAction.getExternalResourceName();
    }

    @Override
    public String getIconUrl(){
        return mAction.getIconUrl();
    }

    public UserBehavior getBehavior(){
        return mBehavior;
    }

    public long getPrimaryCategoryId(){
        return mPrimaryCategoryId;
    }

    public long getPrimaryGoalId(){
        return mPrimaryGoalId;
    }

    public UserCategory getPrimaryCategory(){
        return mPrimaryCategory;
    }

    public UserGoal getPrimaryGoal(){
        return mPrimaryGoal;
    }

    @Override
    public UserGoal getGoal(){
        return mPrimaryGoal;
    }

    public UserBehavior getParentUserBehavior(){
        return mParentUserBehavior;
    }

    public UserGoal getParentUserGoal(){
        return mParentUserGoal;
    }

    public UserCategory getParentUserCategory(){
        return mParentUserCategory;
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
        return "UserAction #" + getId() + " (" + mAction.toString() + ")";
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeLong(getId());
        dest.writeByte((byte)(isEditable() ? 1 : 0));
        dest.writeParcelable(mAction, flags);
        dest.writeLong(mPrimaryGoalId);
        dest.writeLong(mPrimaryCategoryId);
        dest.writeParcelable(getTrigger(), flags);
        dest.writeByte((byte)(getNextReminder() != null ? 1 : 0));
        if (getNextReminder() != null){
            dest.writeString(getNextReminder());
        }
    }

    public static final Parcelable.Creator<UserAction> CREATOR = new Parcelable.Creator<UserAction>(){
        @Override
        public UserAction createFromParcel(Parcel in){
            return new UserAction(in);
        }

        @Override
        public UserAction[] newArray(int size){
            return new UserAction[size];
        }
    };

    /**
     * Constructor to create from parcel.
     *
     * @param in the parcel where the object is stored.
     */
    private UserAction(Parcel in){
        setId(in.readLong());
        setEditable(in.readByte() == 1);
        mAction = in.readParcelable(ActionContent.class.getClassLoader());
        mPrimaryGoalId = in.readLong();
        mPrimaryCategoryId = in.readLong();
        setTrigger((Trigger)in.readParcelable(Trigger.class.getClassLoader()));
        if (in.readByte() == 1){
            setNextReminder(in.readString());
        }
    }
}
