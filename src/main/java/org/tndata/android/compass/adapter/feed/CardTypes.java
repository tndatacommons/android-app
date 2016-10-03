package org.tndata.android.compass.adapter.feed;

import org.tndata.android.compass.model.FeedData;


/**
 * This class is responsible of telling what types of cards different positions
 * in the feed hold.
 *
 * @author Ismael Alonso
 * @author Honorable mention to Brad Montgomery for his help in naming this class. Thanks!
 * @version 1.1.0
 */
final class CardTypes{
    private static FeedData sFeedData;
    private static boolean sDisplaySuggestion;


    /**
     * Sets the data object from which the class draws the information required to make
     * distinctions between positions.
     *
     * @param feedData the data handler of the adapter.
     */
    static void setDataSource(FeedData feedData){
        sFeedData = feedData;
        sDisplaySuggestion = false;
    }

    /**
     * Changes the suggestion display flag.
     *
     * @param displaySuggestion true if a suggestion should be included in the dataset.
     */
    static void displaySuggestion(boolean displaySuggestion){
        sDisplaySuggestion = displaySuggestion;
    }

    /**
     * Tells whether the feed has a welcome card, which happens when the user has no goals.
     *
     * @return true if there is a welcome card, false otherwise.
     */
    static boolean hasWelcome(){
        return sFeedData.getGoals().isEmpty();
    }

    static int getWelcome(){
        return 1;
    }

    static boolean isWelcome(int position){
        return hasWelcome() && position == getWelcome();
    }

    /**
     * Gets the position of the up next card, which depends on whether there is a welcome card.
     *
     * @return the position of the up next card.
     */
    static int getUpNextPosition(){
        return hasWelcome() ? 2 : 1;
    }

    /**
     * Tells whether a position is that of the up next card.
     *
     * @param position the position to be checked.
     * @return true if it is the position of up next, false otherwise.
     */
    static boolean isUpNext(int position){
        return getUpNextPosition() == position;
    }

    /**
     * Gets the position of the streaks card.
     *
     * @return the position of the streaks card.
     */
    static int getStreaksPosition(){
        return getUpNextPosition()+1;
    }

    /**
     * Tells whether the feed should display a streaks card.
     *
     * @return true if there is a streaks card, false otherwise.
     */
    static boolean hasStreaks(){
        return sFeedData.hasStreaks();
    }

    /**
     * Tells whether a position is that of the streaks card.
     *
     * @param position the position to be checked.
     * @return true if it is the position of the streaks, false otherwise.
     */
    static boolean isStreaks(int position){
        return hasStreaks() && getStreaksPosition() == position;
    }

    /**
     * Tells whether the feed has a single suggestion.
     *
     * @return true if the feed should display a suggestion.
     */
    static boolean hasSuggestion(){
        return sDisplaySuggestion && !sFeedData.getGoals().isEmpty();
    }

    /**
     * Gets the position of the suggestion in the feed.
     *
     * @return the position of the suggestion in the feed.
     */
    static int getSuggestionPosition(){
        return getStreaksPosition()+1;
    }

    /**
     * Tells whether the position provided belongs to a suggestion.
     *
     * @param position the position to be checked.
     * @return true if it is a single suggestion card.
     */
    static boolean isSuggestion(int position){
        return hasSuggestion() && position == getSuggestionPosition();
    }

    /**
     * Gets the position of the reward card.
     *
     * @return the position of the reward card.
     */
    static int getRewardPosition(){
        if (hasSuggestion()){
            return getSuggestionPosition()+1;
        }
        if (hasStreaks()){
            return getStreaksPosition()+1;
        }
        return getUpNextPosition()+1;
    }

    /**
     * Tells whether a position is that of the reward card.
     *
     * @param position the position to be checked.
     * @return true if it is the position of the reward, false otherwise.
     */
    static boolean isReward(int position){
        return getRewardPosition() == position;
    }

    /**
     * Gets the position of the progress card.
     *
     * @return the position of the progress card.
     */
    static int getProgressPosition(){
        return getRewardPosition()+1;
    }

    /**
     * Tells whether a position is the position of the progress card.
     *
     * @param position the position to be checked.
     * @return true if it is the position of the progress card, false otherwise.
     */
    static boolean isProgress(int position){
        return getProgressPosition() == position;
    }

    /**
     * Tells whether there are goal suggestions.
     *
     * @return true if there are goal suggestions, false otherwise.
     */
    static boolean hasGoalSuggestions(){
        return !sFeedData.getSuggestions().isEmpty() && sFeedData.getGoals().isEmpty();
    }

    /**
     * Tells whether there are my goals.
     *
     * @return true if there are my goals, false otherwise.
     */
    static boolean hasMyGoals(){
        return !sFeedData.getGoals().isEmpty();
    }

    /**
     * Tells whether there is a goals card or not.
     *
     * @return true if there is a goals card, false otherwise.
     */
    static boolean hasGoals(){
        return hasGoalSuggestions() || hasMyGoals();
    }

    /**
     * Gets the position of a goals card.
     *
     * @return the position of a goals card.
     */
    static int getGoalsPosition(){
        return getProgressPosition()+1;
    }

    /**
     * Tells whether a position is that of the goal suggestions card.
     *
     * @param position the position to be checked.
     * @return true if it is the position of the goal suggestions card, false otherwise.
     */
    static boolean isGoalSuggestions(int position){
        return hasGoalSuggestions() && getGoalsPosition() == position;
    }

    /**
     * Tells whether a position is that of the my goals card.
     *
     * @param position the position to be checked.
     * @return true if it is the position of the my goals card, false otherwise.
     */
    static boolean isMyGoals(int position){
        return hasMyGoals() && getGoalsPosition() == position;
    }

    static boolean isGoals(int position){
        return (hasMyGoals() || hasGoalSuggestions()) && getGoalsPosition() == position;
    }

    /**
     * Gets the total number of cards in the feed.
     *
     * @return the number of cards in the feed.
     */
    static int getItemCount(){
        if (hasGoals()){
            return getGoalsPosition()+1;
        }
        return getProgressPosition()+1;
    }
}
