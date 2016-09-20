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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            ViewGroup target = (ViewGroup)itemView.getRootView();
            Transition transition = new ChangeBounds();
            transition.setDuration(500);
            TransitionManager.beginDelayedTransition(target, transition);
        }
        int start = mAdapter.getDynamicListItemCount() - count;
        mRecyclerViewAdapter.notifyItemRangeInserted(start, count);
        mBinding.dynamicListMoreProgress.setVisibility(View.INVISIBLE);
        mBinding.dynamicListMore.setVisibility(View.VISIBLE);
    }

    public void notifyItemRemoved(int position){
        mRecyclerViewAdapter.notifyItemRemoved(position);
    }

    public void notifyDataSetChanged(){
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    public void hideLoadMore(){
        mBinding.dynamicListMore.setVisibility(View.VISIBLE);
        mBinding.dynamicListMoreProgress.setVisibility(View.INVISIBLE);
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

    private class Adapter extends RecyclerView.Adapter<DynamicItemHolder>{
        @Override
        public int getItemCount(){
            return mAdapter.getDynamicListItemCount();
        }

        @Override
        public int getItemViewType(int position){
            return mAdapter.getDynamicListViewType(position);
        }

        @Override
        public DynamicItemHolder onCreateViewHolder(ViewGroup parent, int viewType){
            DynamicItemHolder holder = mAdapter.onCreateDynamicListViewHolder(parent, viewType);
            holder.setAdapter(mAdapter);
            return holder;
        }

        @Override
        public void onBindViewHolder(DynamicItemHolder holder, int position){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                Transition transition = new AutoTransition();
                transition.setDuration(10);
                TransitionManager.beginDelayedTransition((ViewGroup)holder.itemView, transition);
            }
            mAdapter.onBindDynamicListViewHolder(holder, position);
        }
    }


    public static abstract class DynamicItemHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private DynamicListAdapter mAdapter;

        DynamicItemHolder(View rootView){
            super(rootView);
            itemView.setOnClickListener(this);
        }

        private void setAdapter(DynamicListAdapter adapter){
            mAdapter = adapter;
        }

        @Override
        public void onClick(View view){
            mAdapter.onDynamicListItemClick(getAdapterPosition());
        }
    }


    public interface DynamicListAdapter{
        int getDynamicListItemCount();
        int getDynamicListViewType(int position);
        DynamicItemHolder onCreateDynamicListViewHolder(ViewGroup parent, int viewType);
        void onBindDynamicListViewHolder(DynamicItemHolder holder, int position);
        void onDynamicListItemClick(int position);
        void onDynamicListLoadMore();
    }
}
