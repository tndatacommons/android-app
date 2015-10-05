package org.tndata.android.compass.adapter;

import android.content.Context;
import android.graphics.Rect;
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

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.UserData;
import org.tndata.android.compass.util.CompassUtil;

import at.grabner.circleprogress.CircleProgressView;


/**
 * Created by isma on 9/22/15.
 */
public class MainFeedAdapter extends RecyclerView.Adapter{
    private static final int TYPE_BLANK = 0;
    private static final int TYPE_WELCOME = 1;
    private static final int TYPE_UP_NEXT = 2;
    private static final int TYPE_PROGRESS = 3;
    private static final int TYPE_HEADER = 4;
    private static final int TYPE_SEPARATOR = 5;
    private static final int TYPE_ACTION = 6;
    private static final int TYPE_GOAL = 7;
    private static final int TYPE_OTHER = 8;


    private Context mContext;
    private MainFeedAdapterListener mListener;
    private UserData mUserData;


    public MainFeedAdapter(@NonNull Context context, @NonNull MainFeedAdapterListener listener){
        mContext = context;
        mListener = listener;
        mUserData = ((CompassApplication)mContext.getApplicationContext()).getUserData();
    }

    private boolean hasWelcomeCard(){
        return mUserData.getGoals().isEmpty();
    }

    private int getUpNextPosition(){
        return hasWelcomeCard() ? 2 : 1;
    }

    private boolean isUpNextPosition(int position){
        return getUpNextPosition() == position;
    }

    private int getProgressPosition(){
        return getUpNextPosition()+1;
    }

    private boolean isProgressPosition(int position){
        return getProgressPosition() == position;
    }

    private int getUpcomingHeaderPosition(){
        return getProgressPosition()+1;
    }

    private boolean isUpcomingHeaderPosition(int position){
        return getUpcomingHeaderPosition() == position;
    }

    private int getUpcomingLastItemPosition(){
        return getUpcomingHeaderPosition()+2*mUserData.getFeedData().getUpcomingActions().size();
    }

    private boolean isUpcomingInnerPosition(int position){
        return position > getUpcomingHeaderPosition() && position <= getUpcomingLastItemPosition();
    }

    private int getMyGoalsHeaderPosition(){
        return getUpcomingLastItemPosition()+1;
    }

    private boolean isMyGoalsHeaderPosition(int position){
        return getMyGoalsHeaderPosition() == position;
    }

    private int getMyGoalsLastItemPosition(){
        return getMyGoalsHeaderPosition()+2*mUserData.getGoals().size();
    }

    private boolean isMyGoalsInnerPosition(int position){
        return position > getMyGoalsHeaderPosition() && position <= getMyGoalsLastItemPosition();
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
        else if (viewType == TYPE_HEADER){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            return new HeaderHolder(inflater.inflate(R.layout.card_header, parent, false));
        }
        else if (viewType == TYPE_SEPARATOR){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            return new RecyclerView.ViewHolder(inflater.inflate(R.layout.card_separator, parent, false)){};
        }
        else if (viewType == TYPE_ACTION){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            return new ActionHolder(inflater.inflate(R.layout.card_action, parent, false));
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

        if (position == 0){
            int width = CompassUtil.getScreenWidth(mContext);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, (int)((width*2/3)*0.8));
            rawHolder.itemView.setLayoutParams(params);
            rawHolder.itemView.setVisibility(View.INVISIBLE);
        }
        else if (isUpNextPosition(position)){
            UpNextHolder holder = (UpNextHolder)rawHolder;
            if (mUserData.getFeedData().getNextAction() == null){
                holder.mNoActionsContainer.setVisibility(View.VISIBLE);
                holder.mContentContainer.setVisibility(View.GONE);
                holder.itemView.setOnClickListener(holder);
            }
            else{
                Action action = mUserData.getFeedData().getNextAction();
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
                holder.mIndicator.setValueAnimated(0, mUserData.getFeedData().getProgress(), 1500);
                //holder.mIndicator.setText("Today");
            }
        }
        else if (isProgressPosition(position)){
            ProgressHolder holder = (ProgressHolder)rawHolder;
            holder.mIndicator.setValueAnimated(0, (int)(100*Math.random()), 1500);
        }
        else if (isUpcomingHeaderPosition(position)){
            HeaderHolder holder = (HeaderHolder)rawHolder;
            ((CardView)holder.itemView).setRadius(0);
            holder.mTitle.setText("Today's activities");
        }
        else if (isUpcomingInnerPosition(position)){
            ((CardView)rawHolder.itemView).setRadius(0);
            if (position%2 == getUpcomingHeaderPosition()%2){
                ActionHolder holder = (ActionHolder)rawHolder;
                int actionPosition = (position - getUpcomingHeaderPosition() - 2) / 2;
                Action action = mUserData.getFeedData().getUpcomingActions().get(actionPosition);
                holder.mAction.setText(action.getTitle());
                String goalTitle = action.getPrimaryGoal().getTitle().substring(0, 1).toLowerCase();
                goalTitle += action.getPrimaryGoal().getTitle().substring(1);
                holder.mGoal.setText("To help me " + goalTitle);
                holder.mTime.setText(action.getTrigger().getFormattedTime());
            }
        }
        else if (isMyGoalsHeaderPosition(position)){
            HeaderHolder holder = (HeaderHolder)rawHolder;
            ((CardView)holder.itemView).setRadius(0);
            holder.mTitle.setText("My Goals");
        }
        else if (isMyGoalsInnerPosition(position)){
            ((CardView)rawHolder.itemView).setRadius(0);
            if (position%2 == getMyGoalsHeaderPosition()%2){
                GoalHolder holder = (GoalHolder)rawHolder;
                int goalPosition = (position - getMyGoalsHeaderPosition() - 2) / 2;
                Goal goal = mUserData.getGoals().get(goalPosition);
                holder.mTitle.setText(goal.getTitle());
                goal.loadIconIntoView(mContext, holder.mIcon);
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
        if (hasWelcomeCard() && position == 1){
            return TYPE_WELCOME;
        }
        //The rest of them have checker methods
        if (isUpNextPosition(position)){
            return TYPE_UP_NEXT;
        }
        if (isProgressPosition(position)){
            return TYPE_PROGRESS;
        }
        if (isUpcomingHeaderPosition(position) || isMyGoalsHeaderPosition(position)){
            return TYPE_HEADER;
        }
        if (isUpcomingInnerPosition(position)){
            if (position%2 == getUpcomingHeaderPosition()%2){
                return TYPE_ACTION;
            }
            else{
                return TYPE_SEPARATOR;
            }
        }
        if (isMyGoalsInnerPosition(position)){
            if (position%2 == getMyGoalsHeaderPosition()%2){
                return TYPE_GOAL;
            }
            else{
                return TYPE_SEPARATOR;
            }
        }

        return TYPE_OTHER;
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

    private class HeaderHolder extends RecyclerView.ViewHolder{
        private TextView mTitle;


        public HeaderHolder(View itemView){
            super(itemView);
            mTitle = (TextView)itemView.findViewById(R.id.header_title);
        }
    }

    private class ActionHolder extends RecyclerView.ViewHolder{
        private TextView mAction;
        private TextView mGoal;
        private TextView mTime;


        public ActionHolder(View itemView){
            super(itemView);

            mAction = (TextView)itemView.findViewById(R.id.action_title);
            mGoal = (TextView)itemView.findViewById(R.id.action_goal);
            mTime = (TextView)itemView.findViewById(R.id.action_time);
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
            mListener.onGoalSelected(mUserData.getGoals().get((getAdapterPosition()-getMyGoalsHeaderPosition()-1)/2));
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
            mMargin = CompassUtil.getPixels(context, 12);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state){

            int position = parent.getChildLayoutPosition(view);

            if (mAdapter.isUpcomingHeaderPosition(position) || mAdapter.isMyGoalsHeaderPosition(position)){
                outRect.top = mMargin / 2;
                outRect.left = mMargin;
                outRect.bottom = 0;
                outRect.right = mMargin;
            }
            else if (mAdapter.getUpcomingLastItemPosition() == position || mAdapter.getMyGoalsLastItemPosition()-2 == position){
                outRect.top = 0;
                outRect.left = mMargin;
                outRect.bottom = mMargin/2;
                outRect.right = mMargin;
            }
            else if (mAdapter.isUpcomingInnerPosition(position) || mAdapter.isMyGoalsInnerPosition(position)){
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
