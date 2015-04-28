package org.tndata.android.grow.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.tndata.android.grow.R;
import org.tndata.android.grow.model.Behavior;
import org.tndata.android.grow.model.Category;

public class BehaviorListView extends LinearLayout {
    private View mCircleView;
    private TextView mTitleTextView;
    private Behavior mBehavior;
    private Category mCategory;

    public BehaviorListView(Context context) {
        this(context, null);
    }

    public BehaviorListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BehaviorListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(context, attrs);
    }

    private void initViews(Context context, AttributeSet attrs) {

        View view = inflate(context, R.layout.view_behavior_item, this);

        mCircleView = view.findViewById(R.id.view_behavior_circle_view);
        mTitleTextView = (TextView) view
                .findViewById(R.id.view_behavior_textview);
        if (mBehavior != null) {
            updateUi();
        }
    }

    public void setBehavior(Behavior behavior, Category category) {
        mBehavior = behavior;
        mCategory = category;
        if (mTitleTextView != null) {
            updateUi();
        }
    }

    private void updateUi() {
        try {
            mTitleTextView.setText(mBehavior.getTitle());
            GradientDrawable gradientDrawable = (GradientDrawable) mCircleView.getBackground();
            String colorString = mCategory.getColor();
            if (colorString != null && !colorString.isEmpty()) {
                gradientDrawable.setColor(Color.parseColor(colorString));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mCircleView.setBackground(gradientDrawable);
            } else {
                mCircleView.setBackgroundDrawable(gradientDrawable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Behavior getBehavior() {
        return mBehavior;
    }
}
