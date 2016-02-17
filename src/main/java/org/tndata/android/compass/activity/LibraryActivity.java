package org.tndata.android.compass.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.SearchView;

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
        implements
                MenuItemCompat.OnActionExpandListener,
                SearchView.OnQueryTextListener,
                SearchView.OnCloseListener{

    private Toolbar mToolbar;
    private MenuItem mSearchItem;
    private SearchView mSearchView;
    private Filter mFilter;

    private ParallaxEffect mParallax;
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
        mToolbar.setAlpha(0);

        mHeaderContainer = (FrameLayout)findViewById(R.id.library_header_container);
        mRecyclerView = (RecyclerView)findViewById(R.id.library_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addOnScrollListener(new ParallaxEffect(mHeaderContainer, 0.5f));
    }

    @Override
    public final boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        mSearchItem = menu.findItem(R.id.filter);
        MenuItemCompat.setOnActionExpandListener(mSearchItem, this);

        mSearchView = (SearchView)mSearchItem.getActionView();
        mSearchView.setIconified(false);
        mSearchView.setOnCloseListener(this);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.clearFocus();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public final boolean onMenuItemActionExpand(MenuItem item){
        mSearchView.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        return true;
    }

    @Override
    public final boolean onMenuItemActionCollapse(MenuItem item){
        mSearchView.setQuery("", false);
        mSearchView.clearFocus();
        return true;
    }

    @Override
    public final boolean onClose(){
        mSearchItem.collapseActionView();
        return true;
    }

    @Override
    public final boolean onQueryTextSubmit(String query){
        return false;
    }

    @Override
    public final boolean onQueryTextChange(String newText){
        Log.d("Search", newText);
        mFilter.filter(newText);
        return false;
    }

    protected final void setColor(int color){
        mToolbar.setBackgroundColor(color);
        mHeaderContainer.setBackgroundColor(color);
    }

    protected final void setAdapter(RecyclerView.Adapter adapter){
        mRecyclerView.setAdapter(adapter);
    }

    protected final void setFilter(Filter filter){
        mFilter = filter;
    }

    protected final View inflateHeader(@LayoutRes int layoutResId){
        return LayoutInflater.from(this).inflate(layoutResId, mHeaderContainer);
    }
}
