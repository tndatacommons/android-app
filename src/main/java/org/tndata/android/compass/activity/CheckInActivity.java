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
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.NetworkRequest;
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
                NetworkRequest.RequestCallback,
                CheckInRewardFragment.CheckInRewardListener,
                CheckInFeedbackFragment.CheckInFeedbackListener{

    public static final String TYPE_KEY = "org.tndata.compass.CheckIn.Type";

    public static final int TYPE_REVIEW = 1;
    public static final int TYPE_FEEDBACK = 2;

    private int mType;

    //Magic numbers!! This is the total amount of requests performed by this activity
    public static final int REQUEST_COUNT = 3;


    private CompassApplication mApplication;

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

    private int mGetActionsRequestCode;
    private int mGetRewardRequestCode;
    private int mGetProgressRequestCode;
    private int mCompletedRequests;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        mApplication = (CompassApplication)getApplication();

        mType = getIntent().getIntExtra(TYPE_KEY, TYPE_REVIEW);

        mLoading = (ProgressBar)findViewById(R.id.check_in_loading);
        mContent = findViewById(R.id.check_in_content);
        mPager = (ViewPager)findViewById(R.id.check_in_pager);
        mIndicator = (CircleIndicator)findViewById(R.id.check_in_indicator);

        //API requests
        mCompletedRequests = 0;
        mGetActionsRequestCode = NetworkRequest.get(this, this, API.getTodaysActionsUrl(),
                mApplication.getToken());
        mGetRewardRequestCode = NetworkRequest.get(this, this, API.getRandomRewardUrl(), "");
        mGetProgressRequestCode = NetworkRequest.get(this, this, API.getUserGoalProgressUrl(),
                mApplication.getToken());
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetActionsRequestCode){
            List<Action> actions = new Parser().parseTodaysActions(result);
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
        else if (requestCode == mGetRewardRequestCode){
            mReward = new Parser().parseRewards(result).get(0);
        }
        else if (requestCode == mGetProgressRequestCode){
            try{
                mProgress = (float)new JSONObject(result).getDouble("weekly_checkin_avg");
            }
            catch (JSONException jsonx){
                mProgress = 0;
                jsonx.printStackTrace();
            }
        }
        if (++mCompletedRequests == REQUEST_COUNT){
            setAdapter();
        }
    }

    @Override
    public void onRequestFailed(int requestCode, String message){
        finish();
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
        if (mApplication.getUserData() != null){
            startActivity(new Intent(this, MainActivity.class));
        }
        else{
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }

    @Override
    public void onShareClick(Reward reward){
        //Build the content string
        String content = "";
        if (reward.isJoke()){
            content = "Joke: ";
        }
        if (reward.isFunFact()){
            content = "Fun fact: ";
        }
        if (reward.isFortune()){
            content = "Fortune cookie: ";
        }
        if (reward.isQuote()){
            content = reward.getAuthor() + ": \"";
        }
        content += reward.getMessage();
        if (reward.isQuote()){
            content += "\"";
        }

        //Send the intent
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
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
