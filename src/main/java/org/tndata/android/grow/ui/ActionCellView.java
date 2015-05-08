package org.tndata.android.grow.ui;

import org.tndata.android.grow.GrowApplication;
import org.tndata.android.grow.R;
import org.tndata.android.grow.model.Action;
import org.tndata.android.grow.model.Category;
import org.tndata.android.grow.task.AddActionTask;
import org.tndata.android.grow.task.DeleteActionTask;
import org.tndata.android.grow.util.ImageCache;
import org.tndata.android.grow.util.ImageHelper;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ActionCellView extends RelativeLayout implements AddActionTask
        .AddActionTaskListener, DeleteActionTask.DeleteActionTaskListener {
    private ImageView mImageView;
    private ImageView mAddImageView;
    private TextView mTitleTextView;
    private ProgressBar mProgressBar;
    private Action mAction;
    private Category mCategory;
    private Context mContext;
    private ActionViewListener mCallback;

    public interface ActionViewListener {
        public void actionChanged();
    }

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
        mContext = context;

        View view = inflate(context, R.layout.view_action_cell, this);

        mImageView = (ImageView) view.findViewById(R.id.view_action_imageview);
        mAddImageView = (ImageView) view.findViewById(R.id.view_action_add_imageview);
        mAddImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                mAddImageView.setEnabled(false);
                if (mContext instanceof Activity) {
                    ArrayList<Action> actions = ((GrowApplication) ((Activity) mContext)
                            .getApplication()
                    ).getActions();
                    if (!actions.contains(mAction)) {
                        addUserAction();
                    } else {
                        deleteUserAction();
                    }
                }
            }
        });
        mTitleTextView = (TextView) view
                .findViewById(R.id.view_action_title_textview);
        mProgressBar = (ProgressBar) view.findViewById(R.id.view_action_progressbar);
        if (mAction != null) {
            updateUi();
        }
    }

    public void setAction(Action action, Category category) {
        mCategory = category;
        setAction(action);
    }

    public void setListener(ActionViewListener listener) {
        mCallback = listener;
    }

    public void setAction(Action action) {
        if (mContext instanceof Activity) {
            ArrayList<Action> actions = ((GrowApplication) ((Activity) mContext).getApplication()
            ).getActions();
            for (Action userAction : actions) {
                if (userAction.getId() == action.getId()) {
                    action.setMappingId(userAction.getMappingId());
                    break;
                }
            }
        }
        mAction = action;
        if (mTitleTextView != null) {
            updateUi();
        }
    }

    private void addUserAction() {
        new AddActionTask(mContext, this, mAction).executeOnExecutor(AsyncTask
                .THREAD_POOL_EXECUTOR);
    }

    private void deleteUserAction() {
        new DeleteActionTask(mContext, this, String.valueOf(mAction.getMappingId()))
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void updateUi() {
        try {
            mTitleTextView.setText(mAction.getTitle());
            if (mAction.getIconUrl() != null) {
                ImageCache.instance(getContext()).loadBitmap(mImageView,
                        mAction.getIconUrl(), false);
            }
            updateImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Action getAction() {
        return mAction;
    }

    public void updateImage() {
        if (mContext instanceof Activity) {
            ArrayList<Action> actions = ((GrowApplication) ((Activity) mContext).getApplication()
            ).getActions();
            if (actions.contains(mAction)) {
                ImageHelper.setupImageViewButton(getResources(), mAddImageView,
                        ImageHelper.SELECTED);
            } else {
                ImageHelper.setupImageViewButton(getResources(), mAddImageView, ImageHelper.ADD);
            }
        }
    }

    @Override
    public void actionAdded(Action action) {
        mProgressBar.setVisibility(View.GONE);
        mAddImageView.setEnabled(true);
        if (action == null) {
            return;
        }

        mAction = action;
        if (mContext instanceof Activity) {
            ArrayList<Action> actions = ((GrowApplication) ((Activity) mContext).getApplication()
            ).getActions();

            actions.add(mAction);
            ((GrowApplication) ((Activity) mContext).getApplication()).setActions(actions);
        }
        updateImage();
        if (mCallback != null) {
            mCallback.actionChanged();
        }
    }

    @Override
    public void actionDeleted() {
        mProgressBar.setVisibility(View.GONE);
        mAddImageView.setEnabled(true);
        if (mContext instanceof Activity) {
            ArrayList<Action> actions = ((GrowApplication) ((Activity) mContext).getApplication()
            ).getActions();
            actions.remove(mAction);
            ((GrowApplication) ((Activity) mContext).getApplication()).setActions(actions);
        }
        updateImage();
        if (mCallback != null) {
            mCallback.actionChanged();
        }
    }
}
