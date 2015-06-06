package org.tndata.android.compass.activity;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.fragment.BehaviorFragment;
import org.tndata.android.compass.fragment.GoalDetailsFragment;
import org.tndata.android.compass.fragment.LearnMoreFragment;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.task.DeleteBehaviorTask;
import org.tndata.android.compass.util.Constants;

import java.util.ArrayList;

public class GoalDetailsActivity extends ActionBarActivity implements
        //LearnMoreFragmentListener,
        GoalDetailsFragment.GoalDetailsFragmentListener,
        DeleteBehaviorTask.DeleteBehaviorTaskListener {

    private static final int GOALDETAIL = 0;
    private static final int LEARN_MORE_BEHAVIOR = 1;
    private static final int LEARN_MORE_ACTION = 2;

    private Toolbar mToolbar;
    private Category mCategory;
    private Goal mGoal;

    private GoalDetailsFragment mGoalDetailsFragment = null;
    private LearnMoreFragment mLearnMoreFragment = null;
    private ArrayList<Fragment> mFragmentStack = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mCategory = (Category) getIntent().getSerializableExtra("category");
        mGoal = (Goal) getIntent().getSerializableExtra("goal");

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle(mGoal.getTitle());
        mToolbar.getBackground().setAlpha(255);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (mCategory != null && !mCategory.getColor().isEmpty()) {
            mToolbar.setBackgroundColor(Color.parseColor(mCategory.getColor()));
        }
        swapFragments(GOALDETAIL, true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) { // Back key pressed
            handleBackStack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                handleBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void learnMoreBehavior(Behavior behavior) {
        swapFragments(LEARN_MORE_BEHAVIOR, true);
    }

    @Override
    public void learnMoreAction(Action action) {
        swapFragments(LEARN_MORE_ACTION, true);
    }

    @Override
    public void chooseBehaviors(Goal goal) {
        Intent intent = new Intent(getApplicationContext(), GoalTryActivity.class);
        intent.putExtra("goal", goal);
        intent.putExtra("category", mCategory);
        startActivityForResult(intent, Constants.CHOOSE_BEHAVIORS_REQUEST_CODE);
    }

    private void handleBackStack() {
        if (!mFragmentStack.isEmpty()) {
            mFragmentStack.remove(mFragmentStack.size() - 1);
        }

        if (mFragmentStack.isEmpty()) {
            finish();
        } else {
            swapFragments(GOALDETAIL, false);
        }
    }

    private void swapFragments(int index, boolean addToStack) {
        Fragment fragment = null;
        switch (index) {
            case GOALDETAIL:
                if (mGoalDetailsFragment == null) {
                    mGoalDetailsFragment = GoalDetailsFragment.newInstance(mCategory, mGoal);
                }
                fragment = mGoalDetailsFragment;
                break;
            case LEARN_MORE_BEHAVIOR:
//                mLearnMoreFragment = LearnMoreFragment.newInstance(mBehavior, mCategory);
//                fragment = mLearnMoreFragment;
//                break;
            case LEARN_MORE_ACTION:
//                if (mAction != null) {
//                    mLearnMoreFragment = LearnMoreFragment.newInstance(mAction, mBehavior,
//                            mCategory);
//                    fragment = mLearnMoreFragment;
//                }
//                break;
        }
        if (fragment != null) {
            if (addToStack) {
                mFragmentStack.add(fragment);
            }
            getFragmentManager().beginTransaction()
                    .replace(R.id.base_content, fragment).commit();
        }
    }

    @Override
    public void deleteBehavior(Behavior behavior) {
        ArrayList<String> behaviors = new ArrayList<String>();
        behaviors.add(String.valueOf(behavior.getMappingId()));
        new DeleteBehaviorTask(this, this, behaviors).executeOnExecutor(AsyncTask
                .THREAD_POOL_EXECUTOR);
        ArrayList<Behavior> goalBehaviors = mGoal.getBehaviors();
        goalBehaviors.remove(behavior);
        mGoal.setBehaviors(goalBehaviors);
        ArrayList<Goal> goals = new ArrayList<Goal>();
        goals.addAll(((CompassApplication) getApplication()).getGoals());
        for (Goal goal : goals) {
            if (goal.getId() == mGoal.getId()) {
                goal.setBehaviors(goalBehaviors);
                break;
            }
        }
        ((CompassApplication) getApplication()).setGoals(goals);
        setResult(Constants.BEHAVIOR_CHANGED_RESULT_CODE);
    }

    @Override
    public void behaviorsDeleted() {
        if (!mFragmentStack.isEmpty()) {
            Fragment fragment = mFragmentStack.get(mFragmentStack.size() - 1);
            if (fragment instanceof LearnMoreFragment) {
                ((LearnMoreFragment) fragment).setImageView();
            } else if (fragment instanceof BehaviorFragment) {
                ((BehaviorFragment) fragment).setImageView();
            }
        }
    }

    @Override
    public void actionChanged() {
        setResult(Constants.BEHAVIOR_CHANGED_RESULT_CODE);
    }

    @Override
    public void fireBehaviorPicker(Behavior behavior) {
        //TODO
    }
}
