package org.tndata.android.compass.model;

import android.content.Context;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.feed.DisplayableGoal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Model class for user goals.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class UserGoal extends UserContent implements Serializable, DisplayableGoal{
    private static final long serialVersionUID = 7109406686231550671L;

    //Values retrieved from the API
    //TODO getters
    private Goal goal;

    private int primary_category;

    private Progress progress = new Progress();

    //Values set during post-processing
    private UserCategory primaryCategory;
    private List<UserCategory> userCategories = new ArrayList<>();
    private List<UserBehavior> userBehaviors = new ArrayList<>();


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

    @Override
    public String getColor(Context context){
        if (primaryCategory == null){
            return String.format("#%06X", 0xFFFFFF & context.getResources().getColor(R.color.grow_primary));
        }
        return primaryCategory.getColor();
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

    @Override
    public void init(){
        if (userCategories == null){
            userCategories = new ArrayList<>();
        }
        if (userBehaviors == null){
            userBehaviors = new ArrayList<>();
        }
    }

    public void addBehavior(UserBehavior behavior){
        if (!userBehaviors.contains(behavior)){
            userBehaviors.add(behavior);
        }
    }

    public void removeBehavior(UserBehavior behavior){
        if (userBehaviors.contains(behavior)){
            userBehaviors.remove(behavior);
        }
    }

    public void addCategory(UserCategory category){
        if (!userCategories.contains(category)){
            userCategories.add(category);
        }
    }

    public void removeCategory(UserCategory category){
        if (userCategories.contains(category)){
            userCategories.remove(category);
        }
    }

    @Override
    public String toString(){
        return "UserGoal #" + getId() + " (" + goal.toString() + ")";
    }
}
