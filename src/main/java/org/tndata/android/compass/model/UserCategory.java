package org.tndata.android.compass.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Model class for user categories.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class UserCategory extends UserContent implements Serializable{
    private static final long serialVersionUID = 1751646542285854670L;

    private Category category;

    private List<UserGoal> userGoals = new ArrayList<>();


    /*---------*
     * SETTERS *
     *---------*/

    public void setCategory(Category category){
        this.category = category;
    }

    public void setGoals(List<UserGoal> userGoals){
        this.userGoals = userGoals;
    }


    /*---------*
     * GETTERS *
     *---------*/

    public Category getCategory(){
        return category;
    }

    @Override
    public int getObjectId(){
        return category.getId();
    }

    @Override
    public String getTitle(){
        return category.getTitle();
    }

    @Override
    public String getDescription(){
        return category.getDescription();
    }

    @Override
    public String getHTMLDescription(){
        return category.getHTMLDescription();
    }

    @Override
    public String getIconUrl(){
        return category.getIconUrl();
    }

    public boolean isPackagedContent(){
        return category.isPackagedContent();
    }

    public String getColor(){
        return category.getColor();
    }

    public List<UserGoal> getGoals(){
        return userGoals;
    }


    /*---------*
     * UTILITY *
     *---------*/

    @Override
    public void init(){
        if (userGoals == null){
            userGoals = new ArrayList<>();
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

    @Override
    public String toString(){
        return "UserCategory #" + getId() + " (" + category.toString() + ")";
    }
}
