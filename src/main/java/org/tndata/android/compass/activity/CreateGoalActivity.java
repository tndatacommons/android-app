package org.tndata.android.compass.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import org.tndata.android.compass.R;


/**
 * Activity used to create, edit, and delete custom goals and activities.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CreateGoalActivity extends AppCompatActivity{
    public static final String CUSTOM_GOAL_TITLE_KEY = "org.tndata.compass.CreateGoal.GoalTitle";


    private EditText mGoalTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_goal);

        mGoalTitle = (EditText)findViewById(R.id.create_goal_title);

        String title = getIntent().getStringExtra(CUSTOM_GOAL_TITLE_KEY);
        if (title != null){
            mGoalTitle.setText(title);
        }
    }
}
