package org.tndata.android.compass.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.tndata.android.compass.databinding.CardEditableListBinding;


/**
 * Created by isma on 9/14/16.
 */
public class EditableListCardHolder extends RecyclerView.ViewHolder{
    private CardEditableListBinding mBinding;


    public EditableListCardHolder(CardEditableListBinding binding){
        super(binding.getRoot());
        mBinding = binding;
    }

    public void setInputHint(String hint){
        mBinding.editableListInputLayout.setHint(hint);
    }


    private class EditableListAdapter extends RecyclerView.Adapter<ItemHolder>{
        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType){
            return null;
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position){

        }

        @Override
        public int getItemCount(){
            return 0;
        }
    }


    private class ItemHolder extends RecyclerView.ViewHolder{
        public ItemHolder(View itemView){
            super(itemView);
        }
    }


    public interface Listener{
        void onCreateItem(String title);
        void onEditItem(int row, String newTitle);
        void onDeleteItem(int row);
    }
}
