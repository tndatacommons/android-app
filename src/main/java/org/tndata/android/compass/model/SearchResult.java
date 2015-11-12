package org.tndata.android.compass.model;

/**
 * Data model for a search result.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class SearchResult{
    private static final String CATEGORY_TYPE = "category";
    private static final String GOAL_TYPE = "goal";
    private static final String BEHAVIOR_TYPE = "behavior";
    private static final String ACTION_TYPE = "action";


    private String object_type;
    private int id;
    private String title;
    private String description;
    private String text;
    private String updated_on;


    public boolean isCategory(){
        return object_type.equals(CATEGORY_TYPE);
    }

    public boolean isGoal(){
        return object_type.equals(GOAL_TYPE);
    }

    public boolean isBehavior(){
        return object_type.equals(BEHAVIOR_TYPE);
    }

    public boolean isAction(){
        return object_type.equals(ACTION_TYPE);
    }

    public int getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }
}
