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
public class UserCategory extends TDCBase implements Serializable{
    private static final long serialVersionUID = 1751646542285854670L;

    private Category category;
    private List<UserGoal> user_goals;

    private double progress_value = 0.0;


    public UserCategory(Category category){
        this.category = category;
    }


    /*---------*
     * SETTERS *
     *---------*/

    public void setProgressValue(double value){
        this.progress_value = value;
    }

    public void setUserGoals(List<UserGoal> userGoals){
        this.user_goals = userGoals;
    }


    /*---------*
     * GETTERS *
     *---------*/

    public Category getCategory(){
        return category;
    }

    @Override
    public String getTitle(){
        return category.getTitle();
    }

    @Override
    public String getTitleSlug(){
        return category.getTitleSlug();
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

    public List<UserGoal> getUserGoals(){
        return user_goals;
    }

    public double getProgressValue(){
        return this.progress_value;
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
        if (user_goals != null && user_goals.contains(goal)){
            user_goals.remove(goal);
        }
    }

    @Override
    public String toString(){
        return "UserCategory #" + getId() + " (" + category.toString() + ")";
    }
}
