package org.tndata.android.compass.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.DrawerAdapter;
import org.tndata.android.compass.adapter.SearchAdapter;
import org.tndata.android.compass.adapter.feed.MainFeedAdapter;
import org.tndata.android.compass.adapter.feed.MainFeedAdapterListener;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.SearchResult;
import org.tndata.android.compass.model.UserData;
import org.tndata.android.compass.task.GetContentTask;
import org.tndata.android.compass.task.GetUserDataTask;
import org.tndata.android.compass.task.UpdateProfileTask;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.GcmRegistration;
import org.tndata.android.compass.util.OnScrollListenerHub;
import org.tndata.android.compass.util.ParallaxEffect;
import org.tndata.android.compass.util.Parser;

import java.util.ArrayList;
import java.util.List;


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
                MainFeedAdapterListener,
                MenuItemCompat.OnActionExpandListener,
                SearchView.OnQueryTextListener,
                SearchView.OnCloseListener,
                GetContentTask.GetContentListener,
                RecyclerView.OnItemTouchListener,
                SearchAdapter.SearchAdapterListener{

    //Activity request codes
    private static final int CATEGORIES_REQUEST_CODE = 4821;
    private static final int GOAL_REQUEST_CODE = 3486;
    private static final int GOAL_SUGGESTION_REQUEST_CODE = 8962;
    private static final int ACTION_REQUEST_CODE = 4582;
    private static final int TRIGGER_REQUEST_CODE = 7631;

    //Task request codes
    private static final int SEARCH_REQUEST_CODE = 1;


    //A reference to the application class
    private CompassApplication mApplication;

    private Toolbar mToolbar;

    //Search components
    private MenuItem mSearchItem;
    private SearchView mSearchView;
    private View mSearchDim;
    private View mSearchWrapper;
    private TextView mSearchHeader;
    private RecyclerView mSearchList;
    private SearchAdapter mSearchAdapter;
    private int mLastSearchRequestCode;
    private List<SearchResult> mSearchResults;

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

    private boolean mSuggestionDismissed;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApplication = (CompassApplication)getApplication();

        //Update the timezone and register with GCM
        new UpdateProfileTask(null).execute(mApplication.getUser());
        new GcmRegistration(this);

        mSearchDim = findViewById(R.id.main_search_dim);
        mSearchWrapper = findViewById(R.id.main_search_wrapper);
        mSearchHeader = (TextView)findViewById(R.id.main_search_header);
        mSearchList = (RecyclerView)findViewById(R.id.main_search_list);
        mSearchList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mSearchAdapter = new SearchAdapter(this, this);
        mSearchList.setAdapter(mSearchAdapter);
        mLastSearchRequestCode = SEARCH_REQUEST_CODE;

        //If this is pre L a different color scheme is applied
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            int preLColor = getResources().getColor(R.color.feed_pre_l_background);
            findViewById(R.id.main_container).setBackgroundColor(preLColor);
        }

        //Set up the toolbar
        mToolbar = (Toolbar)findViewById(R.id.main_toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
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
        //mDrawerToggle
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
        mAdapter = new MainFeedAdapter(this, this, false);

        mFeed = (RecyclerView)findViewById(R.id.main_feed);
        mFeed.setAdapter(mAdapter);
        mFeed.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mFeed.addItemDecoration(mAdapter.getMainFeedPadding());

        //Create the hub and add to it all the items that need to be parallaxed
        OnScrollListenerHub hub = new OnScrollListenerHub();

        ParallaxEffect parallax = new ParallaxEffect(header, 0.5f);
        parallax.setItemDecoration(((MainFeedAdapter)mFeed.getAdapter()).getMainFeedPadding());
        hub.addOnScrollListener(parallax);

        ParallaxEffect toolbarEffect = new ParallaxEffect(mToolbar, 1);
        toolbarEffect.setItemDecoration(mAdapter.getMainFeedPadding());
        toolbarEffect.setParallaxCondition(new ParallaxEffect.ParallaxCondition(){
            @Override
            protected boolean doParallax(){
                int height = (int)((CompassUtil.getScreenWidth(MainActivity.this)*2/3)*0.6);
                return -getRecyclerViewOffset() > height;
            }

            @Override
            protected int getFixedState(){
                return CompassUtil.getPixels(MainActivity.this, 10);
            }

            @Override
            protected int getParallaxViewOffset(){
                int height = (int)((CompassUtil.getScreenWidth(MainActivity.this)*2/3)*0.6);
                return  height + getFixedState() + getRecyclerViewOffset();
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

        mSuggestionDismissed = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_search, menu);
        mSearchItem = menu.findItem(R.id.search);
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mSearchItem.setVisible(false);
        }
        MenuItemCompat.setOnActionExpandListener(mSearchItem, this);

        mSearchView = (SearchView)mSearchItem.getActionView();
        mSearchView.setIconified(false);
        mSearchView.setOnCloseListener(this);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.clearFocus();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item){
        mSearchView.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mFeed.addOnItemTouchListener(this);
        mRefresh.setEnabled(false);
        mSearchAdapter.updateDataSet(new ArrayList<SearchResult>());
        mSearchHeader.setVisibility(View.INVISIBLE);
        mSearchDim.setVisibility(View.VISIBLE);
        mSearchWrapper.setVisibility(View.VISIBLE);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.main_toolbar_background_focused));
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item){
        mSearchView.setQuery("", false);
        mSearchView.clearFocus();
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mFeed.removeOnItemTouchListener(this);
        if (mFeed.canScrollVertically(-1)){
            mRefresh.setEnabled(false);
        }
        else{
            mRefresh.setEnabled(true);
        }
        mSearchDim.setVisibility(View.GONE);
        mSearchWrapper.setVisibility(View.GONE);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.main_toolbar_background_inactive));
        return true;
    }

    @Override
    public boolean onClose(){
        mSearchItem.collapseActionView();
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mFeed.removeOnItemTouchListener(this);
        if (mFeed.canScrollVertically(-1)){
            mRefresh.setEnabled(false);
        }
        else{
            mRefresh.setEnabled(true);
        }
        mSearchDim.setVisibility(View.GONE);
        mSearchWrapper.setVisibility(View.GONE);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.main_toolbar_background_inactive));
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query){
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText){
        if (newText.equals("")){
            mSearchHeader.setVisibility(View.INVISIBLE);
            mSearchAdapter.updateDataSet(new ArrayList<SearchResult>());
            mLastSearchRequestCode++;
        }
        else{
            newText = newText.replace(" ", "%20");
            String mUrl = Constants.BASE_URL + "search/?q=" + newText;
            new GetContentTask(this, ++mLastSearchRequestCode).execute(mUrl, mApplication.getToken());
        }
        return false;
    }

    @Override
    public void onContentRetrieved(int requestCode, String content){
        try{
            Log.d("Serch", new JSONObject(content).toString(2));
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        if (requestCode == mLastSearchRequestCode){
            mSearchResults = new Parser().parseSearchResults(content);
        }
    }

    @Override
    public void onRequestComplete(int requestCode){
        if (requestCode == mLastSearchRequestCode){
            mSearchHeader.setVisibility(View.VISIBLE);
            mSearchAdapter.updateDataSet(mSearchResults);
        }
    }

    @Override
    public void onRequestFailed(int requestCode){

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
                startActivity(new Intent(this, CheckInActivity.class).putExtra(CheckInActivity.TYPE_KEY, 2));
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
    public void onSuggestionDismissed(){
        mSuggestionDismissed = true;
    }

    @Override
    public void onSuggestionOpened(Goal goal){
        Intent chooseBehaviors = new Intent(this, ChooseBehaviorsActivity.class)
                .putExtra(ChooseBehaviorsActivity.GOAL_KEY, goal)
                .putExtra(ChooseBehaviorsActivity.CATEGORY_KEY, goal.getCategories().get(0));
        startActivityForResult(chooseBehaviors, GOAL_SUGGESTION_REQUEST_CODE);
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
            else if (requestCode == GOAL_SUGGESTION_REQUEST_CODE){
                mAdapter.dismissSuggestion();
            }
            else if (requestCode == GOAL_REQUEST_CODE){
                mAdapter.notifyDataSetChanged();
            }
            else if (requestCode == ACTION_REQUEST_CODE){
                if (data.getBooleanExtra(ActionActivity.DID_IT_KEY, false)){
                    mAdapter.didIt();
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
            mAdapter = new MainFeedAdapter(this, this, !mSuggestionDismissed);
            mFeed.setAdapter(mAdapter);
            mFeed.addItemDecoration(mAdapter.getMainFeedPadding());

            mSuggestionDismissed = false;
        }
        if (mRefresh.isRefreshing()){
            mRefresh.setRefreshing(false);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e){
        return true;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e){

    }

    @Override
    public void onSearchResultSelected(SearchResult result){
        if (result.isGoal()){
            Intent chooseBehaviors = new Intent(this, ChooseBehaviorsActivity.class)
                    .putExtra(ChooseBehaviorsActivity.GOAL_ID_KEY, result.getId());
            startActivity(chooseBehaviors);
        }
    }
}
