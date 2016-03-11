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
public class SearchAdapter extends RecyclerView.Adapter{
    private static final int TYPE_RESULT = 0;
    private static final int TYPE_CREATE = TYPE_RESULT+1;

    private Context mContext;
    private SearchAdapterListener mListener;
    private List<SearchResult> mDataSet;
    private boolean mShowCreateButton;


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

    public void updateDataSet(@NonNull List<SearchResult> dataSet, boolean showCreateButton){
        mDataSet = dataSet;
        mShowCreateButton = showCreateButton;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position){
        if (position == mDataSet.size()){
            return TYPE_CREATE;
        }
        return TYPE_RESULT;
    }

    @Override
    public int getItemCount(){
        return mDataSet.size() + (mShowCreateButton ? 1 : 0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == TYPE_RESULT){
            return new ResultViewHolder(inflater.inflate(R.layout.item_search_result, parent, false));
        }
        else{
            return new CreateViewHolder(inflater.inflate(R.layout.item_search_create, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
        if (position < mDataSet.size()){
            ResultViewHolder holder = (ResultViewHolder)rawHolder;
            holder.mTitle.setText(mDataSet.get(position).getTitle());
            String highlight = mDataSet.get(position).getHighlighted().replace("\n", "<br>");
            holder.mSummary.setText(Html.fromHtml(highlight));
        }
    }


    /**
     * Holder for a search result interface.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    class ResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitle;
        private TextView mSummary;


        /**
         * Constructor.
         *
         * @param rootView the root view.
         */
        public ResultViewHolder(View rootView){
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


    class CreateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public CreateViewHolder(View rootView){
            super(rootView);
            rootView.findViewById(R.id.search_create).setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            mListener.onCreateCustomGoalSelected();
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

        /**
         * Called when the 'I don't see my goal' button is selected.
         */
        void onCreateCustomGoalSelected();
    }
}
