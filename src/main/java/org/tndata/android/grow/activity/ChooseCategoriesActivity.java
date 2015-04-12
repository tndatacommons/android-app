package org.tndata.android.grow.activity;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;

import org.tndata.android.grow.GrowApplication;
import org.tndata.android.grow.R;
import org.tndata.android.grow.fragment.ChooseCategoriesFragment;
import org.tndata.android.grow.model.Category;
import org.tndata.android.grow.task.AddCategoryTask;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ChooseCategoriesActivity extends ActionBarActivity implements
        ChooseCategoriesFragment.ChooseCategoriesFragmentListener, AddCategoryTask.AddCategoryTaskListener {
    private ArrayList<Category> mCategories;
    private ChooseCategoriesFragment mFragment;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
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
            //TODO show an error?
        } else {
            categoriesSelected(mCategories);
        }
    }

    @Override
    public void categoriesSelected(ArrayList<Category> categories) {
        Log.e("CATS", "CATS SELECTED");
        //Lets just save the new ones...
        ArrayList<Category> categoriesToDelete = new ArrayList<Category>();
        ArrayList<Category> categoriesToAdd = new ArrayList<Category>();
        for (Category cat : ((GrowApplication) getApplication()).getCategories()) {
            Log.d("SHOULD DELETE?", cat.getTitle());
            if (!categories.contains(cat)) {
                Log.d("Delete Category", cat.getTitle());
                categoriesToDelete.add(cat);
            }
        }
        for (Category cat : categories) {
            Log.d("SHOULD ADD?", cat.getTitle());
            if (!((GrowApplication) getApplication()).getCategories().contains(cat)) {
                Log.d("Add Category", cat.getTitle());
                categoriesToAdd.add(cat);
            }
        }

        //TODO delete categories, and remove from application

        ArrayList<String> cats = new ArrayList<String>();
        for (Category cat : categoriesToAdd) {
            cats.add(String.valueOf(cat.getId()));
        }
        if (cats.size() > 0) {
            new AddCategoryTask(this, this, cats)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            finish();
        }
    }

    @Override
    public void categoriesAdded(ArrayList<Category> categories) {
        if (categories != null) {
            mCategories = ((GrowApplication) getApplication()).getCategories();
            mCategories.addAll(categories);
            ((GrowApplication) getApplication()).setCategories(mCategories);
        }

        finish();
    }
}
