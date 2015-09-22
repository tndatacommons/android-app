package org.tndata.android.compass.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.astuetz.PagerSlidingTabStrip;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.DrawerAdapter;
import org.tndata.android.compass.adapter.MainViewPagerAdapter;
import org.tndata.android.compass.fragment.MyGoalsFragment.MyGoalsFragmentListener;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.DrawerItem;
import org.tndata.android.compass.service.LogOutService;
import org.tndata.android.compass.task.UpdateProfileTask;
import org.tndata.android.compass.ui.button.FloatingActionButton;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.GcmRegistration;
import org.tndata.android.compass.util.ImageLoader;

import java.util.ArrayList;


public class MainActivity
        extends AppCompatActivity
        implements
                MyGoalsFragmentListener,
                DrawerAdapter.OnItemClickListener{

    private static final int IMPORTANT_TO_ME = 0;
    private static final int MY_PRIORITIES = 1;
    private static final int MYSELF = 2;
    private static final int PLACES = 3;
    private static final int MY_PRIVACY = 4;
    private static final int TOUR = 5;
    private static final int SETTINGS = 6;
    private static final int DRAWER_COUNT = 7;


    private CompassApplication application;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private RecyclerView mDrawerList;
    private ArrayList<DrawerItem> mDrawerItems;
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    //private HeroView mHeroView;
    private ImageView mHeaderImageView;
    private MainViewPagerAdapter mAdapter;
    private FloatingActionButton mFloatingActionButton;
    private boolean mDrawerIsOpen = false;
    private boolean backButtonSelectsDefaultTab = false;
    private static final int DEFAULT_TAB = 0;
    private int lastViewPagerItem = 0;

    @Override
    public void onBackPressed() {
        // This activity may switch tabs when a user taps a card, so after doing that,
        // we want the back button to return the user to the default tab.
        if (backButtonSelectsDefaultTab) {
            activateTab(DEFAULT_TAB);
            backButtonSelectsDefaultTab = false; // resets default behavior
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        application = (CompassApplication) getApplication();

        //Update the timezone
        new UpdateProfileTask(null).execute(application.getUser());

        // Register the device with Google Cloud Messaging
        new GcmRegistration(this);

        mToolbar = (Toolbar) findViewById(R.id.transparent_tool_bar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);
        mDrawerList = (RecyclerView)findViewById(R.id.main_left_drawer);
        mDrawerList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mDrawerItems = drawerItems();
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        DrawerAdapter drawerAdapter = new DrawerAdapter(this, this, mDrawerItems);
        mDrawerList.setAdapter(drawerAdapter);
        mDrawerList.addItemDecoration(DrawerAdapter.getItemPadding(this));
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.nav_drawer_action, R.string.nav_drawer_action) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                mDrawerIsOpen = false;
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mDrawerIsOpen = true;
                invalidateOptionsMenu();
            }
        };

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.category_fab_button);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        //mHeroView = (HeroView)findViewById(R.id.main_hero_container);
        mHeaderImageView = (ImageView) findViewById(R.id.main_material_imageview);
        mAdapter = new MainViewPagerAdapter(getSupportFragmentManager(), this);
        mAdapter.setFloatingActionButton(mFloatingActionButton);
        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        mViewPager.setAdapter(mAdapter);
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.main_pager_tabstrip);
        tabs.setViewPager(mViewPager);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

                mFloatingActionButton.hidePager();
            }

            @Override
            public void onPageSelected(int position) {
                String url = mAdapter.getPositionImageUrl(position);
                if (url != null) {
                    mHeaderImageView.invalidate();
                    //Log.d("MEASURE", mHeroView.getMeasuredWidth()+"");
                    //Log.d("MEASURE", mHeroView.getMeasuredHeight()+"");
                    ImageLoader.loadBitmap(mHeaderImageView, url, new ImageLoader.Options().setUsePlaceholder(false));
                } else {
                    //TODO there is a bug here, when the user scrolls fast, the resource gets
                    //TODO  loaded before the cached image. When that happens, some other
                    //TODO  category image is displayed. The next line will only fix that
                    //TODO  if the resource is being pulled from the web. Need to make a
                    //TODO  couple of minor adjustments to the tasks spawned by the cache.
                    ImageLoader.cancelPotentialWork("", mHeaderImageView);
                    mHeaderImageView.setImageResource(R.drawable.compass_master_illustration);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mFloatingActionButton.showPager(true);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == Constants.LOGGED_OUT_RESULT_CODE){
            startService(new Intent(this, LogOutService.class));
            finish();
        }
        else if (requestCode == Constants.CHOOSE_CATEGORIES_REQUEST_CODE){
            showUserData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void chooseCategories() {
        Intent intent = new Intent(getApplicationContext(), ChooseCategoriesActivity.class);
        startActivityForResult(intent, Constants.CHOOSE_CATEGORIES_REQUEST_CODE);
    }

    @Override
    public void onItemClick(int position){
        switch (position) {
            case IMPORTANT_TO_ME:
                startActivity(new Intent(getApplicationContext(), BehaviorProgressActivity.class));
                break;

            case MY_PRIORITIES:
                startActivity(new Intent(getApplicationContext(), MyPrioritiesActivity.class));
                break;

            case MYSELF:
                startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
                break;

            case PLACES:
                startActivity(new Intent(getApplicationContext(), PlacesActivity.class));
                break;

            case MY_PRIVACY:
                startActivity(new Intent(getApplicationContext(), PrivacyActivity.class));
                break;

            case SETTINGS:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivityForResult(intent, Constants.SETTINGS_REQUEST_CODE);
                break;

            case TOUR:
                startActivity(new Intent(getApplicationContext(), TourActivity.class));
                break;

            case DRAWER_COUNT:
                //Debug button
                startActivity(new Intent(getApplicationContext(), NewMainActivity.class));
                break;
        }
        mDrawerLayout.closeDrawers();
    }

    /**
     * Creates the list of drawer items.
     * TODO move to DrawerAdapter?
     *
     * @return the list of drawer items.
     */
    private ArrayList<DrawerItem> drawerItems(){
        ArrayList<DrawerItem> items = new ArrayList<>();
        for (int i = 0; i < DRAWER_COUNT; i++){
            switch (i){
                case IMPORTANT_TO_ME:
                    items.add(new DrawerItem(getResources().getString(R.string.action_my_progress),
                            R.drawable.ic_clipboard));
                    break;
                case MY_PRIORITIES:
                    items.add(new DrawerItem(getResources().getString(R.string.action_my_priorities),
                            R.drawable.ic_list_bullet));
                    break;
                case MYSELF:
                    items.add(new DrawerItem(getResources().getString(R.string.action_my_information),
                            R.drawable.ic_profile));
                    break;
                case PLACES:
                    items.add(new DrawerItem(getResources().getString(R.string.action_my_places),
                            R.drawable.ic_place));
                    break;
                case MY_PRIVACY:
                    items.add(new DrawerItem(getResources().getString(R.string.action_my_privacy),
                            R.drawable.ic_info));
                    break;
                case SETTINGS:
                    items.add(new DrawerItem(getResources().getString(R.string.action_settings),
                            R.drawable.ic_settings));
                    break;
                case TOUR:
                    items.add(new DrawerItem(getResources().getString(R.string.action_tour),
                            R.drawable.ic_tour));
                    break;
            }
        }

        return items;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) { // Back key pressed
            if (mDrawerIsOpen) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
        showUserData();
    }

    public void showUserData() {
        mAdapter.setCategories(application.getCategories());
        mAdapter.notifyDataSetChanged();
        // broadcast that goals are available.
        Intent intent = new Intent(Constants.GOAL_UPDATED_BROADCAST_ACTION);
        sendBroadcast(intent);
    }

    public void activateTab(int tabIndex) {
        mViewPager.setCurrentItem(tabIndex);
    }

    @Override
    public void transitionToCategoryTab(Category category) {
        backButtonSelectsDefaultTab = true;
        activateTab(mAdapter.getCategoryPosition(category) + 1);  // add one for the default tab
    }
}
