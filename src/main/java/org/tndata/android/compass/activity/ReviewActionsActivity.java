package org.tndata.android.compass.activity;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.tndata.android.compass.R;
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
        mUserBehavior = (UserBehavior)getIntent().getSerializableExtra(USER_BEHAVIOR_KEY);
        if (mUserBehavior == null){
            mAdapter = new ReviewActionsAdapter(this, this, mUserGoal);
        }
        else{
            mAdapter = new ReviewActionsAdapter(this, this, mUserBehavior);
        }

        Log.d("ReviewActionsActivity", mUserGoal.getPrimaryCategory().getColor());
        setHeader();
        //setAdapter(mAdapter);
        setColor(Color.parseColor(mUserGoal.getPrimaryCategory().getColor()));

        mSelectedAction = null;
    }

    @SuppressWarnings("deprecation")
    private void setHeader(){
        View header = inflateHeader(R.layout.header_icon);
        RelativeLayout circle = (RelativeLayout)header.findViewById(R.id.review_actions_circle);
        ImageView icon = (ImageView)header.findViewById(R.id.review_actions_icon);

        GradientDrawable gradientDrawable = (GradientDrawable) circle.getBackground();
        gradientDrawable.setColor(Color.parseColor(mUserGoal.getPrimaryCategory().getColor()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            circle.setBackground(gradientDrawable);
        }
        else{
            circle.setBackgroundDrawable(gradientDrawable);
        }

        mUserBehavior.getBehavior().loadIconIntoView(icon);
    }

    @Override
    public void onActionSelected(Action action){

    }

    @Override
    public void loadMore(){

    }
}
