package org.tndata.android.grow.activity;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.tndata.android.grow.GrowApplication;
import org.tndata.android.grow.R;
import org.tndata.android.grow.adapter.ChooseGoalAdapter;
import org.tndata.android.grow.model.Category;
import org.tndata.android.grow.model.Goal;
import org.tndata.android.grow.task.AddCategoryTask;
import org.tndata.android.grow.task.AddGoalTask;
import org.tndata.android.grow.task.GoalLoaderTask;

import java.util.ArrayList;

public class ChooseGoalsActivity extends ActionBarActivity implements AddGoalTask.AddGoalsTaskListener,
        ChooseGoalAdapter.ChooseGoalAdapterListener, GoalLoaderTask.GoalLoaderListener {
    private Toolbar mToolbar;
    private ListView mListView;
    private TextView mHeaderTextView;
    private TextView mErrorTextView;
    private Button mDoneButton;
    private ArrayList<Goal> mItems;
    private ArrayList<Goal> mSelectedGoals = new ArrayList<Goal>();
    private ChooseGoalAdapter mAdapter;
    private Fragment mFragment = null;
    private Category mCategory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_goals);

        mCategory = (Category) getIntent().getSerializableExtra("category");

        mToolbar = (Toolbar) findViewById(R.id.choose_goals_toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mListView = (ListView) findViewById(R.id.choose_goals_listview);
        mHeaderTextView = (TextView) findViewById(R.id.choose_goals_header_label_textview);
        mErrorTextView = (TextView) findViewById(R.id.onboarding_goals_error_textview);

        mDoneButton = (Button) findViewById(R.id.choose_goals_done_button);
        mDoneButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                goalsSelected(mSelectedGoals);
            }
        });
        mDoneButton.setVisibility(View.GONE);
        mItems = new ArrayList<Goal>();
        mAdapter = new ChooseGoalAdapter(getApplicationContext(),
                R.id.list_item_category_grid_category_textview,
                mItems, this);
        mListView.setAdapter(mAdapter);

        loadGoals();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) { // Back key pressed
            goalsSelected(mSelectedGoals);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goalsSelected(mSelectedGoals);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
            mHeaderTextView.setText(getString(
                    R.string.onboarding_goals_header_label,
                    mCategory.getTitle().toUpperCase()));
        }
        new GoalLoaderTask(getApplicationContext(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                ((GrowApplication) getApplication()).getToken(),
                String.valueOf(mCategory.getId()));
    }

    @Override
    public void goalsAdded(ArrayList<Goal> goals) {
        ArrayList<Goal> allGoals = ((GrowApplication) getApplication()).getGoals();
        allGoals.addAll(goals);
        ((GrowApplication) getApplication()).setGoals(allGoals);
        finish();
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
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

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

    private void goalsSelected(ArrayList<Goal> goals) {
        Log.e("GOALS", "GOALS SELECTED");
        //Lets just save the new ones...
        ArrayList<Goal> goalsToDelete = new ArrayList<Goal>();
        ArrayList<Goal> goalsToAdd = new ArrayList<Goal>();
        for (Goal goal : ((GrowApplication) getApplication()).getGoals()) {
            Log.d("SHOULD DELETE?",goal.getTitle());
            if (!goals.contains(goal)) {
                Log.d("Delete Goal", goal.getTitle());
                goalsToDelete.add(goal);
            }
        }
        for (Goal goal : goals) {
            Log.d("SHOULD ADD?",goal.getTitle());
            if (!((GrowApplication) getApplication()).getGoals().contains(goal)) {
                Log.d("Add Goal", goal.getTitle());
                goalsToAdd.add(goal);
            }
        }

        ArrayList<String> goalList = new ArrayList<String>();
        for (Goal goal : goalsToAdd) {
            goalList.add(String.valueOf(goal.getId()));
        }
        if(goalList.size() > 0) {
            new AddGoalTask(this, this, goalList)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            finish();
        }
    }

    @Override
    public void goalLoaderFinished(ArrayList<Goal> goals) {
        if (goals != null && !goals.isEmpty()) {
            mItems.addAll(goals);
            mAdapter.notifyDataSetChanged();
        } else {
            showError();
        }
    }
}
