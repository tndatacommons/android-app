package org.tndata.android.compass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.CustomContentAdapter;
import org.tndata.compass.model.Action;
import org.tndata.compass.model.CustomAction;
import org.tndata.compass.model.CustomGoal;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.ItemSpacing;
import org.tndata.compass.model.ResultSet;

import java.util.Collections;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * Activity used to create, edit, and delete custom goals and actions.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CustomContentActivity
        extends MaterialActivity
        implements
                HttpRequest.RequestCallback,
                Parser.ParserCallback,
                CustomContentAdapter.CustomContentManagerListener{

    private static final String TAG = "CustomContentActivity";

    //Bundle keys
    public static final String CUSTOM_GOAL_KEY = "org.tndata.compass.CustomContent.Goal";
    public static final String CUSTOM_GOAL_ID_KEY = "org.tndata.compass.CustomContent.GoalId";
    public static final String CUSTOM_GOAL_TITLE_KEY = "org.tndata.compass.CustomContent.GoalTitle";

    //Result codes and keys
    public static final int GOAL_REMOVED_RC = 4562;
    public static final String REMOVED_GOAL_KEY = "org.tndata.compass.CustomContent.RemovedGoal";

    //Request codes
    private static final int TRIGGER_RC = 1254;


    private CompassApplication mApplication;
    private CustomGoal mCustomGoal;
    private CustomContentAdapter mAdapter;

    //Request codes
    private int mGetGoalRequestCode;
    private int mAddGoalRequestCode;
    private int mGetActionsRequestCode;
    private int mAddActionRequestCode;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mApplication = (CompassApplication)getApplication();

        getRecyclerView().addItemDecoration(new ItemSpacing(this, 8));

        String goalTitle = getIntent().getStringExtra(CUSTOM_GOAL_TITLE_KEY);
        if (goalTitle != null){
            Log.i(TAG, "A goal title was provided, create mode...");
            mAdapter = new CustomContentAdapter(this, goalTitle, this);
            setAdapter(mAdapter);
        }
        else{
            mCustomGoal = getIntent().getParcelableExtra(CUSTOM_GOAL_KEY);
            if (mCustomGoal != null){
                Log.i(TAG, "A custom goal was provided, edition mode...");
                fetchActions(mCustomGoal);
                mAdapter = new CustomContentAdapter(this, mCustomGoal, this);
                setAdapter(mAdapter);
            }
            else{
                long id = getIntent().getLongExtra(CUSTOM_GOAL_ID_KEY, -1L);
                if (id == -1){
                    Log.i(TAG, "No input, blank mode...");
                    mAdapter = new CustomContentAdapter(this, "", this);
                    setAdapter(mAdapter);
                }
                else{
                    Log.i(TAG, "A goal id was provided, fetching...");
                    mGetGoalRequestCode = HttpRequest.get(this, API.URL.getCustomGoal(id));
                }
            }
        }

        setColor(getResources().getColor(R.color.primary));
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
        mGetActionsRequestCode = HttpRequest.get(this, API.URL.getCustomActions(customGoal));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if (mCustomGoal != null){
            getMenuInflater().inflate(R.menu.menu_custom_content, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected boolean menuItemSelected(MenuItem item){
        if (item.getItemId() == R.id.custom_goal_remove){
            HttpRequest.delete(null, API.URL.deleteGoal(mCustomGoal));
            Intent result = new Intent().putExtra(REMOVED_GOAL_KEY, mCustomGoal);
            setResult(GOAL_REMOVED_RC, result);
            finish();
            return true;
        }
        return false;
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
        if (mCustomGoal != null){
            setIntent(new Intent().putExtra(CUSTOM_GOAL_KEY, mCustomGoal));
        }
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetGoalRequestCode){
            Parser.parse(result, CustomGoal.class, this);
        }
        else if (requestCode == mAddGoalRequestCode){
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
        if (requestCode == mGetActionsRequestCode){
            mAdapter.contentLoadError();
        }
    }

    @Override
    public void onProcessResult(int requestCode, ResultSet result){
        if (result instanceof CustomGoal){
            mCustomGoal = (CustomGoal)result;
            if (mAdapter != null){
                mApplication.addGoal(mCustomGoal);
                mCustomGoal.init();
            }
            invalidateOptionsMenu();
        }
        else if (result instanceof ParserModels.CustomActionResultSet){
            Collections.sort(((ParserModels.CustomActionResultSet)result).results);
        }
        else if (result instanceof CustomAction){
            mApplication.addAction((Action)result);
            mCustomGoal.addAction((CustomAction)result);
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ResultSet result){
        if (result instanceof CustomGoal){
            if (mAdapter == null){
                fetchActions(mCustomGoal);
                mAdapter = new CustomContentAdapter(this, mCustomGoal, this);
                setAdapter(mAdapter);
            }
            else{
                mAdapter.customGoalAdded((CustomGoal)result);
            }
        }
        else if (result instanceof ParserModels.CustomActionResultSet){
            mCustomGoal.setActions(((ParserModels.CustomActionResultSet)result).results);
            mAdapter.customActionsSet();
        }
        else if (result instanceof CustomAction){
            final CustomAction newAction = (CustomAction)result;
            mAdapter.customActionAdded();
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    onEditTrigger(newAction);
                }
            }, 500);
        }
    }

    @Override
    public void onParseFailed(int requestCode){

    }

    @Override
    public void onCreateGoal(@NonNull CustomGoal customGoal){
        mAddGoalRequestCode = HttpRequest.post(this, API.URL.postCustomGoal(),
                API.BODY.postPutCustomGoal(customGoal));
    }

    @Override
    public void onSaveGoal(@NonNull CustomGoal customGoal){
        mApplication.updateGoal(customGoal);
        HttpRequest.put(null, API.URL.putCustomGoal(customGoal),
                API.BODY.postPutCustomGoal(customGoal));
    }

    @Override
    public void onCreateAction(@NonNull String customAction){
        mAddActionRequestCode = HttpRequest.post(this, API.URL.postCustomAction(),
                API.BODY.postPutCustomAction(customAction, mCustomGoal));
    }

    @Override
    public void onSaveAction(@NonNull CustomAction customAction){
        mApplication.updateAction(customAction);
        HttpRequest.put(null, API.URL.putCustomAction(customAction),
                API.BODY.postPutCustomAction(customAction.getTitle(), customAction.getGoal()));
    }

    @Override
    public void onRemoveAction(@NonNull CustomAction customAction){
        mApplication.removeAction(customAction);
        HttpRequest.delete(null, API.URL.deleteAction(customAction));
    }

    @Override
    public void onEditTrigger(@NonNull CustomAction customAction){
        startActivityForResult(new Intent(this, TriggerActivity.class)
                .putExtra(TriggerActivity.GOAL_TITLE_KEY, customAction.getGoal().getTitle())
                .putExtra(TriggerActivity.ACTION_KEY, customAction), TRIGGER_RC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == TRIGGER_RC){
            if (resultCode == RESULT_OK){
                Action action = data.getParcelableExtra(TriggerActivity.ACTION_KEY);
                mApplication.updateAction(action);
            }
        }
    }
}
