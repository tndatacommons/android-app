package org.tndata.android.compass.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ViewSwitcher;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.MyPrioritiesCategoryAdapter;
import org.tndata.android.compass.adapter.MyPrioritiesGoalAdapter;
import org.tndata.android.compass.model.Category;


/**
 * Activity that displays the elements chosen by the user hierarchically.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class MyPrioritiesActivity
        extends ActionBarActivity
        implements
                AdapterView.OnItemClickListener,
                Animation.AnimationListener{

    //UI components
    private ViewSwitcher mSwitcher;
    private ListView mCategoryList;
    private ListView mGoalList;

    //Animations
    private Animation mNextIn, mCurrentOutLeft;
    private Animation mPreviousIn, mCurrentOutRight;

    private boolean mAnimationInProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_priorities);

        //Retrieve the UI components
        mSwitcher = (ViewSwitcher)findViewById(R.id.my_priorities_switcher);
        mCategoryList = (ListView)findViewById(R.id.my_priorities_category_list);
        mCategoryList.setAdapter(new MyPrioritiesCategoryAdapter(getApplicationContext(),
                ((CompassApplication)getApplication()).getCategories()));
        mCategoryList.setOnItemClickListener(this);
        mGoalList = (ListView)findViewById(R.id.my_priorities_goal_list);

        //Create the animations
        mNextIn = AnimationUtils.loadAnimation(this, R.anim.next_in);
        mNextIn.setAnimationListener(this);
        mPreviousIn = AnimationUtils.loadAnimation(this, R.anim.previous_in);
        mPreviousIn.setAnimationListener(this);
        mCurrentOutLeft = AnimationUtils.loadAnimation(this, R.anim.current_out_left);
        mCurrentOutRight = AnimationUtils.loadAnimation(this, R.anim.current_out_right);

        mAnimationInProgress = false;
    }

    @Override
    public void onBackPressed(){
        if (!mAnimationInProgress){
            if (mSwitcher.getDisplayedChild() != 0){
                mSwitcher.setInAnimation(mPreviousIn);
                mSwitcher.setOutAnimation(mCurrentOutRight);
                mSwitcher.showPrevious();
            }
            else{
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        if (!mAnimationInProgress){
            Category category = (Category) mCategoryList.getAdapter().getItem(position);
            mGoalList.setAdapter(new MyPrioritiesGoalAdapter(getApplicationContext(), category.getGoals()));
            mSwitcher.setInAnimation(mNextIn);
            mSwitcher.setOutAnimation(mCurrentOutLeft);
            mSwitcher.showNext();
        }
    }

    @Override
    public void onAnimationStart(Animation animation){
        mAnimationInProgress = true;
    }

    @Override
    public void onAnimationEnd(Animation animation){
        mAnimationInProgress = false;
    }

    @Override
    public void onAnimationRepeat(Animation animation){
        //Unused
    }
}
