package org.tndata.android.compass.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.MyGoalAdapter;
import org.tndata.android.compass.model.CustomAction;
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
                Parser.ParserCallback,
                MyGoalAdapter.Listener{

    private static final String USER_GOAL_ID_KEY = "org.tndata.compass.MyGoal.UserGoalId";


    public static Intent getIntent(Context context, long userGoalId){
        return new Intent(context, MyGoalActivity.class)
                .putExtra(USER_GOAL_ID_KEY, userGoalId);
    }


    private CompassApplication mApp;
    private MyGoalAdapter mAdapter;

    private UserGoal mUserGoal;

    private int mGetUserGoalRC;
    private int mGetCustomActionsRC;
    private int mPostCustomActionRC;


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
        else if (requestCode == mGetCustomActionsRC){
            Parser.parse(result, ParserModels.CustomActionResultSet.class, this);
        }
        else if (requestCode == mPostCustomActionRC){
            Parser.parse(result, CustomAction.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        if (requestCode == mGetUserGoalRC){
            displayMessage("Couldn't load goal data...");
        }
        else if (requestCode == mGetCustomActionsRC){
            mAdapter.fetchCustomActionsFailed();
        }
        else if (requestCode == mPostCustomActionRC){
            mAdapter.addCustomActionFailed();
        }
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){

    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof UserGoal){
            setGoal((UserGoal)result);
            mGetCustomActionsRC = HttpRequest.get(this, API.URL.getCustomActions(mUserGoal));
        }
        else if (result instanceof ParserModels.CustomActionResultSet){
            mAdapter.setCustomActions(((ParserModels.CustomActionResultSet)result).results);
        }
        else if (result instanceof CustomAction){
            mAdapter.customActionAdded((CustomAction)result);
        }
    }

    @Override
    public void onParseFailed(int requestCode){
        if (mAdapter == null){
            displayMessage("Couldn't load goal data...");
        }
        else if (mAdapter.areCustomActionsSet()){
            mAdapter.addCustomActionFailed();
        }
        else{
            mAdapter.fetchCustomActionsFailed();
        }
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

        mAdapter = new MyGoalAdapter(this, this, mUserGoal, category);
        setAdapter(mAdapter);
    }

    @Override
    public void retryCustomActionLoad(){
        mGetCustomActionsRC = HttpRequest.get(this, API.URL.getCustomActions(mUserGoal));
    }

    @Override
    public void addCustomAction(String title){
        mPostCustomActionRC = HttpRequest.post(
                this, API.URL.postCustomAction(), API.BODY.postPutCustomAction(title, mUserGoal)
        );
    }

    @Override
    public void saveCustomAction(CustomAction action){

    }

    @Override
    public void deleteCustomAction(CustomAction action){

    }

    @Override
    public void editTrigger(CustomAction action){

    }
}
