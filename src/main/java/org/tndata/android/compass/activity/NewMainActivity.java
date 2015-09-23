package org.tndata.android.compass.activity;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.DrawerAdapter;
import org.tndata.android.compass.adapter.MainFeedAdapter;
import org.tndata.android.compass.util.ParallaxEffect;


/**
 * Created by isma on 9/22/15.
 */
public class NewMainActivity extends AppCompatActivity implements DrawerAdapter.OnItemClickListener{
    private ActionBarDrawerToggle mDrawerToggle;


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

        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
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
        drawerLayout.setDrawerListener(mDrawerToggle);

        RecyclerView drawerList = (RecyclerView)findViewById(R.id.main_drawer);
        drawerList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        drawerList.setAdapter(new DrawerAdapter(this, this));
        drawerList.addItemDecoration(DrawerAdapter.getItemPadding(this));

        View header = findViewById(R.id.main_illustration);

        RecyclerView feed = (RecyclerView)findViewById(R.id.main_feed);
        feed.setAdapter(new MainFeedAdapter(this));
        feed.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        feed.addItemDecoration(new ItemPadding());
        feed.setOnScrollListener(new ParallaxEffect(header, 0.5f));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
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

    }

    /**
     * Decoration class to establish the grid's items margin.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    final class ItemPadding extends RecyclerView.ItemDecoration{
        private int margin;


        /**
         * Constructor.
         */
        public ItemPadding(){
            margin = (int)Math.ceil(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    8, getResources().getDisplayMetrics()));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state){

            outRect.top = margin / 2;
            outRect.left = margin / 2;
            outRect.bottom = margin / 2;
            outRect.right = margin / 2;
        }
    }
}
