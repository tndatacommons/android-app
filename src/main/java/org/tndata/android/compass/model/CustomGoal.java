package org.tndata.android.compass.model;

import android.content.Context;

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
public class CustomGoal extends TDCBase implements DisplayableGoal{
    private List<CustomAction> mActions;


    public List<CustomAction> getActions(){
        return mActions;
    }

    public void init(){
        if (mActions == null){
            mActions = new ArrayList<>();
        }
    }

    public void addAction(CustomAction action){
        mActions.add(action);
    }

    public void removeAction(CustomAction action){
        mActions.remove(action);
    }

    @Override
    public String getIconUrl(){
        return "";
    }

    @Override
    public String getColor(Context context){
        return String.format("#%06X", 0xFFFFFF & context.getResources().getColor(R.color.grow_primary));
    }
}
