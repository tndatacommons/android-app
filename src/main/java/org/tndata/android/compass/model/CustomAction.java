package org.tndata.android.compass.model;

import com.google.gson.annotations.SerializedName;


/**
 * Model object for custom actions.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CustomAction extends Action{
    public static final String TYPE = "custom_action";


    //API delivered values
    @SerializedName("title")
    private String mTitle;
    @SerializedName("customgoal")
    private int mCustomGoalId;
    @SerializedName("notification_text")
    private String mNotificationText;


    //Post processing set values
    private CustomGoal mGoal;


    public int getCustomGoalId(){
        return mCustomGoalId;
    }

    public String getNotificationText(){
        return mNotificationText;
    }

    @Override
    public String getTitle(){
        return mTitle;
    }

    @Override
    public String getGoalTitle(){
        return mGoal.getTitle();
    }

    public void setGoal(CustomGoal goal){
        mGoal = goal;
    }

    public CustomGoal getGoal(){
        return mGoal;
    }

    @Override
    protected String getType(){
        return TYPE;
    }

    @Override
    public void init(){
        //Unused
    }
}
