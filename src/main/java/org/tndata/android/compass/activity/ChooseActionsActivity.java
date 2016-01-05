package org.tndata.android.compass.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONObject;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ChooseActionsAdapter;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.parser.ContentParser;
import org.tndata.android.compass.ui.SpacingItemDecoration;
import org.tndata.android.compass.ui.parallaxrecyclerview.HeaderLayoutManagerFixed;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.CompassTagHandler;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * The ChooseActionsActivity is where a user selects Actions for a chosen Behavior.
 */
public class ChooseActionsActivity
        extends AppCompatActivity
        implements
                NetworkRequest.RequestCallback,
                ChooseActionsAdapter.ChooseActionsListener,
                MenuItemCompat.OnActionExpandListener,
                SearchView.OnQueryTextListener,
                SearchView.OnCloseListener{

    private static final String TAG = "ChooseActionsActivity";

    private CompassApplication mApplication;

    private Toolbar mToolbar;
    private MenuItem mSearchItem;
    private SearchView mSearchView;

    private Category mCategory;
    private Goal mGoal;
    private Behavior mBehavior;
    private ChooseActionsAdapter mAdapter;
    private View mHeaderView;

    private int mGetActionsRequestCode;
    private int mPostGoalRequestCode;
    private int mPostBehaviorRequestCode;
    private int mPostActionRequestCode;
    private int mDeleteActionRequestCode;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_actions);

        mApplication = (CompassApplication)getApplication();
        mBehavior = (Behavior)getIntent().getSerializableExtra("behavior");
        mGoal = (Goal)getIntent().getSerializableExtra("goal");
        mCategory = (Category)getIntent().getSerializableExtra("category");

        Behavior behavior = mApplication.getUserData().getBehavior(mBehavior);
        if (behavior != null){
            mBehavior = behavior;
        }

        mToolbar = (Toolbar)findViewById(R.id.choose_actions_toolbar);
        mToolbar.setTitle(mBehavior.getTitle());
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mHeaderView = findViewById(R.id.choose_actions_material_view);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.choose_actions_list);
        HeaderLayoutManagerFixed manager = new HeaderLayoutManagerFixed(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(new SpacingItemDecoration(this, 10));
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);

        mAdapter = new ChooseActionsAdapter(this, this, mApplication, recyclerView, mBehavior);

        recyclerView.setAdapter(mAdapter);
        if (mCategory != null && !mCategory.getColor().isEmpty()) {
            mHeaderView.setBackgroundColor(Color.parseColor(mCategory.getColor()));
            mToolbar.setBackgroundColor(Color.parseColor(mCategory.getColor()));
        }

        mGetActionsRequestCode = NetworkRequest.get(this, this, API.getActionsUrl(mBehavior.getId()),
                mApplication.getToken());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        mSearchItem = menu.findItem(R.id.filter);
        MenuItemCompat.setOnActionExpandListener(mSearchItem, this);

        mSearchView = (SearchView)mSearchItem.getActionView();
        mSearchView.setIconified(false);
        mSearchView.setOnCloseListener(this);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.clearFocus();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item){
        mSearchView.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item){
        mSearchView.setQuery("", false);
        mSearchView.clearFocus();
        return true;
    }

    @Override
    public boolean onClose(){
        mSearchItem.collapseActionView();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query){
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText){
        Log.d("Search", newText);
        mAdapter.filter(newText);
        return false;
    }

    private boolean isGoalSelected(){
        return mApplication.getUserData().getGoal(mGoal) != null;
    }

    private boolean isBehaviorSelected(){
        return mApplication.getUserData().getBehavior(mBehavior) != null;
    }

    @Override
    public void moreInfo(Action action){
        AlertDialog.Builder builder = new AlertDialog.Builder(ChooseActionsActivity.this);
        if (!action.getHTMLMoreInfo().isEmpty()){
            builder.setMessage(Html.fromHtml(action.getHTMLMoreInfo(), null, new CompassTagHandler(this)));
        }
        else{
            builder.setMessage(action.getMoreInfo());
        }
        builder.setTitle(action.getTitle());
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void editReminder(Action action){
        Intent intent = new Intent(getApplicationContext(), TriggerActivity.class);
        intent.putExtra(TriggerActivity.GOAL_KEY, mGoal);
        //Need to pass the action that contains the trigger set by the user (if any), not the
        //  action in the master list, which likely won't contain that information.
        intent.putExtra(TriggerActivity.USER_ACTION_KEY, mApplication.getUserData().getAction(action));
        startActivity(intent);
    }

    @Override
    public void addAction(Action action){
        if (!isGoalSelected()){
            mGoal.setPrimaryCategory(mCategory);
            mApplication.addGoal(mGoal);
            mPostGoalRequestCode = NetworkRequest.post(this, null, API.getPostGoalUrl(),
                    mApplication.getToken(), API.getPostGoalBody(mGoal.getId()));
        }
        if (!isBehaviorSelected()){
            mApplication.addBehavior(mBehavior);
            mPostBehaviorRequestCode = NetworkRequest.post(this, this, API.getPostBehaviorUrl(),
                    mApplication.getToken(), API.getPostBehaviorBody(mBehavior.getId()));
        }
        Toast.makeText(getApplicationContext(), getText(R.string.action_saving), Toast.LENGTH_SHORT).show();
        mPostActionRequestCode = NetworkRequest.post(this, this, API.getPostActionUrl(),
                mApplication.getToken(), API.getPostActionBody(mGoal.getId(), action.getId()));
    }

    @Override
    public void deleteAction(Action action){
        //Make sure we find the action that contains the user's mapping id.
        if (action.getMappingId() <= 0){
            for (Action a:mApplication.getActions().values()){
                if (action.getId() == a.getId()){
                    action.setMappingId(a.getMappingId());
                    break;
                }
            }
        }

        Log.e(TAG, "Deleting Action, id = " + action.getId() + ", user_action id = "
                + action.getMappingId() + ", " + action.getTitle());

        if (action.getMappingId() > 0){
            NetworkRequest.delete(this, this, API.getDeleteActionUrl(action.getMappingId()),
                    mApplication.getToken(), new JSONObject());

            // Remove from the application's collection
            mApplication.removeAction(action);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void doItNow(Action action){
        CompassUtil.doItNow(this, action.getExternalResource());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == Constants.VIEW_BEHAVIOR_REQUEST_CODE){
            setResult(resultCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onScroll(float percentage, float offset){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            Drawable color = mToolbar.getBackground();
            color.setAlpha(Math.round(percentage*255));
            mToolbar.setBackground(color);
        }
        mHeaderView.setTranslationY(-offset * 0.5f);
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetActionsRequestCode){
            List<Action> actionList = new ArrayList<>();
            ContentParser.parseActionsFromResultSet(result, actionList);
            if (!actionList.isEmpty()){
                Collections.sort(actionList, new Comparator<Action>(){
                    @Override
                    public int compare(Action act1, Action act2){
                        return (act1.getSequenceOrder() < act2.getSequenceOrder()) ? 0 : 1;
                    }
                });
                mAdapter.setActions(actionList);
            }
            mAdapter.notifyDataSetChanged();
        }
        else if (requestCode == mPostGoalRequestCode){
            Goal goal = ContentParser.parseGoal(result);
            if (goal != null){
                mApplication.getUserData().getGoal(goal).setMappingId(goal.getMappingId());
            }
        }
        else if (requestCode == mPostBehaviorRequestCode){
            Behavior behavior = ContentParser.parseBehavior(result);
            if (behavior != null){
                mApplication.getUserData().getBehavior(behavior).setMappingId(behavior.getMappingId());
            }
        }
        else if (requestCode == mPostActionRequestCode){
            Action action = ContentParser.parseAction(result);
            Toast.makeText(getApplicationContext(),
                    getString(R.string.action_added, action.getTitle()),
                    Toast.LENGTH_SHORT).show();

            // Add to the application's collection
            mApplication.addAction(action);
            mAdapter.notifyDataSetChanged();

            // launch trigger stuff
            Intent intent = new Intent(getApplicationContext(), TriggerActivity.class);
            intent.putExtra("goal", mGoal);
            intent.putExtra("action", action);
            startActivity(intent);
        }
        else if (requestCode == mDeleteActionRequestCode){
            Toast.makeText(getApplicationContext(), getString(R.string.action_deleted), Toast.LENGTH_SHORT).show();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestFailed(int requestCode, String message){

    }
}
