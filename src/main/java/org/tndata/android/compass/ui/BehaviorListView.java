package org.tndata.android.compass.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.util.ImageCache;

public class BehaviorListView extends LinearLayout {
    private ImageView mIconImageView;
    private TextView mTitleTextView;
    private Behavior mBehavior;
    private Category mCategory;
    private Context mContext;

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
        mContext = context;
        View view = inflate(context, R.layout.view_behavior_item, this);

        mIconImageView = (ImageView) view.findViewById(R.id.view_behavior_icon_imageview);
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
            if (mBehavior.getIconUrl() != null
                    && !mBehavior.getIconUrl().isEmpty()) {
                ImageCache.instance(mContext).loadBitmap(mIconImageView,
                        mBehavior.getIconUrl(), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Behavior getBehavior() {
        return mBehavior;
    }
}
