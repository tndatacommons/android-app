package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.model.UserBehavior;
import org.tndata.android.compass.model.UserGoal;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by isma on 2/24/16.
 */
public class ReviewActionsAdapter extends RecyclerView.Adapter{
    private static final int TYPE_BLANK = 0;
    private static final int TYPE_CONTENT = TYPE_BLANK+1;
    private static final int TYPE_LOAD = TYPE_CONTENT+1;
    private static final int ITEM_COUNT = TYPE_LOAD+1;


    private Context mContext;
    private UserGoal mUserGoal;
    private UserBehavior mUserBehavior;

    List<UserAction> mActions;


    public ReviewActionsAdapter(Context context, UserGoal userGoal){
        mContext = context;
        mUserGoal = userGoal;
        mUserBehavior = null;

        mActions = new ArrayList<>();
    }

    public ReviewActionsAdapter(Context context, UserBehavior userBehavior){
        mContext = context;
        mUserGoal = null;
        mUserBehavior = userBehavior;

        mActions = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position){
        if (position == 0){
            return TYPE_BLANK;
        }
        else if (position == 1){
            if (mActions.isEmpty()){
                return TYPE_LOAD;
            }
            return TYPE_CONTENT;
        }
        return TYPE_LOAD;
    }

    @Override
    public int getItemCount(){
        if (mActions.isEmpty()){
            return ITEM_COUNT-1;
        }
        return ITEM_COUNT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){

    }
}
