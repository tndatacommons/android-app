package org.tndata.android.compass.model;

import java.util.List;


/**
 * Created by isma on 9/28/15.
 */
public class FeedData{
    private Action mNextAction;

    private int mTotalActions;
    private int mCompletedActions;
    private int mPercentage;

    private List<Goal> mUserGoals;


    public void setNextAction(Action nextAction){
        mNextAction = nextAction;
    }

    public Action getNextAction(){
        return mNextAction;
    }

    public void setTotalActions(int totalActions){
        mTotalActions = totalActions;
    }

    public void setCompletedActions(int completedActions){
        mCompletedActions = completedActions;
    }

    public void setProgressPercentage(int percentage){
        mPercentage = percentage;
    }

    public int getProgress(){
        return mPercentage;
    }

    public String getProgressFraction(){
        return mCompletedActions + "/" + mTotalActions;
    }

    public void setUserGoals(List<Goal> userGoals){
        mUserGoals = userGoals;
    }

    public List<Goal> getUserGoals(){
        return mUserGoals;
    }
}
