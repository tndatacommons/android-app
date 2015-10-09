package org.tndata.android.compass.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ChooseCategoryAdapter;
import org.tndata.android.compass.fragment.ChooseCategoriesFragment;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.UserData;
import org.tndata.android.compass.task.AddCategoryTask;
import org.tndata.android.compass.task.DeleteCategoryTask;
import org.tndata.android.compass.task.GetUserDataTask;

import java.util.ArrayList;
import java.util.List;

public class ChooseCategoriesActivity
        extends AppCompatActivity
        implements
                AddCategoryTask.AddCategoryTaskListener,
                ChooseCategoryAdapter.OnCategoriesSelectedListener,
                GetUserDataTask.GetUserDataCallback{

    private CompassApplication application;

    private List<Category> mSelection;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        application = (CompassApplication) getApplication();

        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        toolbar.getBackground().setAlpha(255);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Bundle args = new Bundle();
        args.putBoolean(ChooseCategoriesFragment.RESTRICTIONS_KEY, false);

        ChooseCategoriesFragment fragment = new ChooseCategoriesFragment();
        fragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.base_content, fragment).commit();
    }

    @Override
    public void onCategoriesSelected(List<Category> selection){
        mSelection = selection;

        List<Category> categoriesToAdd = new ArrayList<>();
        for (Category cat:selection){
            if (!application.getCategories().contains(cat)){
                categoriesToAdd.add(cat);
            }
        }

        ArrayList<String> cats = new ArrayList<>();
        for (Category cat:categoriesToAdd){
            cats.add(String.valueOf(cat.getId()));
        }
        if (cats.size() > 0){
            new AddCategoryTask(this, this, cats)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else{
            deleteCategories();
        }
    }

    @Override
    public void categoriesAdded(ArrayList<Category> categories){
        if (categories != null){
            for (Category category:categories){
                application.addCategory(category);
            }
            deleteCategories();
        }
    }

    private void deleteCategories(){
        ArrayList<String> deleteCats = new ArrayList<>();
        final List<Category> toDelete = new ArrayList<>();
        for (Category cat:application.getCategories()){
            if (!mSelection.contains(cat)){
                deleteCats.add(String.valueOf(cat.getMappingId()));
                toDelete.add(cat);
            }
        }

        if (toDelete.size() > 0){
            new DeleteCategoryTask(this, new DeleteCategoryTask.DeleteCategoryTaskListener(){
                @Override
                public void categoriesDeleted(){
                    for (Category category:toDelete){
                        application.getUserData().removeCategory(category);
                    }
                    getUserData();
                }
            }, deleteCats).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else{
            getUserData();
        }
    }

    private void getUserData(){
        new GetUserDataTask(this, this).execute(application.getToken());
    }

    @Override
    public void userDataLoaded(UserData userData){
        application.setUserData(userData);
        setResult(RESULT_OK);
        finish();
    }
}
