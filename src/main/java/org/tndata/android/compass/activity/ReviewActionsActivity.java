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
import org.tndata.android.compass.model.BehaviorContent;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.model.UserBehavior;
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
                ReviewActionsAdapter.ReviewActionsListener,
                HttpRequest.RequestCallback,
                Parser.ParserCallback{

    public static final String USER_CATEGORY_KEY = "org.tndata.compass.ReviewActions.Category";
    public static final String USER_GOAL_KEY = "org.tndata.compass.ReviewActions.Goal";
    public static final String USER_BEHAVIOR_KEY = "org.tndata.compass.ReviewActions.Behavior";

    public static final int ACTION_ACTIVITY_RC = 4562;


    private CompassApplication mApplication;
    private ReviewActionsAdapter mAdapter;

    //Network request codes and urls
    private int mGetActionsRC;
    private String mGetActionsNextUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mApplication = (CompassApplication)getApplication();

        //A UserCategory is all it's needed when a category's actions are to be displayed
        UserCategory userCategory = (UserCategory)getIntent().getSerializableExtra(USER_CATEGORY_KEY);
        //If either a UserGoal's actions or a UserBehavior's actions are to be displayed
        //  the a CategoryContent can be used to populate headers. In this case a UserGoal
        //  will always be available, so the primary category id can be used to retrieve
        //  it from the Application class list
        UserGoal userGoal = (UserGoal)getIntent().getSerializableExtra(USER_GOAL_KEY);
        UserBehavior userBehavior = (UserBehavior)getIntent().getSerializableExtra(USER_BEHAVIOR_KEY);

        if (userGoal != null){
            CategoryContent category = mApplication.getPublicCategories().get(userGoal.getPrimaryCategoryId());
            if (userBehavior != null){
                String title = getString(R.string.review_actions_header, userBehavior.getTitle());
                mAdapter = new ReviewActionsAdapter(this, this, title);
                mGetActionsNextUrl = API.getUserActionsUrl(userBehavior.getBehavior());
                setColor(Color.parseColor(category.getColor()));
                setBehaviorHeader(userBehavior.getBehavior());
            }
            else{
                String title = getString(R.string.review_actions_header, userGoal.getTitle());
                mAdapter = new ReviewActionsAdapter(this, this, title);
                mGetActionsNextUrl = API.getUserActionsUrl(userGoal.getGoal());
                setColor(Color.parseColor(category.getColor()));
                setGoalHeader(category);
            }
        }
        else if (userCategory != null){
            String title = getString(R.string.review_actions_header_cat, userCategory.getTitle());
            mAdapter = new ReviewActionsAdapter(this, this, title);
            mGetActionsNextUrl = API.getUserActionsUrl(userCategory.getCategory());
            setColor(Color.parseColor(userCategory.getColor()));
            setCategoryHeader(userCategory.getCategory());
        }
        else{
            finish();
        }

        setAdapter(mAdapter);
    }

    private void setBehaviorHeader(BehaviorContent behavior){
        View header = inflateHeader(R.layout.header_icon);
        behavior.loadIconIntoView((ImageView)header.findViewById(R.id.header_icon_icon));
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
        ImageLoader.Options options = new ImageLoader.Options().setUsePlaceholder(false);
        ImageLoader.loadBitmap(hero, category.getImageUrl(), options);
    }

    @Override
    public void onBackPressed(){
        setResult(RESULT_OK);
        finish();
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
        }
    }

    @Override
    public void loadMore(){
        mGetActionsRC = HttpRequest.get(this, mGetActionsNextUrl);
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetActionsRC){
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
        if (result instanceof ParserModels.UserActionResultSet){
            ParserModels.UserActionResultSet set = (ParserModels.UserActionResultSet)result;
            mGetActionsNextUrl = set.next;
            for (Action action:((ParserModels.UserActionResultSet)result).results){
                //TODO
                //mApplication.addAction(action);
            }
            mAdapter.addActions(set.results, mGetActionsNextUrl != null);
        }
    }
}
