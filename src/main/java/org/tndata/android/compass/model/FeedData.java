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

    @SerializedName("upcoming_actions")
    private List<Long> mUpcomingActionIds;
    @SerializedName("upcoming_customactions")
    private List<Long> mUpcomingCustomActionIds;

    @SerializedName("suggestions")
    private List<GoalContent> mSuggestions;


    //Fields set during post-processing
    private Action mNextAction;
    private List<Action> mUpcomingActions;


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

    public void setUpNextActionX(UpcomingAction upNextAction){
        mUpNextActionX = upNextAction;
    }

    public UpcomingAction getUpNextActionX(){
        return mUpNextActionX;
    }

    public List<UpcomingAction> getUpcomingActionsX(){
        return mUpcomingActionsX;
    }

    public void addGoalsX(@NonNull List<? extends Goal> goals, @Nullable String nextBatchUrl){
        mDisplayedGoalsX.addAll(goals);
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
        if (mUpcomingActionsX.isEmpty()){
            mUpNextActionX = null;
        }
        else{
            mUpNextActionX = mUpcomingActionsX.remove(0);
        }
        return mUpNextActionX;
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
        List<UpcomingAction> actions = new ArrayList<>();
        while (actions.size() < LOAD_MORE_COUNT && canLoadMoreActionsX(displayedActions + actions.size())){
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

    /**
     * Adds an action to the upcoming list if the action is due today.
     *
     * @param action the action to be added.
     */
    public void addAction(Action action){
        //TODO edit this to generate an UpcomingAction from an Action
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
     * Synchronizes the feed data with the user data.
     *
     * @param userData the user data bundle.
     */
    public void sync(UserData userData){
        //Create the upcoming action array
        mUpcomingActions = new ArrayList<>();
        //Populate it in action's trigger-time order
        /*while (!mUpcomingActionIds.isEmpty() && !mUpcomingCustomActionIds.isEmpty()){
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
        if (!userData.getGoals().isEmpty() || !userData.getCustomGoals().isEmpty()){
            mGoals.addAll(userData.getGoals().values());
            mGoals.addAll(userData.getCustomGoals().values());
            //Sort by title
            Collections.sort(mGoals, new Comparator<ContentContainer.ContainerGoal>(){
                @Override
                public int compare(ContentContainer.ContainerGoal lhs, ContentContainer.ContainerGoal rhs){
                    return lhs.getTitle().toLowerCase().compareTo(rhs.getTitle().toLowerCase());
                }
            });
        }
        else{
            mGoals.addAll(mSuggestions);
        }*/

        mDisplayedGoalsX = new ArrayList<>();
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
