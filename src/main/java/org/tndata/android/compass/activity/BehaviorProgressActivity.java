package org.tndata.android.compass.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

    private String TAG = "PROGRESS";

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
        Log.d(TAG, "loadBehaviors()");
        new GetUserBehaviorsTask(this).executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR,
                ((CompassApplication) getApplication()).getToken());
    }

    @Override
    public void behaviorsLoaded(ArrayList<Behavior> behaviors) {
        Log.d(TAG, "behaviorsLoaded: behaviors = " + behaviors.toString());
        if (behaviors != null) {
            mBehaviorList.addAll(behaviors);
        }
        mProgressBar.setVisibility(View.INVISIBLE);
        setCurrentBehavior();
    }

    private void setCurrentBehavior() {
        Log.d(TAG, "Setting Current Behavior...");
        if(!mBehaviorList.isEmpty()){
            mCurrentBehavior = mBehaviorList.remove(0);
            Log.d(TAG, "... to " + mCurrentBehavior);
            swapFragments(true);
        } else {
            Log.d(TAG, "... to null. FINISHED recording progress!");
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

        Log.d(TAG, ".saveBehaviorProgress(" + progressValue + ")");

        // TODO: Create a BehaviorProgressTask that sends an update to the API.
        /*
        ArrayList<String> behaviors = new ArrayList<String>();
        behaviors.add(String.valueOf(behavior.getId()));
        new BehaviorProgressTask(this, this, behaviors).executeOnExecutor(AsyncTask
                .THREAD_POOL_EXECUTOR);
        */
        // TODO: show the spinner, then wait for the async task to call behaviorProgressSaved()
        mProgressBar.setVisibility(View.VISIBLE);
        behaviorProgressSaved();
    }


    @Override
    public void behaviorProgressSaved() {
        Log.d(TAG, ".behaviorProgressSaved()");
        setCurrentBehavior();
    }

    private void swapFragments(boolean addToStack) {
        Fragment fragment;
        Log.d(TAG, "swapFragments");
        mProgressBar.setVisibility(View.INVISIBLE);
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