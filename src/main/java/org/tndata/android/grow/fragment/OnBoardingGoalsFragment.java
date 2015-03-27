package org.tndata.android.grow.fragment;

import java.util.ArrayList;

import org.tndata.android.grow.R;
import org.tndata.android.grow.adapter.OnBoardingGoalAdapter;
import org.tndata.android.grow.adapter.OnBoardingGoalAdapter.OnBoardingGoalAdapterListener;
import org.tndata.android.grow.model.Category;
import org.tndata.android.grow.model.Goal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class OnBoardingGoalsFragment extends Fragment implements
        OnBoardingGoalAdapterListener {
    private ListView mListView;
    private TextView mHeaderTextView;
    private TextView mErrorTextView;
    private Button mDoneButton;
    private Category mCategory;
    private ArrayList<Goal> mItems;
    private ArrayList<Goal> mSelectedGoals = new ArrayList<Goal>();
    private OnBoardingGoalAdapter mAdapter;
    private OnBoardingGoalsListener mCallback;

    public interface OnBoardingGoalsListener {
        public void goalsSelected(ArrayList<Goal> goals);
    }

    public static OnBoardingGoalsFragment newInstance(Category category) {
        OnBoardingGoalsFragment fragment = new OnBoardingGoalsFragment();
        Bundle args = new Bundle();
        args.putSerializable("category", category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategory = getArguments() != null ? ((Category) getArguments().get(
                "category")) : new Category();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_onboarding_goals, container, false);
        mListView = (ListView) v.findViewById(R.id.onboarding_goals_listview);
        mHeaderTextView = (TextView) v
                .findViewById(R.id.onboarding_goals_header_label_textview);
        mErrorTextView = (TextView) v
                .findViewById(R.id.onboarding_goals_error_textview);

        mDoneButton = (Button) v
                .findViewById(R.id.onboarding_goals_done_button);
        mDoneButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallback.goalsSelected(mSelectedGoals);
            }
        });
        mDoneButton.setVisibility(View.GONE);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mItems = new ArrayList<Goal>();
        mAdapter = new OnBoardingGoalAdapter(getActivity(),
                R.id.list_item_category_grid_category_textview,
                mCategory.getGoals(), this);
        mListView.setAdapter(mAdapter);
        loadGoals();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity); // This makes sure that the container activity
        // has implemented the callback interface. If not, it throws an
        // exception
        try {
            mCallback = (OnBoardingGoalsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnBoardingGoalsListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public void showDone() {
        mDoneButton.setVisibility(View.VISIBLE);
    }

    @Override
    public ArrayList<Goal> getCurrentlySelectedGoals() {
        return mSelectedGoals;
    }

    @Override
    public void goalSelected(Goal goal) {

        if (mSelectedGoals.contains(goal)) {
            mSelectedGoals.remove(goal);
        } else {
            mSelectedGoals.add(goal);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void moreInfoPressed(Goal goal) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(goal.getDescription()).setTitle(goal.getTitle());
            builder.setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showError() {
        mListView.setVisibility(View.GONE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    @SuppressLint("DefaultLocale")
    private void loadGoals() {
        if (mCategory == null) {
            return;
        } else if (!mCategory.getTitle().isEmpty()) {
            mHeaderTextView.setText(getActivity().getString(
                    R.string.onboarding_goals_header_label,
                    mCategory.getTitle().toUpperCase()));
        }
        if (!mCategory.getGoals().isEmpty()) {
            mItems.addAll(mCategory.getGoals());
            mAdapter.notifyDataSetChanged();
        } else {
            showError();
        }
    }

}
