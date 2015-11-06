package org.tndata.android.compass.adapter.feed;

import android.view.View;
import android.widget.TextView;

import org.tndata.android.compass.R;

import at.grabner.circleprogress.CircleProgressView;


/**
 * View holder for the up next card.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
final class UpNextHolder extends MainFeedViewHolder implements View.OnClickListener{
    //Header
    TextView mHeader;
    View mOverflow;

    //Indicator
    CircleProgressView mIndicator;

    //No actions content
    View mNoActionsContainer;
    TextView mNoActionsTitle;
    TextView mNoActionsSubtitle;

    //Action content
    View mContentContainer;
    TextView mAction;
    TextView mGoal;

    //Footer
    TextView mIndicatorCaption;
    TextView mTime;


    /**
     * Constructor.
     *
     * @param adapter a reference to the adapter that will handle the holder.
     * @param rootView the root view held by the holder.
     */
    UpNextHolder(MainFeedAdapter adapter, View rootView){
        super(adapter, rootView);

        mHeader = (TextView)rootView.findViewById(R.id.up_next_header);
        mOverflow = rootView.findViewById(R.id.up_next_overflow_box);

        mIndicator = (CircleProgressView)rootView.findViewById(R.id.up_next_indicator);

        mNoActionsContainer = rootView.findViewById(R.id.up_next_no_actions);
        mNoActionsTitle = (TextView)rootView.findViewById(R.id.up_next_no_actions_title);
        mNoActionsSubtitle = (TextView)rootView.findViewById(R.id.up_next_no_actions_subtitle);

        mContentContainer = rootView.findViewById(R.id.up_next_content);
        mAction = (TextView)rootView.findViewById(R.id.up_next_action);
        mGoal = (TextView)rootView.findViewById(R.id.up_next_goal);

        mIndicatorCaption = (TextView)rootView.findViewById(R.id.up_next_indicator_caption);
        mTime = (TextView)rootView.findViewById(R.id.up_next_time);

        rootView.setOnClickListener(this);
        mOverflow.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        mAdapter.setSelectedItem(getAdapterPosition());
        switch (view.getId()){
            case R.id.up_next_overflow_box:
                mAdapter.showActionPopup(view, getAdapterPosition());
                break;

            default:
                if (CardTypes.hasUpNextAction()){
                    mAdapter.mListener.onActionSelected(mAdapter.getDataHandler().getUpNext());
                }
        }
    }
}
