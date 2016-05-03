package org.tndata.android.compass.model;

import android.content.Context;
import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.R;

import java.util.ArrayList;
import java.util.List;


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

    //Values set during post-processing
    private UserCategory mPrimaryCategory;
    private List<UserCategory> mUserCategories = new ArrayList<>();
    private List<UserBehavior> mUserBehaviors = new ArrayList<>();


    public UserGoal(TDCGoal goal, UserCategory primaryCategory){
        this.mGoal = goal;
        this.mPrimaryCategory = primaryCategory;
    }


    /*---------*
     * SETTERS *
     *---------*/

    public void setPrimaryCategoryId(int primaryCategory){
        this.mPrimaryCategoryId = primaryCategory;
    }

    public void setPrimaryCategory(UserCategory primaryCategory){
        this.mPrimaryCategory = primaryCategory;
    }

    public void setCategories(List<UserCategory> categories){
        this.mUserCategories = categories;
    }

    public void setBehaviors(List<UserBehavior> behaviors){
        this.mUserBehaviors = behaviors;
    }


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

    @Override
    public String getIconUrl(){
        return mGoal.getIconUrl();
    }

    @Override
    public String getColor(Context context){
        if (mPrimaryCategory == null){
            return String.format("#%06X", 0xFFFFFF & context.getResources().getColor(R.color.primary));
        }
        return mPrimaryCategory.getColor();
    }

    public long getPrimaryCategoryId(){
        return mPrimaryCategoryId;
    }

    public UserCategory getPrimaryCategory(){
        return mPrimaryCategory;
    }

    public List<UserCategory> getCategories(){
        return mUserCategories;
    }

    public List<UserBehavior> getBehaviors() {
        return mUserBehaviors;
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
        if (mUserCategories == null){
            mUserCategories = new ArrayList<>();
        }
        if (mUserBehaviors == null){
            mUserBehaviors = new ArrayList<>();
        }
    }

    public void addBehavior(UserBehavior behavior){
        if (!mUserBehaviors.contains(behavior)){
            mUserBehaviors.add(behavior);
        }
    }

    public void removeBehavior(UserBehavior behavior){
        if (mUserBehaviors.contains(behavior)){
            mUserBehaviors.remove(behavior);
        }
    }

    public void addCategory(UserCategory category){
        if (!mUserCategories.contains(category)){
            mUserCategories.add(category);
        }
    }

    public void removeCategory(UserCategory category){
        if (mUserCategories.contains(category)){
            mUserCategories.remove(category);
        }
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
