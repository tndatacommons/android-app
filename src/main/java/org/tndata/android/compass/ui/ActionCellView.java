package org.tndata.android.compass.ui;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.task.AddActionTask;
import org.tndata.android.compass.task.DeleteActionTask;
import org.tndata.android.compass.util.ImageHelper;

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
        public void actionChanged(Action action);

        public void fireActionPicker();
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

                if (mContext instanceof Activity) {
                    ArrayList<Action> actions = ((CompassApplication) ((Activity) mContext)
                            .getApplication()
                    ).getActions();
                    if (!actions.contains(mAction)) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        mAddImageView.setEnabled(false);
                        addUserAction();
                    } else {
                        showPopup();
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

    private void showPopup() {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(mContext, mAddImageView);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.menu_action_popup_chooser, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_popup_remove_item:
                        deleteUserAction();
                        break;
                    case R.id.menu_popup_edit_item:
                        mCallback.fireActionPicker();
                        break;
                }
                return true;
            }
        });

        popup.show(); //showing popup menu
    }

    public void setListener(ActionViewListener listener) {
        mCallback = listener;
    }

    public void setAction(Action action) {
        if (mContext instanceof Activity) {
            ArrayList<Action> actions = ((CompassApplication) ((Activity) mContext).getApplication()
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
            ArrayList<Action> actions = ((CompassApplication) ((Activity) mContext)
                    .getApplication()).getActions();
            if (actions.contains(mAction)) {
                ImageHelper.setupImageViewButton(getResources(), mAddImageView, ImageHelper.CHOOSE);
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
            ArrayList<Action> actions = ((CompassApplication) ((Activity) mContext)
                    .getApplication()).getActions();

            actions.add(mAction);
            ((CompassApplication) ((Activity) mContext).getApplication()).setActions(actions);
        }
        updateImage();
        if (mCallback != null) {
            mCallback.actionChanged(mAction);
            mCallback.fireActionPicker();
        }
    }

    @Override
    public void actionDeleted() {
        mProgressBar.setVisibility(View.GONE);
        mAddImageView.setEnabled(true);
        if (mContext instanceof Activity) {
            ArrayList<Action> actions = ((CompassApplication) ((Activity) mContext)
                    .getApplication()).getActions();
            actions.remove(mAction);
            ((CompassApplication) ((Activity) mContext).getApplication()).setActions(actions);
        }
        updateImage();
        if (mCallback != null) {
            mCallback.actionChanged(mAction);
        }
    }
}
