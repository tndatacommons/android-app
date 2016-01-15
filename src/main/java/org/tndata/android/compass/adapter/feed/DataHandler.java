package org.tndata.android.compass.adapter.feed;

import org.tndata.android.compass.model.Category;
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

    private List<UserAction> mDisplayedUpcoming;
    private List<UserGoal> mDisplayedUserGoals;
    private List<Goal> mDisplayedGoalSuggestions;


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

        mDisplayedUserGoals = new ArrayList<>();
        mDisplayedGoalSuggestions = new ArrayList<>();
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

    UserAction getUpNext(){
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

    List<UserAction> getUpcoming(){
        return mDisplayedUpcoming;
    }

    UserAction getUpcoming(int position){
        return mDisplayedUpcoming.get(position);
    }

    UserAction removeUpcoming(int position){
        mDisplayedUpcoming.remove(position);
        UserAction removed = mFeedData.getUpcomingActions().remove(position);

        checkActions();

        return removed;
    }

    Category getActionCategory(UserAction action){
        Category category = null;
        if (action.getPrimaryGoal() != null){
            //category = action.getPrimaryGoal().getPrimaryCategory();
            if (category == null){
                UserGoal goal = mUserData.getGoal(action.getPrimaryGoal());
                if (goal.getCategories().size() > 0){
                    category = goal.getCategories().get(0).getCategory();
                }
            }
        }
        return category;
    }

    List<UserGoal> getUserGoals(){
        return mDisplayedUserGoals;
    }

    List<Goal> getSuggestions(){
        return mDisplayedGoalSuggestions;
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
        if (hasUserGoals()){
            List<UserGoal> userGoals = new ArrayList<>(mUserData.getGoals().values());
            while (count < LOAD_MORE_COUNT && canLoadMoreGoals()){
                mDisplayedUserGoals.add(userGoals.get(mDisplayedUpcoming.size()));
                count++;
            }
        }
        else{
            while (count < LOAD_MORE_COUNT && canLoadMoreGoals()){
                mDisplayedGoalSuggestions.add(mFeedData.getSuggestions().get(mDisplayedUpcoming.size()));
                count++;
            }
        }
        return count;
    }

    boolean canLoadMoreGoals(){
        if (hasUserGoals()){
            return mDisplayedUserGoals.size() < mUserData.getGoals().size();
        }
        else{
            return mDisplayedGoalSuggestions.size() < mUserData.getFeedData().getSuggestions().size();
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
}
