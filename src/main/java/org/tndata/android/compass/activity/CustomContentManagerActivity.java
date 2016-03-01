package org.tndata.android.compass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import org.json.JSONObject;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.CustomContentManagerAdapter;
import org.tndata.android.compass.model.CustomAction;
import org.tndata.android.compass.model.CustomGoal;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.NetworkRequest;


/**
 * Activity used to create, edit, and delete custom goals and actions.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CustomContentManagerActivity
        extends MaterialActivity
        implements
                NetworkRequest.RequestCallback,
                Parser.ParserCallback,
                CustomContentManagerAdapter.CustomContentManagerListener{

    private static final String TAG = "CustomContentManager";

    public static final String CUSTOM_GOAL_KEY = "org.tndata.compass.CreateGoal.Goal";
    public static final String CUSTOM_GOAL_TITLE_KEY = "org.tndata.compass.CreateGoal.GoalTitle";


    private CompassApplication mApplication;
    private CustomContentManagerAdapter mAdapter;

    //Request codes
    private int mAddGoalRequestCode;
    private int mAddActionRequestCode;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mApplication = (CompassApplication)getApplication();

        CustomGoal customGoal;
        String goalTitle = getIntent().getStringExtra(CUSTOM_GOAL_TITLE_KEY);
        if (goalTitle != null){
            customGoal = new CustomGoal(goalTitle);
            onCreateGoal(customGoal);
        }
        else{
            customGoal = (CustomGoal)getIntent().getSerializableExtra(CUSTOM_GOAL_KEY);
            if (customGoal != null){
                fetchActions();
            }
        }
        mAdapter = new CustomContentManagerAdapter(this, customGoal, this);
        setAdapter(mAdapter);
        setColor(getResources().getColor(R.color.grow_primary));
    }

    /**
     * Retrieves the actions of a particular goal.
     */
    private void fetchActions(){
        //TODO
    }

    @Override
    public void onBackPressed(){
        setResult(RESULT_OK);
        super.onBackPressed();
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
            CustomGoal customGoal = (CustomGoal)result;
            customGoal.init();
            mApplication.getUserData().addGoal(customGoal);
        }
        else if (result instanceof CustomAction){
            ((CustomAction)result).init();
            mApplication.getUserData().addAction((CustomAction)result);
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof CustomGoal){
            mAdapter.customGoalAdded((CustomGoal)result);
        }
        else if (result instanceof CustomAction){
            mAdapter.customActionAdded();
        }
    }

    @Override
    public void onCreateGoal(@NonNull CustomGoal customGoal){
        mAddGoalRequestCode = NetworkRequest.post(this, this, API.getPostCustomGoalUrl(),
                mApplication.getToken(), API.getPostPutCustomGoalBody(customGoal));
    }

    @Override
    public void onSaveGoal(@NonNull CustomGoal customGoal){
        NetworkRequest.put(this, null, API.getPutCustomGoalUrl(customGoal),
                mApplication.getToken(), API.getPostPutCustomGoalBody(customGoal));
    }

    public void deleteGoal(@NonNull CustomGoal customGoal){
        mApplication.removeGoal(customGoal);
        NetworkRequest.delete(this, null, API.getDeleteGoalUrl(customGoal),
                mApplication.getToken(), new JSONObject());
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onCreateAction(@NonNull CustomAction customAction){
        mAddActionRequestCode = NetworkRequest.post(this, this, API.getPostCustomActionUrl(),
                mApplication.getToken(), API.getPostPutCustomActionBody(customAction, customAction.getGoal()));
    }

    @Override
    public void onSaveAction(@NonNull CustomAction customAction){
        NetworkRequest.put(this, null, API.getPutCustomActionUrl(customAction),
                mApplication.getToken(), API.getPostPutCustomActionBody(customAction, customAction.getGoal()));
    }

    @Override
    public void onEditTrigger(@NonNull CustomAction customAction){
        startActivity(new Intent(this, TriggerActivity.class)
                .putExtra(TriggerActivity.GOAL_KEY, customAction.getGoal())
                .putExtra(TriggerActivity.ACTION_KEY, customAction));
    }

    public void onRemoveClicked(@NonNull CustomAction customAction){
        NetworkRequest.delete(this, this, API.getDeleteActionUrl(customAction),
                mApplication.getToken(), new JSONObject());
        mApplication.getUserData().removeAction(customAction);
    }
}
