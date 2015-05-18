package org.tndata.android.compass.activity;

import java.util.ArrayList;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.fragment.ChooseCategoriesFragment;
import org.tndata.android.compass.fragment.InstrumentFragment;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.task.AddCategoryTask;
import org.tndata.android.compass.task.AddCategoryTask.AddCategoryTaskListener;
import org.tndata.android.compass.util.Constants;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class OnBoardingActivity extends ActionBarActivity implements
        ChooseCategoriesFragment.ChooseCategoriesFragmentListener, AddCategoryTaskListener,
        InstrumentFragment.InstrumentFragmentListener {
    private static final int CHOOSE_CATEGORIES = 0;
    private static final int QOL = 1;
    private static final int BIO = 2;
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
        swapFragments(CHOOSE_CATEGORIES);
    }

    @Override
    public void categoriesSelected(ArrayList<Category> categories) {
        for (Category cat : categories) {
            Log.d("Category", cat.getTitle());
        }
        mCategories = categories;
        ArrayList<String> cats = new ArrayList<String>();
        for (Category cat : mCategories) {
            cats.add(String.valueOf(cat.getId()));
        }
        swapFragments(QOL);
        new AddCategoryTask(this, this, cats)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void swapFragments(int index) {
        switch (index) {
            case CHOOSE_CATEGORIES:
                mFragment = new ChooseCategoriesFragment();
                break;
            case QOL:
                if (!mCategories.isEmpty()) {
                    mFragment = InstrumentFragment.newInstance(Constants.QOL_INSTRUMENT_ID);
                }
                break;
            case BIO:
                mFragment = InstrumentFragment.newInstance(Constants.BIO_INSTRUMENT_ID);
                break;
        }
        if (mFragment != null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.base_content, mFragment).commit();
        }
    }

    @Override
    public void categoriesAdded(ArrayList<Category> categories) {
        if (categories != null) {
            mCategories = categories;
            ((CompassApplication) getApplication()).setCategories(mCategories);
        }
        mCategoriesSaved = true;
    }

    @Override
    public void instrumentFinished(int instrumentId) {
        if (instrumentId == Constants.QOL_INSTRUMENT_ID) {
            swapFragments(BIO);
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
