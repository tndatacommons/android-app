package org.tndata.android.compass.model;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Data holder for the data to be displayed in the feed.
 *
 * TODO major clean up
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class FeedData extends TDCBase{
    public static final String TYPE = "feed_data";

    private static final int LOAD_MORE_COUNT = 3;


    //API delivered fields
    @SerializedName("action_feedback")
    private ActionFeedback mActionFeedback;
    @SerializedName("progress")
    private Progress mProgress;

    @SerializedName("suggestions")
    private List<GoalContent> mSuggestions;


    //Experiment
    private UpcomingAction mUpNextActionX;
    private List<UpcomingAction> mUpcomingActionsX;
    private List<Goal> mDisplayedGoalsX;
    private String mNextGoalBatchUrlX;


    public void setUpcomingActionsX(List<UpcomingAction> upcomingActions){
        if (upcomingActions.isEmpty()){
            mUpNextActionX = null;
            mUpcomingActionsX = upcomingActions;
        }
        else{
            mUpNextActionX = upcomingActions.remove(0);
            if (hasFeedback()){
                mActionFeedback.setFeedbackGoal(mUpNextActionX);
            }
            mUpcomingActionsX = upcomingActions;
        }
    }

    public UpcomingAction getUpNextActionX(){
        return mUpNextActionX;
    }

    public List<UpcomingAction> getUpcomingActionsX(){
        return mUpcomingActionsX;
    }

    public void addGoalsX(@NonNull List<? extends Goal> goals, @Nullable String nextBatchUrl){
        for (Goal goal:goals){
            addGoal(goal);
        }
        mNextGoalBatchUrlX = nextBatchUrl;
    }

    public List<Goal> getGoalsX(){
        return mDisplayedGoalsX;
    }

    public String getNextGoalBatchUrl(){
        return mNextGoalBatchUrlX;
    }

    public UpcomingAction getActionX(Action action){
        if (mUpNextActionX.is(action)){
            return mUpNextActionX;
        }
        for (int i = 0; i < mUpcomingActionsX.size(); i++){
            if (mUpcomingActionsX.get(i).is(action)){
                return mUpcomingActionsX.get(i);
            }
        }
        return null;
    }

    public UpcomingAction replaceUpNextActionX(){
        UpcomingAction oldUpNext = mUpNextActionX;
        if (mUpcomingActionsX.isEmpty()){
            mUpNextActionX = null;
        }
        else{
            mUpNextActionX = mUpcomingActionsX.remove(0);
        }
        return oldUpNext;
    }

    public void removeUpcomingActionX(UpcomingAction action, boolean didIt){
        if (didIt){
            mProgress.complete();
        }
        else{
            mProgress.remove();
        }

        if (mUpNextActionX.equals(action)){
            replaceUpNextActionX();
        }
        else{
            mUpcomingActionsX.remove(action);
        }
    }

    /**
     * Returns the next batch of actions to be displayed in the feed.
     *
     * @param displayedActions the number of actions already being displayed in the feed.
     * @return a list containing the new actions.
     */
    public List<UpcomingAction> loadMoreUpcomingX(int displayedActions){
        Log.d("FeedData", "Total size: " + mUpcomingActionsX.size());
        Log.d("FeedData", "Displayed: " + displayedActions);
        List<UpcomingAction> actions = new ArrayList<>();
        while (actions.size() < LOAD_MORE_COUNT && canLoadMoreActionsX(displayedActions + actions.size())){
            Log.d("FeedData", "Adding: " + (displayedActions + actions.size()));
            actions.add(mUpcomingActionsX.get(displayedActions + actions.size()));
        }
        return actions;
    }

    /**
     * Tells whether there are more actions to display.
     *
     * @param displayedActions the number of actions already being displayed in the feed.
     * @return true if there are more actions to load, false otherwise.
     */
    public boolean canLoadMoreActionsX(int displayedActions){
        return displayedActions < mUpcomingActionsX.size();
    }


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

    public boolean hasFeedback(){
        return mActionFeedback != null;
    }

    public ActionFeedback getFeedback(){
        return mActionFeedback;
    }

    public Progress getProgress(){
        return mProgress;
    }

    /**
     * Gets the list of suggestions.
     *
     * @return the list of suggestions.
     */
    public List<GoalContent> getSuggestions(){
        return mSuggestions;
    }

    public List<UpcomingAction> getUpcomingList(int size){
        Log.d("FeedData", "getUpcomingList() called");
        if (size > mUpcomingActionsX.size()){
            size = mUpcomingActionsX.size();
        }
        return new ArrayList<>(mUpcomingActionsX.subList(0, size));
    }

    public void addGoal(Goal goal){
        Log.d("FeedData", "addGoal() called");
        if (!mDisplayedGoalsX.contains(goal)){
            Log.d("FeedData", "Adding goal");
            mDisplayedGoalsX.add(goal);
        }
    }

    public void updateGoal(Goal goal){
        if (goal instanceof CustomGoal){
            CustomGoal customGoal = (CustomGoal)goal;
            for (Goal listedGoal:mDisplayedGoalsX){
                if (listedGoal.equals(goal)){
                    CustomGoal existingGoal = (CustomGoal)listedGoal;
                    existingGoal.setTitle(customGoal.getTitle());
                }
            }
            //TODO update custom actions
        }
    }

    public void removeGoal(Goal goal){
        for (int i = 0; i < mDisplayedGoalsX.size(); i++){
            if (mDisplayedGoalsX.get(i).equals(goal)){
                mDisplayedGoalsX.remove(i);
                break;
            }
        }
    }

    private boolean happensToday(Action action){
        if (action.getNextReminder() == null){
            Log.d("FeedData", "nextReminder is null");
            return false;
        }

        Calendar tomorrowCalendar = Calendar.getInstance();
        tomorrowCalendar.set(Calendar.DAY_OF_MONTH, tomorrowCalendar.get(Calendar.DAY_OF_MONTH)+1);
        tomorrowCalendar.set(Calendar.HOUR_OF_DAY, 0);
        tomorrowCalendar.set(Calendar.MINUTE, 0);
        tomorrowCalendar.set(Calendar.SECOND, 0);
        tomorrowCalendar.set(Calendar.MILLISECOND, 0);

        Date now = Calendar.getInstance().getTime();
        Date tomorrow = tomorrowCalendar.getTime();
        Date actionDate = action.getNextReminderDate();

        Log.d("FeedData", "Now: " + now);
        Log.d("FeedData", "Action: " + actionDate);
        Log.d("FeedData", "Tomorrow: " + tomorrow);

        return actionDate.after(now) && actionDate.before(tomorrow);
    }

    /**
     * Adds an action to the upcoming list if the action is due today.
     *
     * @param goal the parent goal of the action.
     * @param action the action to be added.
     */
    public void addAction(Goal goal, Action action){
        if (happensToday(action)){
            Date actionDate = action.getNextReminderDate();
            if (mUpNextActionX == null){
                mUpNextActionX = new UpcomingAction(goal, action);
            }
            else if (mUpNextActionX.getTriggerDate().compareTo(actionDate) > 0){
                mUpcomingActionsX.add(0, mUpNextActionX);
                mUpNextActionX = new UpcomingAction(goal, action);
            }
            else{
                for (int i = 0; i < mUpcomingActionsX.size(); i++){
                    if (mUpcomingActionsX.get(i).getTriggerDate().compareTo(actionDate) > 0){
                        Log.d("FeedData", "Added at: " + i);
                        mUpcomingActionsX.add(i, new UpcomingAction(goal, action));
                        break;
                    }
                    else if (i == mUpcomingActionsX.size() - 1){
                        Log.d("FeedData", "Added at the end");
                        mUpcomingActionsX.add(new UpcomingAction(goal, action));
                        break;
                    }
                }
            }
        }
    }

    public void updateAction(Action action){
        logData();
        UpcomingAction upcomingAction = removeAction(action);
        logData();
        if (upcomingAction != null && happensToday(action)){
            upcomingAction.update(action);
            if (mUpNextActionX.getTriggerDate().compareTo(upcomingAction.getTriggerDate()) > 0){
                Log.d("FeedData", "Moving action to the head");
                UpcomingAction oldUpNext = mUpNextActionX;
                mUpNextActionX = upcomingAction;
                mUpcomingActionsX.add(0, oldUpNext);
            }
            else{
                Log.d("FeedData", "Moving action to upcoming");
                for (int i = 0; i < mUpcomingActionsX.size(); i++){
                    UpcomingAction nextUpcoming = mUpcomingActionsX.get(i);
                    if (nextUpcoming.getTriggerDate().compareTo(upcomingAction.getTriggerDate()) > 0){
                        mUpcomingActionsX.add(i, upcomingAction);
                        break;
                    }
                    if (i == mUpcomingActionsX.size() -1){
                        mUpcomingActionsX.add(upcomingAction);
                        break;
                    }
                }
            }
        }
        logData();
    }

    public void updateAction(Goal goal, Action action){
        //Remove and add, it's easier than lookup and update.
        Log.d("FeedData", "updateAction(G, A) called");
        removeAction(action);
        Log.d("FeedData", "Updating action: " + action);
        addAction(goal, action);
    }

    public UpcomingAction removeAction(Action action){
        if (mUpNextActionX != null && mUpNextActionX.is(action)){
            return replaceUpNextActionX();
        }
        else{
            for (int i = 0; i < mUpcomingActionsX.size(); i++){
                UpcomingAction upcomingAction = mUpcomingActionsX.get(i);
                if (upcomingAction.is(action)){
                    return mUpcomingActionsX.remove(i);
                }
            }
        }
        return null;
    }

    /**
     * Initializes the feed data bundle.
     */
    public void init(){
        mDisplayedGoalsX = new ArrayList<>();
    }

    private void logData(){
        Log.d("FeedData", "Up next: " + mUpNextActionX);
        Log.d("FeedData", "Upcoming: " + mUpcomingActionsX);
    }


    /**
     * Model for an action feedback. Also contains information about which goal's feedback
     * is being displayed and the type of such goal.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public class ActionFeedback{
        @SerializedName("title")
        private String mFeedbackTitle;
        @SerializedName("subtitle")
        private String mFeedbackSubtitle;
        @SerializedName("icon")
        private int mFeedbackIconId;

        private long mFeedbackGoalIdX;
        private String mFeedbackGoalTypeX;


        /**
         * Gets the feedback title.
         *
         * @return the feedback title.
         */
        public String getFeedbackTitle(){
            return mFeedbackTitle;
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
         * Gets the feedback icon resource id.
         *
         * @return the feedback icon resource id.
         */
        @DrawableRes
        public int getFeedbackIcon(){
            switch (mFeedbackIconId){
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

        private void setFeedbackGoal(UpcomingAction action){
            mFeedbackGoalIdX = action.getGoalId();
            if (action.isUserAction()){
                mFeedbackGoalTypeX = "usergoal";
            }
            else if (action.isCustomAction()){
                mFeedbackGoalTypeX = "customgoal";
            }
        }

        public long getFeedbackGoalId(){
            return mFeedbackGoalIdX;
        }

        public boolean hasUserGoal(){
            return mFeedbackGoalTypeX.equals("usergoal");
        }

        public boolean hasCustomGoal(){
            return mFeedbackGoalTypeX.equals("customgoal");
        }
    }


    /**
     * Model for the user's daily progress towards actions.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public class Progress{
        @SerializedName("total")
        private int mTotalActions;
        @SerializedName("completed")
        private int mCompletedActions;
        @SerializedName("progress")
        private int mProgressPercentage;


        /**
         * Completes one action's stats in the data set.
         */
        private void complete(){
            mCompletedActions++;
            mProgressPercentage = mCompletedActions * 100 / mTotalActions;
        }

        /**
         * Removes one action's stats from the data set.
         */
        private void remove(){
            mTotalActions--;
            mProgressPercentage = mCompletedActions * 100 / mTotalActions;
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
         * Gets the completed actions,
         *
         * @return the completed actions.
         */
        public int getCompletedActions(){
            return mCompletedActions;
        }

        /**
         * Gets the progress percentage of completed actions.
         *
         * @return the progress percentage of completed actions.
         */
        public int getProgressPercentage(){
            return mProgressPercentage;
        }

        /**
         * Gets the progress percentage as a fraction.
         *
         * @return the progress percentage as a fraction.
         */
        public String getProgressFraction(){
            return mCompletedActions + "/" + mTotalActions;
        }
    }
}
