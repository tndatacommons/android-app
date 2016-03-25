package org.tndata.android.compass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.CheckInPagerAdapter;
import org.tndata.android.compass.fragment.CheckInFeedbackFragment;
import org.tndata.android.compass.fragment.CheckInRewardFragment;
import org.tndata.android.compass.model.DailyProgress;
import org.tndata.android.compass.model.Reward;
import org.tndata.android.compass.model.UserGoal;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;

import java.util.List;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;
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
                HttpRequest.RequestCallback,
                Parser.ParserCallback,
                CheckInRewardFragment.CheckInRewardListener,
                CheckInFeedbackFragment.CheckInFeedbackListener{

    private static final String TAG = "CheckInActivity";

    //Magic numbers!! This is the total amount of requests performed by this activity
    public static final int REQUEST_COUNT = 3;


    //UI components
    private ProgressBar mLoading;
    private View mContent;
    private ViewPager mPager;
    private CircleIndicator mIndicator;

    private CheckInPagerAdapter mAdapter;

    //Data
    private List<UserGoal> mGoals;
    private Reward mReward;
    private float mProgress;
    private int mCurrentProgress[];

    private int mGetGoalsRequestCode;
    private int mGetRewardRequestCode;
    private int mGetProgressRequestCode;
    private int mCompletedRequests;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        mLoading = (ProgressBar)findViewById(R.id.check_in_loading);
        mContent = findViewById(R.id.check_in_content);
        mPager = (ViewPager)findViewById(R.id.check_in_pager);
        mIndicator = (CircleIndicator)findViewById(R.id.check_in_indicator);

        //API requests
        mCompletedRequests = 0;
        mGetGoalsRequestCode = HttpRequest.get(this, API.getTodaysGoalsUrl());
        mGetRewardRequestCode = HttpRequest.get(this, API.getRandomRewardUrl());
        mGetProgressRequestCode = HttpRequest.get(this, API.getUserProgressUrl());
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetGoalsRequestCode){
            Log.d(TAG, "Goals fetched");
            Parser.parse(result, ParserModels.UserGoalsResultSet.class, this);
        }
        else if (requestCode == mGetRewardRequestCode){
            Log.d(TAG, "Reward fetched");
            Parser.parse(result, ParserModels.RewardResultSet.class, this);
        }
        else if (requestCode == mGetProgressRequestCode){
            Log.d(TAG, "Progress fetched");
            Parser.parse(result, ParserModels.DailyProgressResultSet.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        Log.d(TAG, "Request " + requestCode + " failed");
        finish();
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.UserGoalsResultSet){
            mGoals = ((ParserModels.UserGoalsResultSet)result).results;
            mCurrentProgress = new int[mGoals.size()];
        }
        else if (result instanceof ParserModels.RewardResultSet){
            mReward = ((ParserModels.RewardResultSet)result).results.get(0);
        }
        else if (result instanceof ParserModels.DailyProgressResultSet){
            List<DailyProgress> progressList = ((ParserModels.DailyProgressResultSet)result).results;
            mProgress = 0;
            for (DailyProgress progress:progressList){
                mProgress += progress.getCompletedFraction();
            }
            mProgress /= progressList.size();
            mProgress *= 5;
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (++mCompletedRequests == REQUEST_COUNT){
            setAdapter();
        }
    }

    private void setAdapter(){
        mAdapter = new CheckInPagerAdapter(getSupportFragmentManager(), mGoals, mReward);
        mPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mPager);
        mLoading.setVisibility(View.GONE);
        mContent.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHomeClick(){
        startActivity(new Intent(this, LoginActivity.class));
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

        Log.d(TAG, "Progress: " + mProgress);
        Log.d(TAG, "Current progress: " + currentProgressAverage);

        mAdapter.updateRewardFragment(currentProgressAverage >= mProgress);
    }
}
