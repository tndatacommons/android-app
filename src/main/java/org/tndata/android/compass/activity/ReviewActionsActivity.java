package org.tndata.android.compass.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ReviewActionsAdapter;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.model.UserBehavior;
import org.tndata.android.compass.model.UserCategory;
import org.tndata.android.compass.model.UserGoal;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;

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


    private UserCategory mUserCategory;
    private UserGoal mUserGoal;
    private UserBehavior mUserBehavior;

    private Action mSelectedAction;
    private ReviewActionsAdapter mAdapter;

    //Network request codes and urls
    private int mGetActionsRC;
    private String mGetActionsNextUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mUserGoal = (UserGoal)getIntent().getSerializableExtra(USER_GOAL_KEY);
        mUserBehavior = (UserBehavior)getIntent().getSerializableExtra(USER_BEHAVIOR_KEY);
        if (mUserBehavior == null){
            mAdapter = new ReviewActionsAdapter(this, this, mUserGoal);
            mGetActionsNextUrl = API.getUserActionsUrl(mUserGoal.getGoal());
        }
        else{
            mAdapter = new ReviewActionsAdapter(this, this, mUserBehavior);
            mGetActionsNextUrl = API.getUserActionsUrl(mUserBehavior.getBehavior());
        }

        setHeader();
        setAdapter(mAdapter);
        setColor(Color.parseColor(mUserGoal.getPrimaryCategory().getColor()));

        mSelectedAction = null;
    }

    @SuppressWarnings("deprecation")
    private void setHeader(){
        View header = inflateHeader(R.layout.header_icon);
        RelativeLayout circle = (RelativeLayout)header.findViewById(R.id.header_icon_circle);
        ImageView icon = (ImageView)header.findViewById(R.id.header_icon_icon);

        GradientDrawable gradientDrawable = (GradientDrawable) circle.getBackground();
        gradientDrawable.setColor(Color.parseColor(mUserGoal.getPrimaryCategory().getColor()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            circle.setBackground(gradientDrawable);
        }
        else{
            circle.setBackgroundDrawable(gradientDrawable);
        }

        mUserBehavior.getBehavior().loadIconIntoView(icon);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

    }

    @Override
    public void onActionSelected(Action action){
        Intent showAction = new Intent(this, ActionActivity.class)
                .putExtra(ActionActivity.ACTION_KEY, action);
        startActivityForResult(showAction, ACTION_ACTIVITY_RC);
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
            mAdapter.addActions(set.results, mGetActionsNextUrl != null);
        }
    }
}
