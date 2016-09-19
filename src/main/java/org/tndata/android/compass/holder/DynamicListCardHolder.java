package org.tndata.android.compass.holder;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.tndata.android.compass.databinding.CardDynamicListBinding;


/**
 * Created by isma on 9/19/16.
 */
public class DynamicListCardHolder extends RecyclerView.ViewHolder{
    private CardDynamicListBinding mBinding;
    private DynamicListAdapter mAdapter;


    public DynamicListCardHolder(CardDynamicListBinding binding, DynamicListAdapter adapter){
        super(binding.getRoot());
        mBinding = binding;
        mAdapter = adapter;

        mBinding.dynamicListList.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        mBinding.dynamicListList.setAdapter(new Adapter());
    }

    public void setTitle(@StringRes int resId){
        mBinding.dynamicListTitle.setText(resId);
    }

    public void setTitle(@NonNull CharSequence title){
        mBinding.dynamicListTitle.setText(title);
    }

    public void setLoadMoreText(@StringRes int resId){
        mBinding.dynamicListMore.setText(resId);
    }

    public void setLoadMoreText(@NonNull CharSequence text){
        mBinding.dynamicListMore.setText(text);
    }

    public void notifyItemsAdded(int count){

    }

    public void notifyItemRemoved(int position){
        
    }


    private class Adapter extends RecyclerView.Adapter{
        @Override
        public int getItemCount(){
            return mAdapter.getDynamicListItemCount();
        }

        @Override
        public int getItemViewType(int position){
            return mAdapter.getDynamicListViewType(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            return mAdapter.onCreateDynamicListViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
            mAdapter.onBindDynamicListViewHolder(holder, position);
        }
    }


    public interface DynamicListAdapter{
        int getDynamicListItemCount();
        int getDynamicListViewType(int position);
        RecyclerView.ViewHolder onCreateDynamicListViewHolder(ViewGroup parent, int viewType);
        void onBindDynamicListViewHolder(RecyclerView.ViewHolder holder, int position);
        boolean onDynamicListLoadMore();
    }
}
