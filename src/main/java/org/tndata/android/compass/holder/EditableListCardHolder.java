package org.tndata.android.compass.holder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.databinding.ButtonIconBinding;
import org.tndata.android.compass.databinding.CardEditableListBinding;
import org.tndata.android.compass.databinding.ItemEditableListEntryBinding;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by isma on 9/14/16.
 */
public class EditableListCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private CardEditableListBinding mBinding;
    private Listener mListener;
    private List<String> mDataset;
    private List<ButtonSpec> mButtons;
    private EditableListAdapter mAdapter;


    public EditableListCardHolder(@NonNull CardEditableListBinding binding,
                                  @NonNull Listener listener, @NonNull List<String> dataset){

        this(binding, listener, dataset, new ArrayList<ButtonSpec>());
    }

    public EditableListCardHolder(@NonNull CardEditableListBinding binding,
                                  @NonNull Listener listener, @NonNull List<String> dataset,
                                  @NonNull List<ButtonSpec> buttons){

        super(binding.getRoot());
        mBinding = binding;
        mListener = listener;
        mDataset = dataset;
        mButtons = buttons;
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


    private class ItemHolder
            extends RecyclerView.ViewHolder
            implements
                    TextView.OnEditorActionListener,
                    View.OnClickListener,
                    DialogInterface.OnClickListener{

        private ItemEditableListEntryBinding mBinding;

        private Drawable mTitleDefaultBackground;


        public ItemHolder(ItemEditableListEntryBinding binding){
            super(binding.getRoot());
            mBinding = binding;
            mBinding.editableListEntryEdit.setOnClickListener(this);
            mBinding.editableListEntrySave.setOnClickListener(this);

            mTitleDefaultBackground = mBinding.editableListEntryTitle.getBackground();

            mBinding.editableListEntryTitle.setFocusable(false);
            mBinding.editableListEntryTitle.setBackgroundResource(0);

            LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
            for (ButtonSpec button:mButtons){
                ButtonIconBinding buttonBinding = DataBindingUtil.inflate(
                        inflater, R.layout.button_icon, mBinding.editableListEntryButtons, true
                );
                buttonBinding.buttonIcon.setId(button.mId);
                buttonBinding.buttonIcon.setImageResource(button.mIcon);
                buttonBinding.buttonIcon.setOnClickListener(this);
            }
        }

        public void setTitle(String title){
            mBinding.editableListEntryTitle.setText(title);
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
            if (actionId == EditorInfo.IME_ACTION_DONE){
                if (mBinding.editableListEntryTitle.getText().toString().trim().length() > 0){
                    onClick(mBinding.editableListEntrySave);
                    return true;
                }
            }
            return false;
        }

        @Override
        @SuppressWarnings("deprecation")
        public void onClick(DialogInterface dialog, int which){
            //Edit the title
            if (which == 0){
                EditText title = mBinding.editableListEntryTitle;
                //Set the title click listener to null to avoid going into the trigger editor
                title.setOnClickListener(null);
                //Background and focus
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                    title.setBackground(mTitleDefaultBackground);
                }
                else{
                    title.setBackgroundDrawable(mTitleDefaultBackground);
                }
                title.setFocusable(true);
                title.setFocusableInTouchMode(true);
                title.requestFocus();
                //Put the cursor at the end and open the keyboard
                title.setSelection(title.getText().length());
                InputMethodManager imm = (InputMethodManager)itemView.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                //Switch the edit button for the save button
                mBinding.editableListEntryEdit.setVisibility(View.GONE);
                mBinding.editableListEntryButtons.setVisibility(View.GONE);
                mBinding.editableListEntrySave.setVisibility(View.VISIBLE);
            }
            //Remove the action
            else if (which == 1){
                mListener.onDeleteItem(getAdapterPosition());
                mDataset.remove(getAdapterPosition());
                mAdapter.notifyItemRemoved(getAdapterPosition());
            }
        }

        @Override
        public void onClick(View view){
            switch (view.getId()){
                //When the user enters edition mode
                case R.id.editable_list_entry_edit:
                    AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                            .setTitle(R.string.editable_list_edit_dialog_title)
                            .setItems(R.array.editable_list_edit_dialog_options, this)
                            .create();
                    dialog.show();
                    break;

                //When the user saves a currently existing goal (only from edition)
                case R.id.editable_list_entry_save:
                    EditText title = mBinding.editableListEntryTitle;
                    //Grab the title and check it ain't empty
                    String newTitle = title.getText().toString().trim();
                    if (newTitle.length() > 0){
                        mListener.onEditItem(getAdapterPosition(), newTitle);

                        //Hide the keyboard and make the title not focusable
                        InputMethodManager imm2 = (InputMethodManager)itemView.getContext()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm2.hideSoftInputFromWindow(title.getWindowToken(), 0);
                        title.clearFocus();
                        title.setFocusable(false);
                        title.setBackgroundResource(0);
                        //Set the click listener again to start the trigger editor if the user taps
                        title.setOnClickListener(this);

                        //Swap the save button for the edit button
                        mBinding.editableListEntryButtons.setVisibility(View.VISIBLE);
                        mBinding.editableListEntryEdit.setVisibility(View.VISIBLE);
                        mBinding.editableListEntrySave.setVisibility(View.GONE);
                    }
                    break;

                case R.id.editable_list_entry_title:
                    mListener.onItemClick(getAdapterPosition());
                    break;

                default:
                    mListener.onButtonClick(view, getAdapterPosition());
            }
        }
    }


    public static class ButtonSpec{
        private final int mId;
        private final int mIcon;


        public ButtonSpec(@IdRes int id, @DrawableRes int icon){
            mId = id;
            mIcon = icon;
        }
    }


    public interface Listener{
        void onCreateItem(String title);
        void onEditItem(int row, String newTitle);
        void onDeleteItem(int row);
        void onItemClick(int index);
        void onButtonClick(View view, int index);
    }
}
