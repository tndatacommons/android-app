package org.tndata.android.compass.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.MaterialAdapter;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.ParallaxEffect;


/**
 * Generic activity for library screens.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public abstract class MaterialActivity
        extends AppCompatActivity
        implements ParallaxEffect.ScrollListener{

    private Toolbar mToolbar;

    private FrameLayout mHeaderContainer;
    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material);

        //Set up the toolbar
        mToolbar = (Toolbar)findViewById(R.id.library_toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        //Fetch UI components
        mHeaderContainer = (FrameLayout)findViewById(R.id.library_header_container);
        mRecyclerView = (RecyclerView)findViewById(R.id.library_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Make the header the right size
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mHeaderContainer.getLayoutParams();
        params.height = CompassUtil.getScreenWidth(this)/3*2;
        mHeaderContainer.setLayoutParams(params);

        //Add the parallax effect to the header
        ParallaxEffect parallaxEffect = new ParallaxEffect(mHeaderContainer, 0.5f);
        parallaxEffect.setScrollListener(this);
        mRecyclerView.addOnScrollListener(parallaxEffect);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public final boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                onHomeTapped();
                finish();
                return true;

            case R.id.search:
                startActivity(new Intent(this, SearchActivity.class));
                return true;

            default:
                return menuItemSelected(item) || super.onOptionsItemSelected(item);
        }
    }

    protected void onHomeTapped(){}

    protected boolean menuItemSelected(MenuItem item){
        return false;
    }

    @Override
    public void onScroll(float percentage){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            Drawable color = mToolbar.getBackground();
            color.setAlpha(Math.round(percentage * 255));
            mToolbar.setBackground(color);
        }
    }

    protected final void setColor(int color){
        mToolbar.setBackgroundColor(color);
        mHeaderContainer.setBackgroundColor(color);
    }

    protected final void setAdapter(MaterialAdapter adapter){
        mRecyclerView.setAdapter(adapter);
    }

    protected final View inflateHeader(@LayoutRes int layoutResId){
        return LayoutInflater.from(this).inflate(layoutResId, mHeaderContainer);
    }
}
