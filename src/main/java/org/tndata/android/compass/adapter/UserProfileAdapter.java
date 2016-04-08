package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.User;


public class UserProfileAdapter extends RecyclerView.Adapter<UserProfileAdapter.ViewHolder>{
    private Context mContext;
    private User mUser;
    private UserProfileAdapterListener mListener;


    public UserProfileAdapter(@NonNull Context context, @NonNull User user,
                              @NonNull UserProfileAdapterListener listener){

        mContext = context;
        mUser = user;
        mListener = listener;
    }

    @Override
    public int getItemCount(){
        return User.ITEM_COUNT;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new ViewHolder(inflater.inflate(R.layout.item_profile_question, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        holder.bind(mUser.getStatement(mContext, position));
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mQuestion;


        public ViewHolder(View rootView){
            super(rootView);

            mQuestion = (TextView)rootView.findViewById(R.id.profile_item);

            rootView.setOnClickListener(this);
        }

        public void bind(String item){
            mQuestion.setText(item);
        }

        @Override
        public void onClick(View v){
            mListener.onQuestionSelected(getAdapterPosition());
        }
    }


    public interface UserProfileAdapterListener{
        void onQuestionSelected(int index);
    }
}
