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
    private static DataHandler mDataHandler;


    /**
     * Sets the data object from which the class draws the information required to make
     * distinctions between positions.
     *
     * @param dataHandler the data handler of the adapter.
     */
    static void setDataSource(DataHandler dataHandler){
        mDataHandler = dataHandler;
    }

    /**
     * Tells whether the feed has a welcome card, which happens when the user has no goals.
     *
     * @return true if there is a welcome card, false otherwise.
     */
    static boolean hasWelcomeCard(){
        return !mDataHandler.hasGoals();
    }

    /**
     * Tells whether the feed has an up next action card.
     *
     * @return true if there is an up next card, false otherwise.
     */
    static boolean hasUpNextAction(){
        return mDataHandler.getUpNext() != null;
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

    /**
     * Gets the position of the feedback card.
     *
     * @return the position of the feedback card.
     */
    static int getFeedbackPosition(){
        return getUpNextPosition()+1;
    }

    /**
     * Tells whether a position is that of the feedback card.
     *
     * @param position the position to be checked.
     * @return true if it is the position of the feedback, false otherwise.
     */
    static boolean isFeedback(int position){
        return hasUpNextAction() && getFeedbackPosition() == position;
    }

    /**
     * Tells whether there are upcoming actions.
     *
     * @return true if there are upcoming actions, false otherwise.
     */
    static boolean hasUpcoming(){
        return !mDataHandler.getUpcoming().isEmpty();
    }

    /**
     * Gets the position of the upcoming header card.
     *
     * @return the position of the upcoming header card.
     */
    static int getUpcomingHeaderPosition(){
        //If there is up next, then there is feedback
        if (hasUpNextAction()){
            return getFeedbackPosition()+1;
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
    static boolean isUpcomingHeader(int position){
        return hasUpcoming() && getUpcomingHeaderPosition() == position;
    }

    static boolean hasUpcomingFooter(){
        return mDataHandler.canLoadMoreActions();
    }

    /**
     * Gets the position of the upcoming footer.
     *
     * @return the position of the upcoming footer.
     */
    static int getUpcomingFooterPosition(){
        return getUpcomingHeaderPosition()+mDataHandler.getUpcoming().size()+1;
    }

    /**
     * Tells whether a position is that of the upcoming actions last item.
     *
     * @param position the position to be checked.
     * @return true if the position id that of upcoming's last item, false otherwise.
     */
    static boolean isUpcomingFooter(int position){
        return hasUpcoming() && hasUpcomingFooter() && position == getUpcomingFooterPosition();
    }

    /**
     * Tells whether a position is that of an upcoming's inner item.
     *
     * @param position the position to be checked.
     * @return true if the position is that of an upcoming's inner item, false otherwise.
     */
    static boolean isUpcomingAction(int position){
        return hasUpcoming() &&
                position > getUpcomingHeaderPosition() && position < getUpcomingFooterPosition();
    }

    /**
     * Tells whether a position is that of my goals' header.
     *
     * @return true if the position id that of my goals header item, false otherwise.
     */
    static int getMyGoalsHeaderPosition(){
        //If there are upcoming actions then my goals are right after
        if (hasUpcoming()){
            if (hasUpcomingFooter()){
                return getUpcomingFooterPosition() + 1;
            }
            else{
                return getUpcomingFooterPosition();
            }
        }
        //If there ain't upcoming actions then my goals takes its place
        return getUpcomingHeaderPosition();
    }

    /**
     * Tells whether a position is that of the my goals header card.
     *
     * @param position the position to be checked.
     * @return true if it is the position of the my goals header, false otherwise.
     */
    static boolean isMyGoalsHeader(int position){
        return getMyGoalsHeaderPosition() == position;
    }

    static boolean hasMyGoalsFooter(){
        return mDataHandler.canLoadMoreGoals();
    }

    /**
     * Gets the position of my goals footer.
     *
     * @return the position of my goals footer.
     */
    static int getMyGoalsFooterPosition(){
        //My goals can be either my goals or suggestions
        return getMyGoalsHeaderPosition() + mDataHandler.getGoals().size()+1;
    }

    /**
     * Tells whether the position is that of my goals footer.
     *
     * @param position the position to be checked.
     * @return true if the position is that of my goals footer, false otherwise.
     */
    static boolean isMyGoalsFooter(int position){
        return hasMyGoalsFooter() && getMyGoalsFooterPosition() == position;
    }

    /**
     * Tells whether the position is that of a my goals inner item.
     *
     * @param position the position to be checked.
     * @return true if the position is that of a my goals inner item, false otherwise.
     */
    static boolean isGoal(int position){
        return position > getMyGoalsHeaderPosition() && position < getMyGoalsFooterPosition();
    }


    /*-------------------------------------------*
     * METHODS TO DISTINGUISH HIGHER LEVEL TYPES *
     *-------------------------------------------*/

    /**
     * Tells whether the card is at the top of a combined stack.
     *
     * @param position the position to be checked.
     * @return true if the position is that of a card at the top of a combined stack,
     *         false otherwise.
     */
    static boolean isTopCard(int position){
        return isUpcomingHeader(position) || isMyGoalsHeader(position);
    }

    /**
     * Tells whether the card is in the middle of a combined stack.
     *
     * @param position the position to be checked.
     * @return true if the position is that of a card in the middle of a combined stack,
     *         false otherwise.
     */
    static boolean isMiddleCard(int position){
        return isUpcomingAction(position) || isGoal(position);
    }

    /**
     * Tells whether the card is at the bottom of a combined stack.
     *
     * @param position the position to be checked.
     * @return true if the position is that of a card at the bottom of a combined stack,
     *         false otherwise.
     */
    static boolean isBottomCard(int position){
        if (isUpcomingFooter(position)){
            return true;
        }
        else if (isMyGoalsFooter(position)){
            return true;
        }
        else if (!hasUpcomingFooter() && position == getUpcomingFooterPosition()-1){
            return true;
        }
        else if (!hasMyGoalsFooter() && position == getMyGoalsFooterPosition()-1){
            return true;
        }
        return false;
    }
}
