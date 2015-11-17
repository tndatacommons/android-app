package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.SearchResult;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter for the search interface.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{
    private Context mContext;
    private SearchAdapterListener mListener;
    private List<SearchResult> mDataSet;


    /**
     * Constructor.
     *
     * @param context a reference to the context.
     * @param listener the listener object.
     */
    public SearchAdapter(@NonNull Context context, @NonNull SearchAdapterListener listener){
        mContext = context;
        mListener = listener;
        mDataSet = new ArrayList<>();
    }

    public void updateDataSet(@NonNull List<SearchResult> dataSet){
        mDataSet = dataSet;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new ViewHolder(inflater.inflate(R.layout.item_search_result, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        holder.mTitle.setText(mDataSet.get(position).getTitle());
        String highlight = mDataSet.get(position).getHighlighted().replace("\n", "<br>");
        //highlight = highlight.replace("<em>", "<u><span style=\"background-color: yellow\">");
        //highlight = highlight.replace("</em>", "</span></u>");
        holder.mSummary.setText(Html.fromHtml(highlight));
    }

    @Override
    public int getItemCount(){
        return mDataSet.size();
    }


    /**
     * Holder for a search result interface.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitle;
        private TextView mSummary;


        /**
         * Constructor.
         *
         * @param rootView the root view.
         */
        public ViewHolder(View rootView){
            super(rootView);
            mTitle = (TextView)rootView.findViewById(R.id.search_result_title);
            mSummary = (TextView)rootView.findViewById(R.id.search_result_summary);
            rootView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            mListener.onSearchResultSelected(mDataSet.get(getAdapterPosition()));
        }
    }


    /**
     * Listener interface for the search adapter.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface SearchAdapterListener{
        /**
         * Called when an item is tapped.
         *
         * @param result the result tapped.
         */
        void onSearchResultSelected(SearchResult result);
    }
}
