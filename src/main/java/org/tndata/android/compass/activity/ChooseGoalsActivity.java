package org.tndata.android.compass.activity;

import android.content.Context;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONObject;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ChooseGoalsAdapter;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.model.GoalContent;
import org.tndata.android.compass.model.UserGoal;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserCallback;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.ui.SpacingItemDecoration;
import org.tndata.android.compass.ui.parallaxrecyclerview.HeaderLayoutManagerFixed;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.NetworkRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The ChooseGoalsActivity is where a user selects Goals within a selected Category.
 *
 * @author Edited by Ismael Alonso
 * @version 2.0.0
 */
public class ChooseGoalsActivity
        extends AppCompatActivity
        implements
                NetworkRequest.RequestCallback,
                ParserCallback,
                ChooseGoalsAdapter.ChooseGoalsListener,
                MenuItemCompat.OnActionExpandListener,
                SearchView.OnQueryTextListener,
                SearchView.OnCloseListener{

    //NOTE: This needs to be regular content because a user may dive down the library
    //  without selecting things. User content ain't available in that use case, but
    //  if it exists it can be retrieved from the UserData bundle
    public static final String CATEGORY_KEY = "org.tndata.compass.ChooseGoalsActivity.Category";

    private static final String TAG = "ChooseGoalsActivity";


    private CompassApplication mApplication;

    private Toolbar mToolbar;
    private MenuItem mSearchItem;
    private SearchView mSearchView;

    private RecyclerView mRecyclerView;
    private ChooseGoalsAdapter mAdapter;

    private TextView mErrorTextView;
    private View mHeaderView;
    private CategoryContent mCategory = null;

    //Request codes
    private int mGetGoalsRequestCode;
    //The maps are necessary if the request fails, since the goal whose op
    //  failed needs to be indexed
    private Map<Integer, GoalContent> mAddGoalRequestCodeMap;
    private Map<Integer, GoalContent> mDeleteGoalRequestCodeMap;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_goals);

        mApplication = (CompassApplication)getApplication();

        mCategory = (CategoryContent)getIntent().getSerializableExtra(CATEGORY_KEY);

        mToolbar = (Toolbar)findViewById(R.id.choose_goals_toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        mToolbar.setTitle(getString(R.string.choose_goals_header_label, mCategory.getTitle()));
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mHeaderView = findViewById(R.id.choose_goals_material_view);

        mRecyclerView = (RecyclerView)findViewById(R.id.choose_goals_recyclerview);
        HeaderLayoutManagerFixed manager = new HeaderLayoutManagerFixed(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(new SpacingItemDecoration(this, 10));
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);

        mErrorTextView = (TextView)findViewById(R.id.choose_goals_error_textview);

        mAdapter = new ChooseGoalsAdapter(this, this, mApplication, mRecyclerView, mCategory);

        mRecyclerView.setAdapter(mAdapter);

        if (mCategory != null && !mCategory.getColor().isEmpty()){
            mHeaderView.setBackgroundColor(Color.parseColor(mCategory.getColor()));
            mToolbar.setBackgroundColor(Color.parseColor(mCategory.getColor()));
        }

        mAddGoalRequestCodeMap = new HashMap<>();
        mDeleteGoalRequestCodeMap = new HashMap<>();

        loadGoals();
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

    /**
     * Displays an error.
     */
    private void showError() {
        mRecyclerView.setVisibility(View.GONE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Fires the goal loader task.
     */
    private void loadGoals(){
        mGetGoalsRequestCode = NetworkRequest.get(this, this, API.getGoalsUrl(mCategory), "");
    }

    @Override
    public void onGoalAddClicked(GoalContent goal){
        mApplication.getUserData().addGoal(goal.getId());
        int code = NetworkRequest.post(this, this, API.getPostGoalUrl(), mApplication.getToken(),
                API.getPostGoalBody(goal, mCategory));
        mAddGoalRequestCodeMap.put(code, goal);

        if (goal.getBehaviorCount() > 0){
            //Launch the GoalTryActivity (where users choose a behavior for the Goal)
            Intent intent = new Intent(getApplicationContext(), ChooseBehaviorsActivity.class);
            intent.putExtra(ChooseBehaviorsActivity.GOAL_KEY, goal);
            intent.putExtra(ChooseBehaviorsActivity.CATEGORY_KEY, mCategory);
            startActivity(intent);
        }
    }

    @Override
    public void onGoalDeleteClicked(GoalContent goal){
        UserGoal userGoal = mApplication.getUserData().getGoal(goal);
        if (userGoal != null){
            Log.d(TAG, "Deleting Goal: " + userGoal.toString());

            int code = NetworkRequest.delete(this, this, API.getDeleteGoalUrl(userGoal),
                    mApplication.getToken(), new JSONObject());
            mDeleteGoalRequestCodeMap.put(code, goal);

            mApplication.removeGoal(userGoal);
        }
        else{
            Log.d(TAG, "(Delete) Goal not found: " + goal.toString());
        }
    }

    @Override
    public void onScroll(float percentage, float offset){
        Drawable drawable = mToolbar.getBackground();
        drawable.setAlpha(Math.round(percentage*255));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            mToolbar.setBackground(drawable);
        }
        mHeaderView.setTranslationY(-offset*0.5f);
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetGoalsRequestCode){
            Parser.parse(result, ParserModels.GoalContentResultSet.class, this);
        }
        else if (mAddGoalRequestCodeMap.containsKey(requestCode)){
            Parser.parse(result, UserGoal.class, this);
        }
        else if (mDeleteGoalRequestCodeMap.containsKey(requestCode)){
            mAdapter.goalDeleted(mDeleteGoalRequestCodeMap.remove(requestCode));
        }
    }

    @Override
    public void onRequestFailed(int requestCode, String message){
        if (mAddGoalRequestCodeMap.containsKey(requestCode)){
            mAdapter.goalNotAdded(mAddGoalRequestCodeMap.remove(requestCode));
        }
        else if (mDeleteGoalRequestCodeMap.containsKey(requestCode)){
            mAdapter.goalNotDeleted(mDeleteGoalRequestCodeMap.remove(requestCode));
        }
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.GoalContentResultSet){
            List<GoalContent> goals = ((ParserModels.GoalContentResultSet)result).results;
            if (goals != null && !goals.isEmpty()){
                mAdapter.addGoals(goals);
            }
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.GoalContentResultSet){
            mAdapter.update();
        }
        else if (result instanceof UserGoal){
            UserGoal userGoal = (UserGoal)result;
            Log.d(TAG, "(Post) " + userGoal.toString());
            mApplication.addGoal(userGoal);
            mAdapter.goalAdded(userGoal.getGoal());
        }
    }
}
