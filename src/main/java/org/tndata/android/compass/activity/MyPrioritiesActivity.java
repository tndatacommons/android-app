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
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.model.UserBehavior;
import org.tndata.android.compass.model.UserCategory;
import org.tndata.android.compass.model.UserGoal;

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

    private void swapFragments(int index, boolean addToStack, UserCategory userCategory){
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
                args.putSerializable("category", userCategory);
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
                mToolbar.setTitle(userCategory.getTitle());
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
    public void onCategorySelected(UserCategory userCategory){
        swapFragments(GOALS, true, userCategory);
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
    public void onAddGoalsClick(UserCategory userCategory){
        Intent categoryIntent = new Intent(this, ChooseGoalsActivity.class);
        categoryIntent.putExtra(ChooseGoalsActivity.CATEGORY_KEY, userCategory);
        startActivityForResult(categoryIntent, 1);
    }

    @Override
    public void onAddBehaviorsClick(UserCategory userCategory, UserGoal userGoal){
        Intent goalIntent = new Intent(this, ChooseBehaviorsActivity.class);
        goalIntent.putExtra(ChooseBehaviorsActivity.CATEGORY_KEY, userCategory);
        goalIntent.putExtra(ChooseBehaviorsActivity.GOAL_KEY, userGoal);
        startActivityForResult(goalIntent, 1);
    }

    @Override
    public void onBehaviorClick(UserCategory userCategory, UserGoal userGoal, UserBehavior userBehavior){
        Intent behaviorIntent = new Intent(this, ChooseActionsActivity.class);
        behaviorIntent.putExtra(ChooseActionsActivity.CATEGORY_KEY, userCategory);
        behaviorIntent.putExtra(ChooseActionsActivity.GOAL_KEY, userGoal);
        behaviorIntent.putExtra(ChooseActionsActivity.BEHAVIOR_KEY, userBehavior);
        startActivityForResult(behaviorIntent, 1);
    }

    @Override
    public void onActionClick(UserCategory userCategory, UserGoal userGoal, UserBehavior userBehavior, UserAction userAction){
        Intent actionIntent = new Intent(this, TriggerActivity.class)
                .putExtra(TriggerActivity.USER_ACTION_KEY, userAction)
                .putExtra(TriggerActivity.GOAL_KEY, userGoal);
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
