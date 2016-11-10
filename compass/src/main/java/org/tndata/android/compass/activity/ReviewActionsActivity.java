package org.tndata.android.compass.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ReviewActionsAdapter;
import org.tndata.compass.model.Action;
import org.tndata.compass.model.ResultSet;
import org.tndata.compass.model.TDCCategory;
import org.tndata.compass.model.UserAction;
import org.tndata.compass.model.UserCategory;
import org.tndata.compass.model.UserGoal;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.ImageLoader;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * Activity where the user can review the actions in a particular goal.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ReviewActionsActivity
        extends MaterialActivity
        implements
                View.OnClickListener,
                ReviewActionsAdapter.ReviewActionsListener,
                HttpRequest.RequestCallback,
                Parser.ParserCallback{

    public static final String USER_CATEGORY_KEY = "org.tndata.compass.ReviewActions.Category";
    public static final String USER_GOAL_KEY = "org.tndata.compass.ReviewActions.Goal";
    public static final String USER_ACTION_KEY = "org.tndata.compass.ReviewActions.Action";

    //Request codes
    public static final int ACTION_ACTIVITY_RC = 4562;

    //Result codes and keys
    public static final int GOAL_REMOVED_RC = 9652;
    public static final String REMOVED_GOAL_KEY = "org.tndata.compass.ReviewActions.RemovedGoal";


    private CompassApplication mApplication;
    private ReviewActionsAdapter mAdapter;

    private UserCategory mUserCategory;
    private UserGoal mUserGoal;
    private UserAction mUserAction;

    //Network request codes and urls
    private int mGetUserCategoryRC;
    private int mGetActionsRC;
    private String mGetActionsNextUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mApplication = (CompassApplication)getApplication();

        //A UserCategory is all it's needed when a category's actions are to be displayed
        mUserCategory = getIntent().getParcelableExtra(USER_CATEGORY_KEY);
        mUserGoal = getIntent().getParcelableExtra(USER_GOAL_KEY);
        //For now, a UserAction indicates display goal actions.
        mUserAction = getIntent().getParcelableExtra(USER_ACTION_KEY);

        if (mUserGoal != null || mUserAction != null){
            long categoryId, goalId;
            String goalTitle;
            if (mUserGoal != null){
                categoryId = mUserGoal.getPrimaryCategoryId();
                goalId = mUserGoal.getContentId();
                goalTitle = mUserGoal.getTitle();
            }
            else{
                categoryId = mUserAction.getPrimaryCategoryId();
                goalId = mUserAction.getPrimaryGoalId();
                goalTitle = mUserAction.getGoalTitle();
            }
            TDCCategory category = mApplication.getAvailableCategories().get(categoryId);
            mAdapter = new ReviewActionsAdapter(this, this, goalTitle);
            mGetActionsNextUrl = API.URL.getUserActionsByGoal(goalId);
            if (category != null){
                setColor(Color.parseColor(category.getColor()));
                setGoalHeader(category);
            }
            else{
                mGetUserCategoryRC = HttpRequest.get(this, API.URL.getUserCategory(categoryId));
            }
        }
        else if (mUserCategory != null){
            mAdapter = new ReviewActionsAdapter(this, this, mUserCategory.getTitle());
            mGetActionsNextUrl = API.URL.getUserActions(mUserCategory.getCategory());
            setColor(Color.parseColor(mUserCategory.getColor()));
            setCategoryHeader(mUserCategory.getCategory());
            setFAB(R.id.review_fab, this);
        }
        else{
            finish();
        }

        setAdapter(mAdapter);
    }

    private void setGoalHeader(TDCCategory category){
        View header = inflateHeader(R.layout.header_tile);
        ImageView tile = (ImageView)header.findViewById(R.id.header_tile);

        ImageLoader.Options options = new ImageLoader.Options()
                .setUseDefaultPlaceholder(false)
                .setCropToCircle(true);
        ImageLoader.loadBitmap(tile, category.getIconUrl(), options);
    }

    private void setCategoryHeader(TDCCategory category){
        View header = inflateHeader(R.layout.header_hero);
        ImageView hero = (ImageView)header.findViewById(R.id.header_hero_image);
        if (category.getImageUrl() == null || category.getImageUrl().isEmpty()){
            hero.setImageResource(R.drawable.compass_master_illustration);
        }
        else{
            ImageLoader.Options options = new ImageLoader.Options()
                    .setPlaceholder(R.drawable.compass_master_illustration);
            ImageLoader.loadBitmap(hero, category.getImageUrl(), options);
        }
    }

    @Override
    public void onBackPressed(){
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.review_fab:
                if (mUserAction == null && mUserGoal == null){
                    startActivity(new Intent(this, ChooseGoalsActivity.class)
                            .putExtra(ChooseGoalsActivity.CATEGORY_KEY, mUserCategory.getCategory()));
                }
                break;
        }
    }

    @Override
    public void onHeaderSelected(){
        if (mUserCategory != null){
            if (!mUserCategory.getCategory().isPackagedContent()){
                startActivity(new Intent(this, ChooseGoalsActivity.class)
                        .putExtra(ChooseGoalsActivity.CATEGORY_KEY, mUserCategory.getCategory()));
            }
        }
    }

    @Override
    public void onActionSelected(Action action){
        Intent showAction = new Intent(this, ActionActivity.class)
                .putExtra(ActionActivity.ACTION_KEY, action);
        startActivityForResult(showAction, ACTION_ACTIVITY_RC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == ACTION_ACTIVITY_RC && resultCode == RESULT_OK){
            Action action = data.getParcelableExtra(ActionActivity.ACTION_KEY);
            mApplication.updateAction(action);
            mAdapter.updateAction(action);
        }
    }

    @Override
    public void loadMore(){
        mGetActionsRC = HttpRequest.get(this, mGetActionsNextUrl);
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetUserCategoryRC){
            Parser.parse(result, ParserModels.UserCategoryResultSet.class, this);
        }
        else if (requestCode == mGetActionsRC){
            Parser.parse(result, ParserModels.UserActionResultSet.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){

    }

    @Override
    public void onProcessResult(int requestCode, ResultSet result){

    }

    @Override
    public void onParseSuccess(int requestCode, ResultSet result){
        if (result instanceof ParserModels.UserCategoryResultSet){
            mUserCategory = ((ParserModels.UserCategoryResultSet)result).results.get(0);
            setColor(Color.parseColor(mUserCategory.getColor()));
            setCategoryHeader(mUserCategory.getCategory());
        }
        else if (result instanceof ParserModels.UserActionResultSet){
            ParserModels.UserActionResultSet set = (ParserModels.UserActionResultSet)result;
            mGetActionsNextUrl = set.next;
            mAdapter.addActions(set.results, mGetActionsNextUrl != null || set.results.isEmpty());
            if (set.results.isEmpty()){
                mAdapter.displayError("There are no activities");
            }
        }
    }

    @Override
    public void onParseFailed(int requestCode){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if (mUserGoal != null){
            getMenuInflater().inflate(R.menu.menu_goal_remove, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean menuItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.review_actions_remove_goal:
                HttpRequest.delete(null, API.URL.deleteGoal(mUserGoal));
                Intent result = new Intent().putExtra(REMOVED_GOAL_KEY, mUserGoal);
                setResult(GOAL_REMOVED_RC, result);
                finish();
                return true;
        }
        return false;
    }
}
