package org.tndata.android.compass.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.DrawerAdapter;
import org.tndata.android.compass.adapter.feed.MainFeedAdapter;
import org.tndata.android.compass.adapter.feed.MainFeedAdapterListener;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.model.CustomGoal;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.GoalContent;
import org.tndata.android.compass.model.UpcomingAction;
import org.tndata.android.compass.model.UserGoal;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.FeedDataLoader;
import org.tndata.android.compass.util.GcmRegistration;
import org.tndata.android.compass.util.OnScrollListenerHub;
import org.tndata.android.compass.util.ParallaxEffect;

import es.sandwatch.httprequests.HttpRequest;


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
                DrawerAdapter.OnItemClickListener,
                MainFeedAdapterListener,
                View.OnClickListener,
                FeedDataLoader.Callback{

    //Activity request codes
    private static final int CATEGORIES_REQUEST_CODE = 4821;
    private static final int GOAL_RC = 3486;
    private static final int GOAL_SUGGESTION_REQUEST_CODE = 8962;
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

    private boolean mSuggestionDismissed;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApplication = (CompassApplication)getApplication();

        //Update the timezone and register with GCM
        HttpRequest.put(null, API.getPutUserProfileUrl(mApplication.getUser()),
                API.getPutUserProfileBody(mApplication.getUser()));
        new GcmRegistration(this);

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
        mDrawerLayout.addDrawerListener(mDrawerToggle);

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
        mAdapter = new MainFeedAdapter(this, this, false);

        mFeed = (RecyclerView)findViewById(R.id.main_feed);
        mFeed.setAdapter(mAdapter);
        mFeed.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        //Create the hub and add to it all the items that need to be parallaxed
        OnScrollListenerHub hub = new OnScrollListenerHub();

        hub.addOnScrollListener(new ParallaxEffect(header, 0.5f));

        ParallaxEffect toolbarEffect = new ParallaxEffect(toolbar, 1);
        toolbarEffect.setParallaxCondition(new ParallaxEffect.ParallaxCondition(){
            @Override
            protected boolean doParallax(){
                int height = (int)((CompassUtil.getScreenWidth(MainActivity.this) * 2 / 3) * 0.6);
                return -getRecyclerViewOffset() > height;
            }

            @Override
            protected int getFixedState(){
                return CompassUtil.getPixels(MainActivity.this, 10);
            }

            @Override
            protected int getParallaxViewOffset(){
                int height = (int)((CompassUtil.getScreenWidth(MainActivity.this) * 2 / 3) * 0.6);
                return height + getFixedState() + getRecyclerViewOffset();
            }
        });
        hub.addOnScrollListener(toolbarEffect);

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
        mFeed.addOnScrollListener(hub);

        Animation hideAnimation = new ScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        hideAnimation.setDuration(200);
        Animation showAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        showAnimation.setDuration(200);

        mStopper = findViewById(R.id.main_stopper);
        mMenu = (FloatingActionMenu)findViewById(R.id.main_fab_menu);
        mMenu.setMenuButtonHideAnimation(hideAnimation);
        mMenu.setMenuButtonShowAnimation(showAnimation);
        mMenu.setClosedOnTouchOutside(true);
        mMenu.setOnMenuButtonClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (!mMenu.isOpened()){
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

        mSuggestionDismissed = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            searchItem.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public final boolean onOptionsItemSelected(MenuItem item){
        if (mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        switch (item.getItemId()){
            case R.id.search:
                startActivity(new Intent(this, SearchActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
        for (int i = 0; i < 2; i++){
            ContextThemeWrapper ctx = new ContextThemeWrapper(this, R.style.MenuButtonStyle);
            FloatingActionButton fab = new FloatingActionButton(ctx);
            fab.setColorNormalResId(R.color.grow_accent);
            fab.setColorPressedResId(R.color.grow_accent);
            fab.setColorRippleResId(R.color.grow_accent);
            fab.setScaleType(ImageView.ScaleType.FIT_CENTER);
            if (i == 0){
                fab.setId(R.id.fab_search_goals);
                fab.setLabelText(getString(R.string.fab_search_goals));
                fab.setImageResource(R.drawable.ic_search);
            }
            else if (i == 1){
                fab.setId(R.id.fab_browse_goals);
                fab.setLabelText(getString(R.string.fab_browse_goals));
                fab.setImageResource(R.drawable.ic_list_white_24dp);
            }
            fab.setOnClickListener(this);
            mMenu.addMenuButton(fab);
        }
    }

    @Override
    public void onClick(View v){
        mMenu.toggle(true);
        switch (v.getId()){
            case R.id.fab_search_goals:
                search();
                break;

            case R.id.fab_browse_goals:
                browseGoals();
                break;
        }
    }

    /**
     * Called when the search goals FAB is clicked.
     */
    private void search(){
        startActivityForResult(new Intent(this, SearchActivity.class), GOAL_RC);
    }

    /**
     * Called when the browse Goals FAB is clicked.
     */
    private void browseGoals(){
        startActivityForResult(new Intent(this, ChooseCategoryActivity.class), GOAL_RC);
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
            }

            @Override
            public void onAnimationRepeat(Animation animation){
                //Unused
            }
        });
        mStopper.startAnimation(animation);
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
            case DrawerAdapter.MY_ACTIVITIES:
                startActivity(new Intent(this, MyActivitiesActivity.class));
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

            case DrawerAdapter.SUPPORT:
                //Ask the user to open their default email client
                Intent emailIntent = new Intent(Intent.ACTION_SEND)
                        .putExtra(Intent.EXTRA_EMAIL, new String[]{"feedback@tndata.org"})
                        .putExtra(Intent.EXTRA_SUBJECT, getText(R.string.action_support_subject))
                        .setType("text/plain");
                try{
                    startActivity(Intent.createChooser(emailIntent, getText(R.string.action_support_share_title)));
                }
                catch (ActivityNotFoundException anfx){
                    Toast.makeText(MainActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
                break;

            case DrawerAdapter.DRAWER_COUNT:
                //Debug button
                startActivity(new Intent(this, CheckInActivity.class));
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
        ((LinearLayoutManager)mFeed.getLayoutManager()).scrollToPositionWithOffset(mAdapter.getGoalsPosition(), 10);
    }

    @Override
    public void onSuggestionDismissed(){
        mSuggestionDismissed = true;
    }

    @Override
    public void onSuggestionSelected(GoalContent goal){
        CategoryContent category = null;
        for (Long categoryId:goal.getCategoryIdSet()){
            if (mApplication.getPublicCategories().containsKey(categoryId)){
                category = mApplication.getPublicCategories().get(categoryId);
                break;
            }
        }
        Intent chooseBehaviors = new Intent(this, ChooseBehaviorsActivity.class)
                .putExtra(ChooseBehaviorsActivity.GOAL_KEY, goal)
                .putExtra(ChooseBehaviorsActivity.CATEGORY_KEY, category);
        startActivityForResult(chooseBehaviors, GOAL_SUGGESTION_REQUEST_CODE);
    }

    @Override
    public void onGoalSelected(Goal goal){
        if (goal instanceof UserGoal){
            Intent reviewActionsIntent = new Intent(this, ReviewActionsActivity.class)
                    .putExtra(ReviewActionsActivity.USER_GOAL_KEY, goal);
            startActivityForResult(reviewActionsIntent, GOAL_RC);
        }
        else if (goal instanceof CustomGoal){
            Parcelable customGoal = (CustomGoal)goal;
            Intent editGoal = new Intent(this, CustomContentManagerActivity.class)
                    .putExtra(CustomContentManagerActivity.CUSTOM_GOAL_KEY, customGoal);
            startActivityForResult(editGoal, GOAL_RC);
        }
    }

    @Override
    public void onFeedbackSelected(FeedData.ActionFeedback feedback){
        if (feedback.hasUserGoal()){
            /*Intent goalActivityIntent = new Intent(this, GoalActivity.class)
                    .putExtra(GoalActivity.USER_GOAL_KEY, goal);
            startActivityForResult(goalActivityIntent, GOAL_RC);*/
        }
        else if (feedback.hasCustomGoal()){
            //TODO
        }
    }

    @Override
    public void onActionSelected(UpcomingAction action){
        Intent actionIntent = new Intent(this, ActionActivity.class)
                .putExtra(ActionActivity.UPCOMING_ACTION_KEY, action);
        startActivityForResult(actionIntent, ACTION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK){
            if (requestCode == CATEGORIES_REQUEST_CODE){
                mAdapter.notifyDataSetChanged();
            }
            else if (requestCode == GOAL_SUGGESTION_REQUEST_CODE){
                mAdapter.dismissSuggestion();
            }
            else if (requestCode == GOAL_RC){
                mAdapter.updateDataSet();
            }
            else if (requestCode == ACTION_REQUEST_CODE){
                if (data.getBooleanExtra(ActionActivity.DID_IT_KEY, false)){
                    Action action = data.getParcelableExtra(ActionActivity.ACTION_KEY);
                    mAdapter.didIt(mApplication.getFeedData().getActionX(action));
                    mAdapter.updateUpcoming();
                }
                else{
                    mApplication.updateAction((Action)data.getParcelableExtra(ActionActivity.ACTION_KEY));
                    mAdapter.updateUpcoming();
                }
            }
            else if (requestCode == TRIGGER_REQUEST_CODE){
                mAdapter.updateUpcoming();
            }
        }
        else if (resultCode == Constants.LOGGED_OUT_RESULT_CODE){
            finish();
        }
    }

    @Override
    public void onRefresh(){
        FeedDataLoader.load(this);
    }

    @Override
    public void onFeedDataLoaded(@Nullable FeedData feedData){
        if (feedData != null){
            mApplication.setFeedData(feedData);

            mAdapter = new MainFeedAdapter(this, this, !mSuggestionDismissed);
            mFeed.setAdapter(mAdapter);

            mSuggestionDismissed = false;

            if (mRefresh.isRefreshing()){
                mRefresh.setRefreshing(false);
            }
        }
    }
}
