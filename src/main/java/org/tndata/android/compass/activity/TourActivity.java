package org.tndata.android.compass.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.tndata.android.compass.R;
import org.tndata.android.compass.fragment.TourFragment;


public class TourActivity extends AppCompatActivity implements TourFragment.TourFragmentCallback{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);


        getSupportFragmentManager().beginTransaction()
                //.setCustomAnimations(0, R.animator.fade_out_downwards)
                .replace(R.id.my_priorities_fragment_host, new TourFragment())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onTourComplete() {
        finish();
        this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
    }

}
