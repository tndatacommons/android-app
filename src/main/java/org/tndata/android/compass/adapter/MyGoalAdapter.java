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
import org.tndata.android.compass.databinding.CardEditableListBinding;
import org.tndata.android.compass.databinding.ItemProgressFooterBinding;
import org.tndata.android.compass.holder.DetailCardHolder;
import org.tndata.android.compass.holder.EditableListCardHolder;
import org.tndata.android.compass.holder.ProgressFooterHolder;
import org.tndata.android.compass.model.CustomAction;
import org.tndata.android.compass.model.UserGoal;
import org.tndata.android.compass.util.CompassUtil;

import java.util.List;


/**
 * Created by isma on 9/14/16.
 */
public class MyGoalAdapter extends RecyclerView.Adapter{
    private static final int TYPE_BLANK = 0;
    private static final int TYPE_GOAL = TYPE_BLANK+1;
    private static final int TYPE_CUSTOM_ACTIONS = TYPE_GOAL+1;
    private static final int TYPE_FOOTER = TYPE_CUSTOM_ACTIONS+1;


    private Context mContext;
    private UserGoal mUserGoal;
    private List<CustomAction> mCustomActions;

    private ProgressFooterHolder mProgressFooterHolder;


    public MyGoalAdapter(Context context, UserGoal userGoal){
        mContext = context;
        mUserGoal = userGoal;
        mCustomActions = null;
    }

    @Override
    public int getItemViewType(int position){
        if (position == 0){
            return TYPE_BLANK;
        }
        else if (position == 1){
            return TYPE_GOAL;
        }
        else{// if (position == 2){
            if (mCustomActions == null){
                return TYPE_FOOTER;
            }
            else{
                return TYPE_CUSTOM_ACTIONS;
            }
        }
    }

    @Override
    public int getItemCount(){
        return 3;
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
        else if (viewType == TYPE_CUSTOM_ACTIONS){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            CardEditableListBinding binding = DataBindingUtil.inflate(
                    inflater, R.layout.card_editable_list, parent, false
            );
            return new EditableListCardHolder(binding);
        }
        else if (viewType == TYPE_FOOTER){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ItemProgressFooterBinding binding = DataBindingUtil.inflate(
                    inflater, R.layout.item_progress_footer, parent, false
            );
            mProgressFooterHolder = new ProgressFooterHolder(binding);
            return mProgressFooterHolder;
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

            case TYPE_CUSTOM_ACTIONS:
                EditableListCardHolder customActionsHolder = (EditableListCardHolder)rawHolder;
                customActionsHolder.setInputHint("Type an idea here");
                break;
        }
    }
}
