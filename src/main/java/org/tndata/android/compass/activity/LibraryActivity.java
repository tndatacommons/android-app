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

import org.tndata.android.compass.R;
import org.tndata.android.compass.util.ParallaxEffect;


/**
 * Generic activity for library screens.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public abstract class LibraryActivity
        extends AppCompatActivity
        implements ParallaxEffect.ScrollListener{

    private Toolbar mToolbar;

    private FrameLayout mHeaderContainer;
    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        mToolbar = (Toolbar)findViewById(R.id.library_toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mHeaderContainer = (FrameLayout)findViewById(R.id.library_header_container);
        mRecyclerView = (RecyclerView)findViewById(R.id.library_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ParallaxEffect parallaxEffect = new ParallaxEffect(mHeaderContainer, 0.5f);
        parallaxEffect.setScrollListener(this);
        mRecyclerView.addOnScrollListener(parallaxEffect);
    }

    @Override
    public final boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public final boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;

            case R.id.search:
                startActivity(new Intent(this, SearchActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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

    protected final void setAdapter(RecyclerView.Adapter adapter){
        mRecyclerView.setAdapter(adapter);
    }

    protected final View inflateHeader(@LayoutRes int layoutResId){
        return LayoutInflater.from(this).inflate(layoutResId, mHeaderContainer);
    }
}
