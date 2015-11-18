package org.tndata.android.compass.adapter.feed;

import android.util.Log;

import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.UserData;

import java.util.ArrayList;
import java.util.List;


/**
 * Data handler for the main feed adapter. Keeps the model updated.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
class DataHandler{
    private static final int LOAD_MORE_COUNT = 3;


    private UserData mUserData;
    private FeedData mFeedData;

    private Goal mFeedbackGoal;

    private List<Action> mDisplayedUpcoming;
    private List<Goal> mDisplayedGoals;


    /**
     * Constructor.
     *
     * @param userData the data bundle.
     */
    DataHandler(UserData userData){
        mUserData = userData;
        mFeedData = userData.getFeedData();

        if (mUserData.getFeedData().getNextAction() != null){
            mFeedbackGoal = mUserData.getFeedData().getNextAction().getPrimaryGoal();
        }

        mDisplayedUpcoming = new ArrayList<>();
        loadMoreUpcoming();

        mDisplayedGoals = new ArrayList<>();
        loadMoreGoals();
    }

    void didIt(){
        mFeedData.setCompletedActions(mFeedData.getCompletedActions() + 1);
        int percentage = mFeedData.getCompletedActions() * 100 / mFeedData.getTotalActions();
        mFeedData.setProgressPercentage(percentage);
    }

    void remove(Action action){
        mFeedData.setTotalActions(mFeedData.getTotalActions() - 1);
        int percentage = mFeedData.getCompletedActions() * 100 / mFeedData.getTotalActions();
        mFeedData.setProgressPercentage(percentage);

        mUserData.removeAction(action);
    }

    boolean hasGoals(){
        return !mUserData.getGoals().isEmpty();
    }

    Action getUpNext(){
        return mFeedData.getNextAction();
    }

    void replaceUpNext(){
        if (mFeedData.getUpcomingActions().isEmpty()){
            mFeedData.setNextAction(null);
        }
        else{
            mFeedData.setNextAction(mDisplayedUpcoming.remove(0));
            mFeedData.getUpcomingActions().remove(0);

            checkActions();
        }
    }

    Goal getFeedbackGoal(){
        return mFeedbackGoal;
    }

    List<Action> getUpcoming(){
        return mDisplayedUpcoming;
    }

    Action removeUpcoming(int position){
        mDisplayedUpcoming.remove(position);
        Action removed = mFeedData.getUpcomingActions().remove(position);

        checkActions();

        return removed;
    }

    Category getActionCategory(Action action){
        Category category = null;
        if (action.getPrimaryGoal() != null){
            category = action.getPrimaryGoal().getPrimaryCategory();
            if (category == null){
                Goal goal = mUserData.getGoal(action.getPrimaryGoal());
                if (goal.getCategories().size() > 0){
                    category = goal.getCategories().get(0);
                }
            }
        }
        return category;
    }

    List<Goal> getGoals(){
        return mDisplayedGoals;
    }

    int loadMoreUpcoming(){
        int count = 0;
        while (count < LOAD_MORE_COUNT && canLoadMoreActions()){
            mDisplayedUpcoming.add(mFeedData.getUpcomingActions().get(mDisplayedUpcoming.size()));
            count++;
        }
        return count;
    }

    boolean canLoadMoreActions(){
        return mDisplayedUpcoming.size() < mFeedData.getUpcomingActions().size();
    }

    int loadMoreGoals(){
        List<Goal> src = getGoalList();
        int count = 0;
        for (int i = 0; i < LOAD_MORE_COUNT && canLoadMoreGoals(); i++){
            mDisplayedGoals.add(src.get(mDisplayedGoals.size()));
            count++;
        }
        return count;
    }

    boolean canLoadMoreGoals(){
        return mDisplayedGoals.size() < getGoalList().size();
    }

    private List<Goal> getGoalList(){
        List<Goal> src = mUserData.getGoals();
        if (src.isEmpty()){
            src = mFeedData.getSuggestions();
        }
        return src;
    }

    private void checkActions(){
        if (mDisplayedUpcoming.size() < 3 && mFeedData.getUpcomingActions().size() > mDisplayedUpcoming.size()){
            mDisplayedUpcoming.add(mFeedData.getUpcomingActions().get(mDisplayedUpcoming.size()));
        }
    }

    int getTotalActions(){
        return mFeedData.getTotalActions();
    }

    int getProgress(){
        return mFeedData.getProgress();
    }

    String getProgressFraction(){
        return mFeedData.getProgressFraction();
    }
}
