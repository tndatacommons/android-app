package org.tndata.android.compass.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;

import org.tndata.android.compass.R;
import org.tndata.android.compass.fragment.BehaviorProgressFragment;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.task.BehaviorProgressTask;

import java.util.ArrayList;

public class BehaviorProgressActivity extends ActionBarActivity implements
        BehaviorProgressFragment.BehaviorProgressFragmentListener, BehaviorProgressTask.BehaviorProgressTaskListener {

    // TODO: temp list of behavior ids
    private int[] mBehaviorIds = {31, 82};  // 31: Savor, 82: Change my Negative self-image

    private Toolbar mToolbar;
    private Behavior mBehavior;

    private BehaviorProgressFragment mFragment = null;
    private ArrayList<Fragment> mFragmentStack = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // TODO: we want to launch this from a notification
        // TODO: we want to receive info from the notification (e.g. a list of behavior ids?)
        // TODO: we want to instantiate a list of Behaviors

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mBehaviorIds = (int[]) getIntent().getSerializableExtra("behavior_ids");

        // TODO: how to load up Behaviors?
        //mBehavior = (Behavior) getIntent().getSerializableExtra("behavior");
        mBehavior = new Behavior();
        mBehavior.setId(mBehaviorIds[0]);
        mBehavior.setTitle("This is a Placeholder Behavior Title");

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle(mBehavior.getTitle());
        mToolbar.getBackground().setAlpha(255);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        swapFragments(true);
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

        Log.d("BehaviorProgressActivity", ".saveBehaviorProgress(" + progressValue + ")");

        // TODO: Create a BehaviorProgressTask that sends an update to the API.
        /*
        ArrayList<String> behaviors = new ArrayList<String>();
        behaviors.add(String.valueOf(behavior.getId()));
        new BehaviorProgressTask(this, this, behaviors).executeOnExecutor(AsyncTask
                .THREAD_POOL_EXECUTOR);
        */
    }


    @Override
    public void behaviorProgressSaved() {
        // TODO?  Advance to the next behavior?
        swapFragments(true);
    }

    private void swapFragments(boolean addToStack) {
        Fragment fragment = null;

        // TODO: Check to see if we have any more behaviors to report on, and if so, load up the next.
        if(mBehaviorIds.length > 0) {
            mFragment = BehaviorProgressFragment.newInstance(mBehavior);
            fragment = mFragment;
            if (addToStack) {
                mFragmentStack.add(fragment);
            }
            getFragmentManager().beginTransaction()
                    .replace(R.id.base_content, fragment).commit();
        } else {
            // TODO: Otherwise... what? go to the main screen?

        }
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