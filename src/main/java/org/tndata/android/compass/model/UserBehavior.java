package org.tndata.android.compass.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Model class for user behaviors.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class UserBehavior extends TDCBase implements Serializable{
    private static final long serialVersionUID = 774798265423822842L;

    private Behavior behavior;

    private List<UserGoal> user_goals = new ArrayList<>();
    private List<UserAction> user_actions = new ArrayList<>();

    private Progress progress;


    public UserBehavior(Behavior behavior){
        this.behavior = behavior;
    }


    /*---------*
     * SETTERS *
     *---------*/

    public void setGoals(List<UserGoal> goals) {
        this.user_goals = goals;
    }

    public void setActions(List<UserAction> actions){
        this.user_actions = actions;
    }


    /*---------*
     * GETTERS *
     *---------*/

    public Behavior getBehavior(){
        return behavior;
    }

    @Override
    public String getTitle(){
        return behavior.getTitle();
    }

    @Override
    public String getTitleSlug(){
        return behavior.getTitleSlug();
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
        return user_goals;
    }

    public List<UserAction> getActions(){
        return user_actions;
    }

    public Progress getProgress(){
        return progress;
    }


    /*---------*
     * UTILITY *
     *---------*/

    public void addGoal(UserGoal goal){
        if (user_goals == null){
            user_goals = new ArrayList<>();
        }
        if (!user_goals.contains(goal)){
            user_goals.add(goal);
        }
    }

    public void removeGoal(UserGoal goal){
        if (user_goals.contains(goal)){
            user_goals.remove(goal);
        }
    }

    public void addAction(UserAction action){
        if (user_actions == null){
            user_actions = new ArrayList<>();
        }
        if (!user_actions.contains(action)){
            user_actions.add(action);
        }
    }

    public void removeAction(UserAction action){
        if (user_actions.contains(action)){
            user_actions.remove(action);
        }
    }

    @Override
    public String toString(){
        return "UserBehavior #" + getId() + " (" + behavior.toString() + ")";
    }
}
