package org.tndata.android.compass.adapter.feed;

import android.support.v7.widget.RecyclerView;
import android.view.View;


/**
 * Parent class of all the view holders in for the main feed adapter. Provides a reference
 * to the adapter that needs to be passed through the constructor.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
abstract class MainFeedViewHolder extends RecyclerView.ViewHolder{
    protected MainFeedAdapter mAdapter;

    /**
     * Constructor,
     *
     * @param adapter a reference to the adapter that will handle the holder.
     * @param rootView the root view of this adapter.
     */
    protected MainFeedViewHolder(MainFeedAdapter adapter, View rootView){
        super(rootView);
        mAdapter = adapter;
    }
}
