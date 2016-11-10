package org.tndata.android.compass.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.compass.model.FeedData;

public class StreakView extends LinearLayout {

    private static final String TAG = "StreakView";
    private Context mContext;
    private FeedData.Streak mStreak;

    private LinearLayout mLayoutContainer;
    private RelativeLayout mStreakBarContainer;
    private View mStreakBar;
    private RelativeLayout mImageContainer;
    private ImageView mStreakCheckImage;
    private TextView mStreakDay;

    public StreakView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.streak, this, true);
    }

    public void setStreak(FeedData.Streak streak) {
        mStreak = streak;
        if(mStreak != null) {
            setOrientation(LinearLayout.VERTICAL);
            setGravity(Gravity.CENTER_VERTICAL);

            mLayoutContainer = (LinearLayout) getChildAt(0);
            mStreakBarContainer = (RelativeLayout) mLayoutContainer.getChildAt(0);
            mStreakBar = mStreakBarContainer.getChildAt(0);
            setBarHeight(mStreak.getCount());

            mImageContainer = (RelativeLayout) mLayoutContainer.getChildAt(1);
            mStreakCheckImage = (ImageView) mImageContainer.getChildAt(0);
            mStreakDay = (TextView) mLayoutContainer.getChildAt(2);
            mStreakDay.setText(mStreak.getDayAbbrev());

            // Only display the check mark if the item was completed.
            if (!mStreak.completed()) {
                mStreakCheckImage.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setBarHeight(int count) {
        if(mStreakBar != null) {
            final float scale = getContext().getResources().getDisplayMetrics().density;
            int width = mStreakBar.getLayoutParams().width;
            int pixels = (int) ((count * 20) * scale + 2.0f);
            int height = pixels + 5;

            mStreakBar.getLayoutParams().height = height;
            Animation animation = new ScaleAnimation(0, 1, 0, 1, width, height);
            animation.setDuration(1000);
            mStreakBar.startAnimation(animation);
        }
    }
}