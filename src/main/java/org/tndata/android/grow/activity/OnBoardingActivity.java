package org.tndata.android.grow.activity;

import java.util.ArrayList;

import org.tndata.android.grow.GrowApplication;
import org.tndata.android.grow.R;
import org.tndata.android.grow.fragment.OnBoardingCategoryFragment;
import org.tndata.android.grow.fragment.OnBoardingCategoryFragment.OnBoardingCategoryListener;
import org.tndata.android.grow.model.Category;
import org.tndata.android.grow.task.AddCategoryTask;
import org.tndata.android.grow.task.AddCategoryTask.AddCategoryTaskListener;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class OnBoardingActivity extends ActionBarActivity implements
        OnBoardingCategoryListener, AddCategoryTaskListener {
    private static final int CHOOSE_CATEGORIES = 0;
    private static final int CHOOSE_GOALS = 1;
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
        swapFragments(CHOOSE_GOALS);
        new AddCategoryTask(this, this, cats)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void swapFragments(int index) {
        switch (index) {
            case CHOOSE_CATEGORIES:
                mFragment = new OnBoardingCategoryFragment();
                break;
//        case CHOOSE_GOALS:
//            if (!mCategories.isEmpty()) {
//                mFragment = ChooseGoalsFragment.newInstance(mCategories
//                        .get(0));
//                if (mCategoriesSaved) {
//                    if (mFragment instanceof ChooseGoalsFragment) {
//                        ((ChooseGoalsFragment) mFragment).showDone();
//                    }
//                }
//            }
//            break;
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
            ((GrowApplication) getApplication()).setCategories(mCategories);
        }
        mCategoriesSaved = true;
//        if (mFragment instanceof ChooseGoalsFragment) {
//            ((ChooseGoalsFragment) mFragment).showDone();
//        }
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

}
