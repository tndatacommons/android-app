package org.tndata.android.compass.activity;

import android.os.Bundle;

import org.tndata.android.compass.adapter.ReviewActionsAdapter;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.UserBehavior;
import org.tndata.android.compass.model.UserGoal;


/**
 * Created by isma on 2/24/16.
 */
public class ReviewActionsActivity extends LibraryActivity implements ReviewActionsAdapter.ReviewActionsListener{
    public static final String USER_GOAL_KEY = "org.tndata.compass.ReviewActions.Goal";
    public static final String USER_BEHAVIOR_KEY = "org.tndata.compass.ReviewActions.Behavior";


    private UserGoal mUserGoal;
    private UserBehavior mUserBehavior;
    private Action mSelectedAction;
    private ReviewActionsAdapter mAdapter;

    //Network request codes and urls
    private int mGetActionsRequestCode;
    private String mGetActionsNextUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mUserGoal = (UserGoal)getIntent().getSerializableExtra(USER_GOAL_KEY);
        if (mUserGoal != null){
            mAdapter = new ReviewActionsAdapter(this, this, mUserGoal);
        }
        else{
            mUserBehavior = (UserBehavior)getIntent().getSerializableExtra(USER_BEHAVIOR_KEY);
            if (mUserBehavior == null){
                finish();
            }
            mAdapter = new ReviewActionsAdapter(this, this, mUserBehavior);
        }

        setAdapter(mAdapter);

        mSelectedAction = null;
    }

    @Override
    public void onActionSelected(Action action){

    }

    @Override
    public void loadMore(){

    }
}
