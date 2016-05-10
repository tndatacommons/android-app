package org.tndata.android.compass.activity;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    private static final int STAGE_PROFILE = 0;
    private static final int STAGE_CHOOSE_CATEGORY = 1;
    private static final int STAGE_PROGRESS = 2;

    private static final int PROFILE_ITEMS = 3;

    private static final int LIBRARY_RC = 6835;


    private OnBoardingCategoryFragment mCategoryFragment;

    private CompassApplication mApplication;
    private Instrument mInstrument;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mApplication = (CompassApplication)getApplication();

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
        Fragment fragment = null;
        switch (index){
            case STAGE_PROFILE:
                fragment = InstrumentFragment.newInstance(mInstrument, 3);
                break;

            case STAGE_CHOOSE_CATEGORY:
                if (mCategoryFragment == null){
                    mCategoryFragment = new OnBoardingCategoryFragment();
                }
                fragment = mCategoryFragment;
                break;

            case STAGE_PROGRESS:
                fragment = new ProgressFragment();
                break;

        }

        if (fragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.base_content, fragment).commit();
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
        Intent library = new Intent(this, ChooseGoalsActivity.class)
                .putExtra(ChooseGoalsActivity.CATEGORY_KEY, category);
        startActivityForResult(library, LIBRARY_RC);
    }

    @Override
    public void onNext(){
        swapFragments(STAGE_PROGRESS);
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
