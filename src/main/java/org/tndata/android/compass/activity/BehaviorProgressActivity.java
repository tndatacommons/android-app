package org.tndata.android.compass.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;

import org.tndata.android.compass.R;
import org.tndata.android.compass.fragment.BehaviorProgressFragment;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.task.BehaviorProgressTask;

import java.util.ArrayList;
import java.util.Arrays;

public class BehaviorProgressActivity extends Activity implements
        BehaviorProgressFragment.BehaviorProgressFragmentListener, BehaviorProgressTask.BehaviorProgressTaskListener {

    // TODO: temp list of behavior ids
    private ArrayList<Integer> mBehaviorIds;
    private Behavior mBehavior;

    private BehaviorProgressFragment mFragment = null;
    private ArrayList<Fragment> mFragmentStack = new ArrayList<Fragment>();

    private String TAG = "PROGRESS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // TODO: we want to launch this from a notification
        // TODO: we want to receive info from the notification (e.g. a list of behavior ids?)
        // TODO: we want to instantiate a list of Behaviors

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behaviorprogress);

        // TODO: this should give us 31: Savor, 82: Change my Negative self-image
        // TODO: maybe we don't pass any data in. just read all of the user's behaviors from the api
        // ArrayList<Integer> mBehaviorIds
        mBehaviorIds = new ArrayList<>(Arrays.asList(
                (Integer[]) getIntent().getSerializableExtra("behavior_ids")
        ));
        Log.d(TAG, mBehaviorIds.toString());

        setNextBehavior();
    }

    private void setNextBehavior() {
        int bid;
        if(!mBehaviorIds.isEmpty()){
            // TODO: how to load up Behaviors?
            bid = mBehaviorIds.remove(0);
            Log.d(TAG, "removed from mBehaviorIds, not contains: " + mBehaviorIds.toString());
            mBehavior = new Behavior();
            mBehavior.setId(bid);
            mBehavior.setTitle("This is a Placeholder Behavior Title: " + bid);
            swapFragments(true);
        } else {
            mBehavior = null;
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
        behaviorProgressSaved();
    }


    @Override
    public void behaviorProgressSaved() {
        // TODO?  Advance to the next behavior?
        Log.d(TAG, ".behaviorProgressSaved()");
        setNextBehavior();
    }

    private void swapFragments(boolean addToStack) {
        Fragment fragment;

        Log.d(TAG, "swapFragments");
        mFragment = BehaviorProgressFragment.newInstance(mBehavior);
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