package org.tndata.android.compass.activity;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.fragment.BehaviorFragment;
import org.tndata.android.compass.fragment.BehaviorFragment.BehaviorFragmentListener;
import org.tndata.android.compass.fragment.LearnMoreFragment;
import org.tndata.android.compass.fragment.LearnMoreFragment.LearnMoreFragmentListener;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.task.AddBehaviorTask;
import org.tndata.android.compass.task.DeleteBehaviorTask;
import org.tndata.android.compass.util.Constants;

import java.util.ArrayList;
import java.util.List;


public class BehaviorActivity extends TriggerActivity implements
        BehaviorFragmentListener, LearnMoreFragmentListener,
        AddBehaviorTask.AddBehaviorsTaskListener, DeleteBehaviorTask.DeleteBehaviorCallback{
    private static final int BEHAVIOR = 0;
    private static final int LEARN_MORE_BEHAVIOR = 1;
    private static final int LEARN_MORE_ACTION = 2;
    private Toolbar mToolbar;
    private Behavior mBehavior;
    private Goal mGoal;
    private Category mCategory;
    private Action mAction;
    private BehaviorFragment mBehaviorFragment = null;
    private LearnMoreFragment mLearnMoreFragment = null;
    private ArrayList<Fragment> mFragmentStack = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_toolbar);

        mBehavior = (Behavior) getIntent().getSerializableExtra("behavior");
        mGoal = (Goal) getIntent().getSerializableExtra("goal");
        mCategory = (Category) getIntent().getSerializableExtra("category");
        if (mGoal.getBehaviors().contains(mBehavior)) {
            for (Behavior behavior : mGoal.getBehaviors()) {
                if (behavior.getId() == mBehavior.getId()) {
                    mBehavior.setMappingId(behavior.getMappingId());
                    break;
                }
            }
        }


        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle(mBehavior.getTitle());
        mToolbar.getBackground().setAlpha(255);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (mCategory != null && !mCategory.getColor().isEmpty()) {
            mToolbar.setBackgroundColor(Color.parseColor(mCategory.getColor()));
        }

        swapFragments(BEHAVIOR, true);
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
    public void addBehavior(Behavior behavior) {
        ArrayList<String> behaviors = new ArrayList<String>();
        behaviors.add(String.valueOf(behavior.getId()));
        new AddBehaviorTask(this, this, behaviors).executeOnExecutor(AsyncTask
                .THREAD_POOL_EXECUTOR);

    }

    @Override
    public void learnMoreBehavior() {
        swapFragments(LEARN_MORE_BEHAVIOR, true);
    }

    @Override
    public void learnMoreAction(Action action) {
        mAction = action;
        swapFragments(LEARN_MORE_ACTION, true);

    }

    private void handleBackStack() {
        if (!mFragmentStack.isEmpty()) {
            mFragmentStack.remove(mFragmentStack.size() - 1);
        }

        if (mFragmentStack.isEmpty()) {
            finish();
        } else {
            swapFragments(BEHAVIOR, false);
        }
    }

    private void swapFragments(int index, boolean addToStack) {
        Fragment fragment = null;
        switch (index) {
            case BEHAVIOR:
                if (mBehaviorFragment == null) {
                    mBehaviorFragment = BehaviorFragment.newInstance(mBehavior, mCategory);
                }
                fragment = mBehaviorFragment;
                break;
            case LEARN_MORE_BEHAVIOR:
                mLearnMoreFragment = LearnMoreFragment.newInstance(mBehavior, mCategory);
                fragment = mLearnMoreFragment;
                break;
            case LEARN_MORE_ACTION:
                if (mAction != null) {
                    mLearnMoreFragment = LearnMoreFragment.newInstance(mAction, mBehavior,
                            mCategory);
                    fragment = mLearnMoreFragment;
                }
                break;
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
    public void behaviorsAdded(ArrayList<Behavior> behaviors) {
        if (behaviors == null) {
            return;
        }
        List<Behavior> goalBehaviors = mGoal.getBehaviors();
        goalBehaviors.addAll(behaviors);
        mGoal.setBehaviors(goalBehaviors);
        ArrayList<Goal> goals = new ArrayList<Goal>();
        goals.addAll(((CompassApplication) getApplication()).getGoals());
        for (Goal goal : goals) {
            if (goal.getId() == mGoal.getId()) {
                goal.setBehaviors(goalBehaviors);
                break;
            }
        }
        for (Behavior behavior : behaviors) {
            if (behavior.getId() == mBehavior.getId()) {
                mBehavior = behavior;
                break;
            }
        }
        ((CompassApplication) getApplication()).setGoals(goals);
        setResult(Constants.BEHAVIOR_CHANGED_RESULT_CODE);
        if (mLearnMoreFragment != null) {
            mLearnMoreFragment.setBehavior(mBehavior);
        }
        if (mBehaviorFragment != null) {
            mBehaviorFragment.setBehavior(mBehavior);
        }
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
    public void deleteBehavior(Behavior behavior) {
        ArrayList<String> behaviors = new ArrayList<String>();
        behaviors.add(String.valueOf(behavior.getMappingId()));
        new DeleteBehaviorTask(((CompassApplication)getApplication()).getToken(), this, behaviors).executeOnExecutor(AsyncTask
                .THREAD_POOL_EXECUTOR);
        List<Behavior> goalBehaviors = mGoal.getBehaviors();
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
    public void fireActionPicker(Action action) {
        // Launch the ActionTriggerActivity
        Intent intent = new Intent(getApplicationContext(), TriggerActivity.class);
        intent.putExtra("goal", mGoal);
        intent.putExtra("action", action);
        startActivity(intent);
    }

    @Override
    public void fireBehaviorPicker(Behavior behavior) {
        // NOTE: Not implemented at the moment, because we want a single Reminder for all Behaviors.
    }

}
