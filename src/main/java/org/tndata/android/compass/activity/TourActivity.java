package org.tndata.android.compass.activity;

import android.app.Activity;
import android.os.Bundle;

import org.tndata.android.compass.R;
import org.tndata.android.compass.fragment.TourFragment;


public class TourActivity extends Activity implements TourFragment.TourFragmentListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);


        getFragmentManager().beginTransaction()
                .setCustomAnimations(0, R.animator.fade_out_downwards)
                .replace(R.id.my_priorities_fragment_host, new TourFragment())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void tourFinish() {
        finish();
        this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
    }

}
