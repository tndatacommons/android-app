package org.tndata.android.compass.holder;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.databinding.CardEditableListBinding;
import org.tndata.android.compass.databinding.ItemEditableListEntryBinding;

import java.util.List;


/**
 * View holder for an editable list of items with the possibility of adding custom buttons.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class EditableListCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private static final String TAG = "EditableListHolder";


    private CardEditableListBinding mBinding;
    private Listener mListener;
    private List<String> mDataset;
    private int mMenu;

    private EditableListAdapter mAdapter;


    /**
     * Constructor. No additional menu items per listed item.
     *
     * @param binding the binding object associated with the holder's layout.
     * @param listener the listener for holder events.
     * @param dataset the dataset to be displayed.
     */
    public EditableListCardHolder(@NonNull CardEditableListBinding binding,
                                  @NonNull Listener listener, @NonNull List<String> dataset){

        this(binding, listener, dataset, -1);
    }

    /**
     *
     * Constructor. Used to add menu items to listed items' overflows.
     *
     * @param binding the binding object associated with the holder's layout.
     * @param listener the listener for holder events.
     * @param dataset the dataset to be displayed.
     * @param menu the resource of the menu for listed items.
     */
    public EditableListCardHolder(@NonNull CardEditableListBinding binding,
                                  @NonNull Listener listener, @NonNull List<String> dataset,
                                  @MenuRes int menu){

        super(binding.getRoot());

        mBinding = binding;
        mListener = listener;
        mDataset = dataset;
        mMenu = menu;
        mBinding.editableListCreate.setOnClickListener(this);

        mAdapter = new EditableListAdapter();
        mBinding.editableListList.setAdapter(mAdapter);
        mBinding.editableListList.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
    }

    public void setColor(@ColorInt int color){
        mBinding.editableListTitle.setBackgroundColor(color);
    }

    /**
     * Sets the title of the card.
     *
     * @param title the title to be set.
     */
    public void setTitle(String title){
        mBinding.editableListTitle.setText(title);
    }

    /**
     * Sets the title of the card.
     *
     * @param titleId the id of the resource to be set as the title of the card.
     */
    public void setTitle(@StringRes int titleId){
        mBinding.editableListTitle.setText(titleId);
    }

    /**
     * Sets the hint of the input field.
     *
     * @param hint the hint to be set.
     */
    public void setInputHint(@NonNull String hint){
        mBinding.editableListInputLayout.setHint(hint);
    }

    /**
     * Sets the hint of the input field.
     *
     * @param hintId the id of the resource to be set as the hint of the input field.
     */
    public void setInputHint(@StringRes int hintId){
        setInputHint(itemView.getContext().getString(hintId));
    }

    /**
     * Notifies the holder the input is ready to be added to the dataset.
     */
    public void addInputToDataset(){
        setEnabled(true);
        mDataset.add(mBinding.editableListInput.getText().toString().trim());
        mAdapter.notifyItemInserted(mDataset.size()-1);
        mBinding.editableListInput.setText("");
    }

    /**
     * Notifies the holder the input couldn't be added to the dataset.
     */
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

    /**
     * Enables or disabled the form.
     *
     * @param enabled true to enable, false to disable.
     */
    private void setEnabled(boolean enabled){
        mBinding.editableListCreate.setEnabled(enabled);
        mBinding.editableListInput.setEnabled(enabled);
        mBinding.editableListProgress.setVisibility(enabled ? View.GONE : View.VISIBLE);
    }


    /**
     * Adapter for the actual list of items.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class EditableListAdapter extends RecyclerView.Adapter<ItemHolder>{
        @Override
        public int getItemCount(){
            return mDataset.size();
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
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


    /**
     * View holder for an item in the list.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class ItemHolder
            extends RecyclerView.ViewHolder
            implements
                    TextView.OnEditorActionListener,
                    PopupMenu.OnMenuItemClickListener,
                    View.OnClickListener{

        private ItemEditableListEntryBinding mBinding;
        private PopupMenu mPopupMenu;

        private Drawable mTitleDefaultBackground;


        /**
         * Constructor.
         *
         * @param binding the binding object.
         */
        public ItemHolder(ItemEditableListEntryBinding binding){
            super(binding.getRoot());
            mBinding = binding;

            //Set the listeners of the main buttons and input field
            mBinding.editableListEntryOptions.setOnClickListener(this);
            mBinding.editableListEntrySave.setOnClickListener(this);
            mBinding.editableListEntryTitle.setOnClickListener(this);
            mBinding.editableListEntryTitle.setOnEditorActionListener(this);

            //Backup the default background of the input field, this is done so it can be
            //  removed and restored later on to make the EditText look like a TextView
            //  at will
            mTitleDefaultBackground = mBinding.editableListEntryTitle.getBackground();

            //By default the input field should look like a TextView
            mBinding.editableListEntryTitle.setFocusable(false);
            mBinding.editableListEntryTitle.setBackgroundResource(0);

            //Inflate the menus
            mPopupMenu = new PopupMenu(itemView.getContext(), mBinding.editableListEntryOptions);
            mPopupMenu.getMenuInflater().inflate(R.menu.menu_editable_list_item, mPopupMenu.getMenu());
            mPopupMenu.getMenuInflater().inflate(mMenu, mPopupMenu.getMenu());
            mPopupMenu.setOnMenuItemClickListener(this);
        }

        /**
         * Sets the title of the item.
         *
         * @param title the title to be set.
         */
        public void setTitle(String title){
            mBinding.editableListEntryTitle.setText(title);
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
            if (actionId == EditorInfo.IME_ACTION_DONE){
                //Allow saving only if the length ain't zero
                if (mBinding.editableListEntryTitle.getText().toString().trim().length() > 0){
                    onClick(mBinding.editableListEntrySave);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item){
            switch (item.getItemId()){
                case R.id.editable_list_item_edit:
                    edit();
                    return true;

                case R.id.editable_list_item_remove:
                    Log.d(TAG, "Delete requested on item #" + getAdapterPosition());

                    mListener.onDeleteItem(getAdapterPosition());
                    mDataset.remove(getAdapterPosition());
                    mAdapter.notifyItemRemoved(getAdapterPosition());
                    return true;

                default:
                    return mListener.onMenuItemClick(item, getAdapterPosition());
            }
        }

        @Override
        public void onClick(View view){
            switch (view.getId()){
                case R.id.editable_list_entry_options:
                    mPopupMenu.show();
                    break;

                //When the user saves a currently existing goal (only from edition)
                case R.id.editable_list_entry_save:
                    Log.d(TAG, "Save requested, item #" + getAdapterPosition());

                    EditText title = mBinding.editableListEntryTitle;
                    //Grab the title and check it ain't empty
                    String newTitle = title.getText().toString().trim();
                    if (newTitle.length() > 0){
                        mListener.onEditItem(newTitle, getAdapterPosition());

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
                        mBinding.editableListEntryOptions.setVisibility(View.VISIBLE);
                        mBinding.editableListEntrySave.setVisibility(View.GONE);
                    }
                    break;

                case R.id.editable_list_entry_title:
                    mListener.onItemClick(getAdapterPosition());
                    break;
            }
        }

        /**
         * Triggers the edition mode.
         */
        @SuppressWarnings("deprecation")
        private void edit(){
            Log.d(TAG, "Edit mode, item #" + getAdapterPosition());

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
            mBinding.editableListEntryOptions.setVisibility(View.GONE);
            mBinding.editableListEntrySave.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Listener interface for the editable list.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface Listener{
        /**
         * Called when the user taps the create button.
         *
         * @param name the name of the item introduced by the user.
         */
        void onCreateItem(String name);

        /**
         * Called when the user edits an intem currently in the list.
         *
         * @param newName the new name introduced by the user.
         * @param index the index of the item.
         */
        void onEditItem(String newName, int index);

        /**
         * Called when the user chooses to delete an item in the list.
         *
         * @param index the index of the item.
         */
        void onDeleteItem(int index);

        /**
         * Called when the user taps on the title of an item in the list.
         *
         * @param index the index of the item.
         */
        void onItemClick(int index);

        /**
         * Called when the user taps a button added dynamically.
         *
         * @param item the menu item that was tapped.
         * @param index the index of the item.
         * @return true if the event was handled, false otherwise.
         */
        boolean onMenuItemClick(MenuItem item, int index);
    }
}
