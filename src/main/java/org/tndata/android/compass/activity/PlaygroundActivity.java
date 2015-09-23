package org.tndata.android.compass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.tndata.android.compass.R;
import org.tndata.android.compass.service.LocationNotificationService;
import org.tndata.android.compass.ui.button.TransitionButton;

import at.grabner.circleprogress.CircleProgressView;


/**
 * An activity to test new features without compromising the integrity of the
 * rest of the application.
 */
public class PlaygroundActivity extends AppCompatActivity implements View.OnClickListener{

    private TransitionButton button;
    private int state;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playground);

        button = (TransitionButton)findViewById(R.id.playground_button);
        button.setOnClickListener(this);

        state = 0;

        CircleProgressView progress = (CircleProgressView)findViewById(R.id.playground_circle);
        progress.setShowUnit(true);
        progress.setAutoTextSize(true);
        //progress.
    }

    @Override
    public void onClick(View v){
        if (state == 0){
            button.setTransitioningToActive(true);
            state = 1;
        }
        else if (state == 1){
            button.setActive(true);
            state = 2;
        }
        else if (state == 2){
            button.setTransitioningToInactive(true);
            state = 3;
        }
        else if (state == 3){
            button.setInactive(true);
            state = 0;
        }
    }

    @Override
    protected void onDestroy(){
        LocationNotificationService.cancel();
        super.onDestroy();
    }
}
