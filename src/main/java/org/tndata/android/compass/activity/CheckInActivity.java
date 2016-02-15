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
import org.tndata.android.compass.model.BehaviorContent;
import org.tndata.android.compass.model.GoalContent;
import org.tndata.android.compass.model.Reward;
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.model.UserBehavior;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.NetworkRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
                Parser.ParserCallback,
                CheckInRewardFragment.CheckInRewardListener,
                CheckInFeedbackFragment.CheckInFeedbackListener{

    public static final String TYPE_KEY = "org.tndata.compass.CheckIn.Type";

    public static final int TYPE_REVIEW = 1;
    public static final int TYPE_FEEDBACK = 2;

    private int mType;

    //Magic numbers!! This is the total amount of requests performed by this activity
    public int mRequestCount = 3;


    private CompassApplication mApplication;

    //UI components
    private ProgressBar mLoading;
    private View mContent;
    private ViewPager mPager;
    private CircleIndicator mIndicator;

    private CheckInPagerAdapter mAdapter;

    //Data
    private List<UserAction> mActions;
    private Map<GoalContent, List<UserAction>> mDataSet;
    private Set<Integer> mBehaviorRequestSet;
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
            Parser.parse(result, ParserModels.UserActionResultSet.class, this);
        }
        else if (requestCode == mGetRewardRequestCode){
            Parser.parse(result, ParserModels.RewardResultSet.class, this);
        }
        else if (requestCode == mGetProgressRequestCode){
            try{
                mProgress = (float)new JSONObject(result).getDouble("weekly_checkin_avg");
            }
            catch (JSONException jsonx){
                mProgress = 0;
                jsonx.printStackTrace();
            }

            if (++mCompletedRequests == mRequestCount){
                setAdapter();
            }
        }
        else if (mBehaviorRequestSet.contains(requestCode)){
            Parser.parse(result, BehaviorContent.class, this);
        }
        else /* Goals */{
            Parser.parse(result, GoalContent.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, String message){
        finish();
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.UserActionResultSet){
            mActions = ((ParserModels.UserActionResultSet)result).results;
            mDataSet = new HashMap<>();
            mBehaviorRequestSet = new HashSet<>();
            Set<Long> goalRequestSet = new HashSet<>();
            //For each action
            for (UserAction action:mActions){
                if (!goalRequestSet.contains(action.getPrimaryGoalId())){
                    //For each goal we need to add one request to the count
                    mRequestCount++;
                    goalRequestSet.add(action.getPrimaryGoalId());
                    NetworkRequest.get(this, this, API.getGoalUrl(action.getPrimaryGoalId()), "");
                }
                if (mType == TYPE_REVIEW){
                    mRequestCount++;
                    mBehaviorRequestSet.add(NetworkRequest.get(this, this,
                            API.getBehaviorUrl(action.getAction().getBehaviorId()), ""));
                }
            }
            mCurrentProgress = new int[goalRequestSet.size()];
        }
        else if (result instanceof GoalContent){
            GoalContent goal = (GoalContent)result;
            List<UserAction> goalActionList = new ArrayList<>();
            for (UserAction action:mActions){
                if (action.getPrimaryGoalId() == goal.getId()){
                    goalActionList.add(action);
                }
            }
            mDataSet.put(goal, goalActionList);
        }
        else if (result instanceof ParserModels.RewardResultSet){
            mReward = ((ParserModels.RewardResultSet)result).results.get(0);
        }
        else if (result instanceof BehaviorContent){
            BehaviorContent behavior = (BehaviorContent)result;
            for (UserAction action:mActions){
                if (action.getAction().getBehaviorId() == behavior.getId()){
                    action.setBehavior(new UserBehavior(behavior));
                }
            }
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (++mCompletedRequests == mRequestCount){
            setAdapter();
        }
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
