package org.tndata.android.compass.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.tndata.android.compass.R;
import org.tndata.android.compass.fragment.MyPrioritiesCategoriesFragment;
import org.tndata.android.compass.fragment.MyPrioritiesGoalsFragment;
import org.tndata.android.compass.model.Category;

import java.util.ArrayList;


/**
 * Activity that displays the elements chosen by the user hierarchically.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class MyPrioritiesActivity
        extends ActionBarActivity
        implements
                MyPrioritiesCategoriesFragment.OnCategorySelectedListener{

    private static final int CATEGORIES = 0;
    private static final int GOALS = 1;

    private Toolbar mToolbar;
    private MyPrioritiesCategoriesFragment mCategoriesFragment;

    private ArrayList<Fragment> mFragmentStack = new ArrayList<>();

    public boolean firstTransition;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_priorities);

        mToolbar = (Toolbar)findViewById(R.id.tool_bar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firstTransition = true;

        swapFragments(CATEGORIES, true, null);
    }

    private void swapFragments(int index, boolean addToStack, Category category){
        Fragment fragment = null;
        switch (index){
            case CATEGORIES:
                if (mCategoriesFragment == null){
                    mCategoriesFragment = new MyPrioritiesCategoriesFragment();
                }
                fragment = mCategoriesFragment;
                break;
            case GOALS:
                fragment = new MyPrioritiesGoalsFragment();
                Bundle args = new Bundle();
                args.putSerializable("category", category);
                fragment.setArguments(args);
                break;
            default:
                break;
        }

        if (fragment != null){
            if (addToStack){
                mFragmentStack.add(fragment);
            }
            if (index == CATEGORIES){
                mToolbar.setTitle("My Priorities");
                if (firstTransition){
                    getFragmentManager().beginTransaction()
                            .setCustomAnimations(0, R.animator.fade_out_downwards)
                            .replace(R.id.my_priorities_fragment_host, fragment)
                            .commit();
                    firstTransition = false;
                }
                else{
                    getFragmentManager().beginTransaction()
                            .setCustomAnimations(R.animator.fade_in, R.animator.fade_out_downwards)
                            .replace(R.id.my_priorities_fragment_host, fragment)
                            .commit();
                }
            }
            else{
                mToolbar.setTitle(category.getTitle());
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.fade_in_upwards, R.animator.fade_out)
                        .replace(R.id.my_priorities_fragment_host, fragment)
                        .commit();
            }
        }
    }

    private void handleBackStack(){
        if (!mFragmentStack.isEmpty()){
            mFragmentStack.remove(mFragmentStack.size()-1);
        }

        if (mFragmentStack.isEmpty()) {
            finish();
        }
        else{
            Fragment fragment = mFragmentStack.get(mFragmentStack.size()-1);

            int index = GOALS;
            if (fragment instanceof MyPrioritiesCategoriesFragment){
                index = CATEGORIES;
            }

            swapFragments(index, false, null);
        }
    }

    @Override
    public void onBackPressed(){
        handleBackStack();
    }

    @Override
    public void onCategorySelected(Category category){
        swapFragments(GOALS, true, category);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                handleBackStack();
        }
        return true;
    }
}
