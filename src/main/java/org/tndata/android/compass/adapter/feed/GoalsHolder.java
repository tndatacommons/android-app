package org.tndata.android.compass.adapter.feed;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.ui.GoalContainer;


/**
 * View holder for a goal card.
 *
 * @author Ismael Alonso
 * @version 1.1.0
 */
class GoalsHolder
        extends MainFeedViewHolder
        implements View.OnClickListener, GoalContainer.GoalContainerListener{

    private TextView mHeader;
    private GoalContainer mGoalContainer;
    private View mMore;


    /**
     * Constructor.
     *
     * @param adapter a reference to the adapter that will handle the holder.
     * @param rootView the root view held by this holder.
     */
    GoalsHolder(@NonNull MainFeedAdapter adapter, @NonNull View rootView){
        super(adapter, rootView);

        mHeader = (TextView)rootView.findViewById(R.id.card_goals_header);
        mGoalContainer = (GoalContainer)rootView.findViewById(R.id.card_goals_goal_container);
        mGoalContainer.setGoalListener(this);
        mMore = rootView.findViewById(R.id.card_goals_more);
        mMore.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        mAdapter.moreGoals();
    }

    /**
     * Enables or disables animations.
     *
     * @param enabled true to enable animations, false to disable them.
     */
    public void setAnimationsEnabled(boolean enabled){
        mGoalContainer.setAnimationsEnabled(enabled);
    }

    /**
     * Binds a title to the goals card.
     *
     * @param title the title to be used as header.
     */
    void bind(@NonNull String title){
        mHeader.setText(title);
    }

    /**
     * Adds a goal to the list.
     *
     * @param goal the goal to be added.
     */
    void addGoal(@NonNull DisplayableGoal goal){
        mGoalContainer.addGoal(goal);
    }

    /**
     * Refreshes the list from the feed data bundle.
     *
     * @param feedData a reference to the geed data bundle.
     */
    void updateGoals(@NonNull FeedData feedData){
        mGoalContainer.updateGoals(feedData);
    }

    /**
     * Hides the footer of the card.
     */
    void hideFooter(){
        mMore.setVisibility(View.GONE);
    }

    /**
     * Gets the number of items in the list.
     *
     * @return the number of items in the list.
     */
    int getItemCount(){
        return mGoalContainer.getCount();
    }

    @Override
    public void onGoalClick(@NonNull DisplayableGoal goal){
        mAdapter.viewGoal(goal);
    }
}
