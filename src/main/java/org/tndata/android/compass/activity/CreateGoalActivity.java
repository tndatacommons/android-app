package org.tndata.android.compass.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONObject;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.CustomActionAdapter;
import org.tndata.android.compass.model.CustomAction;
import org.tndata.android.compass.model.CustomGoal;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserCallback;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.NetworkRequest;

import java.util.ArrayList;


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
                ParserCallback,
                CustomActionAdapter.CustomActionAdapterListener{

    public static final String CUSTOM_GOAL_TITLE_KEY = "org.tndata.compass.CreateGoal.GoalTitle";


    private CompassApplication mApplication;

    private EditText mGoalTitle;
    private Button mAddGoal;
    private LinearLayout mActionContainer;

    private CustomGoal mCustomGoal;
    private CustomActionAdapter mAdapter;

    private int mAddGoalRequestCode;
    private int mAddActionRequestCode;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_goal);

        mApplication = (CompassApplication)getApplication();

        mGoalTitle = (EditText)findViewById(R.id.create_goal_title);
        mAddGoal = (Button)findViewById(R.id.create_goal_add_goal);
        mActionContainer = (LinearLayout)findViewById(R.id.create_goal_action_container);
        RecyclerView actionList = (RecyclerView)findViewById(R.id.create_goal_action_list);

        mAddGoal.setOnClickListener(this);

        mAdapter = new CustomActionAdapter(this, this, new ArrayList<CustomAction>());
        actionList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        actionList.setAdapter(mAdapter);

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
        else if (requestCode == mAddActionRequestCode){
            Parser.parse(result, CustomAction.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, String message){

    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof CustomGoal){
            ((CustomGoal)result).init();
            mApplication.getUserData().addGoal((CustomGoal)result);
        }
        else if (result instanceof CustomAction){
            ((CustomAction)result).init();
            mApplication.getUserData().addAction((CustomAction)result);
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof CustomGoal){
            mCustomGoal = (CustomGoal)result;
            mActionContainer.setVisibility(View.VISIBLE);
        }
        else if (result instanceof CustomAction){
            mAdapter.addCustomAction((CustomAction)result);
        }
    }

    @Override
    public void onAddClicked(CustomAction customAction){
        mAddActionRequestCode = NetworkRequest.post(this, this, API.getPostCustomActionUrl(),
                mApplication.getToken(), API.getPostPutCustomActionBody(customAction, mCustomGoal));
    }

    @Override
    public void onRemoveClicked(CustomAction customAction){
        NetworkRequest.delete(this, this, API.getDeleteActionUrl(customAction),
                mApplication.getToken(), new JSONObject());
        mApplication.getUserData().removeAction(customAction);
    }
}
