package org.tndata.android.compass.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.fragment.BehaviorProgressFragment;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.task.BehaviorProgressTask;
import org.tndata.android.compass.task.GetUserBehaviorsTask;
import org.tndata.android.compass.util.Constants;

import java.util.ArrayList;
import java.util.LinkedList;


/**
 * Displays behaviors and allows the user to report his progress on them.
 *
 * @author Edited by Ismael Alonso
 * @version 2.1.0
 */
public class BehaviorProgressActivity
        extends Activity
        implements
                View.OnClickListener,
                BehaviorProgressTask.BehaviorProgressTaskListener,
                GetUserBehaviorsTask.GetUserBehaviorsListener{

    private static final String TAG = "BehaviorProgress";

    private BehaviorProgressFragment mCurrentFragment;

    //The actual data
    private LinkedList<Behavior> mBehaviorList;
    private Behavior mCurrentBehavior;

    //A firewall.
    private boolean mSavingBehavior;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behavior_progress);

        mBehaviorList = new LinkedList<>();

        findViewById(R.id.behavior_progress_on_track).setOnClickListener(this);
        findViewById(R.id.behavior_progress_seeking).setOnClickListener(this);
        findViewById(R.id.behavior_progress_off_course).setOnClickListener(this);

        mSavingBehavior = true;

        mCurrentFragment = BehaviorProgressFragment.newInstance();
        getFragmentManager().beginTransaction()
                .replace(R.id.behavior_progress_content, mCurrentFragment)
                .commit();

        loadBehaviors();
    }

    /**
     * Fires up the task that retrieves the user behaviors.
     */
    private void loadBehaviors(){
        Log.d(TAG, "Loading user behaviors");
        CompassApplication application = (CompassApplication)getApplication();
        String token = application.getToken();
        if (token == null || token.isEmpty()){
            // Read from shared preferences instead.
            SharedPreferences settings = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            token = settings.getString("auth_token", "");
        }

        if (token != null && !token.isEmpty()){
            new GetUserBehaviorsTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, token);
        }
        else{
            // Something is wrong and we don't have an auth token for the user, so fail.
            Log.e(TAG, "AUTH Token is null, giving up!");
            finish();
        }
    }

    @Override
    public void behaviorsLoaded(ArrayList<Behavior> behaviors){
        if (behaviors != null && !behaviors.isEmpty()){
            mBehaviorList.addAll(behaviors);
            Log.d(TAG, "Behaviors: " + behaviors.toString());
        }
        else {
            Log.d(TAG, "---- No Behaviors retrieved. ---");
        }
        nextBehavior();
    }

    /**
     * Displays the next behavior in the queue, if there are none, it closes the activity.
     */
    private void nextBehavior(){
        if (!mBehaviorList.isEmpty()){
            mCurrentBehavior = mBehaviorList.removeFirst();
            mSavingBehavior = false;

            int enter = R.animator.behavior_progress_next_in;
            int exit = R.animator.behavior_progress_current_out;

            mCurrentFragment = BehaviorProgressFragment.newInstance(mCurrentBehavior);
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(enter, exit)
                    .replace(R.id.behavior_progress_content, mCurrentFragment)
                    .commit();
        }
        else{
            mCurrentBehavior = null;
            finish();
        }
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.behavior_progress_on_track:
                onProgressSelected(Constants.BEHAVIOR_ON_COURSE);
                break;

            case R.id.behavior_progress_seeking:
                onProgressSelected(Constants.BEHAVIOR_SEEKING);
                break;

            case R.id.behavior_progress_off_course:
                onProgressSelected(Constants.BEHAVIOR_OFF_COURSE);
                break;
        }
    }

    /**
     * Called when a progress button is clicked.
     *
     * @param progressValue indicates which progress button was clicked.
     */
    private void onProgressSelected(int progressValue){
        if (!mSavingBehavior){
            mSavingBehavior = true;
            mCurrentFragment.showProgress();
            new BehaviorProgressTask(this, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    String.valueOf(mCurrentBehavior.getId()), String.valueOf(progressValue));
        }
    }

    @Override
    public void behaviorProgressSaved(){
        Log.d(TAG, "Behavior progress saved");
        nextBehavior();
    }
}
