package org.tndata.android.compass.adapter.feed;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.model.UpcomingAction;
import org.tndata.android.compass.util.CompassUtil;

import java.util.Calendar;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;


/**
 * View holder for the up next card.
 *
 * @author Ismael Alonso
 * @version 1.1.0
 */
final class UpNextHolder extends MainFeedAdapter.ViewHolder implements View.OnClickListener{
    //The action bound to the holder
    private UpcomingAction mAction;

    //Header
    private TextView mHeader;

    //Indicator
    private CircleProgressView mIndicator;

    //No actions content
    private View mNoActionsContainer;
    private TextView mNoActionsTitle;
    private TextView mNoActionsSubtitle;

    //Action content
    private View mContentContainer;
    private TextView mActionTitle;
    private TextView mGoalTitle;

    //Footer
    private TextView mIndicatorCaption;
    private TextView mTime;


    /**
     * Constructor.
     *
     * @param adapter a reference to the adapter that will handle the holder.
     * @param rootView the root view held by the holder.
     */
    UpNextHolder(MainFeedAdapter adapter, View rootView){
        super(adapter, rootView);

        mHeader = (TextView)rootView.findViewById(R.id.up_next_header);

        mIndicator = (CircleProgressView)rootView.findViewById(R.id.up_next_indicator);

        mNoActionsContainer = rootView.findViewById(R.id.up_next_no_actions);
        mNoActionsTitle = (TextView)rootView.findViewById(R.id.up_next_no_actions_title);
        mNoActionsSubtitle = (TextView)rootView.findViewById(R.id.up_next_no_actions_subtitle);

        mContentContainer = rootView.findViewById(R.id.up_next_content);
        mActionTitle = (TextView)rootView.findViewById(R.id.up_next_action);
        mGoalTitle = (TextView)rootView.findViewById(R.id.up_next_goal);

        mIndicatorCaption = (TextView)rootView.findViewById(R.id.up_next_indicator_caption);
        mTime = (TextView)rootView.findViewById(R.id.up_next_time);

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
                mHeader.setText(R.string.card_up_next_header_completed);
                mNoActionsTitle.setText(R.string.card_up_next_title_completed);
                mNoActionsSubtitle.setText(R.string.card_up_next_subtitle_completed);
            }
            else{
                mHeader.setText(R.string.card_up_next_header);
                mNoActionsTitle.setText(R.string.card_up_next_title_empty);
                mNoActionsSubtitle.setText(R.string.card_up_next_subtitle_empty);
            }
            mTime.setText("");
        }
        else{
            mNoActionsContainer.setVisibility(View.GONE);
            mContentContainer.setVisibility(View.VISIBLE);

            mHeader.setText(R.string.card_up_next_header);
            mActionTitle.setText(action.getTitle());
            String goalTitle = action.getGoalTitle().substring(0, 1).toLowerCase();
            goalTitle += action.getGoalTitle().substring(1);
            mGoalTitle.setText(mAdapter.mContext.getString(R.string.card_up_next_goal_title, goalTitle));
            mTime.setText(action.getTriggerDisplay());
        }

        mIndicator.setAutoTextSize(true);
        mIndicator.setValue(progress.getProgressPercentage());
        mIndicator.setTextMode(TextMode.TEXT);
        mIndicator.setValueAnimated(0, progress.getProgressPercentage(), 1500);
        @StringRes int capRes = R.string.card_up_next_indicator_caption;
        mIndicatorCaption.setText(mAdapter.mContext.getString(capRes, progress.getProgressFraction()));
        Calendar calendar = Calendar.getInstance();
        String month = CompassUtil.getMonthString(calendar.get(Calendar.MONTH) + 1);
        mIndicator.setText(month + " " + calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(View view){
        if (mAction != null){
            mAdapter.mListener.onActionSelected(mAction);
        }
    }
}
