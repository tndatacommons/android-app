package org.tndata.android.compass.adapter.feed;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.ui.ContentContainer;

import java.util.List;


/**
 * View holder for a goal card.
 *
 * @author Ismael Alonso
 * @version 1.1.0
 */
class GoalsHolder
        extends MainFeedViewHolder
        implements
                View.OnClickListener,
                ContentContainer.ContentContainerListener<ContentContainer.ContainerGoal>{

    private TextView mHeader;
    private ContentContainer<ContentContainer.ContainerGoal> mContentContainer;
    private View mMore;


    /**
     * Constructor.
     *
     * @param adapter a reference to the adapter that will handle the holder.
     * @param rootView the root view held by this holder.
     */
    @SuppressWarnings("unchecked")
    GoalsHolder(@NonNull MainFeedAdapter adapter, @NonNull View rootView){
        super(adapter, rootView);

        mHeader = (TextView)rootView.findViewById(R.id.card_goals_header);
        mContentContainer = (ContentContainer<ContentContainer.ContainerGoal>)
                rootView.findViewById(R.id.card_goals_goal_container);
        mContentContainer.setListener(this);
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
    void addGoal(@NonNull ContentContainer.ContainerGoal goal){
        mContentContainer.addContent(goal);
    }

    /**
     * Refreshes the list from the feed data bundle.
     *
     * @param dataSet the new data set.
     */
    void updateGoals(@NonNull List<ContentContainer.ContainerGoal> dataSet){
        mContentContainer.updateContent(dataSet);
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
    public void onContentClick(@NonNull ContentContainer.ContainerGoal goal){
        mAdapter.viewGoal(goal);
    }
}
