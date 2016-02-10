package org.tndata.android.compass.adapter.feed;

import android.support.annotation.NonNull;
import android.view.View;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.ui.UpcomingContainer;


/**
 * View holder for the upcoming card.
 *
 * @author Ismael Alonso
 * @version 2.0.0
 */
class UpcomingHolder
        extends MainFeedViewHolder
        implements View.OnClickListener, UpcomingContainer.UpcomingContainerListener{

    private UpcomingContainer mUpcomingContainer;
    private View mMore;


    /**
     * Constructor.
     *
     * @param adapter a reference to the adapter that will handle the holder.
     * @param rootView the root view held by the holder.
     */
    UpcomingHolder(@NonNull MainFeedAdapter adapter, @NonNull View rootView){
        super(adapter, rootView);

        mUpcomingContainer = (UpcomingContainer)rootView.findViewById(R.id.card_upcoming_action_container);
        mUpcomingContainer.setUpcomingListener(this);
        mMore = rootView.findViewById(R.id.card_upcoming_more);
        mMore.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        mAdapter.moreActions();
    }

    /**
     * Enables or disables animations.
     *
     * @param enabled true to enable animations, false to disable them.
     */
    public void setAnimationsEnabled(boolean enabled){
        mUpcomingContainer.setAnimationsEnabled(enabled);
    }

    /**
     * Adds an action to the list,
     *
     * @param action the action to be added.
     */
    void addAction(@NonNull Action action){
        mUpcomingContainer.addAction(action);
    }

    /**
     * Updates a specific action in the list.
     *
     * @param action the action to be updated.
     */
    void updateAction(Action action){
        mUpcomingContainer.updateAction(action);
    }

    /**
     * Refreshes the list of actions to be updated.
     *
     * @param feedData a reference to the feed data. bundle.
     */
    void updateActions(@NonNull FeedData feedData){
        mUpcomingContainer.updateActions(feedData);
    }

    /**
     * Removes an action from the list.
     *
     * @param action the action to be removed.
     */
    void removeAction(@NonNull Action action){
        mUpcomingContainer.removeAction(action);
    }

    /**
     * Removes the first action in the list.
     */
    void removeFirstAction(){
        mUpcomingContainer.removeFirstAction();
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
        return mUpcomingContainer.getCount();
    }

    @Override
    public void onActionClick(@NonNull Action action){
        mAdapter.mListener.onActionSelected(action);
    }

    @Override
    public void onActionOverflowClick(@NonNull View view, @NonNull Action action){
        mAdapter.showActionPopup(view, action);
    }
}
