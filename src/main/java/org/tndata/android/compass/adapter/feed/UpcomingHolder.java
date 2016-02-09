package org.tndata.android.compass.adapter.feed;

import android.view.View;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.ui.UpcomingContainer;


/**
 * View holder for an action card.
 *
 * @author Ismael Alonso
 * @version 1.0.0
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
    UpcomingHolder(MainFeedAdapter adapter, View rootView){
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

    public void setAnimationsEnabled(boolean enabled){
        mUpcomingContainer.setAnimationsEnabled(enabled);
    }

    void addAction(Action action){
        mUpcomingContainer.addAction(action);
    }

    void updateAction(Action action){
        mUpcomingContainer.updateAction(action);
    }

    void removeAction(Action action){
        mUpcomingContainer.removeAction(action);
    }

    void removeFirstAction(){
        mUpcomingContainer.removeFirstAction();
    }

    void hideFooter(){
        mMore.setVisibility(View.GONE);
    }

    int getItemCount(){
        return mUpcomingContainer.getCount();
    }

    @Override
    public void onActionClick(Action action){
        mAdapter.mListener.onActionSelected(action);
    }

    @Override
    public void onActionOverflowClick(View view, Action action){
        mAdapter.showActionPopup(view, action);
    }
}
