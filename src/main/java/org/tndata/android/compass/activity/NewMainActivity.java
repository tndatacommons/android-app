package org.tndata.android.compass.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.OnScrollListenerHub;
import org.tndata.android.compass.util.ParallaxEffect;


/**
 * Created by isma on 9/22/15.
 */
public class NewMainActivity
        extends AppCompatActivity
        implements
                DrawerAdapter.OnItemClickListener,
                MainFeedAdapter.MainFeedAdapterListener{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private RecyclerView mFeed;

    private View mStopper;
    private FloatingActionMenu mMenu;

    private Category mSelectedCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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

        View header = findViewById(R.id.main_illustration);

        mFeed = (RecyclerView)findViewById(R.id.main_feed);
        mFeed.setAdapter(new MainFeedAdapter(this, this));
        mFeed.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mFeed.addItemDecoration(((MainFeedAdapter)mFeed.getAdapter()).getMainFeedPadding());

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

        populateMenu();
    }

    private void populateMenu(){
        CompassApplication app = (CompassApplication)getApplication();
        mMenu.removeAllMenuButtons();
        int i = 0;
        for (Category category:app.getUserData().getCategories()){
            final int position = i;
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
                    addGoalsClicked(position);
                }
            });
            i++;
        }
    }

    private int getIconResourceId(Category category){
        if (category.getTitle().equalsIgnoreCase("Happiness")){
            return R.drawable.ic_award8;
        }
        else if (category.getTitle().equalsIgnoreCase("Community")){
            return R.drawable.ic_award12;
        }
        else if (category.getTitle().equalsIgnoreCase("Family")){
            return R.drawable.ic_award17;
        }
        else if (category.getTitle().equalsIgnoreCase("Home")){
            return R.drawable.ic_award18;
        }
        else if (category.getTitle().equalsIgnoreCase("Romance")){
            return R.drawable.ic_award24;
        }
        else if (category.getTitle().equalsIgnoreCase("Health")){
            return R.drawable.ic_award33;
        }
        else if (category.getTitle().equalsIgnoreCase("Wellness")){
            return R.drawable.ic_commercial16;
        }
        else if (category.getTitle().equalsIgnoreCase("Safety")){
            return R.drawable.ic_olive;
        }
        else if (category.getTitle().equalsIgnoreCase("Parenting")){
            return R.drawable.ic_shield74;
        }
        else if (category.getTitle().equalsIgnoreCase("Education")){
            return R.drawable.ic_sports27;
        }
        else if (category.getTitle().equalsIgnoreCase("Skills")){
            return R.drawable.ic_sports28;
        }
        else if (category.getTitle().equalsIgnoreCase("Work")){
            return R.drawable.ic_thumb34;
        }
        else if (category.getTitle().equalsIgnoreCase("Prosperity")){
            return R.drawable.ic_thumb36;
        }
        else if (category.getTitle().equalsIgnoreCase("Fun")){
            return R.drawable.ic_trophy40;
        }
        else{
            return R.drawable.ic_add_white_24dp;
        }
    }

    private void addGoalsClicked(int position){
        CompassApplication app = (CompassApplication)getApplication();
        mSelectedCategory = app.getUserData().getCategories().get(position);
        mMenu.toggle(true);
    }

    private void animateBackground(final boolean opening){
        Log.d("MainFeed", "opening " + opening);
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
                    Intent intent = new Intent(NewMainActivity.this, ChooseGoalsActivity.class);
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
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
        mDrawerLayout.closeDrawers();
    }

    @Override
    public void onGoalSelected(Goal goal){
        startActivity(new Intent(this, GoalActivity.class).putExtra(GoalActivity.GOAL_KEY, goal));
    }
}
