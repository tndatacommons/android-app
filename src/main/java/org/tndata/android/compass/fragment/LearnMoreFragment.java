package org.tndata.android.compass.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.task.AddActionTask;
import org.tndata.android.compass.task.DeleteActionTask;
import org.tndata.android.compass.util.ImageHelper;

import java.util.ArrayList;

public class LearnMoreFragment extends Fragment implements AddActionTask
        .AddActionTaskListener, DeleteActionTask.DeleteActionTaskListener {
    private Goal mGoal;
    private Behavior mBehavior;
    private Category mCategory;
    private Action mAction;
    private ImageView mAddImageView;
    private ProgressBar mProgressBar;
    private LearnMoreFragmentListener mCallback;

    public interface LearnMoreFragmentListener {
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

    public void setGoal(Goal goal) {
        mGoal = goal;
    }

    public static LearnMoreFragment newInstance(Goal goal, Category category) {
        LearnMoreFragment fragment = new LearnMoreFragment();
        Bundle args = new Bundle();
        args.putSerializable("goal", goal);
        args.putSerializable("category", category);
        fragment.setArguments(args);
        return fragment;
    }

    public static LearnMoreFragment newInstance(Behavior behavior, Category category) {
        LearnMoreFragment fragment = new LearnMoreFragment();
        Bundle args = new Bundle();
        args.putSerializable("behavior", behavior);
        args.putSerializable("category", category);
        fragment.setArguments(args);
        return fragment;
    }

    public static LearnMoreFragment newInstance(Action action, Behavior behavior, Category
            category) {
        LearnMoreFragment fragment = new LearnMoreFragment();
        Bundle args = new Bundle();
        args.putSerializable("action", action);
        args.putSerializable("behavior", behavior);
        args.putSerializable("category", category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAction = getArguments() != null ? ((Action) getArguments().get(
                "action")) : new Action();
        mBehavior = getArguments() != null ? ((Behavior) getArguments().get(
                "behavior")) : new Behavior();
        mCategory = getArguments() != null ? ((Category) getArguments().get(
                "category")) : new Category();
        mGoal = getArguments() != null ? ((Goal) getArguments().get(
                "goal")) : new Goal();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_learn_more, container, false);
        TextView titleTextView = (TextView) v
                .findViewById(R.id.learn_more_behavior_title_textview);
        TextView descriptionTextView = (TextView) v
                .findViewById(R.id.learn_more_description_textview);

        View separator = v.findViewById(R.id.learn_more_separator);
        TextView moreInfo = (TextView) v.findViewById(R.id.learn_more_more_info_textview);

        TextView addLabelTextView = (TextView) v.findViewById(R.id.learn_more_add_label);
        mProgressBar = (ProgressBar) v.findViewById(R.id.learn_more_progressbar);
        mProgressBar.setVisibility(View.GONE);
        mAddImageView = (ImageView) v
                .findViewById(R.id.learn_more_add_imageview);
        mAddImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mAction != null) {
                    ArrayList<Action> actions = ((CompassApplication) getActivity()
                            .getApplication()
                    ).getActions();
                    if (!actions.contains(mAction)) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        mAddImageView.setEnabled(false);
                        new AddActionTask(getActivity(), LearnMoreFragment.this, mAction)
                                .executeOnExecutor(AsyncTask
                                        .THREAD_POOL_EXECUTOR);
                    } else {
                        showPopup();
                    }
                } else {
                    for (Goal goal : ((CompassApplication) getActivity().getApplication())
                            .getGoals()) {
                        if (goal.getBehaviors().contains(mBehavior)) {
                            showPopup();
                            return;
                        }
                    }
                    mProgressBar.setVisibility(View.VISIBLE);
                    mAddImageView.setEnabled(false);
                    mCallback.addBehavior(mBehavior);
                }
            }
        });
        if (mAction != null) {
            // this is a learn more screen for an Action
            titleTextView.setText(mAction.getTitle());
            descriptionTextView.setText(mAction.getDescription());
            addLabelTextView.setText(getText(R.string.action_i_want_this_label));
            if (!mAction.getMoreInfo().isEmpty()) {
                separator.setVisibility(View.VISIBLE);
                moreInfo.setText(mAction.getMoreInfo());
                moreInfo.setVisibility(View.VISIBLE);
            }
        } else if (mGoal != null) {
            // this is a learn more screen for a Goal
            titleTextView.setText(mGoal.getTitle());
            descriptionTextView.setText(mGoal.getDescription());
            addLabelTextView.setVisibility(View.GONE);
            mAddImageView.setVisibility(View.GONE);
        } else {
            // this is a learn more screen for a Behavior
            titleTextView.setText(mBehavior.getTitle());
            descriptionTextView.setText((mBehavior.getDescription()));
            addLabelTextView.setText(getText(R.string.behavior_add_to_priorities_label));
            if (!mBehavior.getMoreInfo().isEmpty()) {
                separator.setVisibility(View.VISIBLE);
                moreInfo.setText(mBehavior.getMoreInfo());
                moreInfo.setVisibility(View.VISIBLE);
            }
        }
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity); // This makes sure that the container activity
        // has implemented the callback interface. If not, it throws an
        // exception
        try {
            mCallback = (LearnMoreFragmentListener) activity;
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

    @Override
    public void onResume() {
        super.onResume();
        setImageView();
    }

    public void setImageView() {
        if (mAction != null) {
            ArrayList<Action> actions = ((CompassApplication) getActivity().getApplication()
            ).getActions();
            if (actions.contains(mAction)) {
                ImageHelper.setupImageViewButton(getResources(), mAddImageView,
                        ImageHelper.CHOOSE);
                mProgressBar.setVisibility(View.GONE);
                mAddImageView.setEnabled(true);
                return;
            }
        } else if (mBehavior != null) {
            for (Goal goal : ((CompassApplication) getActivity().getApplication()).getGoals()) {
                if (goal.getBehaviors().contains(mBehavior)) {
                    ImageHelper.setupImageViewButton(getResources(), mAddImageView,
                            ImageHelper.CHOOSE);
                    mProgressBar.setVisibility(View.GONE);
                    mAddImageView.setEnabled(true);
                    return;
                }
            }
        }
        ImageHelper.setupImageViewButton(getResources(), mAddImageView, ImageHelper.ADD);
        mProgressBar.setVisibility(View.GONE);
        mAddImageView.setEnabled(true);
    }

    private void showPopup() {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(getActivity(), mAddImageView);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.menu_action_popup_chooser, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_popup_remove_item:
                        if (mAction != null) {
                            new DeleteActionTask(getActivity(), LearnMoreFragment.this, String
                                    .valueOf(mAction.getMappingId()))
                                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            mCallback.deleteBehavior(mBehavior);
                        }
                        break;
                    case R.id.menu_popup_edit_item:
                        if (mAction != null) {
                            fireActionPicker();
                        } else {
                            mCallback.fireBehaviorPicker(mBehavior);
                        }
                        break;
                }
                return true;
            }
        });

        popup.show(); //showing popup menu
    }

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

    public void fireActionPicker() {
        mCallback.fireActionPicker(mAction);
    }

    @Override
    public void actionAdded(Action action) {
        mProgressBar.setVisibility(View.GONE);
        mAddImageView.setEnabled(true);
        if (action == null) {
            return;
        }

        mAction = action;
        ArrayList<Action> actions = ((CompassApplication) getActivity().getApplication()
        ).getActions();

        actions.add(mAction);
        ((CompassApplication) getActivity().getApplication()).setActions(actions);

        setImageView();
        if (mCallback != null) {
            actionChanged(mAction);
        }
    }

    @Override
    public void actionDeleted() {
        mProgressBar.setVisibility(View.GONE);
        mAddImageView.setEnabled(true);
        ArrayList<Action> actions = ((CompassApplication) getActivity().getApplication()
        ).getActions();
        actions.remove(mAction);
        ((CompassApplication) getActivity().getApplication()).setActions(actions);

        setImageView();
        if (mCallback != null) {
            actionChanged(mAction);
        }
    }
}
