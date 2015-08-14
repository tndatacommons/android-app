package org.tndata.android.compass.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ChooseCategoryAdapter;
import org.tndata.android.compass.fragment.ChooseCategoriesFragment;
import org.tndata.android.compass.fragment.CheckProgressFragment;
import org.tndata.android.compass.fragment.InstrumentFragment;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.model.UserData;
import org.tndata.android.compass.task.AddCategoryTask;
import org.tndata.android.compass.task.AddCategoryTask.AddCategoryTaskListener;
import org.tndata.android.compass.task.GetUserDataTask;
import org.tndata.android.compass.task.UpdateProfileTask;
import org.tndata.android.compass.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class OnBoardingActivity extends AppCompatActivity implements
        CheckProgressFragment.CheckProgressFragmentListener,
        AddCategoryTaskListener,
        InstrumentFragment.InstrumentFragmentListener,
        ChooseCategoryAdapter.OnCategoriesSelectedListener,
        GetUserDataTask.GetUserDataListener{
    private static final int CHOOSE_CATEGORIES = 0;
    private static final int QOL = 1;
    private static final int BIO = 2;
    private static final int CHECK_PROGRESS = 3;
    private boolean mCategoriesSaved = false;
    private Toolbar mToolbar;
    private ArrayList<Category> mCategories;
    private Fragment mFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().hide();
        swapFragments(BIO); // Start with Bio questions.
    }

    @Override
    public void progressCompleted() {
        // At the very end of the onboarding process, we display the CheckProgressFragment,
        // and tapping the progress icons should end the onboarding.
        instrumentFinished(-1);
    }

    private void swapFragments(int index) {
        switch (index) {
            case CHOOSE_CATEGORIES:
                Bundle args = new Bundle();
                args.putBoolean(ChooseCategoriesFragment.RESTRICTIONS_KEY, true);
                mFragment = new ChooseCategoriesFragment();
                mFragment.setArguments(args);
                break;
            case QOL:
                if (!mCategories.isEmpty()) {
                    mFragment = InstrumentFragment.newInstance(Constants.QOL_INSTRUMENT_ID);
                }
                break;
            case BIO:
                mFragment = InstrumentFragment.newInstance(Constants.BIO_INSTRUMENT_ID);
                break;
            case CHECK_PROGRESS:
                mFragment = new CheckProgressFragment();
                break;
        }
        if (mFragment != null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.base_content, mFragment).commit();
        }
    }

    @Override
    public void categoriesAdded(ArrayList<Category> categories) {
        CompassApplication application = (CompassApplication)getApplication();
        mCategoriesSaved = true;

        // Load all user-selected content from the API
        new GetUserDataTask(this).executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR, application.getToken());
    }

    @Override
    public void instrumentFinished(int instrumentId){
        if (instrumentId == Constants.BIO_INSTRUMENT_ID){
            swapFragments(CHOOSE_CATEGORIES);
        }
        else{
            User user = ((CompassApplication)getApplication()).getUser();
            user.onBoardingComplete(true);
            new UpdateProfileTask(null).execute(user);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onCategoriesSelected(List<Category> selection){
        for (Category cat : selection) {
            Log.d("Category", cat.getTitle());
        }
        mCategories = new ArrayList<>();
        mCategories.addAll(selection);
        ArrayList<String> cats = new ArrayList<String>();
        for (Category cat : mCategories) {
            cats.add(String.valueOf(cat.getId()));
        }
        new AddCategoryTask(this, this, cats)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void userDataLoaded(UserData userData){
        if (userData != null){
            ((CompassApplication)getApplication()).setUserData(userData);
        }
        swapFragments(CHECK_PROGRESS);
    }
}
