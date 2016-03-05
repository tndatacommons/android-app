package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.model.UserBehavior;
import org.tndata.android.compass.model.UserGoal;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by isma on 2/24/16.
 */
public class ReviewActionsAdapter extends MaterialAdapter{
    private ReviewActionsListener mListener;
    private UserGoal mUserGoal;
    private UserBehavior mUserBehavior;

    private ReviewActionAdapter mAdapter;
    private List<Action> mActions;


    public ReviewActionsAdapter(Context context, ReviewActionsListener listener, UserGoal userGoal){
        super(context, ContentType.LIST, true);
        mListener = listener;
        mUserGoal = userGoal;
        mUserBehavior = null;
        mActions = new ArrayList<>();
    }

    public ReviewActionsAdapter(Context context, ReviewActionsListener listener,
                                UserBehavior userBehavior){

        super(context, ContentType.LIST, true);
        mListener = listener;
        mUserGoal = null;
        mUserBehavior = userBehavior;
        mActions = new ArrayList<>();
    }

    @Override
    protected boolean hasHeader(){
        return false;
    }

    @Override
    protected boolean isEmpty(){
        return mActions.isEmpty();
    }

    @Override
    protected void bindListHolder(RecyclerView.ViewHolder rawHolder){
        ListViewHolder holder = (ListViewHolder)rawHolder;
        if (mUserBehavior != null){
            holder.setTitle(getContext().getString(R.string.library_review_action_header,
                    mUserBehavior.getTitle()));
        }
        else{
            holder.setTitle(getContext().getString(R.string.library_review_action_header,
                    mUserGoal.getTitle()));
        }
        holder.setTitleColor(getContext().getResources().getColor(R.color.black));
        if (mAdapter == null){
            mAdapter = new ReviewActionAdapter();
        }
        holder.setAdapter(mAdapter);
    }

    @Override
    protected void loadMore(){
        mListener.loadMore();
    }

    public void addActions(@NonNull List<UserAction> actions, boolean showLoading){
        //If there are no actions, insert the content card
        if (mActions.isEmpty()){
            notifyListInserted();
        }
        //Update the load widget
        updateLoading(showLoading);

        //Add all the actions in the behavior list
        mActions.addAll(actions);
    }


    private class ReviewActionAdapter extends RecyclerView.Adapter<ActionViewHolder>{
        @Override
        public ActionViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View rootView = inflater.inflate(R.layout.item_review_action, parent, false);
            return new ActionViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(ActionViewHolder holder, int position){
            holder.bind(mActions.get(position));
        }

        @Override
        public int getItemCount(){
            return mActions.size();
        }
    }


    private class ActionViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private ImageView mEnabled;
        private TextView mTitle;


        public ActionViewHolder(@NonNull View rootView){
            super(rootView);

            mEnabled = (ImageView)rootView.findViewById(R.id.review_action_enabled);
            mTitle = (TextView)rootView.findViewById(R.id.review_action_title);

            rootView.setOnClickListener(this);
        }

        public void bind(@NonNull Action userAction){
            if (userAction.isTriggerEnabled()){
                mEnabled.setImageResource(R.drawable.ic_enabled_black_36dp);
            }
            else{
                mEnabled.setImageResource(R.drawable.ic_disabled_black_36dp);
            }
            mTitle.setText(userAction.getTitle());
        }

        @Override
        public void onClick(View v){
            mListener.onActionSelected(mActions.get(getAdapterPosition()));
        }
    }


    public interface ReviewActionsListener{
        void onActionSelected(Action action);
        void loadMore();
    }
}
