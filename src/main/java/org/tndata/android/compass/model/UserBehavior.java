package org.tndata.android.compass.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.parser.ParserModels;

import java.util.ArrayList;
import java.util.List;


/**
 * Model class for user behaviors.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class UserBehavior extends UserContent implements Parcelable, ParserModels.ResultSet{
    public static final String TYPE = "userbehavior";


    //Values retrieved from the API
    @SerializedName("behavior")
    private TDCBehavior mBehavior;

    @SerializedName("parent_usergoal")
    private UserGoal mParentUserGoal;
    @SerializedName("parent_usercategory")
    private UserCategory mParentUserCategory;

    //Values set during post-processing
    private List<UserGoal> userGoals = new ArrayList<>();
    private List<UserAction> userActions = new ArrayList<>();


    public UserBehavior(TDCBehavior behavior){
        this.mBehavior = behavior;
    }


    /*---------*
     * SETTERS *
     *---------*/

    public void setGoals(List<UserGoal> goals) {
        this.userGoals = goals;
    }

    public void setActions(List<UserAction> actions){
        this.userActions = actions;
    }


    /*---------*
     * GETTERS *
     *---------*/

    public TDCBehavior getBehavior(){
        return mBehavior;
    }

    @Override
    public long getContentId(){
        return mBehavior.getId();
    }

    public String getTitle(){
        return mBehavior.getTitle();
    }

    public String getDescription(){
        return mBehavior.getDescription();
    }

    public String getHTMLDescription(){
        return mBehavior.getHTMLDescription();
    }

    public String getIconUrl(){
        return mBehavior.getIconUrl();
    }

    public List<UserGoal> getGoals(){
        return userGoals;
    }

    public List<UserAction> getActions(){
        return userActions;
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
    public void init(){
        if (userGoals == null){
            userGoals = new ArrayList<>();
        }
        if (userActions == null){
            userActions = new ArrayList<>();
        }
    }

    public void addGoal(UserGoal goal){
        if (!userGoals.contains(goal)){
            userGoals.add(goal);
        }
    }

    public void removeGoal(UserGoal goal){
        if (userGoals.contains(goal)){
            userGoals.remove(goal);
        }
    }

    public void addAction(UserAction action){
        if (!userActions.contains(action)){
            userActions.add(action);
        }
    }

    public void removeAction(UserAction action){
        if (userActions.contains(action)){
            userActions.remove(action);
        }
    }

    @Override
    public String toString(){
        return "UserBehavior #" + getId() + " (" + mBehavior.toString() + ")";
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        super.addToParcel(dest, flags);
        dest.writeParcelable(mBehavior, flags);
    }

    public static final Creator<UserBehavior> CREATOR = new Creator<UserBehavior>(){
        @Override
        public UserBehavior createFromParcel(Parcel source){
            return new UserBehavior(source);
        }

        @Override
        public UserBehavior[] newArray(int size){
            return new UserBehavior[size];
        }
    };

    private UserBehavior(Parcel src){
        super(src);
        mBehavior = src.readParcelable(TDCBehavior.class.getClassLoader());
    }
}
