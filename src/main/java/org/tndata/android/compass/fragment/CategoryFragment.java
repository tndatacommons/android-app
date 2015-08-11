package org.tndata.android.compass.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.activity.ChooseGoalsActivity;
import org.tndata.android.compass.activity.GoalTryActivity;
import org.tndata.android.compass.adapter.CategoryFragmentAdapter;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.task.DeleteGoalTask;
import org.tndata.android.compass.ui.SpacingItemDecoration;
import org.tndata.android.compass.ui.button.FloatingActionButton;
import org.tndata.android.compass.util.Constants;

import java.util.ArrayList;

/**
 * This class, along with the CategoryFragmentAdapter, manages the Goal Cards selected
 * by the user; The collections of cards are organized by Category, and displayed beneath
 * a Category Tab.
 */
public class CategoryFragment extends Fragment implements
        CategoryFragmentAdapter.CategoryFragmentAdapterInterface,
        DeleteGoalTask.DeleteGoalTaskListener {

    private CompassApplication application;
    private Category mCategory;
    private FloatingActionButton mFloatingActionButton;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private CategoryFragmentAdapter mAdapter;
    private ArrayList<Goal> mItems = new ArrayList<Goal>();
    private boolean mBroadcastIsRegistered = false;
    private static final String TAG = "CategoryFragment";

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "received broadcast, calling categoryGoalsUpdated()");
            categoryGoalsUpdated();
        }
    };

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
        application = (CompassApplication) getActivity().getApplication();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_category, container, false);
        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addGoals();
                }
            });
        }
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) v
                .findViewById(R.id.category_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SpacingItemDecoration(getActivity(), 10));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CategoryFragmentAdapter(getActivity().getApplicationContext(),
                application, mCategory, this);
        mRecyclerView.setAdapter(mAdapter);
        mFloatingActionButton.attachToRecyclerView(mRecyclerView);
        registerReceivers();
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setGoals();
        mAdapter = new CategoryFragmentAdapter(getActivity(), application, mCategory, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        unRegisterReceivers();
        super.onDestroy();
    }

    private void registerReceivers() {
        if (mBroadcastIsRegistered == false) {
            getActivity().getApplicationContext().registerReceiver(broadcastReceiver,
                    new IntentFilter(Constants.GOAL_UPDATED_BROADCAST_ACTION));
            mBroadcastIsRegistered = true;
            Log.d(TAG, "Broadcast registered");
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

    private void addGoals() {
        Intent intent = new Intent(getActivity().getApplicationContext(),
                ChooseGoalsActivity.class);
        intent.putExtra("category", mCategory);
        startActivityForResult(intent, Constants.CHOOSE_GOALS_REQUEST_CODE);
        mAdapter.notifyDataSetChanged();
    }

    public void categoryGoalsUpdated() {
        Log.d(TAG, "categoryGoalsUpdated");
        for (Category category : application.getCategories()) {
            if (category.getId() == mCategory.getId()) {
                mCategory.setGoals(category.getGoals());
                break;
            }
        }
        setGoals();
    }

    /*
    This method will reset the Fragment's list of goals, based on those assigned to the
    category. IT also informs the adapter of the change, which should keep everything up to date.
     */
    private void setGoals() {
        Log.d(TAG, "setGoals");
        ArrayList<Goal> goals = mCategory.getGoals();
        mItems.clear();
        if (goals != null && !goals.isEmpty()) {
            mItems.addAll(goals);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void removeGoal(Goal goal) {
        // NOTE: this is similar to code in GoalDetailsActivity.deleteGoal(); refactor?
        // Delete the goal from the backend api.
        ArrayList<String> goals = new ArrayList<String>();
        goals.add(String.valueOf(goal.getMappingId()));
        new DeleteGoalTask(getActivity(), this, goals).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        // Remove the goal from the category's collection
        mCategory.removeGoal(goal);

        // Delete the goal from the Compass Application's collection
        application.removeGoal(goal);

        setGoals(); // reset the goals collection for this fragment.
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void goalsDeleted() {
        Toast.makeText(getActivity(), getText(R.string.goal_deleted), Toast.LENGTH_SHORT).show();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void chooseBehaviors(Goal goal) {
        Intent intent = new Intent(getActivity()
                .getApplicationContext(), GoalTryActivity.class);
        intent.putExtra("goal", goal);
        intent.putExtra("category", mCategory);
        startActivityForResult(intent, Constants.CHOOSE_BEHAVIORS_REQUEST_CODE);
    }

    @Override
    public void viewGoal(Goal goal) {
        Intent intent = new Intent(getActivity()
                .getApplicationContext(), GoalTryActivity.class);
        intent.putExtra("goal", goal);
        intent.putExtra("category", mCategory);
        startActivityForResult(intent, Constants.CHOOSE_BEHAVIORS_REQUEST_CODE);
    }

    @Override
    public void deleteGoal(final Goal goal) {
        Log.d(TAG, "Deleting Goal: " + goal.getTitle());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getText(R.string.goal_dialog_delete_message))
                .setTitle(getText(R.string.goal_dialog_delete_title))
                .setNegativeButton(R.string.picker_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        removeGoal(goal);
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void cardExpand() {
        mFloatingActionButton.hide();
    }

    @Override
    public void cardCollapse(){
        if (mRecyclerView.canScrollVertically(1)){
            mFloatingActionButton.show();
        }
    }

    public void setFloatingActionButton(FloatingActionButton fab) {
        mFloatingActionButton = fab;
    }

}
