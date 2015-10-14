package org.tndata.android.compass.model;

import java.util.List;


/**
 * Created by isma on 9/28/15.
 */
public class FeedData{
    private Action mNextAction;

    private String mFeedbackTitle;
    private String mFeedbackSubtitle;

    private int mTotalActions;
    private int mCompletedActions;
    private int mPercentage;

    private List<Action> mUpcomingActions;
    private List<Goal> mSuggestions;


    public void setNextAction(Action nextAction){
        mNextAction = nextAction;
    }

    public Action getNextAction(){
        return mNextAction;
    }

    public void setFeedbackTitle(String feedbackTitle){
        mFeedbackTitle = feedbackTitle;
    }

    public String getFeedbackTitle(){
        return mFeedbackTitle;
    }

    public void setFeedbackSubtitle(String feedbackSubtitle){
        mFeedbackSubtitle = feedbackSubtitle;
    }

    public String getFeedbackSubtitle(){
        return mFeedbackSubtitle;
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

    public int getCompletedActions(){
        return mCompletedActions;
    }

    public int getTotalActions(){
        return mTotalActions;
    }

    public int getProgress(){
        return mPercentage;
    }

    public String getProgressFraction(){
        return mCompletedActions + "/" + mTotalActions;
    }

    public void setUpcomingActions(List<Action> upcomingActions){
        mUpcomingActions = upcomingActions;
    }

    public List<Action> getUpcomingActions(){
        return mUpcomingActions;
    }

    public void setSuggestions(List<Goal> suggestions){
        mSuggestions = suggestions;
    }

    public List<Goal> getSuggestions(){
        return mSuggestions;
    }
}
