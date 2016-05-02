package org.tndata.android.compass.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


/**
 * Model class for user categories.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class UserCategory extends UserContent implements Serializable{
    private static final long serialVersionUID = 1751646542285854670L;

    public static final String TYPE = "usercategory";


    //API provided
    @SerializedName("category")
    private CategoryContent mCategory;


    /*---------*
     * SETTERS *
     *---------*/

    public void setCategory(CategoryContent category){
        this.mCategory = category;
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

    public String getTitle(){
        return mCategory.getTitle();
    }

    public String getDescription(){
        return mCategory.getDescription();
    }

    public String getHTMLDescription(){
        return mCategory.getHTMLDescription();
    }

    public String getIconUrl(){
        return mCategory.getIconUrl();
    }

    public boolean isPackagedContent(){
        return mCategory.isPackagedContent();
    }

    public String getColor(){
        return mCategory.getColor();
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

    }

    @Override
    public String toString(){
        return "UserCategory #" + getId() + " (" + mCategory.toString() + ")";
    }
}
