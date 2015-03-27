package org.tndata.android.grow.ui;

import org.tndata.android.grow.R;
import org.tndata.android.grow.model.Action;
import org.tndata.android.grow.util.ImageCache;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ActionCellView extends RelativeLayout {
    private ImageView mImageView;
    private TextView mTitleTextView;
    private TextView mDescriptionTextView;
    private Action mAction;

    public ActionCellView(Context context) {
        this(context, null);
    }

    public ActionCellView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActionCellView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(context, attrs);
    }

    private void initViews(Context context, AttributeSet attrs) {

        View view = inflate(context, R.layout.view_action_cell, this);

        mImageView = (ImageView) view.findViewById(R.id.view_action_image);
        mTitleTextView = (TextView) view
                .findViewById(R.id.view_action_title_textview);
        mDescriptionTextView = (TextView) view
                .findViewById(R.id.view_action_description_textview);
        if (mAction != null) {
            updateUi();
        }
    }

    public void setAction(Action action) {
        mAction = action;
        if (mTitleTextView != null) {
            updateUi();
        }
    }

    private void updateUi() {
        try {
            mTitleTextView.setText(mAction.getTitle());
            mDescriptionTextView.setText(mAction.getNarrativeBlock());
            if (mAction.getIconUrl() != null) {
                ImageCache.instance(getContext()).loadBitmap(mImageView,
                        mAction.getIconUrl(), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Action getAction() {
        return mAction;
    }
}
