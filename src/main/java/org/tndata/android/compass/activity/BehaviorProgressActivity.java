package org.tndata.android.compass.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

    private static final String TAG = "BehaviorProgress";
    private ArrayList<Behavior> mBehaviorList;
    private Behavior mCurrentBehavior;
    private BehaviorProgressFragment mFragment = null;
    private ArrayList<Fragment> mFragmentStack = new ArrayList<Fragment>();
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behavior_progress);
        mProgressBar = (ProgressBar) findViewById(R.id.behavior_progress_progress_bar);
        mBehaviorList = new ArrayList<Behavior>();
        loadBehaviors();
    }

    private void loadBehaviors() {
        CompassApplication application = (CompassApplication) getApplication();
        String token = application.getToken();
        if(token == null || token.isEmpty()) {
            // Read from shared preferences instead.
            SharedPreferences settings = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            token = settings.getString("auth_token", "");
        }

        if(!token.isEmpty()) {
            new GetUserBehaviorsTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, token);
        } else {
            // Something is wrong and we don't have an auth token for the user, so fail.
            Log.e(TAG, "AUTH Token is null, giving up!");
            finish();
        }
    }

    @Override
    public void behaviorsLoaded(ArrayList<Behavior> behaviors) {
        if(behaviors != null && !behaviors.isEmpty()) {
            mBehaviorList.addAll(behaviors);
            mProgressBar.setVisibility(View.GONE);
            Log.d(TAG, "Behaviors: " + behaviors.toString());
        }
        else {
            Log.d(TAG, "---- No Behaviors retrieved. ---");
        }
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