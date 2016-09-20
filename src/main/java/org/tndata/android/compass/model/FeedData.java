package org.tndata.android.compass.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.util.FeedDataLoader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    private static final int LOAD_MORE_COUNT = 3;


    //API delivered fields
    @SerializedName("progress")
    private Progress mProgress;
    @SerializedName("upcoming")
    private List<UpcomingAction> mUpcomingActions;
    @SerializedName("suggestions")
    private List<TDCGoal> mSuggestions;
    @SerializedName("streaks")
    private List<Streak> mStreaks;
    @SerializedName("funcontent")
    private Reward mReward;

    //Fields set during post-processing or after data retrieval
    private UpcomingAction mUpNextAction;
    private List<Goal> mDisplayedGoals;
    private String mNextGoalBatchUrl;


    private Action mUpNext;
    private UserAction mNextUserAction;
    private CustomAction mNextCustomAction;


    /**
     * Initializes the feed data bundle.
     */
    public void init(){
        mDisplayedGoals = new ArrayList<>();
        if (!mUpcomingActions.isEmpty()){
            mUpNextAction = mUpcomingActions.remove(0);
        }
    }


    /*------------------------------*
     * REGULAR GETTERS AND CHECKERS *
     *------------------------------*/

    /**
     * Up next action getter.
     *
     * @return the up next action, null if there ain't one.
     */
    public UpcomingAction getUpNextAction(){
        return mUpNextAction;
    }

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
     * Gets the list of upcoming actions.
     *
     * @return the list of upcoming actions.
     */
    public List<UpcomingAction> getUpcomingActions(){
        return mUpcomingActions;
    }

    /**
     * Gets a sub list if the upcoming actions list.
     *
     * @param size the size of the sub-list. This gets capped to the maximum possible value if
     *             the request cannot be satisfied.
     * @return the requested sub-list of upcoming actions.
     */
    public List<UpcomingAction> getUpcomingActionsSubList(int size){
        //If the requested size is bigger tha the actual size, cap the size to max.
        if (size > mUpcomingActions.size()){
            size = mUpcomingActions.size();
        }
        //Return an independent list, not a backed list.
        return new ArrayList<>(mUpcomingActions.subList(0, size));
    }

    /**
     * Gets the UpcomingAction representing the passed action.
     *
     * @param action the action to be looked up.
     * @return the UpcomingAction representing the passed Action.
     */
    public UpcomingAction getAction(@NonNull Action action){
        if (mUpNextAction.is(action)){
            return mUpNextAction;
        }
        for (int i = 0; i < mUpcomingActions.size(); i++){
            if (mUpcomingActions.get(i).is(action)){
                return mUpcomingActions.get(i);
            }
        }
        return null;
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

    /**
     * Getter for the next goal batch url.
     *
     * @return the url of the next batch of goals to load or null if we are done loading goals.
     */
    public String getNextGoalBatchUrl(){
        return mNextGoalBatchUrl;
    }

    public Reward getReward(){
        return mReward;
    }


    /*--------------------------------------*
     * LOAD MORE / SEE MORE RELATED METHODS *
     *--------------------------------------*/

    /**
     * Tells whether there are more actions to display.
     *
     * @param displayedActions the number of actions already being displayed in the feed.
     * @return true if there are more actions to load, false otherwise.
     */
    public boolean canLoadMoreActions(int displayedActions){
        return displayedActions < mUpcomingActions.size();
    }

    /**
     * Returns the next batch of actions to be displayed in the feed.
     *
     * @param displayedActions the number of actions already being displayed in the feed.
     * @return a list containing the new actions.
     */
    public List<UpcomingAction> loadMoreUpcoming(int displayedActions){
        Log.d(TAG, "Total: " + mUpcomingActions.size() + ". Displayed: " + displayedActions);
        List<UpcomingAction> actions = new ArrayList<>();
        while (actions.size() < LOAD_MORE_COUNT && canLoadMoreActions(displayedActions + actions.size())){
            actions.add(mUpcomingActions.get(displayedActions + actions.size()));
        }
        return actions;
    }

    /**
     * Adds a batch of goals loaded from the API.
     *
     * @param goals a list containing the loaded goals.
     * @param nextBatchUrl the url to the next batch, null if we are done.
     */
    public void addGoals(@NonNull List<? extends Goal> goals, @Nullable String nextBatchUrl){
        for (Goal goal:goals){
            //Call addGoal() to ensure the goal wasn't already added to the bundle.
            addGoal(goal);
        }
        mNextGoalBatchUrl = nextBatchUrl;
    }


    /*------------ ------*
     * DATA MANIPULATION *
     *-------------------*/

    /**
     * Takes the first action in upcoming and places it in up next, if possible.
     *
     * @return the action previously in up next.
     */
    public UpcomingAction replaceUpNextAction(){
        UpcomingAction oldUpNext = mUpNextAction;
        if (mUpcomingActions.isEmpty()){
            mUpNextAction = null;
        }
        else{
            mUpNextAction = mUpcomingActions.remove(0);
        }
        return oldUpNext;
    }


    /*------------------------*
     * NEW STUFF, MOVE AROUND *
     *------------------------*/

    public void setNextUserAction(UserAction userAction){
        mNextUserAction = userAction;
    }

    public void setNextCustomAction(CustomAction customAction){
        mNextCustomAction = customAction;
    }

    public void replaceUpNext(){
        if (mNextUserAction != null && mNextCustomAction == null){
            bumpNextUserAction();
        }
        else if (mNextUserAction == null && mNextCustomAction != null){
            bumpNextCustomAction();
        }
        else if (mNextUserAction != null){ //&& mNextCustomAction != null){ //Implicit
            //Comparing the actions compares their triggers first
            if (mNextUserAction.compareTo(mNextCustomAction) < 0){
                bumpNextUserAction();
            }
            else{
                bumpNextCustomAction();
            }
        }
        //If both actions are null, do nothing
    }

    private void bumpNextUserAction(){
        mUpNext = mNextUserAction;
        mNextUserAction = null;
        FeedDataLoader.getInstance().loadNextUserAction();
    }

    private void bumpNextCustomAction(){
        mUpNext = mNextCustomAction;
        mNextCustomAction = null;
        FeedDataLoader.getInstance().loadNextCustomAction();
    }

    /**
     * Removes an UpcomingAction from the feed.
     *
     * @param action the action to be removed.
     * @param didIt whether this was due to an i did it event.
     */
    public void removeUpcomingActionX(UpcomingAction action, boolean didIt){
        if (didIt){
            mProgress.complete();
        }
        else{
            mProgress.remove();
        }

        if (mUpNextAction.equals(action)){
            replaceUpNextAction();
        }
        else{
            mUpcomingActions.remove(action);
        }
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
            //Try to update all the actions, the update() method checks for equality
            for (UpcomingAction upcomingAction:mUpcomingActions){
                upcomingAction.update(customGoal);
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
     * Tells whether a particular action is scheduled to happen between now and the end of the day.
     *
     * @param action the action to be checked.
     * @return true if the action is happening today, false otherwise.
     */
    private boolean happensToday(Action action){
        if (action.getNextReminder().equals("")){
            Log.d(TAG, "happensToday(): next reminder is not set");
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

        Log.d(TAG, "Action date: " + actionDate);

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
            Log.d(TAG, "Adding: " + action);
            Date actionDate = action.getNextReminderDate();
            if (mUpNextAction == null){
                mUpNextAction = new UpcomingAction(goal, action);
                Log.d(TAG, "Action added to up next");
            }
            else if (mUpNextAction.getTriggerDate().compareTo(actionDate) > 0){
                mUpcomingActions.add(0, mUpNextAction);
                mUpNextAction = new UpcomingAction(goal, action);
                Log.d(TAG, "Action added to up next, previous up next moved to upcoming");
            }
            else{
                for (int i = 0; i < mUpcomingActions.size(); i++){
                    if (mUpcomingActions.get(i).getTriggerDate().compareTo(actionDate) > 0){
                        mUpcomingActions.add(i, new UpcomingAction(goal, action));
                        Log.d(TAG, "Action added at " + i + " in upcoming");
                        break;
                    }
                    else if (i == mUpcomingActions.size() - 1){
                        mUpcomingActions.add(new UpcomingAction(goal, action));
                        Log.d(TAG, "Action added at the end of upcoming");
                        break;
                    }
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
        //In order to update the action it must be found first, remove() will do that for us,
        //  additionally, the action might have changed triggers, which would have made it
        //  change positions any way.
        UpcomingAction upcomingAction = removeAction(action);
        //If the action could be found and it happens today
        if (upcomingAction != null && happensToday(action)){
            //Update the action and insert it wherever it belongs
            upcomingAction.update(action);
            if (mUpNextAction == null){
                mUpNextAction = upcomingAction;
                Log.d(TAG, "Action added to up next");
            }
            else if (mUpNextAction.getTriggerDate().compareTo(upcomingAction.getTriggerDate()) > 0){
                UpcomingAction oldUpNext = mUpNextAction;
                mUpNextAction = upcomingAction;
                mUpcomingActions.add(0, oldUpNext);
                Log.d(TAG, "Action added to up next, previous up next moved to upcoming");
            }
            else{
                for (int i = 0; i < mUpcomingActions.size(); i++){
                    UpcomingAction nextUpcoming = mUpcomingActions.get(i);
                    if (nextUpcoming.getTriggerDate().compareTo(upcomingAction.getTriggerDate()) > 0){
                        mUpcomingActions.add(i, upcomingAction);
                        Log.d(TAG, "Action added at " + i + " in upcoming");
                        break;
                    }
                    if (i == mUpcomingActions.size() -1){
                        mUpcomingActions.add(upcomingAction);
                        Log.d(TAG, "Action added at the end of upcoming");
                        break;
                    }
                }
            }
        }
    }

    /**
     * Update an action in the data set. Use when a goal can be provided.
     *
     * @param goal the parent goal of the action in question.
     * @param action the action to be updated.
     */
    public void updateAction(Goal goal, Action action){
        Log.d(TAG, "Updating action: " + action);
        //Remove and add, it's easier than lookup and update.
        removeAction(action);
        addAction(goal, action);
    }

    /**
     * Removes an action from the data set.
     *
     * @param action the action to be removed.
     * @return the UpcomingAction representation of the removed action.
     */
    public UpcomingAction removeAction(Action action){
        if (mUpNextAction != null && mUpNextAction.is(action)){
            Log.d(TAG, "Removing: " + action);
            return replaceUpNextAction();
        }
        else{
            for (int i = 0; i < mUpcomingActions.size(); i++){
                UpcomingAction upcomingAction = mUpcomingActions.get(i);
                if (upcomingAction.is(action)){
                    Log.d(TAG, "Removing: " + action);
                    return mUpcomingActions.remove(i);
                }
            }
        }
        return null;
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
