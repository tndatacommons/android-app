package org.tndata.android.compass.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.UserData;
import org.tndata.android.compass.util.CompassUtil;

import java.util.Calendar;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;


/**
 * Created by isma on 9/22/15.
 */
public class MainFeedAdapter extends RecyclerView.Adapter{
    private static final int TYPE_BLANK = 0;
    private static final int TYPE_WELCOME = 1;
    private static final int TYPE_UP_NEXT = 2;
    private static final int TYPE_FEEDBACK = 3;
    private static final int TYPE_HEADER = 4;
    private static final int TYPE_SEPARATOR = 5;
    private static final int TYPE_ACTION = 6;
    private static final int TYPE_GOAL = 7;
    private static final int TYPE_OTHER = 8;


    private Context mContext;
    private MainFeedAdapterListener mListener;
    private UserData mUserData;

    private int mSelectedItem;


    public MainFeedAdapter(@NonNull Context context, @NonNull MainFeedAdapterListener listener){
        mContext = context;
        mListener = listener;
        mUserData = ((CompassApplication)mContext.getApplicationContext()).getUserData();
    }

    private boolean hasWelcomeCard(){
        return mUserData.getGoals().isEmpty();
    }

    private boolean hasUpNextAction(){
        return mUserData.getFeedData().getNextAction() != null;
    }

    private int getUpNextPosition(){
        return hasWelcomeCard() ? 2 : 1;
    }

    private boolean isUpNextPosition(int position){
        return getUpNextPosition() == position;
    }

    private int getFeedbackPosition(){
        return getUpNextPosition()+1;
    }

    private boolean isFeedbackPosition(int position){
        return hasUpNextAction() && getFeedbackPosition() == position;
    }

    private boolean hasUpcoming(){
        return !mUserData.getFeedData().getUpcomingActions().isEmpty();
    }

    private int getUpcomingHeaderPosition(){
        //If there is up next, then there is feedback
        if (hasUpNextAction()){
            return getFeedbackPosition()+1;
        }
        //If there ain't up next, then there is no feedback and up next displays something else
        return getUpNextPosition()+1;
    }

    private boolean isUpcomingHeaderPosition(int position){
        return hasUpcoming() && getUpcomingHeaderPosition() == position;
    }

    private int getUpcomingLastItemPosition(){
        return getUpcomingHeaderPosition()+2*mUserData.getFeedData().getUpcomingActions().size();
    }

    private boolean isUpcomingLastItemPosition(int position){
        return hasUpcoming() && position == getUpcomingLastItemPosition();
    }

    private boolean isUpcomingInnerPosition(int position){
        return hasUpcoming() &&
                position > getUpcomingHeaderPosition() && position <= getUpcomingLastItemPosition();
    }

    private int getMyGoalsHeaderPosition(){
        //If there are upcoming actions then my goals are right after
        if (hasUpcoming()){
            return getUpcomingLastItemPosition() + 1;
        }
        //If there ain't upcoming actions then my goals takes its place
        return getUpcomingHeaderPosition();
    }

    private boolean isMyGoalsHeaderPosition(int position){
        return getMyGoalsHeaderPosition() == position;
    }

    private int getMyGoalsLastItemPosition(){
        if (mUserData.getGoals().isEmpty()){
            return getMyGoalsHeaderPosition()+2*mUserData.getFeedData().getSuggestions().size();
        }
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
        else if (viewType == TYPE_FEEDBACK){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            return new FeedbackHolder(inflater.inflate(R.layout.card_feedback, parent, false));
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
            if (!hasUpNextAction()){
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
                holder.mIndicator.setValue(mUserData.getFeedData().getProgress());
                holder.mIndicator.setTextMode(TextMode.TEXT);
                holder.mIndicator.setValueAnimated(0, mUserData.getFeedData().getProgress(), 1500);

                Calendar calendar = Calendar.getInstance();
                String month = CompassUtil.getMonthString(calendar.get(Calendar.MONTH)+1);
                holder.mIndicator.setText(month + " " + calendar.get(Calendar.DAY_OF_MONTH));
            }
        }
        else if (isFeedbackPosition(position)){
            FeedbackHolder holder = (FeedbackHolder)rawHolder;
            holder.mTitle.setText(mUserData.getFeedData().getFeedbackTitle());
            holder.mSubtitle.setText(mUserData.getFeedData().getFeedbackSubtitle());
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
                holder.mTime.setText(action.getTrigger().getFormattedTime().toLowerCase());
            }
        }
        else if (isMyGoalsHeaderPosition(position)){
            HeaderHolder holder = (HeaderHolder)rawHolder;
            ((CardView)holder.itemView).setRadius(0);
            if (mUserData.getGoals().isEmpty()){
                holder.mTitle.setText("Suggested goals");
            }
            else{
                holder.mTitle.setText("My goals");
            }
        }
        else if (isMyGoalsInnerPosition(position)){
            ((CardView)rawHolder.itemView).setRadius(0);
            if (position%2 == getMyGoalsHeaderPosition()%2){
                GoalHolder holder = (GoalHolder)rawHolder;
                int goalPosition = (position - getMyGoalsHeaderPosition() - 2) / 2;
                Goal goal;
                if (mUserData.getGoals().isEmpty()){
                    goal = mUserData.getFeedData().getSuggestions().get(goalPosition);
                }
                else{
                    goal = mUserData.getGoals().get(goalPosition);

                    GradientDrawable gradientDrawable = (GradientDrawable)holder.mIconContainer.getBackground();
                    gradientDrawable.setColor(Color.parseColor(goal.getPrimaryCategory().getColor()));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                        holder.mIconContainer.setBackground(gradientDrawable);
                    }
                    else{
                        holder.mIconContainer.setBackgroundDrawable(gradientDrawable);
                    }
                }

                goal.loadIconIntoView(mContext, holder.mIcon);
                holder.mTitle.setText(goal.getTitle());
            }
        }
    }

    @Override
    public int getItemCount(){
        return getMyGoalsLastItemPosition()+1;
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
        if (isFeedbackPosition(position)){
            return TYPE_FEEDBACK;
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

    public void updateSelectedItem(){
        if (mSelectedItem != -1){
            notifyItemChanged(mSelectedItem);
            mSelectedItem = -1;
        }
    }
    public void deleteSelectedItem(){
        if (isUpcomingInnerPosition(mSelectedItem)){
            int index = (mSelectedItem-getUpcomingHeaderPosition()-1)/2;
            mUserData.getFeedData().getUpcomingActions().remove(index);
            if (!hasUpcoming()){
                notifyItemRemoved(mSelectedItem-2);
            }
            notifyItemRemoved(mSelectedItem - 1);
            notifyItemRemoved(mSelectedItem);
            mSelectedItem = -1;
            notifyItemChanged(getUpcomingLastItemPosition()+1);
            notifyItemChanged(getUpcomingLastItemPosition());
            notifyItemChanged(getUpcomingLastItemPosition()-1);
            notifyItemChanged(getUpcomingLastItemPosition()-2);
        }
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

            mAction.setOnClickListener(this);
            mGoal.setOnClickListener(this);
            mTime.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            Action action = mUserData.getFeedData().getNextAction();
            mSelectedItem = getAdapterPosition();
            switch (view.getId()){
                case R.id.up_next_action:
                    mListener.onActionSelected(action);
                    break;

                case R.id.up_next_goal:
                    mListener.onGoalSelected(action.getPrimaryGoal());
                    break;

                case R.id.up_next_time:
                    mListener.onTriggerSelected(action);
                    break;

                default:
                    Toast.makeText(mContext, "No actions, card clicked", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class FeedbackHolder extends RecyclerView.ViewHolder{
        private TextView mTitle;
        private TextView mSubtitle;


        public FeedbackHolder(View itemView){
            super(itemView);

            mTitle = (TextView)itemView.findViewById(R.id.card_feedback_title);
            mSubtitle = (TextView)itemView.findViewById(R.id.card_feedback_subtitle);
        }
    }

    private class HeaderHolder extends RecyclerView.ViewHolder{
        private TextView mTitle;


        public HeaderHolder(View itemView){
            super(itemView);
            mTitle = (TextView)itemView.findViewById(R.id.header_title);
        }
    }

    private class ActionHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mAction;
        private TextView mGoal;
        private TextView mTime;


        public ActionHolder(View itemView){
            super(itemView);

            mAction = (TextView)itemView.findViewById(R.id.action_title);
            mGoal = (TextView)itemView.findViewById(R.id.action_goal);
            mTime = (TextView)itemView.findViewById(R.id.action_time);

            mAction.setOnClickListener(this);
            mGoal.setOnClickListener(this);
            mTime.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            mSelectedItem = getAdapterPosition();
            int index = (getAdapterPosition()-getUpcomingHeaderPosition()-1)/2;
            Action action = mUserData.getFeedData().getUpcomingActions().get(index);
            switch (view.getId()){
                case R.id.action_title:
                    mListener.onActionSelected(action);
                    break;

                case R.id.action_goal:
                    mListener.onGoalSelected(action.getPrimaryGoal());
                    break;

                case R.id.action_time:
                    mListener.onTriggerSelected(action);
                    break;
            }
        }
    }

    private class GoalHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private RelativeLayout mIconContainer;
        private ImageView mIcon;
        private TextView mTitle;


        public GoalHolder(View itemView){
            super(itemView);

            mIconContainer = (RelativeLayout)itemView.findViewById(R.id.card_goal_icon_container);
            mIcon = (ImageView)itemView.findViewById(R.id.card_goal_icon);
            mTitle = (TextView)itemView.findViewById(R.id.card_goal_title);
            
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            Goal goal;
            if (mUserData.getGoals().isEmpty()){
                goal = mUserData.getFeedData().getSuggestions().get((getAdapterPosition()-getMyGoalsHeaderPosition()-1)/2);
            }
            else{
                goal = mUserData.getGoals().get((getAdapterPosition()-getMyGoalsHeaderPosition()-1)/2);
            }
            mListener.onGoalSelected(goal);
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
            else if (mAdapter.isUpcomingLastItemPosition(position) || mAdapter.getMyGoalsLastItemPosition() == position){
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
        void onActionSelected(Action action);
        void onTriggerSelected(Action action);
    }
}
