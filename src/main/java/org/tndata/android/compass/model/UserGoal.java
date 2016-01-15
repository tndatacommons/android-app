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
public class UserGoal extends UserContent implements Serializable{
    private static final long serialVersionUID = 7109406686231550671L;

    //Values retrieved from the API
    //TODO getters
    private Goal goal;

    private int primary_category;

    private Progress progress = new Progress();

    //Values set during post-processing
    private UserCategory primaryCategory;
    private List<UserCategory> userCategories;
    private List<UserBehavior> userBehaviors;


    public UserGoal(Goal goal, UserCategory primaryCategory){
        this.goal = goal;
        this.primaryCategory = primaryCategory;
    }


    /*---------*
     * SETTERS *
     *---------*/

    public void setPrimaryCategoryId(int primaryCategory){
        this.primary_category = primaryCategory;
    }

    public void setPrimaryCategory(UserCategory primaryCategory){
        this.primaryCategory = primaryCategory;
    }

    public void setCategories(List<UserCategory> categories){
        this.userCategories = categories;
    }

    public void setBehaviors(List<UserBehavior> behaviors){
        this.userBehaviors = behaviors;
    }


    /*---------*
     * GETTERS *
     *---------*/

    public Goal getGoal(){
        return goal;
    }

    @Override
    public int getObjectId(){
        return goal.getId();
    }

    @Override
    public String getTitle(){
        return goal.getTitle();
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

    public int getPrimaryCategoryId(){
        return primary_category;
    }

    public UserCategory getPrimaryCategory(){
        return primaryCategory;
    }

    public List<UserCategory> getCategories(){
        return userCategories;
    }

    public List<UserBehavior> getBehaviors() {
        return userBehaviors;
    }

    public Progress getProgress(){
        return progress;
    }


    /*---------*
     * UTILITY *
     *---------*/

    public void addBehavior(UserBehavior behavior){
        if (userBehaviors == null){
            userBehaviors = new ArrayList<>();
        }
        if (!userBehaviors.contains(behavior)){
            userBehaviors.add(behavior);
        }
    }

    public void removeBehavior(UserBehavior behavior){
        if (userBehaviors != null && userBehaviors.contains(behavior)){
            userBehaviors.remove(behavior);
        }
    }

    public void addCategory(UserCategory category){
        if (userCategories == null){
            userCategories = new ArrayList<>();
        }
        if (!userCategories.contains(category)){
            userCategories.add(category);
        }
    }

    public void removeCategory(UserCategory category){
        if (userCategories != null && userCategories.contains(category)){
            userCategories.remove(category);
        }
    }

    @Override
    public String toString(){
        return "UserGoal #" + getId() + " (" + goal.toString() + ")";
    }
}
