package org.tndata.android.compass.model;

import android.content.Context;
import android.widget.ImageView;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.feed.DisplayableGoal;
import org.tndata.android.compass.util.ImageLoader;

import java.io.Serializable;
import java.util.Set;


/**
 * Model class for goals.
 *
 * @author Edited by Ismael Alonso
 * @version 1.0.0
 */
public class GoalContent extends TDCContent implements Serializable, DisplayableGoal{
    private static final long serialVersionUID = 7109406671934150671L;

    public static final String TYPE = "goal";


    @SerializedName("outcome")
    private String mOutcome = "";
    @SerializedName("categories")
    private Set<Long> mCategoryIdSet;
    @SerializedName("behaviors_count")
    private int mBehaviorCount = 0;

    private String mColor;


    /*---------*
     * SETTERS *
     *---------*/

    public void setOutcome(String outcome){
        this.mOutcome = outcome;
    }

    public void setCategories(Set<Long> categories){
        this.mCategoryIdSet = categories;
    }

    public void setBehaviorCount(int behaviorCount){
        this.mBehaviorCount = behaviorCount;
    }

    public void setColor(String color){
        mColor = color;
    }


    /*---------*
     * GETTERS *
     *---------*/

    public String getOutcome(){
        return mOutcome;
    }

    public Set<Long> getCategoryIdSet(){
        return mCategoryIdSet;
    }

    public int getBehaviorCount(){
        return mBehaviorCount;
    }

    public String getColor(Context context){
        if (mColor == null || mColor.isEmpty()){
            return String.format("#%06X", 0xFFFFFF & context.getResources().getColor(R.color.grow_primary));
        }
        return mColor;
    }

    @Override
    protected String getType(){
        return TYPE;
    }


    /*---------*
     * UTILITY *
     *---------*/

    /**
     * Given a Context and an ImageView, load this Goal's icon (if the user has selected
     * no Behaviors) or load the goal's Progress Icons.
     *
     * @param imageView: an ImageView
     */
    public void loadIconIntoView(ImageView imageView){
        String iconUrl = getIconUrl();
        if (iconUrl != null && !iconUrl.isEmpty()){
            ImageLoader.loadBitmap(imageView, iconUrl);
        }
    }

    @Override
    public String toString(){
        return "GoalContent #" + getId() + ": " + getTitle();
    }
}
