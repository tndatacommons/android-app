package org.tndata.android.grow.fragment;

import org.tndata.android.grow.GrowApplication;
import org.tndata.android.grow.R;
import org.tndata.android.grow.activity.ChooseGoalsActivity;
import org.tndata.android.grow.activity.GoalTryActivity;
import org.tndata.android.grow.adapter.MyGoalsAdapter;
import org.tndata.android.grow.model.Category;
import org.tndata.android.grow.model.Goal;
import org.tndata.android.grow.model.MyGoalsViewItem;
import org.tndata.android.grow.model.Survey;
import org.tndata.android.grow.task.SurveyResponseTask;
import org.tndata.android.grow.ui.SpacingItemDecoration;
import org.tndata.android.grow.ui.button.FloatingActionButton;
import org.tndata.android.grow.util.Constants;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class CategoryFragment extends Fragment implements MyGoalsAdapter.SurveyCompleteInterface {
    private Category mCategory;
    private FloatingActionButton mFloatingActionButton;
    private TextView mErrorTextView;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MyGoalsAdapter mAdapter;
    private ArrayList<MyGoalsViewItem> mItems = new ArrayList<MyGoalsViewItem>();
    private boolean mBroadcastIsRegistered = false;
    private CategoryFragmentListener mCallback;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Category Fragment", "receive broadcast");
            categoryGoalsUpdated();
        }
    };

    public interface CategoryFragmentListener {
        public void assignGoalsToCategories(boolean shouldSendBroadcast);
    }

    public static CategoryFragment newInstance(Category category) {
        CategoryFragment fragment = new CategoryFragment();
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
                R.layout.fragment_category, container, false);
        mErrorTextView = (TextView) v
                .findViewById(R.id.category_error_textview);
        mFloatingActionButton = (FloatingActionButton) v.findViewById(R.id.category_fab_button);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGoals();
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) v
                .findViewById(R.id.category_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SpacingItemDecoration(30));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyGoalsAdapter(getActivity().getApplicationContext(),
                mItems, this);
        mRecyclerView.setAdapter(mAdapter);
        registerReceivers();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setGoals();
        mAdapter = new MyGoalsAdapter(getActivity().getApplicationContext(),
                mItems, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickEvent(new MyGoalsAdapter.OnClickEvent() {

            @Override
            public void onClick(View v, int position) {
                MyGoalsViewItem item = mItems.get(position);
                if (item.getType() == MyGoalsViewItem.TYPE_GOAL) {
                    Goal goal = item.getGoal();
                    Intent intent = new Intent(getActivity()
                            .getApplicationContext(), GoalTryActivity.class);
                    Log.d("Goal?",
                            "id:" + goal.getId() + " title:" + goal.getTitle());
                    intent.putExtra("goal", goal);
                    intent.putExtra("category", mCategory);
                    startActivity(intent);
                }
            }
        });

        if (mItems.isEmpty()) {
            showError();
        } else {
            showList();
        }
    }

    @Override
    public void onDestroy() {
        unRegisterReceivers();
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity); // This makes sure that the container activity
        // has implemented the callback interface. If not, it throws an
        // exception
        try {
            mCallback = (CategoryFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CategoryFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    private void registerReceivers() {
        if (mBroadcastIsRegistered == false) {
            getActivity().getApplicationContext().registerReceiver(broadcastReceiver,
                    new IntentFilter(Constants.GOAL_UPDATED_BROADCAST_ACTION));
            mBroadcastIsRegistered = true;
            Log.d("Category Fragment", "Broadcast registered");
        }
    }

    private void unRegisterReceivers() {
        if (mBroadcastIsRegistered == true) {
            try {
                getActivity().getApplicationContext().unregisterReceiver(broadcastReceiver);
                mBroadcastIsRegistered = false;
            } catch (Exception e) {

            }
        }
    }

    private void showError() {
        mErrorTextView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void showList() {
        mErrorTextView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void addGoals() {
        Intent intent = new Intent(getActivity().getApplicationContext(),
                ChooseGoalsActivity.class);
        intent.putExtra("category", mCategory);
        startActivityForResult(intent, Constants.CHOOSE_GOALS_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Category Fragment", "onActivityResult");
        if (requestCode == Constants.CHOOSE_GOALS_REQUEST_CODE) {
            mCallback.assignGoalsToCategories(true);
        }
    }

    public void categoryGoalsUpdated() {
        Log.d("Category Fragment", "categoryGoalsUpdated");
        for (Category category : ((GrowApplication) getActivity()
                .getApplication()).getCategories()) {
            if (category.getId() == mCategory.getId()) {
                mCategory.setGoals(category.getGoals());
                break;
            }
        }
        setGoals();
        mAdapter.notifyDataSetChanged();
        if (mItems.isEmpty()) {
            showError();
        } else {
            showList();
        }
    }

    private void setGoals() {
        Log.d("Category Fragment", "setGoals");
        ArrayList<Goal> goals = mCategory.getGoals();
        mItems.clear();
        if (goals != null && !goals.isEmpty()) {
            for (Goal goal : goals) {
                MyGoalsViewItem item = new MyGoalsViewItem();
                item.setGoal(goal);
                mItems.add(item);
            }
        }
    }

    @Override
    public void surveyCompleted(Survey survey) {
        //empty for now - but it allows us to use the MyGoalsAdapter
    }

}
