package org.tndata.android.compass.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ChooseBehaviorsAdapter;
import org.tndata.android.compass.model.BehaviorContent;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.model.GoalContent;
import org.tndata.android.compass.model.UserBehavior;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.NetworkRequest;

import java.util.List;


/**
 * The ChooseBehaviorsActivity is where a user selects Behaviors for a chosen Goal.
 *
 * @author Edited by Ismael Alonso
 * @version 2.0.0
 */
public class ChooseBehaviorsActivity
        extends LibraryActivity
        implements
                NetworkRequest.RequestCallback,
                Parser.ParserCallback,
                ChooseBehaviorsAdapter.ChooseBehaviorsListener{

    //Bundle keys
    //NOTE: These need to be regular content because a user may dive down the library
    //  without selecting things. User content ain't available in that use case, but
    //  if it exists it can be retrieved from the UserData bundle
    public static final String CATEGORY_KEY = "org.tndata.compass.ChooseBehaviors.Category";
    public static final String GOAL_KEY = "org.tndata.compass.ChooseBehaviors.Goal";
    //This one is to load search results, which don't deliver the whole object
    public static final String GOAL_ID_KEY = "org.tndata.compass.ChooseBehaviors.GoalId";

    //Activity tag
    private static final String TAG = "ChooseBehaviorsActivity";

    //Activity request codes
    private static final int BEHAVIOR_ACTIVITY_RQ = 5469;


    public CompassApplication mApplication;

    private CategoryContent mCategory;
    private GoalContent mGoal;
    private ChooseBehaviorsAdapter mAdapter;

    //Network request codes
    private int mGetGoalRequestCode;
    private int mGetCategoryRequestCode;
    private int mGetBehaviorsRequestCode;
    private int mPostBehaviorRequestCode;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mApplication = (CompassApplication)getApplication();

        //Pull the goal
        mGoal = (GoalContent)getIntent().getSerializableExtra(GOAL_KEY);
        if (mGoal == null){
            fetchGoal(getIntent().getIntExtra(GOAL_ID_KEY, -1));
        }
        else{
            mCategory = (CategoryContent)getIntent().getSerializableExtra(CATEGORY_KEY);
            mAdapter = new ChooseBehaviorsAdapter(this, this, mGoal);

            setHeader();
            setAdapter(mAdapter);
            setFilter(mAdapter.getFilter());

            if (mCategory != null && !mCategory.getColor().isEmpty()){
                setColor(Color.parseColor(mCategory.getColor()));
            }

            fetchBehaviors();
        }
    }

    @SuppressWarnings("deprecation")
    private void setHeader(){
        FrameLayout header = (FrameLayout)inflateHeader(R.layout.header_choose_behaviors);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)header.getLayoutParams();
        params.height = CompassUtil.getScreenWidth(this)/3*2;
        header.setLayoutParams(params);
        RelativeLayout circle = (RelativeLayout)header.findViewById(R.id.choose_behaviors_circle);
        ImageView icon = (ImageView)header.findViewById(R.id.choose_behaviors_icon);

        GradientDrawable gradientDrawable = (GradientDrawable) circle.getBackground();
        if (mCategory != null && !mCategory.getSecondaryColor().isEmpty()){
            gradientDrawable.setColor(Color.parseColor(mCategory.getSecondaryColor()));
        }
        else{
            gradientDrawable.setColor(getResources().getColor(R.color.grow_accent));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            circle.setBackground(gradientDrawable);
        }
        else{
            circle.setBackgroundDrawable(gradientDrawable);
        }
        mGoal.loadIconIntoView(icon);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (mAdapter != null){
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
        mGetBehaviorsRequestCode = NetworkRequest.get(this, this, API.getBehaviorsUrl(mGoal), "");
    }

    @Override
    public void onBehaviorSelected(BehaviorContent behavior){
        Log.d(TAG, "Behavior selected: " + behavior);
        startActivityForResult(new Intent(this, BehaviorActivity.class)
                .putExtra(BehaviorActivity.CATEGORY_KEY, mCategory)
                .putExtra(BehaviorActivity.GOAL_KEY, mGoal)
                .putExtra(BehaviorActivity.BEHAVIOR_KEY, behavior), BEHAVIOR_ACTIVITY_RQ);
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
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetGoalRequestCode){
            Parser.parse(result, GoalContent.class, this);
        }
        else if (requestCode == mGetCategoryRequestCode){
            Parser.parse(result, CategoryContent.class, this);
        }
        else if (requestCode == mGetBehaviorsRequestCode){
            Parser.parse(result, ParserModels.BehaviorContentResultSet.class, this);
        }
        else if (requestCode == mPostBehaviorRequestCode){
            Parser.parse(result, UserBehavior.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, String message){
        //TODO user feedback
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof GoalContent){
            mGoal = (GoalContent)result;
            Log.d(TAG, "Goal fetched: " + mGoal);
        }
        else if (result instanceof CategoryContent){
            mCategory = (CategoryContent)result;
            Log.d(TAG, "Category fetched: " + mCategory);
        }
        else if (result instanceof ParserModels.BehaviorContentResultSet){
            List<BehaviorContent> behaviorList = ((ParserModels.BehaviorContentResultSet)result).results;
            if (behaviorList != null && !behaviorList.isEmpty()){
                mAdapter.setBehaviors(behaviorList);
            }
        }
        else if (result instanceof UserBehavior){
            UserBehavior userBehavior = (UserBehavior)result;
            Log.d(TAG, "(Post) " + userBehavior.toString());

            mApplication.getUserData().addCategory(userBehavior.getParentUserCategory());
            mApplication.getUserData().addGoal(userBehavior.getParentUserGoal());
            mApplication.getUserData().addBehavior(userBehavior);
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof GoalContent){
            mGetCategoryRequestCode = NetworkRequest.get(this, this,
                    API.getCategoryUrl(mGoal.getCategoryIdSet().iterator().next()), "");
        }
        if (result instanceof CategoryContent){
            mAdapter = new ChooseBehaviorsAdapter(this, this, mGoal);

            setHeader();
            setAdapter(mAdapter);
            setFilter(mAdapter.getFilter());

            if (mCategory != null && !mCategory.getColor().isEmpty()){
                setColor(Color.parseColor(mCategory.getColor()));
            }

            fetchBehaviors();
        }
        else if (result instanceof ParserModels.BehaviorContentResultSet){
            mAdapter.update();
        }
        else if (result instanceof UserBehavior){
            mApplication.addBehavior((UserBehavior)result);
            mAdapter.notifyDataSetChanged();
        }
    }
}
