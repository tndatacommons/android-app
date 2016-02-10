package org.tndata.android.compass.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


/**
 * Model object for custom actions.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CustomAction extends Action implements Serializable{
    private static final long serialVersionUID = 291946133239951923L;

    public static final String TYPE = "custom_action";


    //API delivered values
    @SerializedName("title")
    private String mTitle;
    @SerializedName("customgoal")
    private long mCustomGoalId;
    @SerializedName("notification_text")
    private String mNotificationText;


    //Post processing set values
    private CustomGoal mGoal;


    public CustomAction(String title){
        mTitle = title;
        mNotificationText = title;
    }

    public void setTitle(String title){
        mTitle = title;
    }

    @Override
    public long getContentId(){
        return getId();
    }

    @Override
    public void init(){
        //Unused
    }

    public long getCustomGoalId(){
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
    public boolean isEditable(){
        return true;
    }

    @Override
    public String toString(){
        return "CustomAction #" + getId() + ": " + mTitle;
    }
}
