package org.tndata.android.compass.model;

import android.support.annotation.DrawableRes;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Data holder for the data to be displayed in the feed.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class FeedData{
    @SerializedName("next_action")
    private UserAction mNextAction;

    @SerializedName("action_feedback")
    private ActionFeedback mActionFeedback;
    @SerializedName("progress")
    private Progress mProgress;

    @SerializedName("upcoming_actions")
    private List<UserAction> mUpcomingActions;
    @SerializedName("suggestions")
    private List<Goal> mSuggestions;


    /**
     * Sets the next action.
     *
     * @param nextAction the next action.
     */
    public void setNextAction(UserAction nextAction){
        mNextAction = nextAction;
    }

    /**
     * Gets the next action.
     *
     * @return the next action.
     */
    public UserAction getNextAction(){
        return mNextAction;
    }

    /**
     * Sets the feedback title.
     *
     * @param feedbackTitle the feedback title
     */
    public void setFeedbackTitle(String feedbackTitle){
        mActionFeedback.mFeedbackTitle = feedbackTitle;
    }

    /**
     * Gets the feedback title.
     *
     * @return the feedback title.
     */
    public String getFeedbackTitle(){
        return mActionFeedback.mFeedbackTitle;
    }

    /**
     * Sets the feedback subtitle.
     *
     * @param feedbackSubtitle the feedback subtitle.
     */
    public void setFeedbackSubtitle(String feedbackSubtitle){
        mActionFeedback.mFeedbackSubtitle = feedbackSubtitle;
    }

    /**
     * Gets the feedback subtitle.
     *
     * @return the feedback subtitle.
     */
    public String getFeedbackSubtitle(){
        return mActionFeedback.mFeedbackSubtitle;
    }

    /**
     * Sets the feedback icon id.
     *
     * @param feedbackIconId the feedback icon id.
     */
    public void setFeedbackIconId(int feedbackIconId){
        mActionFeedback.mFeedbackIconId = feedbackIconId;
    }

    /**
     * Gets the feedback icon resource id.
     *
     * @return the feedback icon resource id.
     */
    @DrawableRes
    public int getFeedbackIcon(){
        switch (mActionFeedback.mFeedbackIconId){
            case 1:
                return R.drawable.feedback1;
            case 2:
                return R.drawable.feedback2;
            case 3:
                return R.drawable.feedback3;
            case 4:
                return R.drawable.feedback4;
            default:
                return 0;
        }
    }

    /**
     * Sets the total actions.
     *
     * @param totalActions the total actions.
     */
    public void setTotalActions(int totalActions){
        mProgress.mTotalActions = totalActions;
    }

    /**
     * Sets the completed actions.
     *
     * @param completedActions the completed actions.
     */
    public void setCompletedActions(int completedActions){
        mProgress.mCompletedActions = completedActions;
    }

    /**
     *
     * Sets the progress percentage of completed actions.
     *
     * @param percentage progress percentage of completed actions.
     */
    public void setProgressPercentage(int percentage){
        mProgress.mPercentage = percentage;
    }

    /**
     * Gets the completed actions,
     *
     * @return the completed actions.
     */
    public int getCompletedActions(){
        return mProgress.mCompletedActions;
    }

    /**
     * Gets the total actions.
     *
     * @return the total actions.
     */
    public int getTotalActions(){
        return mProgress.mTotalActions;
    }

    /**
     * Gets the progress percentage of completed actions.
     *
     * @return the progress percentage of completed actions.
     */
    public int getProgress(){
        return mProgress.mPercentage;
    }

    /**
     * Gets the progress percentage as a fraction.
     *
     * @return the progress percentage as a fraction.
     */
    public String getProgressFraction(){
        return mProgress.mCompletedActions + "/" + mProgress.mTotalActions;
    }

    /**
     * Sets the list of upcoming actions.
     *
     * @param upcomingActions the list of upcoming actions.
     */
    public void setUpcomingActions(List<UserAction> upcomingActions){
        mUpcomingActions = upcomingActions;
    }

    /**
     * Gets the list of upcoming actions.
     *
     * @return the list of upcoming actions.
     */
    public List<UserAction> getUpcomingActions(){
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

    public void sync(UserData userData){
        if (mNextAction != null && mNextAction.getAction() != null){
            mNextAction = userData.getAction(mNextAction);
            if (!mUpcomingActions.isEmpty()){
                mUpcomingActions.remove(0);
            }
        }
        else{
            mNextAction = null;
        }
        List<UserAction> upcomingActions = new ArrayList<>();
        for (UserAction userAction:mUpcomingActions){
            upcomingActions.add(userData.getAction(userAction));
        }
        mUpcomingActions = upcomingActions;

        //Assign colors to suggestions
        for (Goal suggestion:mSuggestions){
            for (Integer categoryId:suggestion.getCategories()){
                if (userData.getCategories().containsKey(categoryId)){
                    suggestion.setColor(userData.getCategories().get(categoryId).getColor());
                    break;
                }
            }
        }
    }


    private class ActionFeedback{
        @SerializedName("title")
        private String mFeedbackTitle;
        @SerializedName("subtitle")
        private String mFeedbackSubtitle;
        @SerializedName("icon")
        private int mFeedbackIconId;
    }

    private class Progress{
        @SerializedName("total")
        private int mTotalActions;
        @SerializedName("completed")
        private int mCompletedActions;
        @SerializedName("progress")
        private int mPercentage;
    }
}
