package org.tndata.android.compass.model;

import java.util.List;


/**
 * Data holder for the data to be displayed in the feed.
 *
 * @author Ismael Alonso
 * @version 1.0.0
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


    /**
     * Sets the next action.
     *
     * @param nextAction the next action.
     */
    public void setNextAction(Action nextAction){
        mNextAction = nextAction;
    }

    /**
     * Gets the next action.
     *
     * @return the next action.
     */
    public Action getNextAction(){
        return mNextAction;
    }

    /**
     * Sets the feedback title.
     *
     * @param feedbackTitle the feedback title
     */
    public void setFeedbackTitle(String feedbackTitle){
        mFeedbackTitle = feedbackTitle;
    }

    /**
     * Gets the feedback title.
     *
     * @return the feedback title.
     */
    public String getFeedbackTitle(){
        return mFeedbackTitle;
    }

    /**
     * Sets the feedback subtitle.
     *
     * @param feedbackSubtitle the feedback subtitle.
     */
    public void setFeedbackSubtitle(String feedbackSubtitle){
        mFeedbackSubtitle = feedbackSubtitle;
    }

    /**
     * Gets the feedback subtitle.
     *
     * @return the feedback subtitle.
     */
    public String getFeedbackSubtitle(){
        return mFeedbackSubtitle;
    }

    /**
     * Sets the total actions.
     *
     * @param totalActions the total actions.
     */
    public void setTotalActions(int totalActions){
        mTotalActions = totalActions;
    }

    /**
     * Sets the completed actions.
     *
     * @param completedActions the completed actions.
     */
    public void setCompletedActions(int completedActions){
        mCompletedActions = completedActions;
    }

    /**
     *
     * Sets the progress percentage of completed actions.
     *
     * @param percentage progress percentage of completed actions.
     */
    public void setProgressPercentage(int percentage){
        mPercentage = percentage;
    }

    /**
     * Gets the completed actions,
     *
     * @return the completed actions.
     */
    public int getCompletedActions(){
        return mCompletedActions;
    }

    /**
     * Gets the total actions.
     *
     * @return the total actions.
     */
    public int getTotalActions(){
        return mTotalActions;
    }

    /**
     * Gets the progress percentage of completed actions.
     *
     * @return the progress percentage of completed actions.
     */
    public int getProgress(){
        return mPercentage;
    }

    /**
     * Gets the progress percentage as a fraction.
     *
     * @return the progress percentage as a fraction.
     */
    public String getProgressFraction(){
        return mCompletedActions + "/" + mTotalActions;
    }

    /**
     * Sets the list of upcoming actions.
     *
     * @param upcomingActions the list of upcoming actions.
     */
    public void setUpcomingActions(List<Action> upcomingActions){
        mUpcomingActions = upcomingActions;
    }

    /**
     * Gets the list of upcoming actions.
     *
     * @return the list of upcoming actions.
     */
    public List<Action> getUpcomingActions(){
        return mUpcomingActions;
    }

    /**
     * Sets the list of suggestions.
     *
     * @param suggestions the list of suggerstions.
     */
    public void setSuggestions(List<Goal> suggestions){
        mSuggestions = suggestions;
    }

    /**
     * Gets the list of suggestions.
     *
     * @return the list of suggestions.
     */
    public List<Goal> getSuggestions(){
        return mSuggestions;
    }
}
