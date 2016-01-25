package org.tndata.android.compass.adapter.feed;

import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.UserAction;


/**
 * Listener interface for the main feed adapter.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public interface MainFeedAdapterListener{
    /**
     * Called when the user data is null.
     */
    void onNullData();

    /**
     * Called when the welcome card is tapped.
     */
    void onInstructionsSelected();

    void onSuggestionDismissed();
    void onSuggestionOpened(Goal goal);

    /**
     * Called when a goal from a goal list is selected.
     *
     * @param goal the selected goal.
     */
    void onGoalSelected(DisplayableGoal goal);

    /**
     * Called when the feedback card is tapped.
     *
     * @param goal the goal being displayed at the feedback card.
     */
    void onFeedbackSelected(Goal goal);

    /**
     * Called when an action card is tapped.
     *
     * @param action the action being displayed at the card.
     */
    void onActionSelected(UserAction action);

    /**
     * Called when a trigger is selected from the context menu.
     *
     * @param action the action being displayed at the card.
     */
    void onTriggerSelected(UserAction action);
}
