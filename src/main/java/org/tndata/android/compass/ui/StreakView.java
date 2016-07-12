package org.tndata.android.compass.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.FeedData;

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
    }

    public void setStreak(FeedData.Streak streak) {
        mStreak = streak;
        init();
    }

    public void init() {
        if(mStreak != null) {
            setOrientation(LinearLayout.VERTICAL);
            setGravity(Gravity.CENTER_VERTICAL);

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.streak, this, true);

            mLayoutContainer = (LinearLayout) getChildAt(0);
            mStreakBarContainer = (RelativeLayout) mLayoutContainer.getChildAt(0);
            mStreakBar = mStreakBarContainer.getChildAt(0);
            setBarHeight(mStreak.getCount());

            mImageContainer = (RelativeLayout) mLayoutContainer.getChildAt(1);
            mStreakCheckImage = (ImageView) mImageContainer.getChildAt(0);
            mStreakDay = (TextView) mLayoutContainer.getChildAt(2);

            mStreakDay.setText(mStreak.getDayAbbrev());

            if (mStreak.completed()) {
                // TODO: Only use checkmark if the item was completed.
            }
        }
    }

    public void setBarHeight(int count) {
        // TODO: This is probably a terrible idea, find a better way.
        int h = mStreakBar.getLayoutParams().height;
        int w = mStreakBar.getLayoutParams().width;

        Log.d(TAG, "Bar WxH = (" + w + ", " + h +")");

        if(mStreakBar != null) {
            final float scale = getContext().getResources().getDisplayMetrics().density;
            int pixels = (int) ((count * 20) * scale + 2.0f);
            Log.d(TAG, "- size = " + count);
            Log.d(TAG, "- scale = " + scale);
            Log.d(TAG, "- pixels = " + pixels);
            mStreakBar.getLayoutParams().height = pixels + 5;
        }

    }
}