package org.tndata.android.compass.adapter.feed;

import android.view.View;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.ui.StreakView;

import java.util.List;


/**
 * View holder for the streaks card.
 *
 * @author Brad Montgomery
 * @version 1.0.0
 */
class StreaksHolder extends MainFeedAdapter.ViewHolder implements View.OnClickListener{
    private List<FeedData.Streak> mStreaks;

    // TODO: just hard-coding 7 of these for now.
    private StreakView day0;
    private StreakView day1;
    private StreakView day2;
    private StreakView day3;
    private StreakView day4;
    private StreakView day5;
    private StreakView day6;


    /**
     * Constructor.
     *
     * @param adapter a reference to the adapter that will handle the holder.
     * @param rootView the root view held by this holder.
     */
    StreaksHolder(MainFeedAdapter adapter, View rootView){
        super(adapter, rootView);
        day0 = (StreakView) rootView.findViewById(R.id.streak_day0);
        day1 = (StreakView) rootView.findViewById(R.id.streak_day1);
        day2 = (StreakView) rootView.findViewById(R.id.streak_day2);
        day3 = (StreakView) rootView.findViewById(R.id.streak_day3);
        day4 = (StreakView) rootView.findViewById(R.id.streak_day4);
        day5 = (StreakView) rootView.findViewById(R.id.streak_day5);
        day6 = (StreakView) rootView.findViewById(R.id.streak_day6);
        rootView.setOnClickListener(this);
    }

    /**
     * Retrieves the values of the feedback from the feed data bundle.
     *
     * @param streaks the list of streaks from the FeedData
     */
    void bind(List<FeedData.Streak> streaks){
        mStreaks = streaks;
        if(streaks.size() == 7) {
            day0.setStreak(streaks.get(0));
            day1.setStreak(streaks.get(1));
            day2.setStreak(streaks.get(2));
            day3.setStreak(streaks.get(3));
            day4.setStreak(streaks.get(4));
            day5.setStreak(streaks.get(5));
            day6.setStreak(streaks.get(6));
        }
    }

    @Override
    public void onClick(View v){
        mAdapter.mListener.onStreaksSelected(mStreaks);
    }
}
