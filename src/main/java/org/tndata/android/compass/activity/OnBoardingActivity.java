package org.tndata.android.compass.activity;

import android.support.annotation.Nullable;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.OrganizationsAdapter;
import org.tndata.android.compass.fragment.InstrumentFragment;
import org.tndata.android.compass.fragment.OnBoardingCategoryFragment;
import org.tndata.android.compass.fragment.OrganizationsFragment;
import org.tndata.android.compass.fragment.ProgressFragment;
import org.tndata.android.compass.model.Organization;
import org.tndata.android.compass.model.TDCCategory;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.model.Instrument;
import org.tndata.android.compass.model.Survey;
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
                OrganizationsAdapter.OrganizationsListener,
                HttpRequest.RequestCallback,
                Parser.ParserCallback,
                OnBoardingCategoryFragment.CategoryListener,
                FeedDataLoader.Callback{

    private static final int PROFILE_ITEMS = 3;

    private static final int LIBRARY_RC = 6835;


    private CompassApplication mApplication;
    private OnBoardingCategoryFragment mCategoryFragment;

    private int mPostOrganizationRC;
    private int mGetCategoriesRC;


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

        OrganizationsFragment organizationsFragment = OrganizationsFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.base_content, organizationsFragment)
                .commit();
    }

    @Override
    public void onOrganizationSelected(@Nullable Organization organization){
        if (organization == null){
            mCategoryFragment = new OnBoardingCategoryFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.base_content, mCategoryFragment)
                    .commit();
        }
        else{
            mPostOrganizationRC = HttpRequest.post(this, API.URL.postOrganization(),
                    API.BODY.postOrganization(organization));
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.base_content, new ProgressFragment())
                    .commit();
        }
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mPostOrganizationRC){
            mGetCategoriesRC = HttpRequest.get(this, API.URL.getCategories());
        }
        else if (requestCode == mGetCategoriesRC){
            Parser.parse(result, ParserModels.CategoryContentResultSet.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){

    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.CategoryContentResultSet){
            List<TDCCategory> categories = ((ParserModels.CategoryContentResultSet)result).results;
            mApplication.setAvailableCategories(categories);
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.CategoryContentResultSet){
            mCategoryFragment = new OnBoardingCategoryFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.base_content, mCategoryFragment)
                    .commit();
        }
    }

    @Override
    public void onParseFailed(int requestCode){

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
        user.writeToSharedPreferences(this);
        HttpRequest.put(null, API.URL.putUserProfile(user), API.BODY.putUserProfile(user));
        FeedDataLoader.load(this);
    }

    @Override
    public void onFeedDataLoaded(@Nullable FeedData feedData){
        if (feedData != null){
            mApplication.setFeedData(feedData);
            startActivity(new Intent(getApplicationContext(), FeedActivity.class));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == LIBRARY_RC && resultCode == RESULT_OK){
            mCategoryFragment.notifyContentSelected();
        }
    }
}
