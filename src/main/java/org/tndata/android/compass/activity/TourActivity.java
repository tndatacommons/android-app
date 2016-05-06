package org.tndata.android.compass.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.TourPagerAdapter;
import org.tndata.android.compass.ui.SwipeOutViewPager;

import me.relex.circleindicator.CircleIndicator;


public class TourActivity
        extends AppCompatActivity
        implements
                View.OnClickListener,
                SwipeOutViewPager.OnSwipeOutListener{

    //UI components
    private SwipeOutViewPager mPager;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);

        //Fetch UI components
        mPager = (SwipeOutViewPager)findViewById(R.id.tour_pager);
        CircleIndicator indicator = (CircleIndicator)findViewById(R.id.tour_indicator);

        //Listeners and adapters
        findViewById(R.id.tour_skip).setOnClickListener(this);

        mPager.setAdapter(new TourPagerAdapter(this));
        mPager.setOnSwipeOutListener(this);
        indicator.setViewPager(mPager);
    }

    @Override
    protected void onResume(){
        mPager.setCurrentItem(0);
        super.onResume();
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tour_skip:
                endSequence();
                break;
        }
    }

    @Override
    public void onSwipeOutAtEnd(){
        endSequence();
    }

    private void endSequence(){
        finish();
        overridePendingTransition(R.anim.push_in, R.anim.push_out);
    }
}
