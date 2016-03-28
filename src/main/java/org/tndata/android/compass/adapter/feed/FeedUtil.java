package org.tndata.android.compass.adapter.feed;

import android.view.MenuItem;
import android.view.View;

import org.tndata.android.compass.R;
import org.tndata.android.compass.ui.CompassPopupMenu;


/**
 * Utility class for the main feed.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
class FeedUtil implements CompassPopupMenu.OnMenuItemClickListener{
    private MainFeedAdapter mAdapter;


    /**
     * Constructor.
     *
     * @param adapter a reference to the adapter.
     */
    FeedUtil(MainFeedAdapter adapter){
        mAdapter = adapter;
    }

    /**
     * Display the popup menu for the suggestion.
     *
     * @param anchor the view it should be anchored to.
     */
    void showSuggestionPopup(View anchor){
        CompassPopupMenu popup = CompassPopupMenu.newInstance(mAdapter.mContext, anchor);
        popup.getMenuInflater().inflate(R.menu.popup_goal_suggestion, popup.getMenu());

        //Set the listener
        popup.setOnMenuItemClickListener(this);

        //Show the menu.
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item){
        switch (item.getItemId()){
            case R.id.popup_goal_suggestion_refresh:
                mAdapter.refreshSuggestion();
                break;
        }
        return true;
    }
}
