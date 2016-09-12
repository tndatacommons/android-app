package org.tndata.android.compass.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;

import org.tndata.android.compass.R;
import org.tndata.android.compass.databinding.CardContentBinding;
import org.tndata.android.compass.databinding.CardDetailBinding;
import org.tndata.android.compass.databinding.CardGoalBinding;
import org.tndata.android.compass.holder.ContentCardHolder;
import org.tndata.android.compass.holder.DetailCardHolder;
import org.tndata.android.compass.holder.GoalCardHolder;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.util.CompassUtil;


/**
 * Created by isma on 9/9/16.
 */
public class NewActionAdapter extends RecyclerView.Adapter{
    private static final int TYPE_BLANK = 0;
    private static final int TYPE_GOAL = TYPE_BLANK+1;
    private static final int TYPE_CONTENT = TYPE_GOAL+1;
    private static final int TYPE_DETAIL = TYPE_CONTENT+1;


    private Context mContext;
    private Action mAction;


    public NewActionAdapter(Context context){
        mContext = context;
    }

    @Override
    public int getItemViewType(int position){
        if (position == 0){
            return TYPE_BLANK;
        }
        else if (position == 1){
            return TYPE_GOAL;
        }
        else if (position == 2){
            return TYPE_CONTENT;
        }
        else{
            return TYPE_DETAIL;
        }
    }

    @Override
    public int getItemCount(){
        return 4;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (viewType == TYPE_BLANK){
            return new RecyclerView.ViewHolder(new Space(mContext)){};
        }
        else if (viewType == TYPE_GOAL){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            CardGoalBinding binding = DataBindingUtil.inflate(
                    inflater, R.layout.card_goal, parent, false
            );
            return new GoalCardHolder(binding);
        }
        else if (viewType == TYPE_CONTENT){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            CardContentBinding binding = DataBindingUtil.inflate(
                    inflater, R.layout.card_content, parent, false
            );
            return new ContentCardHolder(binding);
        }
        else if (viewType == TYPE_DETAIL){
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
                        (int)((width*2/3)*0.8)
                );
                rawHolder.itemView.setLayoutParams(params);
                rawHolder.itemView.setVisibility(View.INVISIBLE);
                break;

            case TYPE_CONTENT:

                break;
        }
    }
}
