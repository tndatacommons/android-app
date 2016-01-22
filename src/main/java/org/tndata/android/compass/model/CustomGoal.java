package org.tndata.android.compass.model;

import java.util.ArrayList;
import java.util.List;


/**
 * Model for a custom goal.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CustomGoal extends TDCBase{
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
}
