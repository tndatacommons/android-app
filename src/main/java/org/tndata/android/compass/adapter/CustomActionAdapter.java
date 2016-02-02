package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.CustomAction;

import java.util.List;


/**
 * Created by isma on 2/1/16.
 */
public class CustomActionAdapter extends RecyclerView.Adapter<CustomActionAdapter.ActionHolder>{
    private Context mContext;
    private CustomActionAdapterListener mListener;
    private List<CustomAction> mCustomActions;

    private String mNewActionTitle;


    public CustomActionAdapter(Context context, CustomActionAdapterListener listener,
                               List<CustomAction> customActions){
        mContext = context;
        mListener = listener;
        mCustomActions = customActions;
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

    public void addCustomAction(CustomAction customAction){
        mCustomActions.add(customAction);
        notifyItemInserted(mCustomActions.size() - 1);
        notifyItemChanged(mCustomActions.size());
    }


    class ActionHolder extends RecyclerView.ViewHolder implements View.OnClickListener, TextWatcher{
        private TextView mTitle;
        private Button mAction;


        public ActionHolder(View rootView){
            super(rootView);

            mTitle = (TextView)rootView.findViewById(R.id.custom_action_title);
            mAction = (Button)rootView.findViewById(R.id.custom_action_action);
            mAction.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            switch (v.getId()){
                case R.id.custom_action_title:
                    mListener.onEditTrigger(mCustomActions.get(getAdapterPosition()));
                    break;

                case R.id.custom_action_action:
                    if (getAdapterPosition() < mCustomActions.size()){
                        CustomAction customAction = mCustomActions.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        mListener.onRemoveClicked(customAction);
                    }
                    else{
                        mAction.setEnabled(false);
                        mListener.onAddClicked(new CustomAction(mTitle.getText().toString().trim()));
                        mNewActionTitle = "";
                        mTitle.setEnabled(false);
                        mTitle.setOnClickListener(this);
                    }
            }
        }

        public void bind(@Nullable CustomAction customAction){
            if (customAction == null){
                mTitle.setText(mNewActionTitle);
                mTitle.setFocusable(true);
                mTitle.setOnClickListener(null);
                mTitle.setSelected(true);
                mAction.setText("Add");
                mAction.setEnabled(true);
                recordTitle(true);
            }
            else{
                mTitle.setText(customAction.getTitle());
                mTitle.setFocusable(false);
                mTitle.setOnClickListener(this);
                mAction.setText("Remove");
                mAction.setEnabled(true);
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
        void onAddClicked(CustomAction customAction);
        void onRemoveClicked(CustomAction customAction);
        void onEditTrigger(CustomAction customAction);
    }
}
