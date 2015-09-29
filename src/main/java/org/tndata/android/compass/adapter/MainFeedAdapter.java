package org.tndata.android.compass.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.util.CompassUtil;

import java.util.List;

import at.grabner.circleprogress.CircleProgressView;


/**
 * Created by isma on 9/22/15.
 */
public class MainFeedAdapter extends RecyclerView.Adapter{
    private static final int TYPE_BLANK = 0;
    private static final int TYPE_WELCOME = 1;
    private static final int TYPE_UP_NEXT = 2;
    private static final int TYPE_PROGRESS = 3;
    private static final int TYPE_GOAL_HEADER = 4;
    private static final int TYPE_SEPARATOR = 5;
    private static final int TYPE_GOAL = 6;
    private static final int TYPE_OTHER = 7;


    private Context mContext;
    private MainFeedAdapterListener mListener;
    private List<Goal> mGoals;
    private FeedData mFeedData;


    public MainFeedAdapter(@NonNull Context context, @NonNull MainFeedAdapterListener listener,
                           @NonNull List<Goal> goals, FeedData feedData){
        mContext = context;
        mListener = listener;
        mGoals = goals;
        mFeedData = feedData;
    }

    private boolean hasWelcomeCard(){
        return mFeedData == null || mFeedData.getUserGoals().isEmpty();
    }

    private int getMyGoalsHeaderPosition(){
        int position = 2;
        if (hasWelcomeCard()){
            position++;
        }
        return position;
    }

    private int getMyGoalsLastItemPosition(){
        return getMyGoalsHeaderPosition()+2*mGoals.size();
    }

    private boolean isMyGoalsInnerPosition(int position){
        return position > getMyGoalsHeaderPosition() && position < getMyGoalsLastItemPosition();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (viewType == TYPE_BLANK){
            return new RecyclerView.ViewHolder(new CardView(mContext)){};
        }
        else if (viewType == TYPE_WELCOME){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            return new RecyclerView.ViewHolder(inflater.inflate(R.layout.card_welcome, parent, false)){};
        }
        else if (viewType == TYPE_UP_NEXT){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            return new UpNextHolder(inflater.inflate(R.layout.card_up_next, parent, false));
        }
        else if (viewType == TYPE_PROGRESS){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            return new ProgressHolder(inflater.inflate(R.layout.card_progress, parent, false));
        }
        else if (viewType == TYPE_GOAL_HEADER){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            return new RecyclerView.ViewHolder(inflater.inflate(R.layout.card_my_goals_header, parent, false)){};
        }
        else if (viewType == TYPE_SEPARATOR){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            return new RecyclerView.ViewHolder(inflater.inflate(R.layout.card_separator, parent, false)){};
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
        ((CardView)rawHolder.itemView).setRadius(CompassUtil.getPixels(mContext, 2));

        //Account for the welcome card if necessary
        if (hasWelcomeCard()){
            position--;
        }

        if (position == 1){
            UpNextHolder holder = (UpNextHolder)rawHolder;
            if (mFeedData == null || mFeedData.getNextAction() == null){
                holder.mNoActionsContainer.setVisibility(View.VISIBLE);
                holder.mContentContainer.setVisibility(View.GONE);
                holder.itemView.setOnClickListener(holder);
            }
            else{
                Action action = mFeedData.getNextAction();
                holder.mNoActionsContainer.setVisibility(View.GONE);
                holder.mContentContainer.setVisibility(View.VISIBLE);
                holder.itemView.setOnClickListener(null);
                holder.mAction.setText(action.getTitle());
                String goalTitle = action.getPrimaryGoal().getTitle().substring(0, 1).toLowerCase();
                goalTitle += action.getPrimaryGoal().getTitle().substring(1);
                holder.mGoal.setText("To help me " + goalTitle);
                holder.mTime.setText(action.getTrigger().getFormattedTime().toLowerCase());
                holder.mIndicator.setAutoTextSize(true);
                holder.mIndicator.setShowUnit(true);
                holder.mIndicator.setValue(0);
                holder.mIndicator.setValueAnimated(0, mFeedData.getProgress(), 1500);
                //holder.mIndicator.setText("Today");
            }
        }
        else if (position == 2){
            ProgressHolder holder = (ProgressHolder)rawHolder;
            holder.mIndicator.setValueAnimated(0, (int)(100*Math.random()), 1500);
        }
        else if (position == 3){
            ((CardView)rawHolder.itemView).setRadius(0);
        }
        else if (position > 3){
            ((CardView)rawHolder.itemView).setRadius(0);
            if (position%2 == 1){
                GoalHolder holder = (GoalHolder)rawHolder;
                Goal goal = mGoals.get((position - 3)/2);
                holder.mTitle.setText(goal.getTitle());
                goal.loadIconIntoView(mContext, holder.mIcon);
            }
        }
        else{
            if (position == -1 || (!hasWelcomeCard() && position == 0)){
                int width = CompassUtil.getScreenWidth(mContext);
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, (int)((width*2/3)*0.8));
                rawHolder.itemView.setLayoutParams(params);
                rawHolder.itemView.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount(){
        return getMyGoalsLastItemPosition();
    }

    @Override
    public int getItemViewType(int position){
        //The first card is always a blank card
        if (position == 0){
            return TYPE_BLANK;
        }

        //The second card may be a welcome card, but only if the user has no goals selected
        if (hasWelcomeCard()){
            //If this is position 1 then it is a welcome card
            if (position == 1){
                return TYPE_WELCOME;
            }
            //Otherwise, decrement the position
            else{
                position--;
            }
        }

        if (position == 1){
            return TYPE_UP_NEXT;
        }
        else if (position == 2){
            return TYPE_PROGRESS;
        }
        else if (position == 3){
            return TYPE_GOAL_HEADER;
        }
        else if (position > 3){
            if (position%2 == 0){
                return TYPE_SEPARATOR;
            }
            else{
                return TYPE_GOAL;
            }
        }
        else{
            return TYPE_OTHER;
        }
    }

    public MainFeedPadding getMainFeedPadding(){
        return new MainFeedPadding(mContext, this);
    }

    private class UpNextHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private View mNoActionsContainer;
        private View mContentContainer;
        private CircleProgressView mIndicator;
        private TextView mAction;
        private TextView mGoal;
        private TextView mTime;


        public UpNextHolder(View itemView){
            super(itemView);

            mNoActionsContainer = itemView.findViewById(R.id.up_next_no_actions);
            mContentContainer = itemView.findViewById(R.id.up_next_content);
            mIndicator = (CircleProgressView)itemView.findViewById(R.id.up_next_indicator);
            mAction = (TextView)itemView.findViewById(R.id.up_next_action);
            mGoal = (TextView)itemView.findViewById(R.id.up_next_goal);
            mTime = (TextView)itemView.findViewById(R.id.up_next_time);
        }

        @Override
        public void onClick(View v){
            Toast.makeText(mContext, "No actions, card clicked", Toast.LENGTH_SHORT).show();
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


    /**
     * Decoration class to establish the feed card margin.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public static final class MainFeedPadding extends RecyclerView.ItemDecoration{
        private MainFeedAdapter mAdapter;
        private int mMargin;


        /**
         * Constructor.
         */
        private MainFeedPadding(Context context, MainFeedAdapter adapter){
            mAdapter = adapter;
            mMargin = (int)Math.ceil(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    12, context.getResources().getDisplayMetrics()));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state){

            int position = parent.getChildLayoutPosition(view);

            if (mAdapter.getMyGoalsHeaderPosition() == position){
                outRect.top = mMargin / 2;
                outRect.left = mMargin;
                outRect.bottom = 0;
                outRect.right = mMargin;
            }
            else if (mAdapter.getMyGoalsLastItemPosition()-2 == position){
                outRect.top = 0;
                outRect.left = mMargin;
                outRect.bottom = mMargin;
                outRect.right = mMargin;
            }
            else if (mAdapter.isMyGoalsInnerPosition(position)){
                outRect.top = 0;
                outRect.left = mMargin;
                outRect.bottom = 0;
                outRect.right = mMargin;
            }
            else{
                outRect.top = mMargin / 2;
                outRect.left = mMargin;
                outRect.bottom = mMargin / 2;
                outRect.right = mMargin;
            }
        }
    }

    public interface MainFeedAdapterListener{
        void onGoalSelected(Goal goal);
    }
}
