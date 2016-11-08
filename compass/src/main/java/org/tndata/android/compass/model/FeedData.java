package org.tndata.android.compass.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.util.FeedDataLoader;
import org.tndata.compass.model.Action;
import org.tndata.compass.model.CustomAction;
import org.tndata.compass.model.CustomGoal;
import org.tndata.compass.model.Goal;
import org.tndata.compass.model.Reward;
import org.tndata.compass.model.TDCGoal;
import org.tndata.compass.model.UserAction;

import java.util.ArrayList;
import java.util.List;


/**
 * Data holder for the data to be displayed in the feed.
 *
 * @author Ismael Alonso
 * @version 2.0.0
 */
public class FeedData{
    public static final String TAG = "FeedData";
    public static final String API_TYPE = "feed";


    //API delivered fields
    @SerializedName("progress")
    private Progress mProgress;
    @SerializedName("suggestions")
    private List<TDCGoal> mSuggestions;
    @SerializedName("streaks")
    private List<Streak> mStreaks;
    @SerializedName("funcontent")
    private Reward mReward;

    //Fields set during post-processing or after data retrieval
    private List<Goal> mDisplayedGoals;


    private Action mUpNext;
    private UserAction mNextUserAction;
    private CustomAction mNextCustomAction;


    /**
     * Initializes the feed data bundle.
     */
    public void init(){
        mDisplayedGoals = new ArrayList<>();
    }


    /*------------------------------*
     * REGULAR GETTERS AND CHECKERS *
     *------------------------------*/

    /**
     * Progress getter.
     *
     * @return the action daily progress.
     */
    public Progress getProgress(){
        return mProgress;
    }

    /**
     * Tells whether there are any streaks.
     *
     * @return true if there are streaks, false otherwise.
     */
    public boolean hasStreaks(){
        return mStreaks != null && mStreaks.size() > 0;
    }

    /**
     * Streaks getter.
     *
     * @return the streaks data.
     */
    public List<Streak> getStreaks(){
        return mStreaks;
    }

    /**
     * Goal getter.
     *
     * @return the list of loaded goals.
     */
    public List<Goal> getGoals(){
        return mDisplayedGoals;
    }

    /**
     * Gets the list of suggestions.
     *
     * @return the list of suggestions.
     */
    public List<TDCGoal> getSuggestions(){
        return mSuggestions;
    }

    public Reward getReward(){
        return mReward;
    }


    /*--------------------------------------*
     * LOAD MORE / SEE MORE RELATED METHODS *
     *--------------------------------------*/

    /**
     * Adds a batch of goals loaded from the API.
     *
     * @param goals a list containing the loaded goals.
     */
    public void addGoals(@NonNull List<Goal> goals){
        for (Goal goal:goals){
            //Call addGoal() to ensure the goal wasn't already added to the bundle.
            addGoal(goal);
        }
    }


    /*------------------------*
     * NEW STUFF, MOVE AROUND *
     *------------------------*/

    public void setNextUserAction(UserAction userAction){
        Log.i(TAG, "Setting next user action: " + userAction);
        mNextUserAction = userAction;
    }

    public void setNextCustomAction(CustomAction customAction){
        Log.i(TAG, "Setting next custom action: " + customAction);
        mNextCustomAction = customAction;
    }

    private Action getNextAction(){
        if (mNextUserAction != null && mNextCustomAction == null){
            Log.i(TAG, "Only user actions available");
            return mNextUserAction;
        }
        else if (mNextUserAction == null && mNextCustomAction != null){
            Log.i(TAG, "Only custom actions available");
            return mNextCustomAction;
        }
        else if (mNextUserAction != null){ //&& mNextCustomAction != null){ //Implicit
            //Comparing the actions compares their triggers first
            if (mNextUserAction.happensBefore(mNextCustomAction)){
                Log.i(TAG, "Next is user action");
                return mNextUserAction;
            }
            else{
                Log.i(TAG, "Next is custom action");
                return mNextCustomAction;
            }
        }
        else{
            Log.i(TAG, "No actions available");
            return null;
        }
    }

    public void replaceUpNext(){
        Action next = getNextAction();
        if (next instanceof UserAction){
            bumpNextUserAction();
        }
        else if (next instanceof CustomAction){
            bumpNextCustomAction();
        }
        else{
            mUpNext = null;
        }
    }

    private void bumpNextUserAction(){
        Log.i(TAG, "Setting up next: " + mNextUserAction);
        mUpNext = mNextUserAction;
        mNextUserAction = null;
        FeedDataLoader.getInstance().loadNextUserAction();
    }

    private void bumpNextCustomAction(){
        Log.i(TAG, "Setting up next: " + mNextCustomAction);
        mUpNext = mNextCustomAction;
        mNextCustomAction = null;
        FeedDataLoader.getInstance().loadNextCustomAction();
    }

    public Action getUpNext(){
        return mUpNext;
    }





    /**
     * Adds a goal to the data set.
     *
     * @param goal the goal to be added.
     */
    public void addGoal(Goal goal){
        if (!mDisplayedGoals.contains(goal)){
            Log.d(TAG, "Adding goal: " + goal);
            mDisplayedGoals.add(goal);
        }
    }

    /**
     * Updates a goal in the data set.
     *
     * @param goal the goal to be updated.
     */
    public void updateGoal(Goal goal){
        Log.d(TAG, "Updating goal: " + goal);
        //This operation can only happen for custom goals
        if (goal instanceof CustomGoal){
            CustomGoal customGoal = (CustomGoal)goal;
            //Find the goal and update it
            for (Goal listedGoal:mDisplayedGoals){
                if (listedGoal.equals(goal)){
                    CustomGoal existingGoal = (CustomGoal)listedGoal;
                    existingGoal.setTitle(customGoal.getTitle());
                    break;
                }
            }
        }
    }

    /**
     * Removes a goal from the data set.
     *
     * @param goal the goal to be removed.
     * @return the index of the goal in the backing list prior to removal, -1 if not found.
     */
    public int removeGoal(Goal goal){
        for (int i = 0; i < mDisplayedGoals.size(); i++){
            if (mDisplayedGoals.get(i).equals(goal)){
                Log.d(TAG, "Removing goal: " + goal);
                mDisplayedGoals.remove(i);
                return i;
            }
        }
        return -1;
    }

    /**
     * Adds an action to the upcoming list if the action is due today.
     *
     * @param action the action to be added.
     */
    public void addAction(Action action){
        if (action.happensToday()){
            Log.i(TAG, "Adding " + action + ".");
            if (mUpNext == null){
                Log.i(TAG, "Action is up next.");
                mUpNext = action;
            }
            else if (action.happensBefore(mUpNext)){
                Log.i(TAG, "Action is up next.");
                if (mUpNext instanceof UserAction){
                    mNextUserAction = (UserAction)mUpNext;
                }
                else if (mUpNext instanceof CustomAction){
                    mNextCustomAction = (CustomAction)mUpNext;
                }
            }
            else{
                if (action instanceof UserAction && action.happensBefore(mNextUserAction)){
                    Log.i(TAG, "Action is next user action.");
                    mNextUserAction = (UserAction)action;
                }
                else if (action instanceof CustomAction && action.happensBefore(mNextCustomAction)){
                    Log.i(TAG, "Action is next custom action.");
                    mNextCustomAction = (CustomAction)action;
                }
            }
        }
    }

    /**
     * Updates an action in the data set. Use when no goal can be provided.
     *
     * @param action the action to be updated.
     */
    public void updateAction(Action action){
        Log.d(TAG, "Updating action: " + action);

        if (!action.happensToday()){
            replaceUpNext();
        }
        else{
            //TODO title might've change
            Action next = getNextAction();
            if (next != null && next.compareTo(action) < 0){
                if (next instanceof UserAction){
                    bumpNextUserAction();
                }
                else if (next instanceof CustomAction){
                    bumpNextCustomAction();
                }
            }
        }
    }

    /**
     * Removes an action from the data set.
     *
     * @param action the action to be removed.
     */
    public void removeAction(Action action){
        if (mUpNext != null && mUpNext.equals(action)){
            replaceUpNext();
        }
        else if (mNextUserAction != null && mNextUserAction.equals(action)){
            FeedDataLoader.getInstance().loadNextUserAction();
        }
        else if (mNextCustomAction != null && mNextCustomAction.equals(action)){
            FeedDataLoader.getInstance().loadNextCustomAction();
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
        @SerializedName("weekly_completions")
        private int mWeeklyCompletions;
        @SerializedName("engagement_rank")
        private double mEngagementRank;


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
         * Gets the number of actions completed <b>today</b>.
         *
         * @return the total number of actions completed today.
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
         * Gets the number of actions completed <b>this week</b>.
         *
         * @return the number of actions completed this week.
         */
        public int getWeeklyCompletions(){
            return mWeeklyCompletions;
        }

        /**
         * Gets the engagement rank.
         *
         * @return the user's engagement rank.
         */
        public int getEngagementRank(){
            return (int)mEngagementRank;
        }
    }

    /**
     * Model for the user's daily progress streaks.
     *
     * @author Brad Montgomery
     * @version 1.0.0
     */
    public class Streak {
        @SerializedName("day")
        private String mDay;
        @SerializedName("date")
        private String mDate;
        @SerializedName("count")
        private int mCount = 0;

        public boolean completed() {
            return mCount > 0;
        }
        public String getDay() {
            return mDay;
        }

        public String getDayAbbrev() {
            return mDay.substring(0, 1);
        }

        public String getDate() {
            return mDate;
        }

        public int getCount() {
            return mCount;
        }
    }
}
