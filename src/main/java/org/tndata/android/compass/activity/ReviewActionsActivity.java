package org.tndata.android.compass.activity;

import android.os.Bundle;

import org.tndata.android.compass.model.UserBehavior;
import org.tndata.android.compass.model.UserGoal;


/**
 * Created by isma on 2/24/16.
 */
public class ReviewActionsActivity extends LibraryActivity{
    public static final String USER_GOAL_KEY = "org.tndata.compass.ReviewActions.Goal";
    public static final String USER_BEHAVIOR_KEY = "org.tndata.compass.ReviewActions.Behavior";


    private UserGoal mUserGoal;
    private UserBehavior mUserBehavior;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mUserGoal = (UserGoal)getIntent().getSerializableExtra(USER_GOAL_KEY);
        if (mUserGoal != null){

        }
        else{
            mUserBehavior = (UserBehavior)getIntent().getSerializableExtra(USER_BEHAVIOR_KEY);
            if (mUserBehavior == null){
                finish();
            }

        }
    }
}
