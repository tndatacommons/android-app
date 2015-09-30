package org.tndata.android.compass.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.GoalAdapter;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.ui.button.FloatingActionButton;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.OnScrollListenerHub;
import org.tndata.android.compass.util.ParallaxEffect;

import at.grabner.circleprogress.CircleProgressView;


/**
 * Created by isma on 9/24/15.
 */
public class GoalActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener{
    public static final String GOAL_KEY = "org.tndata.compass.GoalActivity.Goal";


    private Goal mGoal;

    private TextView mTitle;
    private RecyclerView mList;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        mGoal = (Goal)getIntent().getSerializableExtra(GOAL_KEY);

        ImageView hero = (ImageView)findViewById(R.id.goal_hero);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)hero.getLayoutParams();
        int heroHeight = CompassUtil.getScreenWidth(this)/2;
        params.height = heroHeight;
        hero.setLayoutParams(params);
        hero.setBackgroundColor(Color.GREEN);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.goal_fab);
        params = (RelativeLayout.LayoutParams)fab.getLayoutParams();
        params.topMargin = heroHeight-params.height/2;
        fab.setLayoutParams(params);

        CircleProgressView indicator = (CircleProgressView)findViewById(R.id.goal_indicator);
        params = (RelativeLayout.LayoutParams)indicator.getLayoutParams();
        params.topMargin = heroHeight-params.height-2*params.topMargin;
        indicator.setLayoutParams(params);
        indicator.setValue(0);
        indicator.setAutoTextSize(true);
        indicator.setShowUnit(true);
        indicator.setValueAnimated(0, (int)(100 * Math.random()), 1500);

        mTitle = (TextView)findViewById(R.id.goal_title);
        mTitle.setBackgroundColor(Color.GREEN);
        params = (RelativeLayout.LayoutParams)mTitle.getLayoutParams();
        params.topMargin += heroHeight+CompassUtil.getPixels(this, 1);
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
                if (newMargin < getParallaxViewInitialOffset()/2){
                    view.setPadding(getParallaxViewInitialOffset()/2 - newMargin + CompassUtil.getPixels(GoalActivity.this, 15), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
                }
                else{
                    view.setPadding(CompassUtil.getPixels(GoalActivity.this, 15), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
                }
            }
        });
        hub.addOnScrollListener(titleEffect);

        hub.addOnScrollListener(new ParallaxEffect(fab, 1));
        mList.setOnScrollListener(hub);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onGlobalLayout(){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mTitle.getLayoutParams();
        int margin = params.topMargin + mTitle.getHeight() + params.bottomMargin + CompassUtil.getPixels(this, 1);
        mList.setAdapter(new GoalAdapter(GoalActivity.this, margin));

        //The listener is not needed any longer
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
            mTitle.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
        else{
            mTitle.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    }
}
