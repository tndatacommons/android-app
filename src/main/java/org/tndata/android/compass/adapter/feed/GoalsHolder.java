package org.tndata.android.compass.adapter.feed;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.ui.ContentContainer;


/**
 * View holder for a goal card.
 *
 * @author Ismael Alonso
 * @version 1.1.0
 */
class GoalsHolder
        extends MainFeedViewHolder
        implements View.OnClickListener, ContentContainer.ContentContainerListener{

    private TextView mHeader;
    private ContentContainer mContentContainer;
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
        mContentContainer = (ContentContainer)rootView.findViewById(R.id.card_goals_goal_container);
        mContentContainer.setGoalListener(this);
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
        mContentContainer.setAnimationsEnabled(enabled);
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
    void addGoal(@NonNull ContentContainer.ContainerDisplayable goal){
        mContentContainer.addGoal(goal);
    }

    /**
     * Refreshes the list from the feed data bundle.
     *
     * @param feedData a reference to the geed data bundle.
     */
    void updateGoals(@NonNull FeedData feedData){
        mContentContainer.updateContent(feedData);
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
        return mContentContainer.getCount();
    }

    @Override
    public void onContentClick(@NonNull ContentContainer.ContainerDisplayable goal){
        mAdapter.viewGoal(goal);
    }
}
