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
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.parser.ContentParser;
import org.tndata.android.compass.ui.SpacingItemDecoration;
import org.tndata.android.compass.ui.parallaxrecyclerview.HeaderLayoutManagerFixed;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.NetworkRequest;
import org.tndata.android.compass.util.Parser;

import java.util.HashMap;
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
                ChooseGoalsAdapter.ChooseGoalsListener,
                MenuItemCompat.OnActionExpandListener,
                SearchView.OnQueryTextListener,
                SearchView.OnCloseListener{

    public static final String CATEGORY_ID_KEY = "org.tndata.compass.ChooseGoals.CategoryId";


    private CompassApplication mApplication;

    private Toolbar mToolbar;
    private MenuItem mSearchItem;
    private SearchView mSearchView;

    private RecyclerView mRecyclerView;
    private ChooseGoalsAdapter mAdapter;

    private TextView mErrorTextView;
    private View mHeaderView;
    private Category mCategory = null;

    //Request codes
    private int mGetGoalsRequestCode;
    private Map<Integer, Goal> mAddGoalRequestCodeMap;
    private Map<Integer, Goal> mDeleteGoalRequestCodeMap;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_goals);

        mApplication = (CompassApplication)getApplication();

        mCategory = (Category)getIntent().getSerializableExtra("category");
        if (mCategory != null){
            mCategory = mApplication.getUserData().getCategory(mCategory);
        }

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
        if (mCategory == null){
            return;
        }
        mGetGoalsRequestCode = NetworkRequest.get(this, this, API.getGoalsUrl(mCategory.getId()), "");
    }

    @Override
    public void onGoalAddClicked(Goal goal){
        mApplication.getUserData().addGoal(goal);

        int code = NetworkRequest.post(this, this, API.getPostGoalUrl(), mApplication.getToken(),
                API.getPostGoalBody(goal.getId()));
        mAddGoalRequestCodeMap.put(code, goal);

        if (goal.getBehaviorCount() > 0){
            //Launch the GoalTryActivity (where users choose a behavior for the Goal)
            Intent intent = new Intent(getApplicationContext(), ChooseBehaviorsActivity.class);
            intent.putExtra("goal", goal);
            intent.putExtra("category", mCategory);
            startActivity(intent);
        }
    }

    @Override
    public void onGoalDeleteClicked(Goal goal){
        // Remove the goal from the mApplication's collection and DELETE from the API

        // Ensure the goal contains the user mapping id
        if (goal.getMappingId() <= 0) {
            for (Goal g: mApplication.getGoals().values()) {
                if (goal.getId() == g.getId()) {
                    goal.setMappingId(g.getMappingId());
                    break;
                }
            }
        }

        //TODO temp body
        int code = NetworkRequest.delete(this, this, API.getDeleteGoalUrl(goal.getMappingId()),
                mApplication.getToken(), new JSONObject());
        mDeleteGoalRequestCodeMap.put(code, goal);

        mApplication.removeGoal(goal);
    }

    @Override
    public void onGoalOkClicked(Goal goal){
        if (goal.getBehaviorCount() > 0){
            //Launch the GoalTryActivity (where users choose a behavior for the Goal)
            Intent intent = new Intent(getApplicationContext(), ChooseBehaviorsActivity.class);
            intent.putExtra("goal", goal);
            intent.putExtra("category", mCategory);
            startActivity(intent);
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
            Map<Integer, Goal> goals = ContentParser.parseGoals(result);
            if (goals != null && !goals.isEmpty()){
                mAdapter.addGoals(goals.values());
            }
            else{
                showError();
            }
        }
        else if (mAddGoalRequestCodeMap.containsKey(requestCode)){
            Goal addedGoal = new Parser().parseAddedGoal(result);
            Goal goal = mAddGoalRequestCodeMap.remove(requestCode);
            if (addedGoal != null){
                //We've already added the goal to the mApplication's collection.
                Log.d("ChooseGoalsActivity", "Goal added via API");
                mApplication.addGoal(addedGoal); // should include the user's goal mapping id.
                mAdapter.goalAdded(goal);
            }
            else{
                Log.d("ChooseGoalsActivity", "Goal not added");
                mAdapter.goalNotAdded(goal);
            }
        }
        else if (mDeleteGoalRequestCodeMap.containsKey(requestCode)){
            Goal goal = mDeleteGoalRequestCodeMap.remove(requestCode);
            mAdapter.goalDeleted(goal);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, String message){
        if (mAddGoalRequestCodeMap.containsKey(requestCode)){
            Goal goal = mAddGoalRequestCodeMap.remove(requestCode);
            mAdapter.goalNotAdded(goal);
        }
        else if (mDeleteGoalRequestCodeMap.containsKey(requestCode)){
            Goal goal = mDeleteGoalRequestCodeMap.remove(requestCode);
            mAdapter.goalNotDeleted(goal);
        }
    }
}
