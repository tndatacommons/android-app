package org.tndata.android.compass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.CustomContentManagerAdapter;
import org.tndata.android.compass.model.CustomAction;
import org.tndata.android.compass.model.CustomGoal;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;

import java.util.Collections;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * Activity used to create, edit, and delete custom goals and actions.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CustomContentManagerActivity
        extends MaterialActivity
        implements
                HttpRequest.RequestCallback,
                Parser.ParserCallback,
                CustomContentManagerAdapter.CustomContentManagerListener{

    private static final String TAG = "CustomContentManager";

    public static final String CUSTOM_GOAL_KEY = "org.tndata.compass.CreateGoal.Goal";
    public static final String CUSTOM_GOAL_TITLE_KEY = "org.tndata.compass.CreateGoal.GoalTitle";


    private CompassApplication mApplication;
    private CustomGoal mCustomGoal;
    private CustomContentManagerAdapter mAdapter;

    //Request codes
    private int mAddGoalRequestCode;
    private int mGetActionsRequestCode;
    private int mAddActionRequestCode;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mApplication = (CompassApplication)getApplication();

        String goalTitle = getIntent().getStringExtra(CUSTOM_GOAL_TITLE_KEY);
        if (goalTitle != null){
            mAdapter = new CustomContentManagerAdapter(this, goalTitle, this);
        }
        else{
            mCustomGoal = getIntent().getParcelableExtra(CUSTOM_GOAL_KEY);
            if (mCustomGoal != null){
                fetchActions(mCustomGoal);
            }
            mAdapter = new CustomContentManagerAdapter(this, mCustomGoal, this);
        }
        setAdapter(mAdapter);
        setColor(getResources().getColor(R.color.grow_primary));

        View header = inflateHeader(R.layout.header_tile);
        ImageView tile = (ImageView)header.findViewById(R.id.header_tile);
        if (mApplication.getUser().isMale()){
            tile.setImageResource(R.drawable.ic_guy);
        }
        else{
            tile.setImageResource(R.drawable.ic_lady);
        }
    }

    /**
     * Retrieves the actions of a particular goal.
     *
     * @param customGoal the goal whose actions are to be fetched.
     */
    private void fetchActions(@NonNull CustomGoal customGoal){
        mGetActionsRequestCode = HttpRequest.get(this, API.getCustomActionsUrl(customGoal));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        return true;
    }

    @Override
    protected void onHomeTapped(){
        deliverResults();
    }

    @Override
    public void onBackPressed(){
        deliverResults();
        super.onBackPressed();
    }

    private void deliverResults(){
        setResult(RESULT_OK);
        setIntent(new Intent().putExtra(CUSTOM_GOAL_KEY, (Parcelable)mCustomGoal));
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mAddGoalRequestCode){
            Parser.parse(result, CustomGoal.class, this);
        }
        else if (requestCode == mGetActionsRequestCode){
            Parser.parse(result, ParserModels.CustomActionResultSet.class, this);
        }
        else if (requestCode == mAddActionRequestCode){
            Parser.parse(result, CustomAction.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){

    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof CustomGoal){
            mCustomGoal = (CustomGoal)result;
            mCustomGoal.init();
            mApplication.getUserData().addGoal(mCustomGoal);
        }
        else if (result instanceof ParserModels.CustomActionResultSet){
            Collections.sort(((ParserModels.CustomActionResultSet)result).results);
        }
        else if (result instanceof CustomAction){
            mApplication.getUserData().addAction((CustomAction)result);
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof CustomGoal){
            mAdapter.customGoalAdded((CustomGoal)result);
        }
        else if (result instanceof ParserModels.CustomActionResultSet){
            mAdapter.setCustomActions(((ParserModels.CustomActionResultSet)result).results);
        }
        else if (result instanceof CustomAction){
            final CustomAction newAction = (CustomAction)result;
            mAdapter.customActionAdded(newAction);
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    onEditTrigger(newAction);
                }
            }, 500);
        }
    }

    @Override
    public void onCreateGoal(@NonNull CustomGoal customGoal){
        mAddGoalRequestCode = HttpRequest.post(this, API.getPostCustomGoalUrl(),
                API.getPostPutCustomGoalBody(customGoal));
    }

    @Override
    public void onSaveGoal(@NonNull CustomGoal customGoal){
        HttpRequest.put(null, API.getPutCustomGoalUrl(customGoal),
                API.getPostPutCustomGoalBody(customGoal));
    }

    public void deleteGoal(@NonNull CustomGoal customGoal){
        mApplication.removeGoal(customGoal);
        HttpRequest.delete(null, API.getDeleteGoalUrl(customGoal));
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onCreateAction(@NonNull CustomAction customAction){
        mAddActionRequestCode = HttpRequest.post(this, API.getPostCustomActionUrl(),
                API.getPostPutCustomActionBody(customAction, customAction.getGoal()));
    }

    @Override
    public void onSaveAction(@NonNull CustomAction customAction){
        CustomAction original = (CustomAction)mApplication.getUserData().getAction(customAction);
        original.setTitle(customAction.getTitle());
        HttpRequest.put(null, API.getPutCustomActionUrl(customAction),
                API.getPostPutCustomActionBody(customAction, customAction.getGoal()));
    }

    @Override
    public void onEditTrigger(@NonNull CustomAction customAction){
        startActivity(new Intent(this, TriggerActivity.class)
                .putExtra(TriggerActivity.GOAL_KEY, (Parcelable)customAction.getGoal())
                .putExtra(TriggerActivity.ACTION_KEY, (Parcelable)customAction));
    }

    public void onRemoveClicked(@NonNull CustomAction customAction){
        HttpRequest.delete(this, API.getDeleteActionUrl(customAction));
        mApplication.getUserData().removeAction(customAction);
    }
}
