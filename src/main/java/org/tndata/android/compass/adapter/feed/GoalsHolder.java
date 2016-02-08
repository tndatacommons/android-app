package org.tndata.android.compass.adapter.feed;

import android.view.View;
import android.widget.TextView;

import org.tndata.android.compass.R;
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
    GoalsHolder(MainFeedAdapter adapter, View rootView){
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
     * Binds a displayable goal to this holder.
     */
    void bind(String title){
        mHeader.setText(title);
    }

    void addGoal(DisplayableGoal goal){
        mGoalContainer.addGoal(goal);
    }

    void hideFooter(){
        mMore.setVisibility(View.GONE);
    }

    int getItemCount(){
        return mGoalContainer.getCount();
    }

    @Override
    public void onGoalClick(DisplayableGoal goal){
        mAdapter.viewGoal(goal);
    }
}
