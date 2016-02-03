package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.CustomAction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Adapter to display and manage a list of custom actions.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CustomActionAdapter extends RecyclerView.Adapter<CustomActionAdapter.ActionHolder>{
    private Context mContext;
    private CustomActionAdapterListener mListener;
    private List<CustomAction> mCustomActions;
    private Set<Long> mEditing;

    private String mNewActionTitle;


    public CustomActionAdapter(Context context, CustomActionAdapterListener listener,
                               List<CustomAction> customActions){
        mContext = context;
        mListener = listener;
        mCustomActions = customActions;
        mEditing = new HashSet<>();

        mNewActionTitle = "";
    }


    @Override
    public ActionHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new ActionHolder(inflater.inflate(R.layout.item_custom_action, parent, false));
    }

    @Override
    public void onBindViewHolder(ActionHolder holder, int position){
        if (position == mCustomActions.size()){
            holder.bind(null);
        }
        else{
            holder.bind(mCustomActions.get(position));
        }
    }

    @Override
    public int getItemCount(){
        return mCustomActions.size()+1;
    }

    public void customActionAdded(){
        notifyItemInserted(mCustomActions.size() - 1);
        notifyItemChanged(mCustomActions.size());
    }


    class ActionHolder extends RecyclerView.ViewHolder implements View.OnClickListener, TextWatcher{
        private EditText mTitle;

        private ImageView mEditAction;
        private ImageView mSaveAction;
        private ImageView mAddAction;
        private ImageView mDeleteAction;


        public ActionHolder(View rootView){
            super(rootView);

            mTitle = (EditText)rootView.findViewById(R.id.custom_action_title);
            mEditAction = (ImageView)rootView.findViewById(R.id.custom_action_edit);
            mSaveAction = (ImageView)rootView.findViewById(R.id.custom_action_save);
            mAddAction = (ImageView)rootView.findViewById(R.id.custom_action_add);
            mDeleteAction = (ImageView)rootView.findViewById(R.id.custom_action_delete);

            mEditAction.setOnClickListener(this);
            mSaveAction.setOnClickListener(this);
            mAddAction.setOnClickListener(this);
            mDeleteAction.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            switch (v.getId()){
                case R.id.custom_action_title:
                    mListener.onEditTrigger(mCustomActions.get(getAdapterPosition()));
                    break;

                case R.id.custom_action_edit:
                    mEditing.add(mCustomActions.get(getAdapterPosition()).getId());

                    mTitle.setOnClickListener(null);
                    mTitle.setFocusable(true);
                    mTitle.setFocusableInTouchMode(true);
                    mTitle.requestFocus();
                    mTitle.setSelection(mTitle.getText().length());
                    InputMethodManager imm = (InputMethodManager)mContext
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                    mEditAction.setVisibility(View.GONE);
                    mSaveAction.setVisibility(View.VISIBLE);
                    break;

                case R.id.custom_action_save:
                    String newTitle = mTitle.getText().toString().trim();
                    if (newTitle.length() > 0){
                        CustomAction customAction = mCustomActions.get(getAdapterPosition());
                        mEditing.remove(customAction.getId());
                        if (!customAction.getTitle().equals(newTitle)){
                            customAction.setTitle(newTitle);
                            mListener.onSaveAction(customAction);
                        }
                        InputMethodManager imm2 = (InputMethodManager)mContext
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm2.hideSoftInputFromWindow(mTitle.getWindowToken(), 0);
                        mTitle.setOnClickListener(this);
                        mTitle.clearFocus();
                        mTitle.setFocusable(false);

                        mEditAction.setVisibility(View.VISIBLE);
                        mSaveAction.setVisibility(View.GONE);
                    }
                    break;

                case R.id.custom_action_add:
                    if (mNewActionTitle.length() > 0){
                        mAddAction.setEnabled(false);
                        mListener.onAddClicked(new CustomAction(mTitle.getText().toString().trim()));
                        mNewActionTitle = "";
                        mTitle.setEnabled(false);
                        mTitle.setOnClickListener(this);
                    }
                    break;

                case R.id.custom_action_delete:
                    CustomAction customAction = mCustomActions.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    mListener.onRemoveClicked(customAction);
                    break;
            }
        }

        public void bind(@Nullable CustomAction customAction){
            if (customAction == null){
                mTitle.setText(mNewActionTitle);
                mTitle.setFocusable(true);
                mTitle.setOnClickListener(null);
                mTitle.setSelected(true);
                mAddAction.setEnabled(true);
                mEditAction.setVisibility(View.GONE);
                mSaveAction.setVisibility(View.GONE);
                mAddAction.setVisibility(View.VISIBLE);
                mDeleteAction.setVisibility(View.GONE);
                recordTitle(true);
            }
            else{
                mTitle.setText(customAction.getTitle());
                mTitle.setFocusable(false);
                mTitle.setOnClickListener(this);
                if (mEditing.contains(customAction.getId())){
                    mEditAction.setVisibility(View.GONE);
                    mSaveAction.setVisibility(View.VISIBLE);
                }
                else{
                    mEditAction.setVisibility(View.VISIBLE);
                    mSaveAction.setVisibility(View.GONE);
                }
                mAddAction.setVisibility(View.GONE);
                mDeleteAction.setVisibility(View.VISIBLE);
                recordTitle(false);
            }
        }

        private void recordTitle(boolean enabled){
            mTitle.removeTextChangedListener(this);
            if (enabled){
                mTitle.addTextChangedListener(this);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after){
            //Unused
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count){
            mNewActionTitle = s.toString();
        }

        @Override
        public void afterTextChanged(Editable s){
            //Unused
        }
    }


    public interface CustomActionAdapterListener{
        void onSaveAction(CustomAction customAction);
        void onAddClicked(CustomAction customAction);
        void onRemoveClicked(CustomAction customAction);
        void onEditTrigger(CustomAction customAction);
    }
}
