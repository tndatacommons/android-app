package org.tndata.android.compass.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ReviewActionsAdapter;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.model.UserCategory;
import org.tndata.android.compass.model.UserGoal;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.ImageHelper;
import org.tndata.android.compass.util.ImageLoader;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * Activity where the user can review the actions in a particular goal or behavior.
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
    public static final String USER_BEHAVIOR_KEY = "org.tndata.compass.ReviewActions.Behavior";

    public static final int ACTION_ACTIVITY_RC = 4562;


    private CompassApplication mApplication;
    private ReviewActionsAdapter mAdapter;

    private UserCategory mUserCategory;
    private UserGoal mUserGoal;
    private CategoryContent mGoalCategory;

    //Network request codes and urls
    private int mGetUserCategoryRC;
    private int mGetActionsRC;
    private String mGetActionsNextUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mApplication = (CompassApplication)getApplication();

        //A UserCategory is all it's needed when a category's actions are to be displayed
        mUserCategory = (UserCategory)getIntent().getSerializableExtra(USER_CATEGORY_KEY);
        //If either a UserGoal's actions or a UserBehavior's actions are to be displayed
        //  the a CategoryContent can be used to populate headers. In this case a UserGoal
        //  will always be available, so the primary category id can be used to retrieve
        //  it from the Application class list
        mUserGoal = (UserGoal)getIntent().getSerializableExtra(USER_GOAL_KEY);

        if (mUserGoal != null){
            mGoalCategory = mApplication.getPublicCategories().get(mUserGoal.getPrimaryCategoryId());
            mAdapter = new ReviewActionsAdapter(this, this, mUserGoal.getTitle());
            mGetActionsNextUrl = API.getUserActionsUrl(mUserGoal.getGoal());
            if (mGoalCategory != null){
                setColor(Color.parseColor(mGoalCategory.getColor()));
                setGoalHeader(mGoalCategory);
                setFAB(R.id.review_fab, this);
            }
            else{
                long categoryId = mUserGoal.getPrimaryCategoryId();
                mGetUserCategoryRC = HttpRequest.get(this, API.getUserCategoryUrl(categoryId));
            }
        }
        else if (mUserCategory != null){
            mAdapter = new ReviewActionsAdapter(this, this, mUserCategory.getTitle());
            mGetActionsNextUrl = API.getUserActionsUrl(mUserCategory.getCategory());
            setColor(Color.parseColor(mUserCategory.getColor()));
            setCategoryHeader(mUserCategory.getCategory());
            setFAB(R.id.review_fab, this);
        }
        else{
            finish();
        }

        setAdapter(mAdapter);
    }

    private void setGoalHeader(CategoryContent category){
        View header = inflateHeader(R.layout.header_tile);
        ImageView tile = (ImageView)header.findViewById(R.id.header_tile);

        int id = CompassUtil.getCategoryTileResId(category.getTitle());
        Bitmap image = BitmapFactory.decodeResource(getResources(), id);
        Bitmap circle = ImageHelper.getCircleBitmap(image, CompassUtil.getPixels(this, 200));
        tile.setImageBitmap(circle);
        image.recycle();
    }

    private void setCategoryHeader(CategoryContent category){
        View header = inflateHeader(R.layout.header_hero);
        ImageView hero = (ImageView)header.findViewById(R.id.header_hero_image);
        if (category.getImageUrl() == null){
            hero.setImageResource(R.drawable.compass_master_illustration);
        }
        else{
            ImageLoader.Options options = new ImageLoader.Options().setUsePlaceholder(false);
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
                if (mUserGoal == null){
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
                .putExtra(ActionActivity.ACTION_KEY, (Parcelable)action);
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
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){

    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.UserCategoryResultSet){
            mUserCategory = ((ParserModels.UserCategoryResultSet)result).results.get(0);
            setColor(Color.parseColor(mUserCategory.getColor()));
            setCategoryHeader(mUserCategory.getCategory());
        }
        else if (result instanceof ParserModels.UserActionResultSet){
            ParserModels.UserActionResultSet set = (ParserModels.UserActionResultSet)result;
            mGetActionsNextUrl = set.next;
            for (Action action:((ParserModels.UserActionResultSet)result).results){
                //TODO
                //mApplication.addAction(action);
            }
            mAdapter.addActions(set.results, mGetActionsNextUrl != null || set.results.isEmpty());
            if (set.results.isEmpty()){
                mAdapter.displayError("There are no activities");
            }
        }
    }
}
