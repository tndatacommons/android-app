package org.tndata.android.compass.adapter.feed;

import android.util.Log;

import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.UserData;
import org.tndata.android.compass.model.UserGoal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    private List<DisplayableGoal> mDisplayedGoals;


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

        mDisplayedGoals = new ArrayList<>();
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

    boolean hasUserGoals(){
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
            mFeedData.setNextAction(mFeedData.getUpcomingActions().remove(0));
        }
    }

    Goal getFeedbackGoal(){
        return mFeedbackGoal;
    }

    boolean hasUpcoming(){
        return !mFeedData.getUpcomingActions().isEmpty();
    }

    void removeUpcoming(Action action){
        mFeedData.getUpcomingActions().remove(action);
    }

    List<DisplayableGoal> getGoals(){
        return mDisplayedGoals;
    }

    List<Action> loadMoreUpcoming(int displayedActionCount){
        List<Action> newActions = new ArrayList<>();
        while (newActions.size() < LOAD_MORE_COUNT && canLoadMoreActions(displayedActionCount+newActions.size())){
            newActions.add(mFeedData.getUpcomingActions().get(displayedActionCount + newActions.size()));
        }
        return newActions;
    }

    boolean canLoadMoreActions(int displayedActionCount){
        return displayedActionCount < mFeedData.getUpcomingActions().size();
    }

    List<DisplayableGoal> loadMoreGoals(int displayedGoalCount){
        List<DisplayableGoal> src = new ArrayList<>();
        if (hasUserGoals()){
            src.addAll(mUserData.getGoals().values());
            src.addAll(mUserData.getCustomGoals().values());
            Log.d("Adapter", "CustomGoals: " + mUserData.getCustomGoals().size());
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
        int count = 0;
        List<DisplayableGoal> newGoals = new ArrayList<>();
        while (count < LOAD_MORE_COUNT && canLoadMoreGoals(displayedGoalCount+count)){
            DisplayableGoal goal = src.get(mDisplayedGoals.size());
            mDisplayedGoals.add(goal);
            newGoals.add(goal);
            Log.d("Adapter", "Loading: " + goal);
            count++;
        }
        return newGoals;
    }

    boolean canLoadMoreGoals(int displayedGoalCount){
        if (hasUserGoals()){
            return displayedGoalCount < mUserData.getGoals().size() + mUserData.getCustomGoals().size();
        }
        else{
            return displayedGoalCount < mUserData.getFeedData().getSuggestions().size();
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

    void reload(){
        int size = mDisplayedGoals.size();
        mDisplayedGoals.clear();
        List<UserGoal> userGoals = new ArrayList<>(mUserData.getGoals().values());
        /*while (size > mDisplayedGoals.size() && canLoadMoreGoals()){
            mDisplayedGoals.add(userGoals.get(mDisplayedGoals.size()));
        }*/
    }
}
