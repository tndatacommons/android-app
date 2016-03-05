package org.tndata.android.compass.activity;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ReviewActionsAdapter;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.UserBehavior;
import org.tndata.android.compass.model.UserGoal;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * Created by isma on 2/24/16.
 */
public class ReviewActionsActivity
        extends MaterialActivity
        implements
                ReviewActionsAdapter.ReviewActionsListener,
                HttpRequest.RequestCallback,
                Parser.ParserCallback{

    public static final String USER_GOAL_KEY = "org.tndata.compass.ReviewActions.Goal";
    public static final String USER_BEHAVIOR_KEY = "org.tndata.compass.ReviewActions.Behavior";


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

        Log.d("ReviewActionsActivity", mUserGoal.getPrimaryCategory().getColor());
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
    public void onActionSelected(Action action){
        //TODO Open trigger editor
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
