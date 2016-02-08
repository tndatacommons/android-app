package org.tndata.android.compass.adapter.feed;

import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.UserData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Collection of methods that mutate or retrieve information from the model when needed.
 *
 * @author Ismael Alonso
 * @version 2.0.0
 */
class DataHandler{
    private static final int LOAD_MORE_COUNT = 3;


    private UserData mUserData;
    private FeedData mFeedData;

    private Goal mFeedbackGoal;


    /**
     * Constructor.
     *
     * @param userData the user data bundle.
     */
    DataHandler(UserData userData){
        mUserData = userData;
        mFeedData = userData.getFeedData();

        if (mFeedData.getNextAction() != null){
            mFeedbackGoal = mFeedData.getNextAction().getGoal();
        }
    }

    /**
     * Updates the progress indicator when a user taps I did it.
     */
    void didIt(){
        mFeedData.setCompletedActions(mFeedData.getCompletedActions() + 1);
        int percentage = mFeedData.getCompletedActions() * 100 / mFeedData.getTotalActions();
        mFeedData.setProgressPercentage(percentage);
    }

    /**
     * Removes an action from the user selection and updates the progress.
     *
     * @param action the action to be removed.
     */
    void remove(Action action){
        mFeedData.setTotalActions(mFeedData.getTotalActions() - 1);
        int percentage = mFeedData.getCompletedActions() * 100 / mFeedData.getTotalActions();
        mFeedData.setProgressPercentage(percentage);

        mUserData.removeAction(action);
    }

    /**
     * Tells whether the user has any upcoming actions left for the day.
     *
     * @return true if the user does, false otherwise.
     */
    boolean hasUpcoming(){
        return !mFeedData.getUpcomingActions().isEmpty();
    }

    /**
     * Tells whether the user has selected goals or not.
     *
     * @return true if the user has selected goals, false otherwise.
     */
    boolean hasUserGoals(){
        return !mUserData.getGoals().isEmpty();
    }

    /**
     * Getter for the user's next action.
     *
     * @return the next action.
     */
    Action getUpNext(){
        return mFeedData.getNextAction();
    }

    /**
     * Replaces the up next action with the one after.
     *
     * @return the new next action.
     */
    Action replaceUpNext(){
        if (mFeedData.getUpcomingActions().isEmpty()){
            mFeedData.setNextAction(null);
        }
        else{
            mFeedData.setNextAction(mFeedData.getUpcomingActions().remove(0));
        }
        return mFeedData.getNextAction();
    }

    /**
     * Getter for the goal whose progress is being displayed in the feedback card.
     *
     * @return the goal displayed by the feedback card.
     */
    Goal getFeedbackGoal(){
        return mFeedbackGoal;
    }

    /**
     * Returns the next batch of actions to be displayed in the feed.
     *
     * @param displayedActions the number of actions already being displayed in the feed.
     * @return a list containing the new actions.
     */
    List<Action> loadMoreUpcoming(int displayedActions){
        List<Action> actions = new ArrayList<>();
        while (actions.size() < LOAD_MORE_COUNT && canLoadMoreActions(displayedActions + actions.size())){
            actions.add(mFeedData.getUpcomingActions().get(displayedActions + actions.size()));
        }
        return actions;
    }

    /**
     * Tells whether there are more actions to display.
     *
     * @param displayedActions the number of actions already being displayed in the feed.
     * @return true if there are more actions to load, false otherwise.
     */
    boolean canLoadMoreActions(int displayedActions){
        return displayedActions < mFeedData.getUpcomingActions().size();
    }

    /**
     * Returns the next batch of goals to be displayed in the feed.
     *
     * @param displayedGoals the number of goals already being displayed din the feed.
     * @return a list containing the new goals.
     */
    List<DisplayableGoal> loadMoreGoals(int displayedGoals){
        //Select the source
        List<DisplayableGoal> src = new ArrayList<>();
        if (hasUserGoals()){
            src.addAll(mUserData.getGoals().values());
            src.addAll(mUserData.getCustomGoals().values());
            //Sort by title
            Collections.sort(src, new Comparator<DisplayableGoal>(){
                @Override
                public int compare(DisplayableGoal lhs, DisplayableGoal rhs){
                    return lhs.getTitle().toLowerCase().compareTo(rhs.getTitle().toLowerCase());
                }
            });
        }
        else{
            src.addAll(mFeedData.getSuggestions());
        }

        //Populate the new list
        List<DisplayableGoal> goals = new ArrayList<>();
        while (goals.size() < LOAD_MORE_COUNT && canLoadMoreGoals(displayedGoals + goals.size())){
            goals.add(src.get(displayedGoals + goals.size()));
        }
        return goals;
    }

    /**
     * Tells whether there are more goals to display.
     *
     * @param displayedGoals the number of goals already being displayed din the feed.
     * @return true if there are more goals to load, false otherwise.
     */
    boolean canLoadMoreGoals(int displayedGoals){
        if (hasUserGoals()){
            return displayedGoals < mUserData.getGoals().size() + mUserData.getCustomGoals().size();
        }
        else{
            return displayedGoals < mUserData.getFeedData().getSuggestions().size();
        }
    }

    /**
     * Gets the total number of actions scheduled for today.
     *
     * @return the total number of actions scheduled for today.
     */
    int getTotalActions(){
        return mFeedData.getTotalActions();
    }

    /**
     * Gets the number of actions completed today.
     *
     * @return the number of actions completed today.
     */
    int getProgress(){
        return mFeedData.getProgress();
    }

    /**
     * Gets the fraction of actions completed out of the total scheduled today.
     *
     * @return the fraction of actions completed out of the total scheduled today.
     */
    String getProgressFraction(){
        return mFeedData.getProgressFraction();
    }
}
