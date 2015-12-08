package org.tndata.android.compass.activity;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ChooseCategoriesAdapter;
import org.tndata.android.compass.fragment.ChooseCategoriesFragment;
import org.tndata.android.compass.fragment.InstrumentFragment;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.model.UserData;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkRequest;
import org.tndata.android.compass.util.Parser;

import java.util.List;


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
                ChooseCategoriesAdapter.OnCategoriesSelectedListener,
                NetworkRequest.RequestCallback{

    private static final int STAGE_PROFILE = 0;
    private static final int STAGE_CHOOSE_CATEGORIES = 1;


    private CompassApplication mApplication;
    private Fragment mFragment = null;

    //Request codes
    private int mInitialPostCategoryRequestCode;
    private int mLastPostCategoryRequestCode;
    private int mGetDataRequestCode;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_toolbar);

        mApplication = (CompassApplication)getApplication();

        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().hide();
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
                mFragment = InstrumentFragment.newInstance(Constants.INITIAL_PROFILE_INSTRUMENT_ID, 3);
                break;

            case STAGE_CHOOSE_CATEGORIES:
                Bundle args = new Bundle();
                args.putBoolean(ChooseCategoriesFragment.ON_BOARDING_KEY, true);
                mFragment = new ChooseCategoriesFragment();
                mFragment.setArguments(args);
                break;
        }

        if (mFragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.base_content, mFragment).commit();
        }
    }

    @Override
    public void onInstrumentFinished(int instrumentId){
        //When the user is done with the survey, he is taken to the category picker
        if (instrumentId == Constants.INITIAL_PROFILE_INSTRUMENT_ID){
            swapFragments(STAGE_CHOOSE_CATEGORIES);
        }
    }

    @Override
    public void onCategoriesSelected(List<Category> selection){
        //Process and log the selection, and save it
        //int[] categoryIds = new int[selection.size()];
        for (int i = 0; i < selection.size(); i++){
            if (i == 0){
                mInitialPostCategoryRequestCode = NetworkRequest.post(this, this,
                        API.getUserCategoriesUrl(), mApplication.getToken(),
                        API.getPostUserCategoryBody(selection.get(i).getId()));
                mLastPostCategoryRequestCode = mInitialPostCategoryRequestCode+selection.size();
            }
            else{
                NetworkRequest.post(this, this, API.getUserCategoriesUrl(), mApplication.getToken(),
                        API.getPostUserCategoryBody(selection.get(i).getId()));
            }
        }
        //new AddCategoryTask(this, this, cats).execute();
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode < mLastPostCategoryRequestCode){
            mInitialPostCategoryRequestCode++;
            if (mInitialPostCategoryRequestCode == mLastPostCategoryRequestCode){
                mGetDataRequestCode = NetworkRequest.get(this, this, API.getUserDataUrl(),
                        mApplication.getToken(), 60 * 1000);
            }
        }
        else if (requestCode == mGetDataRequestCode){
            UserData userData = new Parser().parseUserData(this, result);
            if (userData != null){
                mApplication.setUserData(userData);
            }
            User user = mApplication.getUser();
            user.setOnBoardingComplete();
            NetworkRequest.put(this, null, API.getPutUserProfileUrl(user), mApplication.getToken(),
                    API.getPutUserProfileBody(user));
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onRequestFailed(int requestCode){
        if (requestCode < mLastPostCategoryRequestCode){
            mInitialPostCategoryRequestCode++;
            if (mInitialPostCategoryRequestCode == mLastPostCategoryRequestCode){
                mGetDataRequestCode = NetworkRequest.get(this, this, API.getUserDataUrl(),
                        mApplication.getToken(), 60 * 1000);
            }
        }
    }
}
