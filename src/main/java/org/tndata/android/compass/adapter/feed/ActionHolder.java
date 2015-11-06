package org.tndata.android.compass.adapter.feed;

import android.view.View;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;


/**
 * View holder for an action card.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
class ActionHolder extends MainFeedViewHolder implements View.OnClickListener{
    View mOverflow;
    TextView mAction;
    TextView mGoal;
    TextView mTime;


    /**
     * Constructor.
     *
     * @param adapter a reference to the adapter that will handle the holder.
     * @param rootView the root view held by the holder.
     */
    ActionHolder(MainFeedAdapter adapter, View rootView){
        super(adapter, rootView);

        mOverflow = rootView.findViewById(R.id.action_overflow_box);
        mAction = (TextView)rootView.findViewById(R.id.action_title);
        mGoal = (TextView)rootView.findViewById(R.id.action_goal);
        mTime = (TextView)rootView.findViewById(R.id.action_time);

        rootView.setOnClickListener(this);
        mOverflow.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        mAdapter.setSelectedItem(getAdapterPosition());
        int index = getAdapterPosition()-(CardTypes.getUpcomingHeaderPosition()+1);
        Action action = mAdapter.getDataHandler().getUpcoming().get(index);
        switch (view.getId()){
            case R.id.action_overflow_box:
                mAdapter.showActionPopup(view, getAdapterPosition());
                break;

            default:
                mAdapter.mListener.onActionSelected(action);
        }
    }
}
