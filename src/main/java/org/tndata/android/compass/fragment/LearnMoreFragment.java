package org.tndata.android.compass.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import org.tndata.android.compass.util.ImageHelper;

public class LearnMoreFragment extends Fragment {
    private Behavior mBehavior;
    private Category mCategory;
    private Action mAction;
    private ImageView mAddImageView;
    private ProgressBar mProgressBar;
    private LearnMoreFragmentListener mCallback;

    public interface LearnMoreFragmentListener {
        public void addBehavior(Behavior behavior);

        public void deleteBehavior(Behavior behavior);
    }

    public void setBehavior(Behavior behavior) {
        mBehavior = behavior;
    }

    public void setCategory(Category category) {
        mCategory = category;
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
        TextView addLabelTextView = (TextView) v.findViewById(R.id.learn_more_add_label);
        mProgressBar = (ProgressBar) v.findViewById(R.id.learn_more_progressbar);
        mProgressBar.setVisibility(View.GONE);
        mAddImageView = (ImageView) v
                .findViewById(R.id.learn_more_add_imageview);
        mAddImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                mAddImageView.setEnabled(false);
                for (Goal goal : ((CompassApplication) getActivity().getApplication()).getGoals()) {
                    if (goal.getBehaviors().contains(mBehavior)) {
                        mCallback.deleteBehavior(mBehavior);
                        return;
                    }
                }
                mCallback.addBehavior(mBehavior);
            }
        });
        if (mAction != null) {
            // this is a learn more screen for an Action
            titleTextView.setText(mAction.getTitle());
            descriptionTextView.setText(mAction.getDescription());
            addLabelTextView.setText(getText(R.string.action_i_want_this_label));
        } else {
            // this is a learn more screen for a Behavior
            titleTextView.setText(mBehavior.getTitle());
            descriptionTextView.setText(mBehavior.getMoreInfo());
            addLabelTextView.setText(getText(R.string.behavior_add_to_priorities_label));
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
        for (Goal goal : ((CompassApplication) getActivity().getApplication()).getGoals()) {
            if (goal.getBehaviors().contains(mBehavior)) {
                ImageHelper.setupImageViewButton(getResources(), mAddImageView,
                        ImageHelper.SELECTED);
                mProgressBar.setVisibility(View.GONE);
                mAddImageView.setEnabled(true);
                return;
            }
        }
        ImageHelper.setupImageViewButton(getResources(), mAddImageView, ImageHelper.ADD);
        mProgressBar.setVisibility(View.GONE);
        mAddImageView.setEnabled(true);
    }
}
