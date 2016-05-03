package org.tndata.android.compass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ChooseCategoryAdapter;
import org.tndata.android.compass.model.TDCCategory;


/**
 * Intermediate step to choose goals. Lets the user pick the category to choose goals from.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ChooseCategoryActivity
        extends AppCompatActivity
        implements ChooseCategoryAdapter.ChooseCategoryAdapterListener{

    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);

        mToolbar = (Toolbar)findViewById(R.id.choose_category_toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CompassApplication app = (CompassApplication)getApplication();

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.choose_category_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ChooseCategoryAdapter(this, this, app.getFilteredCategoryList()));
    }

    @Override
    protected void onResume(){
        super.onResume();
        mToolbar.getBackground().setAlpha(255);
    }

    @Override
    public void onCategorySelected(TDCCategory category){
        Intent chooseGoals = new Intent(this, ChooseGoalsActivity.class)
                .putExtra(ChooseGoalsActivity.CATEGORY_KEY, category);
        startActivity(chooseGoals);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                setResult(RESULT_OK);
        }
        return super.onOptionsItemSelected(item);
    }
}
