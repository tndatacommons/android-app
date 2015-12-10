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

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ChooseBehaviorsAdapter;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.task.AddBehaviorTask;
import org.tndata.android.compass.task.AddGoalTask;
import org.tndata.android.compass.task.DeleteBehaviorTask;
import org.tndata.android.compass.ui.SpacingItemDecoration;
import org.tndata.android.compass.ui.parallaxrecyclerview.HeaderLayoutManagerFixed;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.CompassTagHandler;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkRequest;
import org.tndata.android.compass.util.Parser;

import java.util.ArrayList;
import java.util.List;


/**
 * The ChooseBehaviorsActivity is where a user selects Behaviors for a chosen Goal.
 *
 * @author Edited by Ismael Alonso
 * @version 2.0.0
 */
public class ChooseBehaviorsActivity
        extends AppCompatActivity
        implements
                NetworkRequest.RequestCallback,
                AddBehaviorTask.AddBehaviorsTaskListener,
                AddGoalTask.AddGoalsTaskListener,
                DeleteBehaviorTask.DeleteBehaviorCallback,
                ChooseBehaviorsAdapter.ChooseBehaviorsListener,
                MenuItemCompat.OnActionExpandListener,
                SearchView.OnQueryTextListener,
                SearchView.OnCloseListener{

    //Bundle keys
    public static final String CATEGORY_KEY = "org.tndata.compass.ChooseBehaviors.Category";
    public static final String GOAL_KEY = "org.tndata.compass.ChooseBehaviors.Goal";
    public static final String GOAL_ID_KEY = "org.tndata.compass.ChooseBehaviors.GoalId";

    //Activity tag
    private static final String TAG = "ChooseBehaviorsActivity";


    public CompassApplication mApplication;

    private Toolbar mToolbar;
    private MenuItem mSearchItem;
    private SearchView mSearchView;

    private Category mCategory;
    private Goal mGoal;
    private ChooseBehaviorsAdapter mAdapter;
    private View mHeaderView;
    private RecyclerView mBehaviorList;

    //Request codes
    private int mGetGoalRequestCode;
    private int mGetBehaviorsRequestCode;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_behaviors);
        mApplication = (CompassApplication)getApplication();

        //Get and set the toolbar
        mToolbar = (Toolbar)findViewById(R.id.choose_behaviors_toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mHeaderView = findViewById(R.id.choose_behaviors_material_view);

        //Set the recycler view
        mBehaviorList = (RecyclerView)findViewById(R.id.choose_behaviors_list);
        HeaderLayoutManagerFixed manager = new HeaderLayoutManagerFixed(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mBehaviorList.addItemDecoration(new SpacingItemDecoration(this, 10));
        mBehaviorList.setLayoutManager(manager);
        mBehaviorList.setHasFixedSize(true);

        //TODO remove the use of old keys in favor of the new ones from the app
        //Pull the goal, try with the new key first, if that fails, try the old one, then log
        mGoal = (Goal)getIntent().getSerializableExtra(GOAL_KEY);
        if (mGoal == null){
            mGoal = (Goal)getIntent().getSerializableExtra("goal");
        }

        if (mGoal == null){
            fetchGoal(getIntent().getIntExtra(GOAL_ID_KEY, -1));
        }
        else{
            mToolbar.setTitle(mGoal.getTitle());
            setCategoryAndUserGoal();
            setAdapter();
            fetchBehaviors();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (mAdapter != null){
            if (isGoalSelected()){
                mAdapter.disableAddGoalButton();
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Retrieves a goal from the database.
     *
     * @param goalId the id of the goal to be fetched.
     */
    private void fetchGoal(int goalId){
        Log.d(TAG, "Fetching goal: " + goalId);
        mGetGoalRequestCode = NetworkRequest.get(this, this, API.getGoalUrl(goalId), "");
    }

    /**
     * Retrieves the behaviors of the current goal
     */
    private void fetchBehaviors(){
        mGetBehaviorsRequestCode = NetworkRequest.get(this, this,
                API.getBehaviorsUrl(mGoal.getId()), "");
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetGoalRequestCode){
            //Parse the goal
            mGoal = new Parser().parseGoal(result);
            //Look for a primary category
            for (Category category:mGoal.getCategories()){
                if (mApplication.getUserData().getCategories().contains(category)){
                    mGoal.setPrimaryCategory(category);
                    break;
                }
            }

            //Set UI and fetch the behaviors
            mToolbar.setTitle(mGoal.getTitle());
            setCategoryAndUserGoal();
            setAdapter();
            fetchBehaviors();
            mSearchItem.setVisible(true);
        }
        else if (requestCode == mGetBehaviorsRequestCode){
            List<Behavior> behaviors = new Parser().parseBehaviors(result);
            if (behaviors != null){
                mAdapter.setBehaviors(behaviors);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestFailed(int requestCode){

    }

    /**
     * Sets the category and the user goal properly.
     */
    public void setCategoryAndUserGoal(){
        Goal userGoal = mApplication.getUserData().getGoal(mGoal);
        if (userGoal != null){
            mGoal = userGoal;
        }

        //Pull the category, try with the new key first, if that fails try the old, of that
        //  fails as well, pull it from the goal
        mCategory = (Category)getIntent().getSerializableExtra(CATEGORY_KEY);
        if (mCategory == null){
            mCategory = (Category)getIntent().getSerializableExtra("category");
            if (mCategory == null){
                mCategory = mGoal.getPrimaryCategory();
                if (mCategory == null){
                    if (mGoal.getCategories() != null && !mGoal.getCategories().isEmpty()){
                        mCategory = mGoal.getCategories().get(0);
                    }
                }
            }
        }
    }

    /**
     * Sets the adapter once everything else is in place.
     */
    private void setAdapter(){
        mAdapter = new ChooseBehaviorsAdapter(this, this, mApplication, mBehaviorList, mCategory, mGoal, isGoalSelected());
        mBehaviorList.setAdapter(mAdapter);
        if (mCategory != null && !mCategory.getColor().isEmpty()){
            mHeaderView.setBackgroundColor(Color.parseColor(mCategory.getColor()));
            mToolbar.setBackgroundColor(Color.parseColor(mCategory.getColor()));
        }
    }

    private boolean isGoalSelected(){
        return mApplication.getUserData().getGoals().contains(mGoal);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        mSearchItem = menu.findItem(R.id.filter);
        mSearchItem.setVisible(false);
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

    @Override
    public void addGoal(){
        mGoal.setPrimaryCategory(mCategory);
        mApplication.getUserData().addGoal(mGoal);
        NetworkRequest.post(this, null, API.getPostGoalUrl(), mApplication.getToken(),
                API.getPostGoalBody(mGoal.getId()));
        setResult(RESULT_OK);
    }

    @Override
    public void addBehavior(Behavior behavior){
        //If the goal isn't selected, select it
        if (!isGoalSelected()){
            addGoal();
            mAdapter.disableAddGoalButton();
        }

        //Then select the behavior
        NetworkRequest.post(this, null, API.getPostBehaviorUrl(), mApplication.getToken(),
                API.getPostBehaviorBody(behavior.getId()));

        if (behavior.getActionCount() > 0){
            selectActions(behavior);
        }
    }

    @Override
    public void deleteBehavior(Behavior behavior){
        //Make sure we find the behavior that contains the user's mapping id
        if (behavior.getMappingId() <= 0){
            for (Behavior b:mApplication.getBehaviors()){
                if (behavior.getId() == b.getId()){
                    behavior.setMappingId(b.getMappingId());
                    break;
                }
            }
        }

        Log.e(TAG, "Deleting Behavior, id = " + behavior.getId() +
                ", user_behavior id = " + behavior.getMappingId() + ", " + behavior.getTitle());

        ArrayList<String> behaviorsToDelete = new ArrayList<>();
        behaviorsToDelete.add(String.valueOf(behavior.getMappingId()));
        new DeleteBehaviorTask(mApplication.getToken(), this, behaviorsToDelete).execute();

        mApplication.removeBehavior(behavior);
        Toast.makeText(this, getText(R.string.choose_behaviors_behavior_removed), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void selectActions(Behavior behavior){
        // Launch the ChooseActionsActivity (where users choose actions for this Behavior)
        Intent intent = new Intent(getApplicationContext(), ChooseActionsActivity.class);
        intent.putExtra("category", mCategory);
        intent.putExtra("goal", mGoal);
        intent.putExtra("behavior", behavior);
        startActivity(intent);
    }

    @Override
    public void moreInfo(Behavior behavior){
        AlertDialog.Builder builder = new AlertDialog.Builder(ChooseBehaviorsActivity.this);
        if (!behavior.getHTMLMoreInfo().isEmpty()){
            builder.setMessage(Html.fromHtml(behavior.getHTMLMoreInfo(), null, new CompassTagHandler(this)));
        }
        else{
            builder.setMessage(behavior.getMoreInfo());
        }
        builder.setTitle(behavior.getTitle());
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
    public void doItNow(Behavior behavior){
        CompassUtil.doItNow(this, behavior.getExternalResource());
    }

    @Override
    public void onScroll(float percentage, float offset){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            Drawable color = mToolbar.getBackground();
            color.setAlpha(Math.round(percentage * 255));
            mToolbar.setBackground(color);
        }
        mHeaderView.setTranslationY(-offset * 0.5f);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.VIEW_BEHAVIOR_REQUEST_CODE) {
            setResult(resultCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void behaviorsAdded(ArrayList<Behavior> behaviors){
        if (behaviors != null){
            for (Behavior b:behaviors){
                mApplication.addBehavior(b);
            }
        }
        else{
            Log.e(TAG, "No behaviors added");
        }
        mAdapter.notifyDataSetChanged();
        Toast.makeText(this, getText(R.string.choose_behaviors_behavior_added), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void behaviorsDeleted(){
        Log.d(TAG, "DeleteBehaviorTask completed");
        mAdapter.notifyDataSetChanged();
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
    public void goalsAdded(ArrayList<Goal> goals, Goal goal){
        mApplication.getUserData().getGoal(goal).setMappingId(goal.getMappingId());
    }
}
