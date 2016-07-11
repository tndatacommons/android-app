package org.tndata.android.compass.adapter.feed;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.FeedData;

import java.util.List;


/**
 * View holder for the streaks card.
 *
 * @author Brad Montgomery
 * @version 1.0.0
 */
class StreaksHolder extends MainFeedAdapter.ViewHolder implements View.OnClickListener{
    private List<FeedData.Streak> mStreaks;

    private ImageView mIcon;
    private TextView mTitle;
    private TextView mSubtitle;


    /**
     * Constructor.
     *
     * @param adapter a reference to the adapter that will handle the holder.
     * @param rootView the root view held by this holder.
     */
    StreaksHolder(MainFeedAdapter adapter, View rootView){
        super(adapter, rootView);

        // TODO: revise layout file, and bind card's components here
        mIcon = (ImageView)rootView.findViewById(R.id.card_feedback_icon);
        mTitle = (TextView)rootView.findViewById(R.id.card_feedback_title);
        mSubtitle = (TextView)rootView.findViewById(R.id.card_feedback_subtitle);

        rootView.setOnClickListener(this);
    }

    /**
     * Retrieves the values of the feedback from the feed data bundle.
     *
     * @param streaks the list of streaks from the FeedData
     */
    void bind(List<FeedData.Streak> streaks){
        mStreaks = streaks;

        // TODO: Set the layout's values from data in streaks
//        mIcon.setImageResource(feedback.getFeedbackIcon());
        mTitle.setText("Wooooo Streaks");
        mSubtitle.setText("oh yeah, man!");
    }

    @Override
    public void onClick(View v){
        mAdapter.mListener.onStreaksSelected(mStreaks);
    }
}
