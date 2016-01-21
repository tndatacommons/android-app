package org.tndata.android.compass.model;

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
public class UserBehavior extends UserContent implements Serializable, ParserModels.ResultSet{
    private static final long serialVersionUID = 774798265423822842L;

    //Values retrieved from the API
    private Behavior behavior;

    private Progress progress;

    private UserGoal parent_usergoal;
    private UserCategory parent_usercategory;

    //Values set during post-processing
    private List<UserGoal> userGoals = new ArrayList<>();
    private List<UserAction> userActions = new ArrayList<>();


    public UserBehavior(Behavior behavior){
        this.behavior = behavior;
    }


    /*---------*
     * SETTERS *
     *---------*/

    public void setProgress(Progress progress){
        this.progress = progress;
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

    public Behavior getBehavior(){
        return behavior;
    }

    @Override
    public int getObjectId(){
        return behavior.getId();
    }

    @Override
    public String getTitle(){
        return behavior.getTitle();
    }

    @Override
    public String getDescription(){
        return behavior.getDescription();
    }

    @Override
    public String getHTMLDescription(){
        return behavior.getHTMLDescription();
    }

    @Override
    public String getIconUrl(){
        return behavior.getIconUrl();
    }

    public List<UserGoal> getGoals(){
        return userGoals;
    }

    public List<UserAction> getActions(){
        return userActions;
    }

    public Progress getProgress(){
        return progress;
    }

    public UserGoal getParentUserGoal(){
        return parent_usergoal;
    }

    public UserCategory getParentUserCategory(){
        return parent_usercategory;
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
        return "UserBehavior #" + getId() + " (" + behavior.toString() + ")";
    }
}
