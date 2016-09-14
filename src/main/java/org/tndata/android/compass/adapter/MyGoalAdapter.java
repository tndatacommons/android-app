package org.tndata.android.compass.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;

import org.tndata.android.compass.R;
import org.tndata.android.compass.databinding.CardDetailBinding;
import org.tndata.android.compass.holder.DetailCardHolder;
import org.tndata.android.compass.model.UserGoal;
import org.tndata.android.compass.util.CompassUtil;


/**
 * Created by isma on 9/14/16.
 */
public class MyGoalAdapter extends RecyclerView.Adapter{
    private static final int TYPE_BLANK = 0;
    private static final int TYPE_GOAL = TYPE_BLANK+1;


    private Context mContext;
    private UserGoal mUserGoal;


    public MyGoalAdapter(Context context, UserGoal userGoal){
        mContext = context;
        mUserGoal = userGoal;
    }

    @Override
    public int getItemViewType(int position){
        switch (position){
            case 0: return TYPE_BLANK;
            case 1: return TYPE_GOAL;
            default: return TYPE_GOAL;
        }
    }

    @Override
    public int getItemCount(){
        return 2;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (viewType == TYPE_BLANK){
            return new RecyclerView.ViewHolder(new Space(mContext)){};
        }
        else if (viewType == TYPE_GOAL){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            CardDetailBinding binding = DataBindingUtil.inflate(
                    inflater, R.layout.card_detail, parent, false
            );
            return new DetailCardHolder(binding);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
        switch (getItemViewType(position)){
            case TYPE_BLANK:
                int width = CompassUtil.getScreenWidth(mContext);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        (int)((width * 2 / 3) * 0.8)
                );
                rawHolder.itemView.setLayoutParams(params);
                rawHolder.itemView.setVisibility(View.INVISIBLE);
                break;

            case TYPE_GOAL:
                DetailCardHolder goalHolder = (DetailCardHolder)rawHolder;
                goalHolder.setTitle(mUserGoal.getTitle());
                goalHolder.setContent(mUserGoal.getDescription());
                break;
        }
    }
}
