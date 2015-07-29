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

import java.util.ArrayList;
import java.util.LinkedList;


/**
 * Displays behaviors and allows the user to report his progress on them.
 *
 * @author Edited by Ismael Alonso
 * @version 2.0.0
 */
public class BehaviorProgressActivity
        extends Activity
        implements
        BehaviorProgressFragment.OnProgressSelectedListener,
                BehaviorProgressTask.BehaviorProgressTaskListener,
                GetUserBehaviorsTask.GetUserBehaviorsListener{

    private static final String TAG = "BehaviorProgress";

    //UI components
    private ProgressBar mProgressBar;

    //The actual data
    private LinkedList<Behavior> mBehaviorList;
    private Behavior mCurrentBehavior;

    //A firewall.
    private boolean mSavingBehavior;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behavior_progress);

        mProgressBar = (ProgressBar) findViewById(R.id.behavior_progress_progress_bar);
        mBehaviorList = new LinkedList<>();

        mSavingBehavior = false;

        loadBehaviors();
    }

    /**
     * Fires up the task that retrieves the user behaviors.
     */
    private void loadBehaviors(){
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
            mProgressBar.setVisibility(View.GONE);
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

            Fragment fragment = BehaviorProgressFragment.newInstance(mCurrentBehavior);
            getFragmentManager().beginTransaction()
                    .replace(R.id.behavior_progress_content, fragment).commit();
        }
        else{
            mCurrentBehavior = null;
            finish();
        }
    }

    @Override
    public void onProgressSelected(int progressValue){
        if (!mSavingBehavior){
            mSavingBehavior = true;
            new BehaviorProgressTask(this, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    String.valueOf(mCurrentBehavior.getId()),
                    String.valueOf(progressValue));
        }
    }

    @Override
    public void behaviorProgressSaved(){
        nextBehavior();
        mSavingBehavior = false;
    }
}
