package org.tndata.android.compass.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.Trigger;
import org.tndata.android.compass.task.AddActionTask;
import org.tndata.android.compass.task.DeleteActionTask;
import org.tndata.android.compass.ui.CompassPopupMenu;
import org.tndata.android.compass.util.CompassTagHandler;
import org.tndata.android.compass.util.ImageHelper;

import java.util.List;


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
        TextView moreInfoHeader = (TextView) v.findViewById(R.id.learn_more_more_info_header_textview);
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
                    List<Action> actions = ((CompassApplication) getActivity()
                            .getApplication()
                    ).getActions();
                    if (!actions.contains(mAction)) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        mAddImageView.setEnabled(false);
                        new AddActionTask(getActivity(), LearnMoreFragment.this, mGoal, mAction)
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
            if (!mAction.getHTMLDescription().isEmpty()) {
                descriptionTextView.setText(Html.fromHtml(mAction.getHTMLDescription(), null, new CompassTagHandler(getActivity())));
            } else {
                descriptionTextView.setText(mAction.getDescription());
            }
            // Display different content in the "Add this" label when the user
            // has already selected the item.
            Trigger trigger = mAction.getTrigger();
            if (trigger != null) {
                // Construct a string for the Action's Label
                String recurrence = trigger.getRecurrencesDisplay();
                String trigger_date = trigger.getFormattedDate();
                String trigger_time = trigger.getFormattedTime();

                if (!recurrence.isEmpty() && !trigger_time.isEmpty()) {
                    addLabelTextView.setText(getString(
                            R.string.trigger_details, recurrence, trigger_time));
                } else if (!trigger_date.isEmpty() && !trigger_time.isEmpty()) {
                    addLabelTextView.setText(getString(
                            R.string.trigger_details, trigger_date, trigger_time));
                }

            } else if (mAction.getMappingId() > 0) {
                addLabelTextView.setText(getText(R.string.action_management_label));
            } else {
                addLabelTextView.setText(getText(R.string.action_i_want_this_label));
            }
            if (!mAction.getMoreInfo().isEmpty()) {
                separator.setVisibility(View.VISIBLE);
                if (!mAction.getHTMLMoreInfo().isEmpty()) {
                    moreInfo.setText(Html.fromHtml(mAction.getHTMLMoreInfo(), null, new CompassTagHandler(getActivity())));
                } else {
                    moreInfo.setText(mAction.getMoreInfo());
                }
                moreInfo.setVisibility(View.VISIBLE);
                moreInfoHeader.setVisibility(View.VISIBLE);
            }
        } else if (mGoal != null) {
            // this is a learn more screen for a Goal
            titleTextView.setText(mGoal.getTitle());
            if (!mGoal.getHTMLDescription().isEmpty()) {
                descriptionTextView.setText(Html.fromHtml(mGoal.getHTMLDescription(), null, new CompassTagHandler(getActivity())));
            } else {
                descriptionTextView.setText(mGoal.getDescription());
            }
            addLabelTextView.setVisibility(View.GONE);
            mAddImageView.setVisibility(View.GONE);
        } else {
            // this is a learn more screen for a Behavior
            titleTextView.setText(mBehavior.getTitle());
            if (!mGoal.getHTMLDescription().isEmpty()) {
                descriptionTextView.setText(Html.fromHtml(mBehavior.getHTMLDescription(), null, new CompassTagHandler(getActivity())));
            } else {
                descriptionTextView.setText(mBehavior.getDescription());
            }
            // Display different content in the "Add this" label when the user
            // has already selected the item.
            if (mBehavior.getMappingId() > 0) {
                addLabelTextView.setText(getText(R.string.behavior_management_label));
            } else {
                addLabelTextView.setText(getText(R.string.behavior_add_to_priorities_label));
            }
            if (!mBehavior.getMoreInfo().isEmpty()) {
                separator.setVisibility(View.VISIBLE);
                if (!mBehavior.getHTMLMoreInfo().isEmpty()) {
                    moreInfo.setText(Html.fromHtml(mBehavior.getHTMLMoreInfo(), null, new CompassTagHandler(getActivity())));
                } else {
                    moreInfo.setText(mBehavior.getMoreInfo());
                }
                moreInfo.setVisibility(View.VISIBLE);
                moreInfoHeader.setVisibility(View.VISIBLE);
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
            List<Action> actions = ((CompassApplication) getActivity().getApplication()
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
        CompassPopupMenu popup = CompassPopupMenu.newInstance(getActivity(), mAddImageView);
        // Inflating the correct menu depending on which kind of content we're viewing.
        if (mAction != null) {
            popup.getMenuInflater()
                    .inflate(R.menu.menu_action_popup_chooser, popup.getMenu());
        } else {
            popup.getMenuInflater().inflate(R.menu.menu_behavior_popup_chooser, popup.getMenu());
        }

        popup.setOnMenuItemClickListener(new CompassPopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_popup_remove_item:
                    case R.id.menu_behavior_popup_remove_item:
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
                            mCallback.fireActionPicker(mAction);
                        } else {
                            mCallback.fireBehaviorPicker(mBehavior);
                        }
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    public void actionChanged(Action action) {
        mCallback.actionChanged();
        List<Action> actions = ((CompassApplication) getActivity().getApplication()
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
    public void actionAdded(Action action) {
        mProgressBar.setVisibility(View.GONE);
        mAddImageView.setEnabled(true);
        if (action == null) {
            return;
        }

        mAction = action;
        List<Action> actions = ((CompassApplication) getActivity().getApplication()
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
        List<Action> actions = ((CompassApplication) getActivity().getApplication()
        ).getActions();
        actions.remove(mAction);
        ((CompassApplication) getActivity().getApplication()).setActions(actions);

        setImageView();
        if (mCallback != null) {
            actionChanged(mAction);
        }
    }
}
