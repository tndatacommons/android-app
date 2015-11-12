package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.SearchResult;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by isma on 11/12/15.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{
    private Context mContext;
    private List<SearchResult> mDataSet;


    public SearchAdapter(@NonNull Context context){
        mContext = context;
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
    }

    @Override
    public int getItemCount(){
        return mDataSet.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mTitle;


        public ViewHolder(View rootView){
            super(rootView);
            mTitle = (TextView)rootView.findViewById(R.id.search_result_title);
        }
    }
}
