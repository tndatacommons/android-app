package org.tndata.android.compass.activity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ChooseGoalAdapter;
import org.tndata.android.compass.fragment.ChooseGoalFragment;
import org.tndata.android.compass.fragment.InstrumentFragment;
import org.tndata.android.compass.fragment.OnBoardingCategoryFragment;
import org.tndata.android.compass.fragment.ProgressFragment;
import org.tndata.android.compass.model.TDCCategory;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.model.Instrument;
import org.tndata.android.compass.model.Survey;
import org.tndata.android.compass.model.TDCGoal;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.FeedDataLoader;

import java.util.List;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * Activity to present the user the onboarding process, which includes a short survey and
 * a category picker.
 *
 * @author Edited by Ismael Alonso
 * @version 1.1.0
 */
public class OnBoardingActivity
        extends AppCompatActivity
        implements
                InstrumentFragment.InstrumentFragmentCallback,
                OnBoardingCategoryFragment.CategoryListener,
                ChooseGoalAdapter.ChooseGoalListener,
                HttpRequest.RequestCallback,
                Parser.ParserCallback,
                FeedDataLoader.Callback{

    private static final int STAGE_PROFILE = 0;
    private static final int STAGE_CHOOSE_CATEGORY = 1;
    private static final int STAGE_CHOOSE_GOAL = 2;
    private static final int STAGE_PROGRESS = 3;

    private static final int PROFILE_ITEMS = 3;


    private CompassApplication mApplication;
    private Fragment mFragment = null;
    private Instrument mInstrument;
    private TDCCategory mCategory;
    private List<TDCGoal> mGoals;

    private boolean firewall = false;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_toolbar);

        mApplication = (CompassApplication)getApplication();

        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        toolbar.setNavigationIcon(R.drawable.ic_back_white_24dp);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().hide();
        }

        String title = getString(R.string.onboarding_instrument_title);
        String description = getString(R.string.onboarding_instrument_description);
        String instructions = getString(R.string.onboarding_instrument_instructions);
        mInstrument = new Instrument(title, description, instructions);
        for (int i = 0; i < PROFILE_ITEMS; i++){
            mInstrument.addSurvey(mApplication.getUser().generateSurvey(this, i));
        }
        swapFragments(STAGE_PROFILE);
    }

    /**
     * Replaces the current fragment for a new one.
     *
     * @param index the fragment id.
     */
    private void swapFragments(int index){
        switch (index){
            case STAGE_PROFILE:
                mFragment = InstrumentFragment.newInstance(mInstrument, 3);
                break;

            case STAGE_CHOOSE_CATEGORY:
                mFragment = new OnBoardingCategoryFragment();
                break;

            case STAGE_CHOOSE_GOAL:
                mFragment = ChooseGoalFragment.newInstance(mCategory, mGoals);
                break;

            case STAGE_PROGRESS:
                mFragment = new ProgressFragment();
                break;

        }

        if (mFragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.base_content, mFragment).commit();
        }
    }

    @Override
    public void onInstrumentFinished(Instrument instrument){
        //There is no need to save the profile here, it gets saved after selecting the categories
        for (Survey survey:instrument.getQuestions()){
            mApplication.getUser().postSurvey(survey);
        }

        swapFragments(STAGE_CHOOSE_CATEGORY);
    }

    @Override
    public void onCategorySelected(TDCCategory category){
        mCategory = category;
        HttpRequest.get(this, API.getGoalsUrl(category) + "&page_size=999");
        swapFragments(STAGE_PROGRESS);
    }

    @Override
    public void onSkip(){
        swapFragments(STAGE_PROGRESS);
        User user = mApplication.getUser();
        user.setOnBoardingComplete();
        HttpRequest.put(null, API.getPutUserProfileUrl(user), API.getPutUserProfileBody(user));
        FeedDataLoader.load(this);
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        Parser.parse(result, ParserModels.GoalContentResultSet.class, this);
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){

    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){

    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        mGoals = ((ParserModels.GoalContentResultSet)result).results;
        swapFragments(STAGE_CHOOSE_GOAL);
    }

    @Override
    public void onFeedDataLoaded(@Nullable FeedData feedData){
        if (feedData != null){
            mApplication.setFeedData(feedData);
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    @Override
    public void onGoalSelected(@NonNull TDCGoal goal, TDCCategory category){
        if (!firewall){
            swapFragments(STAGE_PROGRESS);
            firewall = true;
            HttpRequest.post(null, API.getPostGoalUrl(goal), API.getPostCategoryBody(category));
            User user = mApplication.getUser();
            user.setOnBoardingComplete();
            HttpRequest.put(null, API.getPutUserProfileUrl(user), API.getPutUserProfileBody(user));
            FeedDataLoader.load(this);
        }
    }
}
