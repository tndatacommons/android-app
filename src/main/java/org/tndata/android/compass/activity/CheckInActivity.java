package org.tndata.android.compass.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.CheckInPagerAdapter;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.Reward;
import org.tndata.android.compass.task.GetContentTask;
import org.tndata.android.compass.task.GetTodaysActionsTask;
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
                GetTodaysActionsTask.GetTodaysActionsCallback,
                GetContentTask.GetContentListener{

    public static final String TYPE_KEY = "org.tndata.compass.CheckIn.Type";

    public static final int TYPE_REVIEW = 0;
    public static final int TYPE_FEEDBACK = 1;

    public static final int REWARD_REQUEST_CODE = 2;

    public int mRequestCount;


    private int mType;

    private ProgressBar mLoading;
    private View mContent;
    private ViewPager mPager;
    private CircleIndicator mIndicator;

    private Map<Goal, List<Action>> mDataSet;
    private Reward mReward;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        mRequestCount = 0;
        mType = getIntent().getIntExtra(TYPE_KEY, TYPE_REVIEW);

        mLoading = (ProgressBar)findViewById(R.id.check_in_loading);
        mContent = findViewById(R.id.check_in_content);
        mPager = (ViewPager)findViewById(R.id.check_in_pager);
        mIndicator = (CircleIndicator)findViewById(R.id.check_in_indicator);

        String token = ((CompassApplication)getApplication()).getToken();

        new GetTodaysActionsTask(this, token).execute();
        new GetContentTask(this, REWARD_REQUEST_CODE).execute(Constants.BASE_URL+"rewards/?random=1", token);
    }

    @Override
    public void onActionsLoaded(List<Action> actions){
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
        if (++mRequestCount == 2){
            setAdapter();
        }
    }

    @Override
    public void onContentRetrieved(int requestCode, String content){
        mReward = new Parser().parseRewards(content).get(0);
    }

    @Override
    public void onRequestComplete(int requestCode){
        if (++mRequestCount == 2){
            setAdapter();
        }
    }

    @Override
    public void onRequestFailed(int requestCode){

    }

    private void setAdapter(){
        mPager.setAdapter(new CheckInPagerAdapter(getSupportFragmentManager(), mDataSet, mReward, mType == TYPE_REVIEW));
        mIndicator.setViewPager(mPager);
        mLoading.setVisibility(View.GONE);
        mContent.setVisibility(View.VISIBLE);
    }
}
