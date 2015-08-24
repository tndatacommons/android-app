package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.activity.SourcesActivity.Source;

import java.util.List;


/**
 * Adapter for the source list.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class SourcesAdapter extends RecyclerView.Adapter<SourcesAdapter.SourcesViewHolder>{
    private final Context mContext;
    private final List<Source> mSources;
    private final OnSourceClickListener mListener;


    /**
     * Constructor.
     *
     * @param context the context.
     * @param sources the list of sources to be displayed.
     * @param listener the listener interface.
     */
    public SourcesAdapter(Context context, List<Source> sources, OnSourceClickListener listener){
        mContext = context;
        mSources = sources;
        mListener = listener;
    }

    /**
     * Helper method to retrieve an item from the backing array.
     *
     * @param position the position of the item in the backing array.
     * @return the requested item if the position is valid,
     */
    private Source getItem(int position){
        return mSources.get(position);
    }

    @Override
    public SourcesViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new SourcesViewHolder(inflater.inflate(R.layout.item_sources_source, parent, false));
    }

    @Override
    public void onBindViewHolder(SourcesViewHolder holder, int position){
        Source source = getItem(position);

        holder.mCaption.setText(source.getCaption());
        holder.mUrl.setText(source.getUrl());
    }

    @Override
    public int getItemCount(){
        return mSources.size();
    }

    /**
     * Handles click events from the items in the holder.
     *
     * @param position the position clicked.
     */
    private void onItemClick(int position){
        String url = getItem(position).getUrl();
        if (url != null && !url.isEmpty()){
            mListener.onSourceClick(url);
        }
    }


    /**
     * View holder for a source.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    class SourcesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mCaption;
        private TextView mUrl;


        /**
         * Constructor.
         *
         * @param rootView the root view of the item held by the holder.
         */
        public SourcesViewHolder(View rootView){
            super(rootView);

            mCaption = (TextView)rootView.findViewById(R.id.source_caption);
            mUrl = (TextView)rootView.findViewById(R.id.source_url);

            rootView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            onItemClick(getAdapterPosition());
        }
    }


    /**
     * Listener interface for source click events.
     */
    public interface OnSourceClickListener{
        /**
         * Called when a source with a string in the url field is called.
         *
         * @param url the url of the source.
         */
        void onSourceClick(String url);
    }
}
