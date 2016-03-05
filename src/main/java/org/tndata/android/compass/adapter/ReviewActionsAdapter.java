package org.tndata.android.compass.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.UserBehavior;
import org.tndata.android.compass.model.UserGoal;
import org.tndata.android.compass.ui.ContentContainer;
import org.tndata.android.compass.util.CompassUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by isma on 2/24/16.
 */
public class ReviewActionsAdapter extends MaterialAdapter{
    private ReviewActionsListener mListener;
    private UserGoal mUserGoal;
    private UserBehavior mUserBehavior;

    private List<Action> mActions;

    private boolean mShowLoading;
    private String mLoadError;


    public ReviewActionsAdapter(Context context, ReviewActionsListener listener, UserGoal userGoal){
        super(context, ContentType.LIST, true);
        init(listener);
        mUserGoal = userGoal;
        mUserBehavior = null;
    }

    public ReviewActionsAdapter(Context context, ReviewActionsListener listener,
                                UserBehavior userBehavior){

        super(context, ContentType.LIST, true);
        init(listener);
        mUserGoal = null;
        mUserBehavior = userBehavior;
    }

    private void init(ReviewActionsListener listener){
        mListener = listener;

        mActions = new ArrayList<>();
        mShowLoading = true;
        mLoadError = "";
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
            holder.setTitle(mUserBehavior.getTitle());
        }
        else{
            holder.setTitle(mUserGoal.getTitle());
        }
    }

    /*@Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
        //Blank space
        if (position == 0){
            int width = CompassUtil.getScreenWidth(mContext);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (int)((width*2/3)*0.8)
            );
            rawHolder.itemView.setLayoutParams(params);
            rawHolder.itemView.setVisibility(View.INVISIBLE);
        }
        //Actions or load switch
        else if (position == 1){
            if (mActions.isEmpty()){
                if (mShowLoading){
                    if (mLoadError.isEmpty()){
                        mListener.loadMore();
                    }
                    else{
                        rawHolder.itemView.findViewById(R.id.material_progress_progress).setVisibility(View.GONE);
                        TextView error = (TextView)rawHolder.itemView.findViewById(R.id.material_progress_error);
                        error.setVisibility(View.VISIBLE);
                        error.setText(mLoadError);
                    }
                }
            }
            else{
                if (mUserGoal != null){
                    ((ActionsViewHolder)rawHolder).bind(mUserGoal);
                }
                else if (mUserBehavior != null){
                    ((ActionsViewHolder)rawHolder).bind(mUserBehavior);
                }
            }
        }
    }

    public void addActions(@NonNull List<Action> actions, boolean showLoading){
        //If there are no actions, insert the content card
        if (mActions.isEmpty()){
            notifyItemInserted(TYPE_CONTENT);
        }

        //Set the new loading state
        mShowLoading = showLoading;
        //If we should no longer load, remove the load switch
        if (!mShowLoading){
            notifyItemRemoved(TYPE_LOAD);
        }
        //Otherwise, schedule an item refresh for the load switch half a second from now
        //  to avoid the load callback getting called twice
        else{
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    notifyItemChanged(TYPE_LOAD);
                }
            }, 500);
        }

        //Add all the actions in the behavior list
        mActions.addAll(actions);

        //If the holder has been created already
        if (mActionsHolder != null){
            //Add the actions
            mActionsHolder.addActions(actions);
        }
    }*/


    private class ActionViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        public ActionViewHolder(@NonNull View rootView){
            super(rootView);
        }

        @Override
        public void onClick(View v){

        }
    }


    public interface ReviewActionsListener{
        void onActionSelected(Action action);
        void loadMore();
    }
}
