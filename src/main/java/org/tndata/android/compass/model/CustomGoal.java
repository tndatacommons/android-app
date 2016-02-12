package org.tndata.android.compass.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Model for a custom goal.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CustomGoal extends Goal implements Serializable{
    private static final long serialVersionUID = 3564217130245211923L;

    public static final String TYPE = "customgoal";


    //API delivered values
    @SerializedName("title")
    private String mTitle;

    //Set values
    private List<CustomAction> mActions;


    public CustomGoal(String title){
        mTitle = title;
        mActions = new ArrayList<>();
    }

    @Override
    public long getContentId(){
        return getId();
    }

    @Override
    public void init(){
        if (mActions == null){
            mActions = new ArrayList<>();
        }
    }

    public void setTitle(String title){
        mTitle = title;
    }

    @Override
    public String getTitle(){
        return mTitle;
    }

    @Override
    protected String getType(){
        return TYPE;
    }

    @Override
    public String getIconUrl(){
        return "";
    }

    @Override
    public String getColor(Context context){
        return String.format("#%06X", 0xFFFFFF & context.getResources().getColor(R.color.grow_primary));
    }

    @Override
    public boolean isEditable(){
        return true;
    }

    public List<CustomAction> getActions(){
        return mActions;
    }

    public void addAction(CustomAction action){
        mActions.add(action);
    }

    public void removeAction(CustomAction action){
        mActions.remove(action);
    }

    @Override
    public String toString(){
        return "CustomGoal #" + getId() + ": " + mTitle;
    }
}