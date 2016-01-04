package org.tndata.android.compass.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Model class for user goals.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class UserGoal extends TDCBase implements Serializable{
    private static final long serialVersionUID = 7109406686231550671L;

    private Goal goal;

    private Category primary_category;
    private List<UserCategory> user_categories = new ArrayList<>();
    private List<UserBehavior> user_behaviors = new ArrayList<>();

    private double progress_value = 0.0;
    private Progress progress = new Progress();


    public UserGoal(Goal goal, Category primaryCategory){
        this.goal = goal;
        this.primary_category = primaryCategory;
    }


    /*---------*
     * SETTERS *
     *---------*/

    public void setCategories(List<UserCategory> categories){
        this.user_categories = categories;
    }

    public void setBehaviors(List<UserBehavior> behaviors){
        this.user_behaviors = behaviors;
    }


    /*---------*
     * GETTERS *
     *---------*/

    public Goal getGoal(){
        return goal;
    }

    public int getGoalId(){
        return goal.getId();
    }

    @Override
    public String getTitle(){
        return goal.getTitle();
    }

    @Override
    public String getTitleSlug(){
        return goal.getTitleSlug();
    }

    @Override
    public String getDescription(){
        return goal.getDescription();
    }

    @Override
    public String getHTMLDescription(){
        return goal.getHTMLDescription();
    }

    @Override
    public String getIconUrl(){
        return goal.getIconUrl();
    }

    public Category getPrimaryCategory(){
        return primary_category;
    }

    public List<UserCategory> getCategories(){
        return user_categories;
    }

    public List<UserBehavior> getBehaviors() {
        return user_behaviors;
    }

    public double getProgressValue() {
        return this.progress_value;
    }

    public Progress getProgress(){
        return progress;
    }


    /*---------*
     * UTILITY *
     *---------*/

    public void addBehavior(UserBehavior behavior){
        if (user_behaviors == null){
            user_behaviors = new ArrayList<>();
        }
        if (!user_behaviors.contains(behavior)){
            user_behaviors.add(behavior);
        }
    }

    public void removeBehavior(UserBehavior behavior){
        if (user_behaviors != null && user_behaviors.contains(behavior)){
            user_behaviors.remove(behavior);
        }
    }

    public void addCategory(UserCategory category){
        if (user_categories == null){
            user_categories = new ArrayList<>();
        }
        if (!user_categories.contains(category)){
            user_categories.add(category);
        }
    }

    public void removeCategory(UserCategory category){
        if (user_categories != null && user_categories.contains(category)){
            user_categories.remove(category);
        }
    }

    @Override
    public String toString(){
        return "UserGoal #" + getId() + " (" + goal.toString() + ")";
    }
}
