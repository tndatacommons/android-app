package org.tndata.android.grow.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tndata.android.grow.R;
import org.tndata.android.grow.model.Category;
import org.tndata.android.grow.model.Goal;
import org.tndata.android.grow.util.ImageCache;

public class GoalCellView extends LinearLayout {
    private RelativeLayout mCircleView;
    private TextView mTitleTextView;
    private ImageView mIconImageView;
    private Goal mGoal;
    private Category mCategory;
    private Context mContext;

    public GoalCellView(Context context) {
        this(context, null);
    }

    public GoalCellView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GoalCellView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(context, attrs);
    }

    private void initViews(Context context, AttributeSet attrs) {
        mContext = context;
        View view = inflate(context, R.layout.view_goal_item, this);

        mCircleView = (RelativeLayout) view.findViewById(R.id.view_goal_circle_view);
        mTitleTextView = (TextView) view
                .findViewById(R.id.view_goal_textview);
        mIconImageView = (ImageView) view.findViewById(R.id.view_goal_icon_imageview);
        if (mGoal != null) {
            updateUi();
        }
    }

    public void setGoal(Goal goal, Category category) {
        mGoal = goal;
        mCategory = category;
        if (mTitleTextView != null) {
            updateUi();
        }
    }

    private void updateUi() {
        try {
            mTitleTextView.setText(mGoal.getTitle());
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
            if (mGoal.getIconUrl() != null && !mGoal.getIconUrl().isEmpty()) {
                ImageCache.instance(mContext).loadBitmap(mIconImageView,
                        mGoal.getIconUrl(), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Goal getGoal() {
        return mGoal;
    }
}
