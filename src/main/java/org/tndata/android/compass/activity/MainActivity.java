package org.tndata.android.compass.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.DrawerAdapter;
import org.tndata.android.compass.adapter.MainFeedAdapter;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.UserData;
import org.tndata.android.compass.task.GetUserDataTask;
import org.tndata.android.compass.task.UpdateProfileTask;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.GcmRegistration;
import org.tndata.android.compass.util.OnScrollListenerHub;
import org.tndata.android.compass.util.ParallaxEffect;


/**
 * The application's main activity. Contains a feed, a drawer, and a floating
 * action button. In the future it will also include a search functionality.
 *
 * The feed displays up next cards, reward cards, and goal cards.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class MainActivity
        extends AppCompatActivity
        implements
                SwipeRefreshLayout.OnRefreshListener,
                GetUserDataTask.GetUserDataCallback,
                DrawerAdapter.OnItemClickListener,
                MainFeedAdapter.MainFeedAdapterListener{

    //Request codes
    private static final int CATEGORIES_REQUEST_CODE = 4821;
    private static final int GOAL_REQUEST_CODE = 3486;
    private static final int ACTION_REQUEST_CODE = 4582;
    private static final int TRIGGER_REQUEST_CODE = 7631;


    //A reference to the application class
    private CompassApplication mApplication;

    //Drawer components
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    //Feed components
    private SwipeRefreshLayout mRefresh;
    private RecyclerView mFeed;
    private MainFeedAdapter mAdapter;

    //Floating action menu components
    private View mStopper;
    private FloatingActionMenu mMenu;

    //The selected category from the FAB
    private Category mSelectedCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApplication = (CompassApplication)getApplication();

        //Update the timezone and register with GCM
        new UpdateProfileTask(null).execute(mApplication.getUser());
        new GcmRegistration(this);

        //If this is pre L a different color scheme is applied
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            int preLColor = getResources().getColor(R.color.feed_pre_l_background);
            findViewById(R.id.main_container).setBackgroundColor(preLColor);
        }

        //Set up the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Set up the drawer
        mDrawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.nav_drawer_action, R.string.nav_drawer_action){
            @Override
            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        RecyclerView drawerList = (RecyclerView)findViewById(R.id.main_drawer);
        drawerList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        drawerList.setAdapter(new DrawerAdapter(this, this));
        drawerList.addItemDecoration(DrawerAdapter.getItemPadding(this));

        //Refresh functionality
        mRefresh = (SwipeRefreshLayout)findViewById(R.id.main_refresh);
        mRefresh.setColorSchemeColors(0xFFFF0000, 0xFFFFE900, 0xFF572364);
        mRefresh.setOnRefreshListener(this);

        View header = findViewById(R.id.main_illustration);

        //Create the adapter and set the feed
        mAdapter = new MainFeedAdapter(this, this);

        mFeed = (RecyclerView)findViewById(R.id.main_feed);
        mFeed.setAdapter(mAdapter);
        mFeed.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mFeed.addItemDecoration(mAdapter.getMainFeedPadding());

        //Create the hub and add to it all the items that need to be parallaxed
        OnScrollListenerHub hub = new OnScrollListenerHub();
        ParallaxEffect parallax = new ParallaxEffect(header, 0.5f);
        parallax.setItemDecoration(((MainFeedAdapter)mFeed.getAdapter()).getMainFeedPadding());
        hub.addOnScrollListener(parallax);
        hub.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0){
                    mMenu.hideMenuButton(true);
                }
                else if (dy < 0){
                    mMenu.showMenuButton(true);
                }
                if (recyclerView.canScrollVertically(-1)){
                    mRefresh.setEnabled(false);
                }
                else{
                    mRefresh.setEnabled(true);
                }
            }
        });
        mFeed.setOnScrollListener(hub);

        mStopper = findViewById(R.id.main_stopper);
        mMenu = (FloatingActionMenu)findViewById(R.id.main_fab_menu);
        mMenu.setClosedOnTouchOutside(true);
        mMenu.setOnMenuButtonClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (!mMenu.isOpened()){
                    mSelectedCategory = null;
                    animateBackground(true);
                }
                mMenu.toggle(true);
            }
        });
        mMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener(){
            @Override
            public void onMenuToggle(boolean opened){
                if (!opened){
                    animateBackground(false);
                }
            }
        });

        //Set up the FAB menu
        populateMenu();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mAdapter.notifyDataSetChanged();
        mMenu.showMenuButton(false);
    }

    /**
     * Creates the FAB menu.
     */
    private void populateMenu(){
        //First of all, clear the menu
        mMenu.removeAllMenuButtons();

        //Populate the menu with a button per category
        for (final Category category:mApplication.getUserData().getCategories()){
            //Skip packaged categories
            if (category.isPackagedContent()){
                continue;
            }
            ContextThemeWrapper ctx = new ContextThemeWrapper(this, R.style.MenuButtonStyle);
            FloatingActionButton fab = new FloatingActionButton(ctx);
            fab.setLabelText(category.getTitle());
            fab.setColorNormal(Color.parseColor(category.getColor()));
            fab.setColorPressed(Color.parseColor(category.getColor()));
            fab.setScaleType(ImageView.ScaleType.FIT_CENTER);
            fab.setImageResource(getIconResourceId(category));
            mMenu.addMenuButton(fab);
            fab.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    addGoalsClicked(category);
                }
            });
        }

        //Add categories button
        ContextThemeWrapper ctx = new ContextThemeWrapper(this, R.style.MenuButtonStyle);
        FloatingActionButton fab = new FloatingActionButton(ctx);
        fab.setLabelText("Add categories");
        fab.setScaleType(ImageView.ScaleType.FIT_CENTER);
        fab.setImageResource(R.drawable.fab_add);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                addCategoriesClicked();
            }
        });
        mMenu.addMenuButton(fab);
    }

    /**
     * Gets a drawable resource id mapped to a category.
     *
     * @param category the category whose drawable resource id is to be retrieved
     * @return the drawable resource id of the category's drawable.
     */
    @DrawableRes
    private int getIconResourceId(Category category){
        if (category.getTitle().equalsIgnoreCase("Happiness & fun")){
            return R.drawable.ic_category1;
        }
        else if (category.getTitle().equalsIgnoreCase("Family & parenting")){
            return R.drawable.ic_category2;
        }
        else if (category.getTitle().equalsIgnoreCase("Work & prosperity")){
            return R.drawable.ic_category3;
        }
        else if (category.getTitle().equalsIgnoreCase("Home & safety")){
            return R.drawable.ic_category4;
        }
        else if (category.getTitle().equalsIgnoreCase("Education & skills")){
            return R.drawable.ic_category5;
        }
        else if (category.getTitle().equalsIgnoreCase("Health & wellness")){
            return R.drawable.ic_category6;
        }
        else if (category.getTitle().equalsIgnoreCase("Community & friendship")){
            return R.drawable.ic_category7;
        }
        else if (category.getTitle().equalsIgnoreCase("Romance & relationships")){
            return R.drawable.ic_category8;
        }
        else{
            return R.drawable.ic_add_white_24dp;
        }
    }

    /**
     * Called when a FAB is clicked.
     *
     * @param category the selected category.
     */
    private void addGoalsClicked(Category category){
        mSelectedCategory = category;
        mMenu.toggle(true);
    }

    /**
     * Called when the add categories FAB is clicked.
     */
    private void addCategoriesClicked(){
        startActivityForResult(new Intent(MainActivity.this, ChooseCategoriesActivity.class), CATEGORIES_REQUEST_CODE);
        mMenu.toggle(false);
    }

    /**
     * Creates the fade in/out effect over the FAB menu background.
     *
     * @param opening true if the menu is opening, false otherwise.
     */
    private void animateBackground(final boolean opening){
        AlphaAnimation animation;
        if (opening){
            animation = new AlphaAnimation(0, 1);
        }
        else{
            animation = new AlphaAnimation(1, 0);
        }
        //Start the animation
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation animation){
                mStopper.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation){
                if (!opening){
                    mStopper.setVisibility(View.GONE);
                }
                if (mSelectedCategory != null){
                    Intent intent = new Intent(MainActivity.this, ChooseGoalsActivity.class);
                    intent.putExtra("category", mSelectedCategory);
                    startActivityForResult(intent, Constants.CHOOSE_GOALS_REQUEST_CODE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation){
                //Unused
            }
        });
        mStopper.startAnimation(animation);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        //Order: drawer, FAB menu, application
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawers();
        }
        else if (mMenu.isOpened()){
            mMenu.toggle(true);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onItemClick(int position){
        switch (position){
            case DrawerAdapter.IMPORTANT_TO_ME:
                startActivity(new Intent(this, BehaviorProgressActivity.class));
                break;

            case DrawerAdapter.MY_PRIORITIES:
                startActivity(new Intent(this, MyPrioritiesActivity.class));
                break;

            case DrawerAdapter.MYSELF:
                startActivity(new Intent(this, UserProfileActivity.class));
                break;

            case DrawerAdapter.PLACES:
                startActivity(new Intent(this, PlacesActivity.class));
                break;

            case DrawerAdapter.MY_PRIVACY:
                startActivity(new Intent(this, PrivacyActivity.class));
                break;

            case DrawerAdapter.SETTINGS:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, Constants.SETTINGS_REQUEST_CODE);
                break;

            case DrawerAdapter.TOUR:
                startActivity(new Intent(this, TourActivity.class));
                break;

            case DrawerAdapter.DRAWER_COUNT:
                //Debug button
                startActivity(new Intent(this, CheckInActivity.class).putExtra(CheckInActivity.TYPE_KEY, 0));
                break;
        }
        mDrawerLayout.closeDrawers();
    }

    @Override
    public void onNullData(){
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onInstructionsSelected(){
        ((LinearLayoutManager)mFeed.getLayoutManager()).scrollToPositionWithOffset(mAdapter.getMyGoalsHeaderPosition(), 10);
    }

    @Override
    public void onGoalSelected(Goal goal){
        //User goal
        if (!mApplication.getUserData().getGoals().isEmpty()){
            startActivityForResult(new Intent(this, GoalActivity.class).putExtra(GoalActivity.GOAL_KEY, goal),
                    GOAL_REQUEST_CODE);
        }
        //Recommendation
        else{
            Intent chooseBehaviors = new Intent(this, ChooseBehaviorsActivity.class)
                    .putExtra(ChooseBehaviorsActivity.GOAL_KEY, goal)
                    .putExtra(ChooseBehaviorsActivity.CATEGORY_KEY, goal.getCategories().get(0));
            startActivity(chooseBehaviors);
        }
    }

    @Override
    public void onFeedbackSelected(Goal goal){
        if (goal != null){
            Intent chooseBehaviors = new Intent(this, ChooseBehaviorsActivity.class)
                    .putExtra(ChooseBehaviorsActivity.GOAL_KEY, goal);
            startActivity(chooseBehaviors);
        }
    }

    @Override
    public void onActionSelected(Action action){
        Intent actionIntent = new Intent(this, ActionActivity.class)
                .putExtra(ActionActivity.ACTION_KEY, action);
        startActivityForResult(actionIntent, ACTION_REQUEST_CODE);
    }

    @Override
    public void onTriggerSelected(Action action){
        Intent triggerIntent = new Intent(this, TriggerActivity.class)
                .putExtra("action", action)
                .putExtra("goal", action.getPrimaryGoal());
        startActivityForResult(triggerIntent, TRIGGER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK){
            if (requestCode == CATEGORIES_REQUEST_CODE){
                populateMenu();
                mAdapter.notifyDataSetChanged();
            }
            else if (requestCode == GOAL_REQUEST_CODE){
                mAdapter.notifyDataSetChanged();
            }
            else if (requestCode == ACTION_REQUEST_CODE){
                if (data.getBooleanExtra(ActionActivity.DID_IT_KEY, false)){
                    mAdapter.deleteSelectedItem();
                }
                else{
                    mAdapter.updateSelectedItem();
                }
            }
            else if (requestCode == TRIGGER_REQUEST_CODE){
                mAdapter.updateSelectedItem();
            }
        }
        else if (resultCode == Constants.LOGGED_OUT_RESULT_CODE){
            finish();
        }
    }

    @Override
    public void onRefresh(){
        new GetUserDataTask(this, this).execute(mApplication.getToken());
    }

    @Override
    public void userDataLoaded(@Nullable UserData userData){
        if (userData != null){
            mApplication.setUserData(userData);

            //Remove the previous item decoration before recreating the adapter
            mFeed.removeItemDecoration(mAdapter.getMainFeedPadding());

            //Recreate the adapter and set the new decoration
            mAdapter = new MainFeedAdapter(this, this);
            mFeed.setAdapter(mAdapter);
            mFeed.addItemDecoration(mAdapter.getMainFeedPadding());
        }
        if (mRefresh.isRefreshing()){
            mRefresh.setRefreshing(false);
        }
    }
}
