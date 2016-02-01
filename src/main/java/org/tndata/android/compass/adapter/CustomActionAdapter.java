package org.tndata.android.compass.adapter;

import android.content.Context;
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
        if (position < mCustomActions.size()){
            holder.mTitle.setText(mCustomActions.get(position).getTitle());
            holder.mAction.setText("Remove");
        }
        else{
            holder.mTitle.setText(mNewActionTitle);
            holder.mAction.setText("Add");
        }
    }

    @Override
    public int getItemCount(){
        return mCustomActions.size()+1;
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
            if (getAdapterPosition() < mCustomActions.size()){
                mListener.onRemoveClicked(mCustomActions.get(getAdapterPosition()));
            }
            else{
                mListener.onAddClicked(new CustomAction(mTitle.getText().toString().trim()));
                mNewActionTitle = "";
            }
        }

        public void recordTitle(boolean enabled){
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
    }
}
