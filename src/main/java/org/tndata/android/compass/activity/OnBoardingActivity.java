package org.tndata.android.compass.activity;

import android.support.annotation.Nullable;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.fragment.InstrumentFragment;
import org.tndata.android.compass.fragment.OnBoardingCategoryFragment;
import org.tndata.android.compass.fragment.ProgressFragment;
import org.tndata.android.compass.model.TDCCategory;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.model.Instrument;
import org.tndata.android.compass.model.Survey;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.FeedDataLoader;

import es.sandwatch.httprequests.HttpRequest;


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
                FeedDataLoader.Callback{

    private static final int PROFILE_ITEMS = 3;

    private static final int LIBRARY_RC = 6835;


    private CompassApplication mApplication;
    private OnBoardingCategoryFragment mCategoryFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mApplication = (CompassApplication)getApplication();

        String title = getString(R.string.onboarding_instrument_title);
        String description = getString(R.string.onboarding_instrument_description);
        String instructions = getString(R.string.onboarding_instrument_instructions);
        Instrument instrument = new Instrument(title, description, instructions);
        for (int i = 0; i < PROFILE_ITEMS; i++){
            instrument.addSurvey(mApplication.getUser().generateSurvey(this, i));
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.base_content, InstrumentFragment.newInstance(instrument, 3))
                .commit();
    }

    @Override
    public void onBackPressed(){
        FeedDataLoader.cancel();
        super.onBackPressed();
    }

    @Override
    public void onInstrumentFinished(Instrument instrument){
        //There is no need to save the profile here, it gets saved after selecting the categories
        for (Survey survey:instrument.getQuestions()){
            //This only changes the value the user object stores
            mApplication.getUser().postSurvey(survey);
        }

        mCategoryFragment = new OnBoardingCategoryFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.base_content, mCategoryFragment)
                .commit();
    }

    @Override
    public void onCategorySelected(TDCCategory category){
        Intent library = new Intent(this, ChooseGoalsActivity.class)
                .putExtra(ChooseGoalsActivity.CATEGORY_KEY, category);
        startActivityForResult(library, LIBRARY_RC);
    }

    @Override
    public void onNext(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.base_content, new ProgressFragment())
                .commit();
        User user = mApplication.getUser();
        user.setOnBoardingComplete();
        HttpRequest.put(null, API.getPutUserProfileUrl(user), API.getPutUserProfileBody(user));
        FeedDataLoader.load(this);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == LIBRARY_RC && resultCode == RESULT_OK){
            mCategoryFragment.onContentSelected();
        }
    }
}
