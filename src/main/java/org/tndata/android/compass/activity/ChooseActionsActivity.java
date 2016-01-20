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
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.model.UserBehavior;
import org.tndata.android.compass.model.UserGoal;
import org.tndata.android.compass.parser.ContentParser;
import org.tndata.android.compass.ui.SpacingItemDecoration;
import org.tndata.android.compass.ui.parallaxrecyclerview.HeaderLayoutManagerFixed;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.CompassTagHandler;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.NetworkRequest;

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


    //NOTE: These need to be regular content because a user may dive down the library
    //  without selecting things. User content ain't available in that use case, but
    //  if it exists it can be retrieved from the UserData bundle
    public static final String CATEGORY_KEY = "org.tndata.compass.ChooseActionsActivity.Category";
    public static final String GOAL_KEY = "org.tndata.compass.ChooseActionsActivity.Goal";
    public static final String BEHAVIOR_KEY = "org.tndata.compass.ChooseActionsActivity.Behavior";

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
        mCategory = (Category)getIntent().getSerializableExtra(CATEGORY_KEY);
        mGoal = (Goal)getIntent().getSerializableExtra(GOAL_KEY);
        mBehavior = (Behavior)getIntent().getSerializableExtra(BEHAVIOR_KEY);

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

        mGetActionsRequestCode = NetworkRequest.get(this, this, API.getActionsUrl(mBehavior),
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
        startTriggerActivity(mApplication.getUserData().getAction(action));
    }

    @Override
    public void addAction(Action action){
        if (!isGoalSelected()){
            mPostGoalRequestCode = NetworkRequest.post(this, null, API.getPostGoalUrl(),
                    mApplication.getToken(), API.getPostGoalBody(mGoal, mCategory));
        }
        if (!isBehaviorSelected()){
            mPostBehaviorRequestCode = NetworkRequest.post(this, this, API.getPostBehaviorUrl(),
                    mApplication.getToken(), API.getPostBehaviorBody(mBehavior));
        }
        Toast.makeText(getApplicationContext(), getText(R.string.action_saving), Toast.LENGTH_SHORT).show();
        mPostActionRequestCode = NetworkRequest.post(this, this, API.getPostActionUrl(),
                mApplication.getToken(), API.getPostActionBody(action, mGoal));
    }

    @Override
    public void deleteAction(Action action){
        //Make sure we find the user action.
        UserAction userAction = mApplication.getUserData().getAction(action);
        if (userAction != null){
            Log.e(TAG, "Deleting Action: " + userAction.toString());

            mDeleteActionRequestCode = NetworkRequest.delete(this, this,
                    API.getDeleteActionUrl(userAction), mApplication.getToken(), new JSONObject());

            // Remove from the application's collection
            mApplication.removeAction(action);
            mAdapter.notifyDataSetChanged();
        }
        else{
            Log.d(TAG, "(Delete) Action not found: " + action.toString());
        }
    }

    @Override
    public void doItNow(Action action){
        CompassUtil.doItNow(this, action.getExternalResource());
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
            List<Action> actionList = ContentParser.parseActionsFromResultSet(result);
            if (actionList != null && !actionList.isEmpty()){
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
            UserGoal userGoal = ContentParser.parseUserGoal(result);
            Log.d(TAG, "(Post) " + userGoal.toString());
            mApplication.addGoal(userGoal);
        }
        else if (requestCode == mPostBehaviorRequestCode){
            UserBehavior userBehavior = ContentParser.parseUserBehavior(result);
            Log.d(TAG, "(Post) " + userBehavior.toString());
            mApplication.addBehavior(userBehavior);
        }
        else if (requestCode == mPostActionRequestCode){
            UserAction userAction = ContentParser.parseUserAction(result);
            Log.d(TAG, "(Post) " + userAction.toString());
            mApplication.addAction(userAction);

            String toast = getString(R.string.action_added, userAction.getTitle());
            Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();

            mAdapter.notifyDataSetChanged();
            startTriggerActivity(userAction);
        }
        else if (requestCode == mDeleteActionRequestCode){
            Toast.makeText(this, getString(R.string.action_deleted), Toast.LENGTH_SHORT).show();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestFailed(int requestCode, String message){

    }

    private void startTriggerActivity(UserAction userAction){
        startActivity(new Intent(getApplicationContext(), TriggerActivity.class)
                .putExtra(TriggerActivity.USER_GOAL_KEY, mApplication.getUserData().getGoal(mGoal))
                .putExtra(TriggerActivity.USER_ACTION_KEY, userAction));
    }
}
