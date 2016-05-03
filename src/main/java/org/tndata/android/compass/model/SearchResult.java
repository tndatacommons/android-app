package org.tndata.android.compass.model;

import com.google.gson.annotations.SerializedName;


/**
 * Data model for a search result.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class SearchResult extends TDCBase{
    private static final String CATEGORY_TYPE = "search-category";
    private static final String GOAL_TYPE = "search-goal";
    private static final String BEHAVIOR_TYPE = "search-behavior";
    private static final String ACTION_TYPE = "search-action";

    public static final String TYPE = "search_result";


    @SerializedName("title")
    private String mTitle;
    @SerializedName("text")
    private String mText;
    @SerializedName("goal")
    private GoalContent mGoal;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("highlighted")
    private String mHighlighted;
    @SerializedName("updated_on")
    private String mUpdatedOn;
    @SerializedName("object_type")
    private String mObjectType;


    @Override
    protected String getType(){
        return TYPE;
    }

    public boolean isCategory(){
        return mObjectType.equals(CATEGORY_TYPE);
    }

    public boolean isGoal(){
        return mObjectType.equals(GOAL_TYPE);
    }

    public boolean isBehavior(){
        return mObjectType.equals(BEHAVIOR_TYPE);
    }

    public boolean isAction(){
        return mObjectType.equals(ACTION_TYPE);
    }

    public String getTitle(){
        return mTitle;
    }

    public String getHighlighted(){
        return mHighlighted;
    }

    public GoalContent getGoal(){
        return mGoal;
    }
}
