package org.tndata.android.compass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

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

    //Dataset and adapter
    private CustomGoal mCustomGoal;
    private CustomContentManagerAdapter mAdapter;

    //Request codes
    private int mAddGoalRequestCode;
    private int mAddActionRequestCode;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mApplication = (CompassApplication)getApplication();

        //Retrieve the data set and populate the UI accordingly
        mCustomGoal = (CustomGoal)getIntent().getSerializableExtra(CUSTOM_GOAL_KEY);
        if (mCustomGoal != null){
            Log.d(TAG, "Edit goal mode");

            mCustomGoal = (CustomGoal)mApplication.getUserData().getGoal(mCustomGoal);

            //Set the adapter
            mAdapter = new CustomContentManagerAdapter(this, mCustomGoal, this);
        }
        else{
            Log.d(TAG, "New goal mode");

            mAdapter = new CustomContentManagerAdapter(this, null, this);
        }
        setAdapter(mAdapter);
        setColor(getResources().getColor(R.color.grow_primary));
    }

    @Override
    public void onBackPressed(){
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    //Keep this snippet around just in case
    //When the user deletes a goal
    /*case R.id.create_goal_delete:
        //Remove it from the dataset, send a DELETE request, and exit the activity
        mApplication.removeGoal(mCustomGoal);
        NetworkRequest.delete(this, null, API.getDeleteGoalUrl(mCustomGoal),
                mApplication.getToken(), new JSONObject());
        setResult(RESULT_OK);
        finish();
        break;*/

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
            mAdapter.customActionAdded((CustomAction)result);
        }
    }

    @Override
    public void createGoal(@NonNull CustomGoal customGoal){
        mAddGoalRequestCode = NetworkRequest.post(this, this, API.getPostCustomGoalUrl(),
                mApplication.getToken(), API.getPostPutCustomGoalBody(customGoal));
    }

    @Override
    public void saveGoal(@NonNull CustomGoal customGoal){
        NetworkRequest.put(this, null, API.getPutCustomGoalUrl(customGoal),
                mApplication.getToken(), API.getPostPutCustomGoalBody(customGoal));
    }

    @Override
    public void createAction(@NonNull CustomAction customAction){
        mAddActionRequestCode = NetworkRequest.post(this, this, API.getPostCustomActionUrl(),
                mApplication.getToken(), API.getPostPutCustomActionBody(customAction, customAction.getGoal()));
    }

    @Override
    public void saveAction(@NonNull CustomAction customAction){
        NetworkRequest.put(this, null, API.getPutCustomActionUrl(customAction),
                mApplication.getToken(), API.getPostPutCustomActionBody(customAction, customAction.getGoal()));
    }

    @Override
    public void editTrigger(@NonNull CustomAction customAction){
        startActivity(new Intent(this, TriggerActivity.class)
                .putExtra(TriggerActivity.GOAL_KEY, customAction.getGoal())
                .putExtra(TriggerActivity.ACTION_KEY, customAction));
    }

    public void onRemoveClicked(CustomAction customAction){
        NetworkRequest.delete(this, this, API.getDeleteActionUrl(customAction),
                mApplication.getToken(), new JSONObject());
        mApplication.getUserData().removeAction(customAction);
    }
}
