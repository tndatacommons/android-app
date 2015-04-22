package org.tndata.android.grow.activity;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import org.tndata.android.grow.GrowApplication;
import org.tndata.android.grow.R;
import org.tndata.android.grow.fragment.ChooseCategoriesFragment;
import org.tndata.android.grow.model.Category;
import org.tndata.android.grow.task.AddCategoryTask;
import org.tndata.android.grow.task.DeleteCategoryTask;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ChooseCategoriesActivity extends ActionBarActivity implements
        ChooseCategoriesFragment.ChooseCategoriesFragmentListener,
        AddCategoryTask.AddCategoryTaskListener, DeleteCategoryTask.DeleteCategoryTaskListener {
    private ArrayList<Category> mCategories;
    private ChooseCategoriesFragment mFragment;
    private Toolbar mToolbar;
    private boolean mAdding = false;
    private boolean mDeleting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.getBackground().setAlpha(255);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mFragment = new ChooseCategoriesFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.base_content, mFragment).commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) { // Back key pressed
            saveCategories();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                saveCategories();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveCategories() {
        mCategories = mFragment.getCurrentlySelectedCategories();
        if (mCategories.size() < ChooseCategoriesFragment.MIN_CATEGORIES_REQUIRED) {
            Toast.makeText(getApplicationContext(), R.string.choose_categories_need_more,
                    Toast.LENGTH_LONG).show();
        } else {
            categoriesSelected(mCategories);
        }
    }

    @Override
    public void categoriesSelected(ArrayList<Category> categories) {
        //Lets just save the new ones...
        ArrayList<String> deleteCats = new ArrayList<String>();
        ArrayList<Category> categoriesToAdd = new ArrayList<Category>();
        ArrayList<Category> categoriesWithDelete = new ArrayList<Category>();
        categoriesWithDelete.addAll(((GrowApplication) getApplication()).getCategories());
        for (Category cat : ((GrowApplication) getApplication()).getCategories()) {
            if (!categories.contains(cat)) {
                deleteCats.add(String.valueOf(cat.getMappingId()));
                categoriesWithDelete.remove(cat);
            }
        }

        for (Category cat : categories) {
            if (!((GrowApplication) getApplication()).getCategories().contains(cat)) {
                categoriesToAdd.add(cat);
            }
        }

        ((GrowApplication) getApplication()).setCategories(categoriesWithDelete);

        if (deleteCats.size() > 0) {
            mDeleting = true;
            new DeleteCategoryTask(this, this, deleteCats).executeOnExecutor(AsyncTask
                    .THREAD_POOL_EXECUTOR);
        }

        ArrayList<String> cats = new ArrayList<String>();
        for (Category cat : categoriesToAdd) {
            cats.add(String.valueOf(cat.getId()));
        }
        if (cats.size() > 0) {
            mAdding = true;
            new AddCategoryTask(this, this, cats)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (deleteCats.size() < 1) {
            finish();
        }
    }

    @Override
    public void categoriesAdded(ArrayList<Category> categories) {
        mAdding = false;
        if (categories != null) {
            mCategories = ((GrowApplication) getApplication()).getCategories();
            mCategories.addAll(categories);
            ((GrowApplication) getApplication()).setCategories(mCategories);
        }

        if (!mDeleting) {
            finish();
        }
    }

    @Override
    public void categoriesDeleted() {
        mDeleting = false;

        if (!mAdding) {
            finish();
        }
    }
}
