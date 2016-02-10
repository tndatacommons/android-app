package org.tndata.android.compass.model;

import android.support.annotation.DrawableRes;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.feed.DisplayableGoal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 * Data holder for the data to be displayed in the feed.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class FeedData extends TDCBase{
    public static final String TYPE = "feed_data";


    //API delivered fields
    @SerializedName("action_feedback")
    private ActionFeedback mActionFeedback;
    @SerializedName("progress")
    private Progress mProgress;

    @SerializedName("upcoming_actions")
    private List<Long> mUpcomingActionIds;
    @SerializedName("upcoming_customactions")
    private List<Long> mUpcomingCustomActionIds;

    @SerializedName("suggestions")
    private List<GoalContent> mSuggestions;


    //Fields set during post-processing
    private Action mNextAction;
    private List<Action> mUpcomingActions;
    private List<DisplayableGoal> mGoals;


    @Override
    protected String getType(){
        return TYPE;
    }

    @Override
    public long getId(){
        //This class ain't actual content, but TDCBase requires this, and extending TDCBase is
        //  required to parse JSONArrays
        return -1;
    }

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
     * Gets the list of upcoming actions.
     *
     * @return the list of upcoming actions.
     */
    public List<Action> getUpcomingActions(){
        return mUpcomingActions;
    }

    public List<DisplayableGoal> getGoals(){
        return mGoals;
    }

    /**
     * Gets the list of suggestions.
     *
     * @return the list of suggestions.
     */
    public List<GoalContent> getSuggestions(){
        return mSuggestions;
    }

    /**
     * Adds a goal to the list of goals being displayed.
     *
     * @param goal the goal to be added.
     */
    public void addGoal(DisplayableGoal goal){
        //If the list contained suggestions, clear it and add the goal
        if (mGoals.get(0) instanceof GoalContent){
            mGoals.clear();
            mGoals.add(goal);
        }
        //Otherwise, add the goal in the relevant position
        else{
            for (int i = 0; i < mGoals.size(); i++){
                if (mGoals.get(i).getTitle().compareTo(goal.getTitle()) > 0){
                    mGoals.add(i, goal);
                    break;
                }
                else if (i == mGoals.size()-1){
                    mGoals.add(goal);
                    break;
                }
            }
        }
    }

    /**
     * Removes a goal from the list of goals to be displayed.
     *
     * @param goal the goal to be removed.
     */
    public void removeGoal(DisplayableGoal goal){
        mGoals.remove(goal);
    }

    /**
     * Adds an action to the upcoming list if the action is due today.
     *
     * @param action the action to be added.
     */
    public void addAction(Action action){
        Log.d("FeedData", "addAction() called: " + action);

        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
        todayCalendar.set(Calendar.MINUTE, 0);
        todayCalendar.set(Calendar.SECOND, 0);
        todayCalendar.set(Calendar.MILLISECOND, 0);

        Calendar tomorrowCalendar = Calendar.getInstance();
        tomorrowCalendar.set(Calendar.DAY_OF_MONTH, todayCalendar.get(Calendar.DAY_OF_MONTH)+1);
        tomorrowCalendar.set(Calendar.HOUR_OF_DAY, 0);
        tomorrowCalendar.set(Calendar.MINUTE, 0);
        tomorrowCalendar.set(Calendar.SECOND, 0);
        tomorrowCalendar.set(Calendar.MILLISECOND, 0);

        Date today = todayCalendar.getTime();
        Date tomorrow = tomorrowCalendar.getTime();
        Date actionDate = action.getNextReminderDate();

        Log.d("FeedData", "Today: " + today);
        Log.d("FeedData", "Action: " + actionDate);
        Log.d("FeedData", "Tomorrow: " + tomorrow);

        if (actionDate.after(today) && actionDate.before(tomorrow)){
            for (int i = 0; i < mUpcomingActions.size(); i++){
                if (mUpcomingActions.get(i).getNextReminderDate().compareTo(actionDate) > 0){
                    Log.d("FeedData", "Added at: " + i);
                    mUpcomingActions.add(i, action);
                    break;
                }
                else if (i == mUpcomingActions.size()-1){
                    Log.d("FeedData", "Added at the end");
                    mUpcomingActions.add(action);
                    break;
                }
            }
        }
    }

    /**
     * Removes an action from the action list.
     *
     * @param action the action to be removed.
     */
    public void removeAction(Action action){
        mUpcomingActions.remove(action);
    }

    /**
     * Synchronizes the feed data with the user data.
     *
     * @param userData the user data bundle.
     */
    public void sync(UserData userData){
        //Create the upcoming action array
        mUpcomingActions = new ArrayList<>();
        //Populate it in action's trigger-time order
        while (!mUpcomingActionIds.isEmpty() && !mUpcomingCustomActionIds.isEmpty()){
            Action userAction = userData.getActions().get(mUpcomingActionIds.get(0));
            Action customAction = userData.getCustomActions().get(mUpcomingCustomActionIds.get(0));
            //This favors CustomActions over UserActions in case of equal trigger time
            if (userAction.getNextReminderDate().compareTo(customAction.getNextReminderDate()) < 0){
                mUpcomingActions.add(userAction);
                mUpcomingActionIds.remove(0);
            }
            else{
                mUpcomingActions.add(customAction);
                mUpcomingCustomActionIds.remove(0);
            }
        }

        //The remaining actions are added (Note that only one of the two for loops will
        //  get to execute the inner block)
        for (Long upcomingActionId:mUpcomingActionIds){
            mUpcomingActions.add(userData.getActions().get(upcomingActionId));
        }
        for (Long upcomingCustomActionId:mUpcomingCustomActionIds){
            mUpcomingActions.add(userData.getCustomActions().get(upcomingCustomActionId));
        }

        //Set the next Action of there is one
        if (!mUpcomingActions.isEmpty()){
            mNextAction = mUpcomingActions.remove(0);
        }

        //Assign colors to suggestions
        for (GoalContent suggestion:mSuggestions){
            for (Long categoryId:suggestion.getCategoryIdSet()){
                if (userData.getCategories().containsKey(categoryId)){
                    suggestion.setColor(userData.getCategories().get(categoryId).getColor());
                    break;
                }
            }
        }

        //Select the source
        mGoals = new ArrayList<>();
        if (!userData.getGoals().isEmpty()){
            mGoals.addAll(userData.getGoals().values());
            mGoals.addAll(userData.getCustomGoals().values());
            //Sort by title
            Collections.sort(mGoals, new Comparator<DisplayableGoal>(){
                @Override
                public int compare(DisplayableGoal lhs, DisplayableGoal rhs){
                    return lhs.getTitle().toLowerCase().compareTo(rhs.getTitle().toLowerCase());
                }
            });
        }
        else{
            mGoals.addAll(mSuggestions);
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
