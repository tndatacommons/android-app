package org.tndata.android.compass.adapter.feed;


/**
 * This class is responsible of telling what types of cards different positions
 * in the feed hold.
 *
 * @author Ismael Alonso
 * @author Honorable mention to Brad Montgomery for his help naming this class. Thanks!
 * @version 1.0.0
 */
final class CardTypes{
    private static DataHandler sDataHandler;
    private static boolean sDisplaySuggestion;


    /**
     * Sets the data object from which the class draws the information required to make
     * distinctions between positions.
     *
     * @param dataHandler the data handler of the adapter.
     */
    static void setDataSource(DataHandler dataHandler){
        sDataHandler = dataHandler;
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
    static boolean hasWelcomeCard(){
        return !sDataHandler.hasGoals();
    }

    /**
     * Tells whether the feed has an up next action card.
     *
     * @return true if there is an up next card, false otherwise.
     */
    static boolean hasUpNextAction(){
        return sDataHandler.getUpNext() != null;
    }

    /**
     * Gets the position of the up next card, which depends on whether there is a welcome card.
     *
     * @return the position of the up next card.
     */
    static int getUpNextPosition(){
        return hasWelcomeCard() ? 2 : 1;
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

    static boolean hasFeedback(){
        return hasUpNextAction() && sDataHandler.hasFeedback();
    }

    /**
     * Gets the position of the feedback card.
     *
     * @return the position of the feedback card.
     */
    static int getFeedbackPosition(){
        if (hasSuggestion()){
            return getSuggestionPosition()+1;
        }
        return getUpNextPosition()+1;
    }

    /**
     * Tells whether a position is that of the feedback card.
     *
     * @param position the position to be checked.
     * @return true if it is the position of the feedback, false otherwise.
     */
    static boolean isFeedback(int position){
        return hasFeedback() && getFeedbackPosition() == position;
    }

    /**
     * Tells whether the feed has a single suggestion.
     *
     * @return true if the feed should display a suggestion.
     */
    static boolean hasSuggestion(){
        return sDisplaySuggestion && sDataHandler.hasGoals();
    }

    /**
     * Gets the position of the suggestion in the feed.
     *
     * @return the position of the suggestion in the feed.
     */
    static int getSuggestionPosition(){
        return getUpNextPosition()+1;
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
     * Tells whether there are upcoming actions.
     *
     * @return true if there are upcoming actions, false otherwise.
     */
    static boolean hasUpcoming(){
        return sDataHandler.hasUpcoming();
    }

    /**
     * Gets the position of the upcoming header card.
     *
     * @return the position of the upcoming header card.
     */
    static int getUpcomingPosition(){
        //TEMP: There might be up-next but no feedback
        if (hasFeedback()){
            return getFeedbackPosition()+1;
        }
        if (hasSuggestion()){
            return getSuggestionPosition()+1;
        }
        if (hasUpNextAction()){
            return getUpNextPosition()+1;
        }
        //If there ain't up next, then there is no feedback and up next displays something else
        return getUpNextPosition()+1;
    }

    /**
     * Tells whether a position is that of the upcoming header card.
     *
     * @param position the position to be checked.
     * @return true if it is the position of the upcoming header, false otherwise.
     */
    static boolean isUpcoming(int position){
        return hasUpcoming() && getUpcomingPosition() == position;
    }

    /**
     * Tells whether a position is that of my goals' header.
     *
     * @return true if the position id that of my goals header item, false otherwise.
     */
    static int getGoalsPosition(){
        //If there are upcoming actions then my goals are right after
        if (hasUpcoming()){
            return getUpcomingPosition()+1;
        }
        //If there ain't upcoming actions then my goals takes its place
        return getUpcomingPosition();
    }

    /**
     * Tells whether a position is that of the my goals header card.
     *
     * @param position the position to be checked.
     * @return true if it is the position of the my goals header, false otherwise.
     */
    static boolean isGoals(int position){
        return getGoalsPosition() == position;
    }

    /**
     * Gets the total number of cards in the feed.
     *
     * @return the number of cards in the feed.
     */
    static int getItemCount(){
        return getGoalsPosition()+1;
    }
}
