package org.tndata.android.compass.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.DrawerAdapter;
import org.tndata.android.compass.adapter.SearchAdapter;
import org.tndata.android.compass.adapter.feed.DisplayableGoal;
import org.tndata.android.compass.adapter.feed.MainFeedAdapter;
import org.tndata.android.compass.adapter.feed.MainFeedAdapterListener;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.model.CustomGoal;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.GoalContent;
import org.tndata.android.compass.model.SearchResult;
import org.tndata.android.compass.model.UserData;
import org.tndata.android.compass.model.UserGoal;
import org.tndata.android.compass.parser.MiscellaneousParser;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserCallback;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.GcmRegistration;
import org.tndata.android.compass.util.NetworkRequest;
import org.tndata.android.compass.util.OnScrollListenerHub;
import org.tndata.android.compass.util.ParallaxEffect;

import java.util.ArrayList;


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
                NetworkRequest.RequestCallback,
                DrawerAdapter.OnItemClickListener,
                MainFeedAdapterListener,
                MenuItemCompat.OnActionExpandListener,
                SearchView.OnQueryTextListener,
                SearchView.OnCloseListener,
                RecyclerView.OnItemTouchListener,
                SearchAdapter.SearchAdapterListener,
                View.OnClickListener,
                ParserCallback{

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
    private CategoryContent mSelectedCategory;

    private boolean mSuggestionDismissed;

    private int mGetUserDataRequestCode;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApplication = (CompassApplication)getApplication();

        //Update the timezone and register with GCM
        NetworkRequest.put(this, null, API.getPutUserProfileUrl(mApplication.getUser()),
                mApplication.getToken(), API.getPutUserProfileBody(mApplication.getUser()));
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
        mFeed.addOnScrollListener(hub);

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
            mLastSearchRequestCode = NetworkRequest.get(this, this, API.getSearchUrl(newText),
                    mApplication.getToken());
        }
        return false;
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetUserDataRequestCode){
            Parser.parse(result, ParserModels.UserDataResultSet.class, this);
        }
        else if (requestCode == mLastSearchRequestCode){
            mSearchHeader.setVisibility(View.VISIBLE);
            mSearchAdapter.updateDataSet(MiscellaneousParser.parseSearchResults(result));
        }
    }

    @Override
    public void onRequestFailed(int requestCode, String message){
        if (requestCode == mGetUserDataRequestCode){
            Toast.makeText(this, "Couldn't reload", Toast.LENGTH_SHORT).show();
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
        for (int i = 0; i < 3; i++){
            ContextThemeWrapper ctx = new ContextThemeWrapper(this, R.style.MenuButtonStyle);
            FloatingActionButton fab = new FloatingActionButton(ctx);
            fab.setColorNormalResId(R.color.grow_accent);
            fab.setColorPressedResId(R.color.grow_accent);
            fab.setColorRippleResId(R.color.grow_accent);
            fab.setScaleType(ImageView.ScaleType.FIT_CENTER);
            fab.setImageResource(R.drawable.fab_add);
            fab.setOnClickListener(this);
            if (i == 0){
                fab.setId(R.id.fab_choose_goals);
                fab.setLabelText(getString(R.string.fab_choose_goals));
            }
            else if (i == 1){
                fab.setId(R.id.fab_create_goal);
                fab.setLabelText(getString(R.string.fab_create_goal));
            }
            else if (i == 2){
                fab.setId(R.id.fab_choose_interests);
                fab.setLabelText(getString(R.string.fab_choose_interests));
            }
            mMenu.addMenuButton(fab);
        }
    }

    @Override
    public void onClick(View v){
        mMenu.toggle(true);
        switch (v.getId()){
            case R.id.fab_choose_goals:
                chooseGoalsClicked();
                break;

            case R.id.fab_create_goal:
                createCustomGoalClicked();
                break;

            case R.id.fab_choose_interests:
                chooseInterestsClicked();
                break;
        }
    }

    /**
     * Called when the choose Goals FAB is clicked.
     */
    private void chooseGoalsClicked(){
        startActivity(new Intent(this, ChooseCategoryActivity.class));
    }

    /**
     * Called when the create goal FAB is clicked.
     */
    private void createCustomGoalClicked(){
        startActivity(new Intent(this, CustomContentManagerActivity.class));
    }

    /**
     * Called when the choose interests FAB is clicked.
    */
    private void chooseInterestsClicked(){
        startActivityForResult(new Intent(this, ChooseInterestsActivity.class),
                CATEGORIES_REQUEST_CODE);
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
                    intent.putExtra(ChooseGoalsActivity.CATEGORY_KEY, mSelectedCategory);
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
        ((LinearLayoutManager)mFeed.getLayoutManager()).scrollToPositionWithOffset(mAdapter.getGoalsPosition(), 10);
    }

    @Override
    public void onSuggestionDismissed(){
        mSuggestionDismissed = true;
    }

    @Override
    public void onSuggestionOpened(GoalContent goal){
        CategoryContent category = null;
        for (Long categoryId:goal.getCategoryIdSet()){
            if (mApplication.getUserData().getCategories().containsKey(categoryId)){
                category = mApplication.getCategories().get(categoryId).getCategory();
            }
        }
        Intent chooseBehaviors = new Intent(this, ChooseBehaviorsActivity.class)
                .putExtra(ChooseBehaviorsActivity.GOAL_KEY, goal)
                .putExtra(ChooseBehaviorsActivity.CATEGORY_KEY, category);
        startActivityForResult(chooseBehaviors, GOAL_SUGGESTION_REQUEST_CODE);
    }

    @Override
    public void onGoalSelected(DisplayableGoal goal){
        if (goal instanceof UserGoal){
            Intent goalActivityIntent = new Intent(this, GoalActivity.class)
                    .putExtra(GoalActivity.USER_GOAL_KEY, (UserGoal)goal);
            startActivityForResult(goalActivityIntent, GOAL_REQUEST_CODE);
        }
        else if (goal instanceof CustomGoal){
            Intent editGoal = new Intent(this, CustomContentManagerActivity.class)
                    .putExtra(CustomContentManagerActivity.CUSTOM_GOAL_KEY, (CustomGoal)goal);
            startActivityForResult(editGoal, GOAL_REQUEST_CODE);
        }
    }

    @Override
    public void onGoalSelected(Goal goal){
        Intent goalActivityIntent = new Intent(this, GoalActivity.class)
                .putExtra(GoalActivity.USER_GOAL_KEY, goal);
        startActivityForResult(goalActivityIntent, GOAL_REQUEST_CODE);
    }

    @Override
    public void onFeedbackSelected(Goal goal){
        if (goal != null && goal instanceof UserGoal){
            Intent chooseBehaviors = new Intent(this, ChooseBehaviorsActivity.class)
                    .putExtra(ChooseBehaviorsActivity.GOAL_KEY, ((UserGoal)goal).getGoal());
            startActivity(chooseBehaviors);
        }
    }

    @Override
    public void onActionSelected(Action userAction){
        Intent actionIntent = new Intent(this, ActionActivity.class)
                .putExtra(ActionActivity.ACTION_KEY, userAction);
        startActivityForResult(actionIntent, ACTION_REQUEST_CODE);
    }

    @Override
    public void onTriggerSelected(Action userAction){
        Intent triggerIntent = new Intent(this, TriggerActivity.class)
                .putExtra(TriggerActivity.ACTION_KEY, userAction)
                .putExtra(TriggerActivity.GOAL_KEY, userAction.getGoal());
        startActivityForResult(triggerIntent, TRIGGER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK){
            if (requestCode == CATEGORIES_REQUEST_CODE){
                //populateMenu();
                mAdapter.notifyDataSetChanged();
            }
            else if (requestCode == GOAL_SUGGESTION_REQUEST_CODE){
                mAdapter.dismissSuggestion();
            }
            else if (requestCode == GOAL_REQUEST_CODE){
                mAdapter.dataSetChanged();
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
        mGetUserDataRequestCode = NetworkRequest.get(this, this, API.getUserDataUrl(),
                mApplication.getToken());
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e){
        return true;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e){

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept){

    }

    @Override
    public void onSearchResultSelected(SearchResult result){
        if (result.isGoal()){
            Intent chooseBehaviors = new Intent(this, ChooseBehaviorsActivity.class)
                    .putExtra(ChooseBehaviorsActivity.GOAL_ID_KEY, result.getId());
            startActivity(chooseBehaviors);
        }
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.UserDataResultSet){
            UserData userData = ((ParserModels.UserDataResultSet)result).results.get(0);

            userData.sync();
            userData.logData();
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.UserDataResultSet){
            UserData userData = ((ParserModels.UserDataResultSet)result).results.get(0);

            mApplication.setUserData(userData);

            //Remove the previous item decoration before recreating the adapter
            mFeed.removeItemDecoration(mAdapter.getMainFeedPadding());

            //Recreate the adapter and set the new decoration
            mAdapter = new MainFeedAdapter(this, this, !mSuggestionDismissed);
            mFeed.setAdapter(mAdapter);
            mFeed.addItemDecoration(mAdapter.getMainFeedPadding());

            mSuggestionDismissed = false;

            if (mRefresh.isRefreshing()){
                mRefresh.setRefreshing(false);
            }
        }
    }
}
