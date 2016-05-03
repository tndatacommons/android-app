package org.tndata.android.compass.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;


/**
 * Model class for user goals.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class UserGoal extends Goal{
    public static final String TYPE = "usergoal";


    //Values retrieved from the API
    @SerializedName("goal")
    private TDCGoal mGoal;
    @SerializedName("primary_category")
    private long mPrimaryCategoryId;


    /*---------*
     * GETTERS *
     *---------*/

    public TDCGoal getGoal(){
        return mGoal;
    }

    @Override
    public long getContentId(){
        return mGoal.getId();
    }

    @Override
    public String getTitle(){
        return mGoal.getTitle();
    }

    public String getDescription(){
        return mGoal.getDescription();
    }

    public String getHTMLDescription(){
        return mGoal.getHTMLDescription();
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
    public void init(){

    }

    @Override
    public String toString(){
        return "UserGoal #" + getId() + " (" + mGoal.toString() + ")";
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        super.addToParcel(dest, flags);
        dest.writeParcelable(mGoal, flags);
        dest.writeLong(mPrimaryCategoryId);
    }

    public static final Creator<UserGoal> CREATOR = new Creator<UserGoal>(){
        @Override
        public UserGoal createFromParcel(Parcel source){
            return new UserGoal(source);
        }

        @Override
        public UserGoal[] newArray(int size){
            return new UserGoal[size];
        }
    };

    private UserGoal(Parcel src){
        super(src);
        mGoal = src.readParcelable(TDCGoal.class.getClassLoader());
        mPrimaryCategoryId = src.readLong();
    }
}
