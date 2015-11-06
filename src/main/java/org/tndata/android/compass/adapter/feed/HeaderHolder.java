package org.tndata.android.compass.adapter.feed;

import android.view.View;
import android.widget.TextView;

import org.tndata.android.compass.R;


/**
 * View holder for a header view.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
class HeaderHolder extends MainFeedViewHolder{
    TextView mTitle;


    /**
     * Constructor.
     *
     * @param adapter a reference to the adapter that will handle the holder.
     * @param rootView the root view held by this holder.
     */
    HeaderHolder(MainFeedAdapter adapter, View rootView){
        super(adapter, rootView);
        mTitle = (TextView)rootView.findViewById(R.id.header_title);
    }
}
