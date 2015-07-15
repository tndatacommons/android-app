package org.tndata.android.compass.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.task.ActionLoaderTask;
import org.tndata.android.compass.task.ActionLoaderTask.ActionLoaderListener;
import org.tndata.android.compass.ui.ActionCellView;
import org.tndata.android.compass.ui.CompassPopupMenu;
import org.tndata.android.compass.util.ImageCache;
import org.tndata.android.compass.util.ImageHelper;
import org.tndata.android.compass.util.ImageLoader;

import java.util.ArrayList;

public class BehaviorFragment extends Fragment implements ActionLoaderListener, ActionCellView
        .ActionViewListener {
    private Behavior mBehavior;
    private Category mCategory;
    private LinearLayout mActionsContainer;
    private ImageView mAddImageView;
    private ProgressBar mProgressBar;
    private BehaviorFragmentListener mCallback;
    private ArrayList<Action> mActionList;

    private ImageLoader mImageLoader;

    public interface BehaviorFragmentListener {
        public void learnMoreBehavior();

        public void learnMoreAction(Action action);

        public void addBehavior(Behavior behavior);

        public void deleteBehavior(Behavior behavior);

        public void actionChanged();

        public void fireBehaviorPicker(Behavior behavior);

        public void fireActionPicker(Action action);
    }

    public void setBehavior(Behavior behavior) {
        mBehavior = behavior;
    }

    public void setCategory(Category category) {
        mCategory = category;
    }

    public static BehaviorFragment newInstance(Behavior behavior, Category category) {
        BehaviorFragment fragment = new BehaviorFragment();
        Bundle args = new Bundle();
        args.putSerializable("behavior", behavior);
        args.putSerializable("category", category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBehavior = getArguments() != null ? ((Behavior) getArguments().get(
                "behavior")) : new Behavior();
        mCategory = getArguments() != null ? ((Category) getArguments().get(
                "category")) : new Category();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mImageLoader = new ImageLoader(getActivity().getApplicationContext());

        View v = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_behavior, container, false);
        TextView titleTextView = (TextView) v
                .findViewById(R.id.behavior_title_textview);
        titleTextView.setText(mBehavior.getTitle());
        RelativeLayout behaviorContentContainer = (RelativeLayout) v
                .findViewById(R.id.behavior_content_container);
        behaviorContentContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallback.learnMoreBehavior();
            }
        });
        if (mBehavior.getIconUrl() != null && !mBehavior.getIconUrl().isEmpty()) {
            ImageView iconImageView = (ImageView) v.findViewById(R.id.behavior_icon_imageview);
            mImageLoader.loadBitmap(iconImageView, mBehavior.getIconUrl(), false);

        }
        mProgressBar = (ProgressBar) v.findViewById(R.id.behavior_progressbar);
        mAddImageView = (ImageView) v.findViewById(R.id.behavior_add_imageview);
        mAddImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                for (Goal goal : ((CompassApplication) getActivity().getApplication()).getGoals()) {
                    if (goal.getBehaviors().contains(mBehavior)) {
                        showPopup();
                        return;
                    }
                }
                mProgressBar.setVisibility(View.VISIBLE);
                mAddImageView.setEnabled(false);
                mCallback.addBehavior(mBehavior);
            }
        });
        mActionsContainer = (LinearLayout) v
                .findViewById(R.id.behavior_actions_container);
        return v;
    }

    @Override
    public void onPause(){
        mImageLoader.closeCache();
        super.onPause();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadActions();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity); // This makes sure that the container activity
        // has implemented the callback interface. If not, it throws an
        // exception
        try {
            mCallback = (BehaviorFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement LearnMoreListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    private void loadActions() {
        new ActionLoaderTask(this).executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR,
                ((CompassApplication) getActivity().getApplication()).getToken(),
                String.valueOf(mBehavior.getId()));
    }

    @Override
    public void actionsLoaded(ArrayList<Action> actions) {
        mActionList = actions;
        if (mActionList != null) {
            drawActions();
        }
    }

    private void drawActions() {
        for (Action action : mActionList) {
            Log.d("Action", "*draw name:" + action.getTitle());
            ActionCellView acv = new ActionCellView(getActivity());
            acv.setAction(action, mCategory);
            acv.setListener(this);
            acv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (v instanceof ActionCellView) {
                        Action a = ((ActionCellView) v).getAction();
                        mCallback.learnMoreAction(a);
                    }

                }
            });
            mActionsContainer.addView(acv);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setImageView();
    }

    public void setImageView() {
        for (Goal goal : ((CompassApplication) getActivity().getApplication()).getGoals()) {
            if (goal.getBehaviors().contains(mBehavior)) {
                ImageHelper.setupImageViewButton(getResources(), mAddImageView,
                        ImageHelper.CHOOSE);
                mProgressBar.setVisibility(View.GONE);
                mAddImageView.setEnabled(true);
                return;
            }
        }
        ImageHelper.setupImageViewButton(getResources(), mAddImageView, ImageHelper.ADD);
        mProgressBar.setVisibility(View.GONE);
        mAddImageView.setEnabled(true);
    }

    private void showPopup() {
        //Creating the instance of PopupMenu
        CompassPopupMenu popup = CompassPopupMenu.newInstance(getActivity(), mAddImageView);

        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.menu_behavior_popup_chooser, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new CompassPopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_behavior_popup_remove_item:
                        mCallback.deleteBehavior(mBehavior);
                        break;
                }
                return true;
            }
        });

        popup.show(); //showing popup menu
    }

    @Override
    public void actionChanged(Action action) {
        mCallback.actionChanged();
        ArrayList<Action> actions = ((CompassApplication) getActivity().getApplication()
        ).getActions();
        if (actions.contains(action)) {
            for (Goal goal : ((CompassApplication) getActivity().getApplication()).getGoals()) {
                if (goal.getBehaviors().contains(mBehavior)) {
                    return;
                }
            }
            mCallback.addBehavior(mBehavior);
        }
    }

    @Override
    public void fireActionPicker(Action action) {
        mCallback.fireActionPicker(action);
    }
}
