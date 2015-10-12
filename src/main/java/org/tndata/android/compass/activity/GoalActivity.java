package org.tndata.android.compass.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.GoalAdapter;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.Progress;
import org.tndata.android.compass.ui.button.FloatingActionButton;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.ImageHelper;
import org.tndata.android.compass.util.ImageLoader;
import org.tndata.android.compass.util.OnScrollListenerHub;
import org.tndata.android.compass.util.ParallaxEffect;

import at.grabner.circleprogress.CircleProgressView;


/**
 * Created by isma on 9/24/15.
 */
public class GoalActivity
        extends AppCompatActivity
        implements
                GoalAdapter.GoalAdapterListener,
                ViewTreeObserver.OnGlobalLayoutListener,
                View.OnClickListener{

    public static final String GOAL_KEY = "org.tndata.compass.GoalActivity.Goal";

    private static final int CHOOSE_BEHAVIORS_REQUEST_CODE = 57943;
    private static final int CHOOSE_ACTIONS_REQUEST_CODE = 45875;
    private static final int TRIGGER_REQUEST_CODE = 15426;


    private CompassApplication mApplication;

    private Goal mGoal;

    private TextView mTitle;
    private RecyclerView mList;
    private GoalAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        mApplication = (CompassApplication)getApplication();

        mGoal = (Goal)getIntent().getSerializableExtra(GOAL_KEY);
        mGoal = mApplication.getUserData().getGoal(mGoal);

        ImageView hero = (ImageView)findViewById(R.id.goal_hero);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)hero.getLayoutParams();
        int heroHeight = CompassUtil.getScreenWidth(this)/2;
        params.height = heroHeight;
        hero.setLayoutParams(params);
        //Packaged content ain't got no hero
        if (mGoal.getPrimaryCategory().getImageUrl() != null){
            ImageLoader.loadBitmap(hero, mGoal.getPrimaryCategory().getImageUrl(),
                    new ImageLoader.Options().setCropBottom(true));
        }
        else{
            hero.setImageBitmap(ImageHelper.cropOutBottom(
                    BitmapFactory.decodeResource(getResources(), R.drawable.compass_master_illustration)));
        }

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.goal_fab);
        params = (RelativeLayout.LayoutParams)fab.getLayoutParams();
        params.topMargin = heroHeight-params.height/2;
        fab.setLayoutParams(params);
        fab.setOnClickListener(this);
        fab.setColorNormal(Color.parseColor(mGoal.getPrimaryCategory().getSecondaryColor()));
        fab.setColorPressed(Color.parseColor(mGoal.getPrimaryCategory().getSecondaryColor()));

        Progress progress = mGoal.getProgress();

        CircleProgressView indicator = (CircleProgressView)findViewById(R.id.goal_indicator);
        params = (RelativeLayout.LayoutParams)indicator.getLayoutParams();
        params.topMargin = heroHeight-params.height-2*params.topMargin;
        indicator.setLayoutParams(params);
        indicator.setValue(0);
        indicator.setAutoTextSize(true);
        indicator.setShowUnit(true);
        if (progress != null){
            Log.d("GoalActivity", "Progress ain't null, setting...");
            indicator.setValueAnimated(0, progress.getActionsProgress(), 1500);
        }

        mTitle = (TextView)findViewById(R.id.goal_title);
        mTitle.setBackgroundColor(Color.parseColor(mGoal.getPrimaryCategory().getColor()));
        params = (RelativeLayout.LayoutParams)mTitle.getLayoutParams();
        params.topMargin += heroHeight;
        mTitle.setLayoutParams(params);
        mTitle.setText(mGoal.getTitle());
        mTitle.getViewTreeObserver().addOnGlobalLayoutListener(this);

        mList = (RecyclerView)findViewById(R.id.goal_item_list);
        mList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        OnScrollListenerHub hub = new OnScrollListenerHub();
        hub.addOnScrollListener(new ParallaxEffect(hero, 1));

        ParallaxEffect indicatorEffect = new ParallaxEffect(indicator, 1);
        indicatorEffect.setParallaxCondition(new ParallaxEffect.ParallaxCondition(){
            @Override
            protected boolean doParallax(){
                return getParallaxViewOffset() > 0;
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
        mList.setOnScrollListener(hub);
    }

    private void setAdapter(){
        //Since we are moving serializables around, the object that actually changes is not the
        //  one we are referencing. The Goal with the new list of behaviors needs to be pulled
        //  from the application's list
        mGoal = mApplication.getUserData().getGoal(mGoal);

        //Set the adapter with the fresh goal
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mTitle.getLayoutParams();
        int margin = params.topMargin + mTitle.getHeight() + params.bottomMargin + CompassUtil.getPixels(this, 1);
        mAdapter = new GoalAdapter(this, this, mGoal, margin);
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
    public void onClick(View view){
        switch (view.getId()){
            case R.id.goal_fab:
                Intent chooseBehaviors = new Intent(this, ChooseBehaviorsActivity.class)
                        .putExtra(ChooseBehaviorsActivity.GOAL_KEY, mGoal);
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
    public void onBehaviorSelected(Behavior behavior){
        Intent actionPicker = new Intent(this, ChooseActionsActivity.class)
                .putExtra("category", mGoal.getPrimaryCategory())
                .putExtra("goal", mGoal)
                .putExtra("behavior", behavior);
        startActivityForResult(actionPicker, CHOOSE_ACTIONS_REQUEST_CODE);
    }

    @Override
    public void onActionSelected(Behavior behavior, Action action){
        Intent trigger = new Intent(this, TriggerActivity.class)
                .putExtra("action", action)
                .putExtra("goal", mGoal);
        startActivityForResult(trigger, TRIGGER_REQUEST_CODE);
    }
}
