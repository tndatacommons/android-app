package org.tndata.android.compass.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.List;


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

    public static final String CUSTOM_GOAL_KEY = "org.tndata.compass.CreateGoal.Goal";
    public static final String CUSTOM_GOAL_TITLE_KEY = "org.tndata.compass.CreateGoal.GoalTitle";


    private CompassApplication mApplication;

    private EditText mGoalTitle;
    private ImageView mEditGoal;
    private ImageView mSaveGoal;
    private ImageView mAddGoal;
    private ImageView mDeleteGoal;
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
        mEditGoal = (ImageView)findViewById(R.id.create_goal_edit);
        mSaveGoal = (ImageView)findViewById(R.id.create_goal_save);
        mAddGoal = (ImageView)findViewById(R.id.create_goal_add);
        mDeleteGoal = (ImageView)findViewById(R.id.create_goal_delete);
        mActionContainer = (LinearLayout)findViewById(R.id.create_goal_action_container);
        RecyclerView actionRecyclerView = (RecyclerView)findViewById(R.id.create_goal_action_list);

        mEditGoal.setOnClickListener(this);
        mSaveGoal.setOnClickListener(this);
        mAddGoal.setOnClickListener(this);
        mDeleteGoal.setOnClickListener(this);

        List<CustomAction> actionList;
        mCustomGoal = (CustomGoal)getIntent().getSerializableExtra(CUSTOM_GOAL_KEY);
        if (mCustomGoal != null){
            mCustomGoal = (CustomGoal)mApplication.getUserData().getGoal(mCustomGoal);

            mGoalTitle.setText(mCustomGoal.getTitle());
            mGoalTitle.setFocusable(false);
            actionList = mCustomGoal.getActions();
            mEditGoal.setVisibility(View.VISIBLE);
            mAddGoal.setVisibility(View.GONE);
            mDeleteGoal.setVisibility(View.VISIBLE);
            mActionContainer.setVisibility(View.VISIBLE);
        }
        else{
            actionList = new ArrayList<>();
            String title = getIntent().getStringExtra(CUSTOM_GOAL_TITLE_KEY);
            if (title != null){
                mGoalTitle.setText(title);
            }
        }

        mAdapter = new CustomActionAdapter(this, this, actionList);
        actionRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        actionRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.create_goal_edit:
                mGoalTitle.setFocusable(true);
                mGoalTitle.setFocusableInTouchMode(true);
                mGoalTitle.requestFocus();
                mGoalTitle.setSelection(mGoalTitle.getText().length());
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                mEditGoal.setVisibility(View.GONE);
                mSaveGoal.setVisibility(View.VISIBLE);
                break;

            case R.id.create_goal_save:
                String newTitle = mGoalTitle.getText().toString().trim();
                if (newTitle.length() > 0){
                    if (!mCustomGoal.getTitle().equals(newTitle)){
                        mCustomGoal.setTitle(newTitle);
                        NetworkRequest.put(this, null, API.getPutCustomGoalUrl(mCustomGoal),
                                mApplication.getToken(), API.getPostPutCustomGoalBody(mCustomGoal));
                    }
                    InputMethodManager imm2 = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm2.hideSoftInputFromWindow(mGoalTitle.getWindowToken(), 0);
                    mGoalTitle.clearFocus();
                    mGoalTitle.setFocusable(false);

                    mEditGoal.setVisibility(View.VISIBLE);
                    mSaveGoal.setVisibility(View.GONE);
                }
                break;

            case R.id.create_goal_add:
                String goalTitle = mGoalTitle.getText().toString().trim();
                if (goalTitle.length() > 0){
                    mAddGoalRequestCode = NetworkRequest.post(this, this, API.getPostCustomGoalUrl(),
                            mApplication.getToken(), API.getPostPutCustomGoalBody(new CustomGoal(goalTitle)));
                    mAddGoal.setEnabled(false);
                }
                break;

            case R.id.create_goal_delete:
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
            mAdapter.customActionAdded();
        }
    }

    @Override
    public void onSaveAction(CustomAction customAction){
        NetworkRequest.put(this, null, API.getPutCustomActionUrl(customAction),
                mApplication.getToken(), API.getPostPutCustomActionBody(customAction, mCustomGoal));
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

    @Override
    public void onEditTrigger(CustomAction customAction){
        startActivity(new Intent(this, TriggerActivity.class)
                .putExtra(TriggerActivity.GOAL_KEY, mCustomGoal)
                .putExtra(TriggerActivity.ACTION_KEY, customAction));
    }
}
