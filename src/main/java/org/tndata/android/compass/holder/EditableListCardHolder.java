package org.tndata.android.compass.holder;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.tndata.android.compass.R;
import org.tndata.android.compass.databinding.CardEditableListBinding;
import org.tndata.android.compass.databinding.ItemEditableListEntryBinding;

import java.util.List;


/**
 * Created by isma on 9/14/16.
 */
public class EditableListCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private CardEditableListBinding mBinding;
    private Listener mListener;
    private List<String> mDataset;
    private EditableListAdapter mAdapter;


    public EditableListCardHolder(CardEditableListBinding binding, Listener listener, List<String> dataset){
        super(binding.getRoot());
        mBinding = binding;
        mListener = listener;
        mDataset = dataset;
        mBinding.editableListCreate.setOnClickListener(this);

        mAdapter = new EditableListAdapter(itemView.getContext());
        mBinding.editableListList.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        mBinding.editableListList.setAdapter(mAdapter);
    }

    public void setTitle(String title){
        mBinding.editableListTitle.setText(title);
    }

    public void setTitle(@StringRes int titleId){
        mBinding.editableListTitle.setText(titleId);
    }

    public void setInputHint(@NonNull String hint){
        mBinding.editableListInputLayout.setHint(hint);
    }

    public void addInputToDataset(){
        setEnabled(true);
        mDataset.add(mBinding.editableListInput.getText().toString().trim());
        mAdapter.notifyItemInserted(mDataset.size()-1);
        mBinding.editableListInput.setText("");
    }

    public void inputAdditionFailed(){
        setEnabled(true);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.editable_list_create:
                setEnabled(false);
                mListener.onCreateItem(mBinding.editableListInput.getText().toString().trim());
                break;
        }
    }

    private void setEnabled(boolean enabled){
        mBinding.editableListCreate.setEnabled(enabled);
        mBinding.editableListInput.setEnabled(enabled);
        mBinding.editableListProgress.setVisibility(enabled ? View.GONE : View.VISIBLE);
    }


    private class EditableListAdapter extends RecyclerView.Adapter<ItemHolder>{
        private Context mContext;


        private EditableListAdapter(Context context){
            mContext = context;
        }

        @Override
        public int getItemCount(){
            return mDataset.size();
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ItemEditableListEntryBinding binding = DataBindingUtil.inflate(
                    inflater, R.layout.item_editable_list_entry, parent, false
            );
            return new ItemHolder(binding);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position){
            holder.setTitle(mDataset.get(position));
        }
    }


    private class ItemHolder extends RecyclerView.ViewHolder{
        private ItemEditableListEntryBinding mBinding;


        public ItemHolder(ItemEditableListEntryBinding binding){
            super(binding.getRoot());
            mBinding = binding;
        }

        public void setTitle(String title){
            mBinding.editableListEntryTitle.setText(title);
        }
    }


    public interface Listener{
        void onCreateItem(String title);
        void onEditItem(int row, String newTitle);
        void onDeleteItem(int row);
    }
}
