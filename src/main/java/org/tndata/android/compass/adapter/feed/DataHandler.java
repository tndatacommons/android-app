package org.tndata.android.compass.adapter.feed;

import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.UpcomingAction;
import org.tndata.android.compass.model.UserData;

import java.util.ArrayList;
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
    void remove(UpcomingAction action){
        mFeedData.setTotalActions(mFeedData.getTotalActions() - 1);
        int percentage = mFeedData.getCompletedActions() * 100 / mFeedData.getTotalActions();
        mFeedData.setProgressPercentage(percentage);

        //mUserData.removeAction(action);
        if (mFeedData.getNextAction().equals(action)){
            replaceUpNext();
        }
        else{
            mFeedData.getUpcomingActions().remove(action);
        }
    }

    /**
     * Replaces the up next action with the one after.
     *
     * @return the new next action.
     */
    UpcomingAction replaceUpNext(){
        if (mFeedData.getUpcomingActionsX().isEmpty()){
            mFeedData.setUpNextActionX(null);
        }
        else{
            mFeedData.setUpNextActionX(mFeedData.getUpcomingActionsX().remove(0));
        }
        return mFeedData.getUpNextActionX();
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
    List<UpcomingAction> loadMoreUpcoming(int displayedActions){
        List<UpcomingAction> actions = new ArrayList<>();
        while (actions.size() < LOAD_MORE_COUNT && canLoadMoreActions(displayedActions + actions.size())){
            actions.add(mFeedData.getUpcomingActionsX().get(displayedActions + actions.size()));
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
        return displayedActions < mFeedData.getUpcomingActionsX().size();
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
