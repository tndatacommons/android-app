package org.tndata.android.compass.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.GoalAdapter;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.model.Progress;
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.model.UserBehavior;
import org.tndata.android.compass.model.UserGoal;
import org.tndata.android.compass.ui.button.FloatingActionButton;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.ImageLoader;
import org.tndata.android.compass.util.NetworkRequest;
import org.tndata.android.compass.util.OnScrollListenerHub;
import org.tndata.android.compass.util.ParallaxEffect;

import at.grabner.circleprogress.CircleProgressView;


/**
 * Displays the content hierarchy of a user goal with all the behaviors and actions.
 *
 * TODO this cass needs some fixin'
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class GoalActivity
        extends AppCompatActivity
        implements
                GoalAdapter.GoalAdapterListener,
                ViewTreeObserver.OnGlobalLayoutListener,
                View.OnClickListener{

    //Parameter keys
    public static final String USER_GOAL_KEY = "org.tndata.compass.GoalActivity.UserGoal";

    private static final String TAG = "GoalActivity";

    //Request codes
    private static final int CHOOSE_BEHAVIORS_REQUEST_CODE = 57943;
    private static final int CHOOSE_ACTIONS_REQUEST_CODE = 45875;
    private static final int TRIGGER_REQUEST_CODE = 15426;


    //A reference to the application class
    private CompassApplication mApplication;

    //The goal to be displayed
    private UserGoal mUserGoal;

    //UI components
    private TextView mTitle;
    private RecyclerView mList;
    private GoalAdapter mAdapter;

    //The height of the title bar
    private int mTitleHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        mApplication = (CompassApplication)getApplication();

        //Retrieve the goal
        mUserGoal = (UserGoal)getIntent().getSerializableExtra(USER_GOAL_KEY);

        //Set up the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.goal_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //Set up the hero image
        ImageView hero = (ImageView)findViewById(R.id.goal_hero);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)hero.getLayoutParams();
        int heroHeight = CompassUtil.getScreenWidth(this)*2/3;
        params.height = heroHeight;
        hero.setLayoutParams(params);
        //TODO the first part of the if is a workaround
        //Packaged content ain't got no hero
        if (mUserGoal.getPrimaryCategory() != null && mUserGoal.getPrimaryCategory().getCategory().getImageUrl() != null){
            ImageLoader.loadBitmap(hero, mUserGoal.getPrimaryCategory().getCategory().getImageUrl());
        }
        else{
            int resId = R.drawable.compass_master_illustration;
            hero.setImageBitmap(BitmapFactory.decodeResource(getResources(), resId));
        }

        //Set up the FAB
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.goal_fab);
        params = (RelativeLayout.LayoutParams)fab.getLayoutParams();
        params.topMargin = heroHeight-params.height/2;
        fab.setLayoutParams(params);
        fab.setOnClickListener(this);
        //TODO this is a workaround
        if (mUserGoal.getPrimaryCategory() != null){
            fab.setColorNormal(Color.parseColor(mUserGoal.getPrimaryCategory().getCategory().getSecondaryColor()));
            fab.setColorPressed(Color.parseColor(mUserGoal.getPrimaryCategory().getCategory().getSecondaryColor()));
        }
        else{
            fab.setColorNormal(getResources().getColor(R.color.grow_accent));
            fab.setColorPressed(getResources().getColor(R.color.grow_accent));
        }

        //Set up the general progress indicator
        Progress progress = mUserGoal.getProgress();
        CircleProgressView indicator = (CircleProgressView)findViewById(R.id.goal_indicator);
        params = (RelativeLayout.LayoutParams)indicator.getLayoutParams();
        params.topMargin = heroHeight-params.height-2*params.topMargin;
        indicator.setLayoutParams(params);
        indicator.setValue(0);
        indicator.setAutoTextSize(true);
        indicator.setShowUnit(true);
        if (progress != null){
            Log.d(TAG, "Progress ain't null, setting...");
            indicator.setValueAnimated(0, progress.getActionsProgressPercent(), 1500);
        }

        //Set up the title bar
        mTitle = (TextView)findViewById(R.id.goal_title);
        params = (RelativeLayout.LayoutParams)mTitle.getLayoutParams();
        mTitleHeight = heroHeight/2;
        params.height = mTitleHeight;
        mTitle.setLayoutParams(params);
        //TODO this is a workaround
        if (mUserGoal.getPrimaryCategory() != null){
            mTitle.setBackgroundColor(Color.parseColor(mUserGoal.getPrimaryCategory().getColor()));
        }
        else{
            mTitle.setBackgroundColor(getResources().getColor(R.color.grow_primary));
        }
        params = (RelativeLayout.LayoutParams)mTitle.getLayoutParams();
        params.topMargin += heroHeight;
        mTitle.setLayoutParams(params);
        mTitle.setText(mUserGoal.getTitle());
        mTitle.getViewTreeObserver().addOnGlobalLayoutListener(this);

        //Behavior list
        mList = (RecyclerView)findViewById(R.id.goal_item_list);
        mList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        //Set up the Elements that scroll along with the list
        OnScrollListenerHub hub = new OnScrollListenerHub();
        hub.addOnScrollListener(new ParallaxEffect(hero, 1));

        final int indicatorHeight = CompassUtil.getPixels(this, 90);
        ParallaxEffect indicatorEffect = new ParallaxEffect(indicator, 1);
        indicatorEffect.setParallaxCondition(new ParallaxEffect.ParallaxCondition(){
            @Override
            protected boolean doParallax(){
                return getParallaxViewOffset() > mTitleHeight/2-indicatorHeight/2;
            }

            @Override
            protected int getFixedState(){
                return mTitleHeight/2-indicatorHeight/2;
            }
        });
        hub.addOnScrollListener(indicatorEffect);

        ParallaxEffect titleEffect = new ParallaxEffect(mTitle, 1);
        titleEffect.setParallaxCondition(new ParallaxEffect.ParallaxCondition(){
            @Override
            protected boolean doParallax(){
                return getParallaxViewOffset() > 0;
            }

            @Override
            protected void onStateChanged(int newMargin){
                View view = getParallaxView();
                int padding = CompassUtil.getPixels(GoalActivity.this, 15);
                padding += (int)(((float)(getParallaxViewInitialOffset()-newMargin)/getParallaxViewInitialOffset())*CompassUtil.getPixels(GoalActivity.this, 75));
                view.setPadding(padding, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
            }
        });
        hub.addOnScrollListener(titleEffect);

        hub.addOnScrollListener(new ParallaxEffect(fab, 1));
        mList.addOnScrollListener(hub);
    }

    /**
     * Sets up the behavior list adapter.
     */
    private void setAdapter(){
        //Since we are moving serializables around, the object that actually changes is not the
        //  one we are referencing. The Goal with the new list of behaviors needs to be pulled
        //  from the application's list
        mUserGoal = (UserGoal)mApplication.getUserData().getGoal(mUserGoal);

        //Set the adapter with the fresh goal
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mTitle.getLayoutParams();
        int margin = params.topMargin + mTitle.getHeight() + params.bottomMargin + CompassUtil.getPixels(this, 1);
        mAdapter = new GoalAdapter(this, this, mUserGoal, margin);
        mList.setAdapter(mAdapter);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onGlobalLayout(){
        setAdapter();

        //The listener is not needed any longer
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
            mTitle.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
        else{
            mTitle.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    }

    @Override
    public void onBackPressed(){
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if (mUserGoal.isEditable()){
            getMenuInflater().inflate(R.menu.goal, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.goal_remove){
            NetworkRequest.delete(this, null, API.getDeleteGoalUrl(mUserGoal),
                    mApplication.getToken(), new JSONObject());
            mApplication.getUserData().removeGoal(mUserGoal);
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.goal_fab:
                Intent chooseBehaviors = new Intent(this, ChooseBehaviorsActivity.class)
                        .putExtra(ChooseBehaviorsActivity.GOAL_KEY, mUserGoal.getGoal());
                if (mUserGoal.getPrimaryCategory() != null){
                    CategoryContent category = mUserGoal.getPrimaryCategory().getCategory();
                    chooseBehaviors.putExtra(ChooseBehaviorsActivity.CATEGORY_KEY, category);
                }
                startActivityForResult(chooseBehaviors, CHOOSE_BEHAVIORS_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        //if (resultCode == RESULT_OK){
        if (requestCode == CHOOSE_BEHAVIORS_REQUEST_CODE){
            setAdapter();
        }
        else if (requestCode == CHOOSE_ACTIONS_REQUEST_CODE){
            mAdapter.updateSelectedBehavior();
        }
        else if (requestCode == TRIGGER_REQUEST_CODE){
            mAdapter.updateSelectedBehavior();
        }
        //}
    }

    @Override
    public void onBehaviorSelected(UserBehavior userBehavior){
        //TODO what here?
        /*Intent actionPicker = new Intent(this, ChooseActionsActivity.class)
                .putExtra(ChooseActionsActivity.CATEGORY_KEY, mUserGoal.getPrimaryCategory().getCategory())
                .putExtra(ChooseActionsActivity.GOAL_KEY, mUserGoal.getGoal())
                .putExtra(ChooseActionsActivity.BEHAVIOR_KEY, userBehavior.getBehavior());
        startActivityForResult(actionPicker, CHOOSE_ACTIONS_REQUEST_CODE);*/
    }

    @Override
    public void onActionSelected(UserBehavior userBehavior, UserAction userAction){
        Intent trigger = new Intent(this, TriggerActivity.class)
                .putExtra(TriggerActivity.ACTION_KEY, userAction)
                .putExtra(TriggerActivity.GOAL_KEY, mUserGoal);
        startActivityForResult(trigger, TRIGGER_REQUEST_CODE);
    }
}
