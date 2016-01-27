package org.tndata.android.compass.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.feed.DisplayableGoal;

import java.util.ArrayList;
import java.util.List;


/**
 * Model for a custom goal.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CustomGoal extends Goal{
    public static final String TYPE = "customgoal";


    //API delivered values
    @SerializedName("title")
    private String mTitle;

    //Set values
    private List<CustomAction> mActions;


    @Override
    public void init(){
        if (mActions == null){
            mActions = new ArrayList<>();
        }
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

    public List<CustomAction> getActions(){
        return mActions;
    }

    public void addAction(CustomAction action){
        mActions.add(action);
    }

    public void removeAction(CustomAction action){
        mActions.remove(action);
    }
}
