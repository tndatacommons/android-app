package org.tndata.android.compass.adapter.feed;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.model.UpcomingAction;


/**
 * View holder for the up next card.
 *
 * @author Ismael Alonso
 * @version 1.1.0
 */
final class UpNextHolder extends MainFeedAdapter.ViewHolder implements View.OnClickListener{
    //The action bound to the holder
    private UpcomingAction mAction;

    //No actions content
    private View mNoActionsContainer;
    private TextView mNoActionsTitle;
    private TextView mNoActionsSubtitle;

    //Action content
    private View mContentContainer;
    private TextView mActionTitle;
    private TextView mGoalTitle;


    /**
     * Constructor.
     *
     * @param adapter a reference to the adapter that will handle the holder.
     * @param rootView the root view held by the holder.
     */
    UpNextHolder(MainFeedAdapter adapter, View rootView){
        super(adapter, rootView);

        mNoActionsContainer = rootView.findViewById(R.id.up_next_no_actions);
        mNoActionsTitle = (TextView)rootView.findViewById(R.id.up_next_no_actions_title);
        mNoActionsSubtitle = (TextView)rootView.findViewById(R.id.up_next_no_actions_subtitle);

        mContentContainer = rootView.findViewById(R.id.up_next_content);
        mActionTitle = (TextView)rootView.findViewById(R.id.up_next_action);
        mGoalTitle = (TextView)rootView.findViewById(R.id.up_next_goal);

        rootView.setOnClickListener(this);
    }

    /**
     * Binds an action to the holder.
     *
     * @param action the action to be bound to the holder.
     */
    void bind(@Nullable UpcomingAction action, @NonNull FeedData.Progress progress){
        mAction = action;

        if (action == null){
            mNoActionsContainer.setVisibility(View.VISIBLE);
            mContentContainer.setVisibility(View.GONE);

            if (progress.getTotalActions() != 0){
                mNoActionsTitle.setText(R.string.card_up_next_title_completed);
                mNoActionsSubtitle.setText(R.string.card_up_next_subtitle_completed);
            }
            else{
                mNoActionsTitle.setText(R.string.card_up_next_title_empty);
                mNoActionsSubtitle.setText(R.string.card_up_next_subtitle_empty);
            }
        }
        else{
            mNoActionsContainer.setVisibility(View.GONE);
            mContentContainer.setVisibility(View.VISIBLE);

            mActionTitle.setText(action.getTitle());
            String goalTitle = action.getGoalTitle().substring(0, 1).toLowerCase();
            goalTitle += action.getGoalTitle().substring(1);
            mGoalTitle.setText(mAdapter.mContext.getString(R.string.card_up_next_goal_title, goalTitle));
        }
    }

    @Override
    public void onClick(View view){
        if (mAction != null){
            mAdapter.mListener.onActionSelected(mAction);
        }
    }
}
