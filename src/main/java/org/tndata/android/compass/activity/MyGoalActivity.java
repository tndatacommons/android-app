package org.tndata.android.compass.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.MyGoalAdapter;
import org.tndata.android.compass.model.TDCCategory;
import org.tndata.android.compass.model.UserGoal;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.ImageLoader;
import org.tndata.android.compass.util.ItemSpacing;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * Created by isma on 9/14/16.
 */
public class MyGoalActivity
        extends MaterialActivity
        implements
                HttpRequest.RequestCallback,
                Parser.ParserCallback{

    private static final String USER_GOAL_ID_KEY = "org.tndata.compass.MyGoal.UserGoalId";


    public static Intent getIntent(Context context, long userGoalId){
        return new Intent(context, MyGoalActivity.class)
                .putExtra(USER_GOAL_ID_KEY, userGoalId);
    }


    private CompassApplication mApp;

    private UserGoal mUserGoal;

    private int mGetUserGoalRC;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mApp = (CompassApplication)getApplication();

        getRecyclerView().addItemDecoration(new ItemSpacing(this, 8));

        long userGoalId = getIntent().getLongExtra(USER_GOAL_ID_KEY, -1);
        mGetUserGoalRC = HttpRequest.get(this, API.URL.getUserGoal(userGoalId));
    }

    @Override
    protected void onHomeTapped(){
        finish();
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetUserGoalRC){
            Parser.parse(result, UserGoal.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        if (requestCode == mGetUserGoalRC){
            displayMessage("Couldn't load goal data...");
        }
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){

    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof UserGoal){
            setGoal((UserGoal)result);
        }
    }

    @Override
    public void onParseFailed(int requestCode){
        displayMessage("Couldn't load goal data...");
    }

    private void setGoal(UserGoal userGoal){
        mUserGoal = userGoal;

        TDCCategory category = mApp.getAvailableCategories().get(mUserGoal.getPrimaryCategoryId());
        setColor(category.getColorInt());
        View header = inflateHeader(R.layout.header_hero);
        ImageView image = (ImageView)header.findViewById(R.id.header_hero_image);
        if (category.getImageUrl() == null || category.getImageUrl().isEmpty()){
            image.setImageResource(R.drawable.compass_master_illustration);
        }
        else{
            ImageLoader.Options options = new ImageLoader.Options()
                    .setPlaceholder(R.drawable.compass_master_illustration);
            ImageLoader.loadBitmap(image, category.getImageUrl(), options);
        }

        setAdapter(new MyGoalAdapter(this, mUserGoal));
    }
}
