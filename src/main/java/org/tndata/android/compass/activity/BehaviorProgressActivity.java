package org.tndata.android.compass.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.fragment.BehaviorProgressFragment;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.task.BehaviorProgressTask;
import org.tndata.android.compass.task.GetUserBehaviorsTask;

import java.util.ArrayList;

public class BehaviorProgressActivity extends Activity implements
        BehaviorProgressFragment.BehaviorProgressFragmentListener,
        BehaviorProgressTask.BehaviorProgressTaskListener,
        GetUserBehaviorsTask.GetUserBehaviorsListener {

    private ArrayList<Behavior> mBehaviorList;
    private Behavior mCurrentBehavior;
    private BehaviorProgressFragment mFragment = null;
    private ArrayList<Fragment> mFragmentStack = new ArrayList<Fragment>();
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: we want to launch this from a notification
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behaviorprogress);
        mProgressBar = (ProgressBar) findViewById(R.id.activity_behaviorprogress_loading);
        mBehaviorList = new ArrayList<Behavior>();
        loadBehaviors();
    }

    private void loadBehaviors() {
        new GetUserBehaviorsTask(this).executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR,
                ((CompassApplication) getApplication()).getToken());
    }

    @Override
    public void behaviorsLoaded(ArrayList<Behavior> behaviors) {
        mBehaviorList.addAll(behaviors);
        mProgressBar.setVisibility(View.INVISIBLE);
        setCurrentBehavior();
    }

    private void setCurrentBehavior() {
        if(!mBehaviorList.isEmpty()){
            mCurrentBehavior = mBehaviorList.remove(0);
            swapFragments(true);
        } else {
            mCurrentBehavior = null;
            finish();
        }
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
    public void saveBehaviorProgress(int progressValue) {
        new BehaviorProgressTask(this, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                String.valueOf(mCurrentBehavior.getId()),
                String.valueOf(progressValue));
        // NOTE: behaviorProgressSaved() is the above task's callback
    }

    @Override
    public void behaviorProgressSaved() {
        setCurrentBehavior();
    }

    private void swapFragments(boolean addToStack) {
        Fragment fragment;

        mFragment = BehaviorProgressFragment.newInstance(mCurrentBehavior);
        fragment = mFragment;
        if (addToStack) {
            mFragmentStack.add(fragment);
        }
        getFragmentManager().beginTransaction()
                .replace(R.id.behavior_progress_content, fragment).commit();
    }

    // TODO: Do we even want to do this?
    private void handleBackStack() {
        if (!mFragmentStack.isEmpty()) {
            mFragmentStack.remove(mFragmentStack.size() - 1);
        }
        if (mFragmentStack.isEmpty()) {
            finish();
        } else {
            swapFragments(false);
        }
    }
}