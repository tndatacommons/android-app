package org.tndata.android.compass.holder;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.Visibility;
import android.view.View;
import android.view.ViewGroup;

import org.tndata.android.compass.R;
import org.tndata.android.compass.databinding.CardDynamicListBinding;


/**
 * Generic holder for a dynamically loaded list of items with a title.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class DynamicListCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private CardDynamicListBinding mBinding;
    private DynamicListAdapter mAdapter;
    private Adapter mRecyclerViewAdapter;


    public DynamicListCardHolder(CardDynamicListBinding binding, DynamicListAdapter adapter){
        super(binding.getRoot());
        mBinding = binding;
        mAdapter = adapter;

        mRecyclerViewAdapter = new Adapter();
        mBinding.dynamicListList.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        mBinding.dynamicListList.setAdapter(mRecyclerViewAdapter);
        mBinding.dynamicListMore.setOnClickListener(this);
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

    public void notifyItemsInserted(int count){
        int start = mAdapter.getDynamicListItemCount() - count;
        mRecyclerViewAdapter.notifyItemRangeInserted(start, count);
    }

    public void notifyItemRemoved(int position){
        mRecyclerViewAdapter.notifyItemRemoved(position);
    }

    public void notifyDataSetChanged(){
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    public void hideLoadMore(){
        mBinding.dynamicListMore.setVisibility(View.VISIBLE);
        mBinding.dynamicListMoreProgress.setVisibility(View.GONE);
        mBinding.dynamicListMoreContainer.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.dynamic_list_more:
                mBinding.dynamicListMore.setVisibility(View.GONE);
                mBinding.dynamicListMoreProgress.setVisibility(View.VISIBLE);
                mAdapter.onDynamicListLoadMore();
        }
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                Transition transition = new AutoTransition();
                transition.setDuration(10);
                TransitionManager.beginDelayedTransition((ViewGroup)holder.itemView, transition);
            }
            mAdapter.onBindDynamicListViewHolder(holder, position);
        }
    }


    public interface DynamicListAdapter{
        int getDynamicListItemCount();
        int getDynamicListViewType(int position);
        RecyclerView.ViewHolder onCreateDynamicListViewHolder(ViewGroup parent, int viewType);
        void onBindDynamicListViewHolder(RecyclerView.ViewHolder holder, int position);
        void onDynamicListLoadMore();
    }
}
