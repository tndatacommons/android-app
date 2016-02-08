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
class UpcomingHolder extends MainFeedViewHolder implements View.OnClickListener{
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
        mMore = rootView.findViewById(R.id.card_upcoming_more);
        mMore.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        mAdapter.moreActions();
    }

    void addAction(Action action){
        mUpcomingContainer.addAction(action);
    }

    void hideFooter(){
        mMore.setVisibility(View.GONE);
    }

    int getItemCount(){
        return mUpcomingContainer.getCount();
    }
}
