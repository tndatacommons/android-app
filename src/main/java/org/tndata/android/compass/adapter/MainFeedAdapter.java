package org.tndata.android.compass.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.util.CompassUtil;

import java.util.List;

import at.grabner.circleprogress.CircleProgressView;


/**
 * Created by isma on 9/22/15.
 */
public class MainFeedAdapter extends RecyclerView.Adapter{
    private static final int TYPE_BLANK = 0;
    private static final int TYPE_UP_NEXT = 1;
    private static final int TYPE_PROGRESS = 2;
    private static final int TYPE_GOAL = 3;
    private static final int TYPE_OTHER = 4;


    private Context mContext;
    private MainFeedAdapterListener mListener;
    private List<Goal> mGoals;


    public MainFeedAdapter(@NonNull Context context, @NonNull MainFeedAdapterListener listener,
                           @NonNull List<Goal> goals){
        mContext = context;
        mListener = listener;
        mGoals = goals;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (viewType == TYPE_BLANK){
            return new RecyclerView.ViewHolder(new CardView(mContext)){};
        }
        else if (viewType == TYPE_UP_NEXT){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            return new UpNextHolder(inflater.inflate(R.layout.card_up_next, parent, false));
        }
        else if (viewType == TYPE_PROGRESS){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            return new ProgressHolder(inflater.inflate(R.layout.card_progress, parent, false));
        }
        else if (viewType == TYPE_GOAL){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            return new GoalHolder(inflater.inflate(R.layout.card_goal, parent, false));
        }
        else if (viewType == TYPE_OTHER){
            return new RecyclerView.ViewHolder(new CardView(mContext)){};
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
        if (position == 1){
            UpNextHolder holder = (UpNextHolder)rawHolder;
        }
        else if (position == 2){
            ProgressHolder holder = (ProgressHolder)rawHolder;
            holder.mIndicator.setValueAnimated(0, (int)(100*Math.random()), 1500);
        }
        else if (position > 2){
            GoalHolder holder = (GoalHolder)rawHolder;
            Goal goal = mGoals.get(position-3);
            holder.mTitle.setText(goal.getTitle());
            goal.loadIconIntoView(mContext, holder.mIcon);
        }
        else{
            int width = CompassUtil.getScreenWidth(mContext);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, (int)((width*2/3)*0.8));
            rawHolder.itemView.setLayoutParams(params);
            if (position == 0){
                //holder.itemView.setBackgroundColor(Color.RED);
                rawHolder.itemView.setVisibility(View.INVISIBLE);
            }
            else{
                ((CardView)rawHolder.itemView).setCardBackgroundColor(Color.GREEN);
            }
        }
    }

    @Override
    public int getItemCount(){
        return mGoals.size()+3;
    }

    @Override
    public int getItemViewType(int position){
        if (position == 0){
            return TYPE_BLANK;
        }
        else if (position == 1){
            return TYPE_UP_NEXT;
        }
        else if (position == 2){
            return TYPE_PROGRESS;
        }
        else if (position > 2){
            return TYPE_GOAL;
        }
        else{
            return TYPE_OTHER;
        }
    }

    private class UpNextHolder extends RecyclerView.ViewHolder{
        private TextView mAction;
        private TextView mTime;


        public UpNextHolder(View itemView){
            super(itemView);

            mAction = (TextView)itemView.findViewById(R.id.up_next_action);
            mTime = (TextView)itemView.findViewById(R.id.up_next_time);
        }
    }

    private class ProgressHolder extends RecyclerView.ViewHolder{
        private CircleProgressView mIndicator;
        private TextView mTitle;
        private TextView mSubtitle;


        public ProgressHolder(View itemView){
            super(itemView);

            mIndicator = (CircleProgressView)itemView.findViewById(R.id.card_progress_indicator);
            mTitle = (TextView)itemView.findViewById(R.id.card_progress_title);

            mIndicator.setValue(0);
            mIndicator.setAutoTextSize(true);
            mIndicator.setShowUnit(true);
        }
    }

    private class GoalHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mIcon;
        private TextView mTitle;


        public GoalHolder(View itemView){
            super(itemView);

            mIcon = (ImageView)itemView.findViewById(R.id.card_goal_icon);
            mTitle = (TextView)itemView.findViewById(R.id.card_goal_title);
            
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            mListener.onGoalSelected(mGoals.get(getAdapterPosition()-3));
        }
    }

    public interface MainFeedAdapterListener{
        void onGoalSelected(Goal goal);
    }
}
