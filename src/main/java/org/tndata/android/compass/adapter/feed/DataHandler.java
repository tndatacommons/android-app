package org.tndata.android.compass.adapter.feed;

import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.model.UserData;
import org.tndata.android.compass.model.UserGoal;

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

    void remove(UserAction action){
        mFeedData.setTotalActions(mFeedData.getTotalActions() - 1);
        int percentage = mFeedData.getCompletedActions() * 100 / mFeedData.getTotalActions();
        mFeedData.setProgressPercentage(percentage);

        mUserData.removeAction(action.getAction());
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

    Action getUpcoming(int position){
        return mDisplayedUpcoming.get(position);
    }

    Action removeUpcoming(int position){
        mDisplayedUpcoming.remove(position);
        Action removed = mFeedData.getUpcomingActions().remove(position);

        checkActions();

        return removed;
    }

    List<DisplayableGoal> getGoals(){
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
        int count = 0;
        List<DisplayableGoal> src = new ArrayList<>();
        if (hasUserGoals()){
            src.addAll(mUserData.getGoals().values());
        }
        else{
            src.addAll(mFeedData.getSuggestions());
        }
        while (count < LOAD_MORE_COUNT && canLoadMoreGoals()){
            mDisplayedGoals.add(src.get(mDisplayedGoals.size()));
            count++;
        }
        return count;
    }

    boolean canLoadMoreGoals(){
        if (hasUserGoals()){
            return mDisplayedGoals.size() < mUserData.getGoals().size();
        }
        else{
            return mDisplayedGoals.size() < mUserData.getFeedData().getSuggestions().size();
        }
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

    void reload(){
        int size = mDisplayedGoals.size();
        mDisplayedGoals.clear();
        List<UserGoal> userGoals = new ArrayList<>(mUserData.getGoals().values());
        while (size > mDisplayedGoals.size() && canLoadMoreGoals()){
            mDisplayedGoals.add(userGoals.get(mDisplayedGoals.size()));
        }
    }
}
