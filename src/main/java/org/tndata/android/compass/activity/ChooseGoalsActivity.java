package org.tndata.android.compass.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ChooseGoalsAdapter;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.model.GoalContent;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.ImageLoader;
import org.tndata.android.compass.util.NetworkRequest;

import java.util.List;


/**
 * The ChooseGoalsActivity is where a user selects Goals within a selected Category.
 *
 * @author Edited by Ismael Alonso
 * @version 2.0.0
 */
public class ChooseGoalsActivity
        extends MaterialActivity
        implements
                NetworkRequest.RequestCallback,
                Parser.ParserCallback,
                ChooseGoalsAdapter.ChooseGoalsListener{

    //NOTE: This needs to be regular content because a user may dive down the library
    //  without selecting things. User content ain't available in that use case, but
    //  if it exists it can be retrieved from the UserData bundle
    public static final String CATEGORY_KEY = "org.tndata.compass.ChooseGoalsActivity.Category";


    public CompassApplication mApplication;

    private CategoryContent mCategory;
    private ChooseGoalsAdapter mAdapter;

    //Request codes and urls
    private int mGetGoalsRequestCode;
    private String mGetGoalsNextUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mApplication = (CompassApplication)getApplication();

        //Pull the content
        mCategory = (CategoryContent)getIntent().getSerializableExtra(CATEGORY_KEY);

        //Set up the loading process and the adapter
        mGetGoalsNextUrl = API.getGoalsUrl(mCategory);
        mAdapter = new ChooseGoalsAdapter(this, this, mCategory);

        setColor(Color.parseColor(mCategory.getColor()));
        setHeader();
        setAdapter(mAdapter);
    }

    private void setHeader(){
        View header = inflateHeader(R.layout.header_hero);
        ImageView hero = (ImageView)header.findViewById(R.id.header_hero_image);
        ImageLoader.Options options = new ImageLoader.Options().setUsePlaceholder(false);
        ImageLoader.loadBitmap(hero, mCategory.getImageUrl(), options);
    }

    @Override
    public void onGoalSelected(@NonNull GoalContent goal){
        startActivity(new Intent(this, ChooseBehaviorsActivity.class)
                .putExtra(ChooseBehaviorsActivity.GOAL_KEY, goal)
                .putExtra(ChooseBehaviorsActivity.CATEGORY_KEY, mCategory));
    }

    @Override
    public void loadMore(){
        if (API.STAGING && mGetGoalsNextUrl.startsWith("https")){
            mGetGoalsNextUrl = mGetGoalsNextUrl.replaceFirst("s", "");
        }
        mGetGoalsRequestCode = NetworkRequest.get(this, this, mGetGoalsNextUrl,
                mApplication.getToken());
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetGoalsRequestCode){
            Parser.parse(result, ParserModels.GoalContentResultSet.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, String message){
        mAdapter.displayError("Couldn't load goals");
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        //Unused
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.GoalContentResultSet){
            ParserModels.GoalContentResultSet set = (ParserModels.GoalContentResultSet)result;
            mGetGoalsNextUrl = set.next;
            List<GoalContent> goals = set.results;
            if (goals != null && !goals.isEmpty()){
                mAdapter.add(goals, mGetGoalsNextUrl != null);
            }
        }
    }
}
