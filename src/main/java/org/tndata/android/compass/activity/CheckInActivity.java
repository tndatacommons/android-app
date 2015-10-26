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
import org.tndata.android.compass.task.GetUserActionsTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by isma on 10/26/15.
 */
public class CheckInActivity extends AppCompatActivity implements GetUserActionsTask.GetUserActionsCallback{
    public static final String TYPE_KEY = "org.tndata.compass.CheckIn.Type";

    public static final int TYPE_REVIEW = 0;
    public static final int TYPE_FEEDBACK = 1;


    private int mType;

    private ViewPager mPager;
    private ProgressBar mLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        mType = getIntent().getIntExtra(TYPE_KEY, TYPE_REVIEW);

        mPager = (ViewPager)findViewById(R.id.check_in_pager);
        mLoading = (ProgressBar)findViewById(R.id.check_in_loading);

        new GetUserActionsTask(this).execute(((CompassApplication)getApplication()).getToken(), "today");
    }

    @Override
    public void onActionsLoaded(List<Action> actions){
        if (mType == TYPE_REVIEW){
            Map<Goal, List<Action>> dataSet = new HashMap<>();
            //For each action
            for (Action action:actions){
                //If there is a primary goal
                if (action.getPrimaryGoal() != null){
                    //If the primary goal is already in the data set
                    if (dataSet.containsKey(action.getPrimaryGoal())){
                        //Add the action to the associated list
                        dataSet.get(action.getPrimaryGoal()).add(action);
                    }
                    //Otherwise
                    else{
                        //Create the list and add the goal to the data set
                        List<Action> actionList = new ArrayList<>();
                        actionList.add(action);
                        dataSet.put(action.getPrimaryGoal(), actionList);
                    }
                }
            }
            mPager.setAdapter(new CheckInPagerAdapter(getSupportFragmentManager(), dataSet));
        }
        mLoading.setVisibility(View.GONE);
        mPager.setVisibility(View.VISIBLE);
    }
}
