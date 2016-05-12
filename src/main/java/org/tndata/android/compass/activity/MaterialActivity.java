package org.tndata.android.compass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.github.clans.fab.FloatingActionButton;

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
public abstract class MaterialActivity extends AppCompatActivity{
    private FrameLayout mHeaderContainer;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFAB;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_material);

        //Set up the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.material_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_white_24dp);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        //Fetch UI components
        mHeaderContainer = (FrameLayout)findViewById(R.id.material_header_container);
        mRecyclerView = (RecyclerView)findViewById(R.id.material_list);
        mFAB = (FloatingActionButton)findViewById(R.id.material_fab);

        //Make the header the right size
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mHeaderContainer.getLayoutParams();
        params.height = CompassUtil.getScreenWidth(this)/3*2;
        mHeaderContainer.setLayoutParams(params);

        //Add the parallax effect to the header
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addOnScrollListener(new ParallaxEffect(mHeaderContainer, 0.5f));

        //Add the toolbar effect
        ParallaxEffect toolbarEffect = new ParallaxEffect(toolbar, 1);
        toolbarEffect.setParallaxCondition(new ParallaxEffect.ParallaxCondition(){
            @Override
            protected boolean doParallax(){
                int height = (int)((CompassUtil.getScreenWidth(MaterialActivity.this) * 2 / 3) * 0.6);
                return -getRecyclerViewOffset() > height;
            }

            @Override
            protected int getParallaxViewOffset(){
                int height = (int)((CompassUtil.getScreenWidth(MaterialActivity.this) * 2 / 3) * 0.6);
                return height + getFixedState() + getRecyclerViewOffset();
            }
        });
        mRecyclerView.addOnScrollListener(toolbarEffect);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID){
        /* no-op */
        //This and the two following methods are overriden and empty to prevent
        //  the programmer from changing the default layout of this activity
    }

    @Override
    public void setContentView(View view){
        /* no-op */
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params){
        /* no-op */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        return super.onCreateOptionsMenu(menu);
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

    protected final void setColor(int color){
        mHeaderContainer.setBackgroundColor(color);
        mFAB.setColorNormal(color);
        mFAB.setColorPressed(color);
    }

    protected final void setAdapter(MaterialAdapter adapter){
        mRecyclerView.setAdapter(adapter);
    }

    protected final View inflateHeader(@LayoutRes int layoutResId){
        return LayoutInflater.from(this).inflate(layoutResId, mHeaderContainer);
    }

    protected final void setFAB(@IdRes int id, View.OnClickListener listener){
        mFAB.setId(id);
        mFAB.setOnClickListener(listener);
        mFAB.setVisibility(View.VISIBLE);
    }
}
