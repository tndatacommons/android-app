package org.tndata.android.compass.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.CustomGoal;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserCallback;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.NetworkRequest;


/**
 * Activity used to create, edit, and delete custom goals and activities.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CreateGoalActivity
        extends AppCompatActivity
        implements
                View.OnClickListener,
                NetworkRequest.RequestCallback,
                ParserCallback{

    public static final String CUSTOM_GOAL_TITLE_KEY = "org.tndata.compass.CreateGoal.GoalTitle";


    private CompassApplication mApplication;

    private EditText mGoalTitle;
    private Button mAddGoal;
    private LinearLayout mActionContainer;

    private int mAddGoalRequestCode;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_goal);

        mApplication = (CompassApplication)getApplication();

        mGoalTitle = (EditText)findViewById(R.id.create_goal_title);
        mAddGoal = (Button)findViewById(R.id.create_goal_add_goal);
        mActionContainer = (LinearLayout)findViewById(R.id.create_goal_action_container);
        mAddGoal.setOnClickListener(this);

        String title = getIntent().getStringExtra(CUSTOM_GOAL_TITLE_KEY);
        if (title != null){
            mGoalTitle.setText(title);
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.create_goal_add_goal:
                String goalTitle = mGoalTitle.getText().toString().trim();
                if (goalTitle.length() > 0){
                    mAddGoalRequestCode = NetworkRequest.post(this, this, API.getPostCustomGoalUrl(),
                            mApplication.getToken(), API.getPostPutCustomGoalBody(new CustomGoal(goalTitle)));
                    mAddGoal.setEnabled(false);
                }
                break;
        }
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mAddGoalRequestCode){
            Parser.parse(result, CustomGoal.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, String message){

    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        mApplication.getUserData().addGoal((CustomGoal)result);
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        mActionContainer.setVisibility(View.VISIBLE);
    }
}
