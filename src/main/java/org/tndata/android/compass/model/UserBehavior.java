package org.tndata.android.compass.model;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.parser.ParserModels;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Model class for user behaviors.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class UserBehavior
        extends UserContent
        implements
                Serializable,
                ParserModels.ResultSet,
                UserSelectedContent{

    private static final long serialVersionUID = 774798265423822842L;

    public static final String TYPE = "userbehavior";


    //Values retrieved from the API
    @SerializedName("behavior")
    private BehaviorContent mBehavior;
    @SerializedName("progress")
    private Progress mProgress;

    @SerializedName("parent_usergoal")
    private UserGoal mParentUserGoal;
    @SerializedName("parent_usercategory")
    private UserCategory mParentUserCategory;

    //Values set during post-processing
    private List<UserGoal> userGoals = new ArrayList<>();
    private List<UserAction> userActions = new ArrayList<>();


    public UserBehavior(BehaviorContent behavior){
        this.mBehavior = behavior;
    }


    /*---------*
     * SETTERS *
     *---------*/

    public void setProgress(Progress progress){
        this.mProgress = progress;
    }

    public void setGoals(List<UserGoal> goals) {
        this.userGoals = goals;
    }

    public void setActions(List<UserAction> actions){
        this.userActions = actions;
    }


    /*---------*
     * GETTERS *
     *---------*/

    public BehaviorContent getBehavior(){
        return mBehavior;
    }

    @Override
    public long getContentId(){
        return mBehavior.getId();
    }

    @Override
    public String getTitle(){
        return mBehavior.getTitle();
    }

    @Override
    public String getDescription(){
        return mBehavior.getDescription();
    }

    @Override
    public String getHTMLDescription(){
        return mBehavior.getHTMLDescription();
    }

    @Override
    public String getIconUrl(){
        return mBehavior.getIconUrl();
    }

    public List<UserGoal> getGoals(){
        return userGoals;
    }

    public List<UserAction> getActions(){
        return userActions;
    }

    public Progress getProgress(){
        return mProgress;
    }

    public UserGoal getParentUserGoal(){
        return mParentUserGoal;
    }

    public UserCategory getParentUserCategory(){
        return mParentUserCategory;
    }

    @Override
    public boolean isEditable(){
        return mBehavior.isEditable();
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
}
