package org.tndata.android.compass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.CheckInPagerAdapter;
import org.tndata.android.compass.fragment.CheckInFeedbackFragment;
import org.tndata.android.compass.fragment.CheckInRewardFragment;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.Reward;
import org.tndata.android.compass.task.GetContentTask;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.Parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator;


/**
 * Activity to display review and feedback check-in screens.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CheckInActivity
        extends AppCompatActivity
        implements
                GetContentTask.GetContentListener,
                CheckInRewardFragment.CheckInRewardListener,
                CheckInFeedbackFragment.CheckInFeedbackListener{

    public static final String TYPE_KEY = "org.tndata.compass.CheckIn.Type";

    public static final int TYPE_REVIEW = 1;
    public static final int TYPE_FEEDBACK = 2;

    public static final int TODAYS_ACTIONS_REQUEST_CODE = 0;
    public static final int REWARD_REQUEST_CODE = TODAYS_ACTIONS_REQUEST_CODE+1;
    public static final int PROGRESS_REQUEST_CODE = REWARD_REQUEST_CODE+1;
    public static final int REQUEST_COUNT = PROGRESS_REQUEST_CODE+1;

    public int mCompletedRequests;


    private int mType;

    //UI components
    private ProgressBar mLoading;
    private View mContent;
    private ViewPager mPager;
    private CircleIndicator mIndicator;

    private CheckInPagerAdapter mAdapter;

    //Data
    private Map<Goal, List<Action>> mDataSet;
    private Reward mReward;
    private float mProgress;
    private int mCurrentProgress[];


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        mCompletedRequests = 0;
        mType = getIntent().getIntExtra(TYPE_KEY, TYPE_REVIEW);

        mLoading = (ProgressBar)findViewById(R.id.check_in_loading);
        mContent = findViewById(R.id.check_in_content);
        mPager = (ViewPager)findViewById(R.id.check_in_pager);
        mIndicator = (CircleIndicator)findViewById(R.id.check_in_indicator);

        String token = ((CompassApplication)getApplication()).getToken();
        String url = Constants.BASE_URL + "users/actions/?today=1";
        new GetContentTask(this, TODAYS_ACTIONS_REQUEST_CODE).execute(url, token);

        url = Constants.BASE_URL + "rewards/?random=1";
        new GetContentTask(this, REWARD_REQUEST_CODE).execute(url, token);

        url = Constants.BASE_URL + "users/goals/progress/average/?current=5";
        new GetContentTask(this, PROGRESS_REQUEST_CODE).execute(url, token);
    }

    @Override
    public void onContentRetrieved(int requestCode, String content){
        if (requestCode == TODAYS_ACTIONS_REQUEST_CODE){
            List<Action> actions = new Parser().parseTodaysActions(content);
            mDataSet = new HashMap<>();
            //For each action
            for (Action action:actions){
                //If there is a primary goal
                if (action.getPrimaryGoal() != null){
                    //If the primary goal is already in the data set
                    if (mDataSet.containsKey(action.getPrimaryGoal())){
                        //Add the action to the associated list
                        mDataSet.get(action.getPrimaryGoal()).add(action);
                    }
                    //Otherwise
                    else{
                        //Create the list and add the goal to the data set
                        List<Action> actionList = new ArrayList<>();
                        actionList.add(action);
                        mDataSet.put(action.getPrimaryGoal(), actionList);
                    }
                }
            }
            mCurrentProgress = new int[mDataSet.size()];
        }
        else if (requestCode == REWARD_REQUEST_CODE){
            mReward = new Parser().parseRewards(content).get(0);
        }
        else if (requestCode == PROGRESS_REQUEST_CODE){
            try{
                mProgress = (float)new JSONObject(content).getDouble("weekly_checkin_avg");
            }
            catch (JSONException jsonx){
                mProgress = 0;
                jsonx.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestComplete(int requestCode){
        if (++mCompletedRequests == REQUEST_COUNT){
            setAdapter();
        }
    }

    @Override
    public void onRequestFailed(int requestCode){

    }

    private void setAdapter(){
        mAdapter = new CheckInPagerAdapter(getSupportFragmentManager(),
                mDataSet, mReward, mType == TYPE_REVIEW);
        mPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mPager);
        mLoading.setVisibility(View.GONE);
        mContent.setVisibility(View.VISIBLE);
    }

    @Override
    public void onReviewClick(){
        if (((CompassApplication)getApplication()).getUserData() != null){
            startActivity(new Intent(this, MainActivity.class));
        }
        else{
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }

    @Override
    public void onProgressChanged(int index, int progress){
        mCurrentProgress[index] = progress;
        float currentProgressAverage = 0;
        for (int currentProgress:mCurrentProgress){
            currentProgressAverage += currentProgress;
        }
        currentProgressAverage /= mCurrentProgress.length;
        mAdapter.updateRewardFragment(currentProgressAverage >= mProgress);
    }
}
