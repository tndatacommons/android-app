package org.tndata.android.compass.adapter.feed;

import android.view.View;

import org.tndata.android.compass.R;


/**
 * View holder for footer cards.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
class FooterHolder extends MainFeedViewHolder implements View.OnClickListener{
    /**
     * Constructor.
     *
     * @param adapter a reference to the adapter that will handle the holder.
     * @param rootView the root view held by this holder.
     */
    public FooterHolder(MainFeedAdapter adapter, View rootView){
        super(adapter, rootView);

        rootView.findViewById(R.id.footer_more).setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        mAdapter.more(getAdapterPosition());
    }
}
