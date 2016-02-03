package org.tndata.android.compass.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Model class for user categories.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class UserCategory extends UserContent implements Serializable, UserSelectedContent{
    private static final long serialVersionUID = 1751646542285854670L;

    public static final String TYPE = "usercategory";


    //API provided
    @SerializedName("category")
    private CategoryContent mCategory;

    //Set during post-processing
    private List<UserGoal> mUserGoals;


    /*---------*
     * SETTERS *
     *---------*/

    public void setCategory(CategoryContent category){
        this.mCategory = category;
    }

    public void setGoals(List<UserGoal> userGoals){
        this.mUserGoals = userGoals;
    }


    /*---------*
     * GETTERS *
     *---------*/

    public CategoryContent getCategory(){
        return mCategory;
    }

    @Override
    public long getContentId(){
        return mCategory.getId();
    }

    @Override
    public String getTitle(){
        return mCategory.getTitle();
    }

    @Override
    public String getDescription(){
        return mCategory.getDescription();
    }

    @Override
    public String getHTMLDescription(){
        return mCategory.getHTMLDescription();
    }

    @Override
    public String getIconUrl(){
        return mCategory.getIconUrl();
    }

    public boolean isPackagedContent(){
        return mCategory.isPackagedContent();
    }

    public String getColor(){
        return mCategory.getColor();
    }

    public List<UserGoal> getGoals(){
        return mUserGoals;
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
        if (mUserGoals == null){
            mUserGoals = new ArrayList<>();
        }
    }

    public void addGoal(UserGoal goal){
        if (!mUserGoals.contains(goal)){
            mUserGoals.add(goal);
        }
    }

    public void removeGoal(UserGoal goal){
        if (mUserGoals.contains(goal)){
            mUserGoals.remove(goal);
        }
    }

    @Override
    public String toString(){
        return "UserCategory #" + getId() + " (" + mCategory.toString() + ")";
    }
}
