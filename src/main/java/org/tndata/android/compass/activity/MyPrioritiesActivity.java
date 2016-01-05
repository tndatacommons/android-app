package org.tndata.android.compass.activity;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.MyPrioritiesGoalAdapter;
import org.tndata.android.compass.fragment.MyPrioritiesCategoriesFragment;
import org.tndata.android.compass.fragment.MyPrioritiesGoalsFragment;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;

import java.util.ArrayList;


/**
 * Activity that displays the elements chosen by the user hierarchically.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class MyPrioritiesActivity
        extends AppCompatActivity
        implements
                MyPrioritiesCategoriesFragment.OnCategorySelectedListener,
                MyPrioritiesGoalAdapter.OnItemClickListener{

    private static final int CATEGORIES = 0;
    private static final int GOALS = 1;

    private Toolbar mToolbar;
    private MyPrioritiesCategoriesFragment mCategoriesFragment;
    private MyPrioritiesGoalsFragment mGoalsFragment;

    private ArrayList<Fragment> mFragmentStack = new ArrayList<>();

    public boolean firstTransition;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_priorities);

        mToolbar = (Toolbar)findViewById(R.id.tool_bar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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
                mGoalsFragment = new MyPrioritiesGoalsFragment();
                fragment = mGoalsFragment;
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
                mToolbar.setTitle(R.string.my_priorities_title);
                if (firstTransition){
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(0, R.anim.fade_out_downwards)
                            .replace(R.id.my_priorities_fragment_host, fragment)
                            .commit();
                    firstTransition = false;
                }
                else{
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out_downwards)
                            .replace(R.id.my_priorities_fragment_host, fragment)
                            .commit();
                }
            }
            else{
                mToolbar.setTitle(category.getTitle());
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fade_in_upwards, R.anim.fade_out)
                        .replace(R.id.my_priorities_fragment_host, fragment)
                        .commit();
            }
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        mToolbar.getBackground().setAlpha(255);
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

    @Override
    public void onAddGoalsClick(Category category){
        Intent categoryIntent = new Intent(this, ChooseGoalsActivity.class);
        categoryIntent.putExtra("category", category);
        startActivityForResult(categoryIntent, 1);
    }

    @Override
    public void onAddBehaviorsClick(Category category, Goal goal){
        Intent goalIntent = new Intent(this, ChooseBehaviorsActivity.class);
        goalIntent.putExtra("category", category);
        goalIntent.putExtra("goal", goal);
        startActivityForResult(goalIntent, 1);
    }

    @Override
    public void onBehaviorClick(Category category, Goal goal, Behavior behavior){
        Intent behaviorIntent = new Intent(this, ChooseActionsActivity.class);
        behaviorIntent.putExtra("category", category);
        behaviorIntent.putExtra("goal", goal);
        behaviorIntent.putExtra("behavior", behavior);
        startActivityForResult(behaviorIntent, 1);
    }

    @Override
    public void onActionClick(Category category, Goal goal, Behavior behavior, Action action){
        Intent actionIntent = new Intent(this, TriggerActivity.class)
                .putExtra(TriggerActivity.USER_ACTION_KEY, action)
                .putExtra(TriggerActivity.GOAL_KEY, goal);
        startActivityForResult(actionIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (mGoalsFragment != null){
            mGoalsFragment.updateAdapterData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
