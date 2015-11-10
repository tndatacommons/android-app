package org.tndata.android.compass.adapter.feed;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.UserData;
import org.tndata.android.compass.task.DeleteActionTask;
import org.tndata.android.compass.util.CompassUtil;

import java.util.Calendar;

import at.grabner.circleprogress.TextMode;


/**
 * Adapter for the main feed.
 *
 * @author Ismael Alonso
 * @version 1.1.0
 */
public class MainFeedAdapter extends RecyclerView.Adapter{
    //Item view types
    private static final int TYPE_BLANK = 0;
    private static final int TYPE_WELCOME = 1;
    private static final int TYPE_UP_NEXT = 2;
    private static final int TYPE_FEEDBACK = 3;
    private static final int TYPE_HEADER = 4;
    private static final int TYPE_ACTION = 5;
    private static final int TYPE_GOAL = 6;
    private static final int TYPE_FOOTER = 7;
    private static final int TYPE_OTHER = 8;


    final Context mContext;
    final MainFeedAdapterListener mListener;

    private UserData mUserData;
    private DataHandler mDataHandler;
    private FeedUtil mFeedUtil;

    private MainFeedPadding mMainFeedPadding;

    private int mSelectedItem;


    /**
     * Constructor.
     *
     * @param context the context,
     * @param listener the listener.
     */
    public MainFeedAdapter(@NonNull Context context, @NonNull MainFeedAdapterListener listener){
        mContext = context;
        mListener = listener;
        mUserData = ((CompassApplication)mContext.getApplicationContext()).getUserData();

        if (mUserData.getFeedData() == null){
            mListener.onNullData();
        }
        else{
            mDataHandler = new DataHandler(mUserData);
            CardTypes.setDataSource(mDataHandler);
            mFeedUtil = new FeedUtil(this);
        }
        mMainFeedPadding = null;
    }

    DataHandler getDataHandler(){
        return mDataHandler;
    }

    void setSelectedItem(int selectedItem){
        mSelectedItem = selectedItem;
    }

    /**
     * Display the popup menu for a specific goal.
     *
     * @param anchor the view it should be anchored to.
     * @param position the position of the view.
     */
    void showActionPopup(View anchor, int position){
        mFeedUtil.showActionPopup(anchor, position);
    }

    /**
     * Calculates the position of an action given its position in the list,
     *
     * @param adapterPosition a position in the list.
     * @return the position of the action in the backing array.
     */
    int getActionPosition(int adapterPosition){
        return adapterPosition-(CardTypes.getUpcomingHeaderPosition()+1);
    }

    /**
     * Animates the replacement of the up next card.
     */
    void replaceUpNext(){
        if (mDataHandler.getUpNext() != null){
            int headerPosition = CardTypes.getUpcomingHeaderPosition();
            if (!CardTypes.hasUpcoming()){
                notifyItemRemoved(headerPosition);
            }
            notifyItemRemoved(headerPosition + 1);
            notifyItemRangeChanged(headerPosition + 2, getItemCount() - (headerPosition + 2));
        }

        //Update the up next card
        notifyItemChanged(CardTypes.getUpNextPosition());
    }

    /**
     * Removes an action from the feed.
     *
     * @param position the position if the item in the feed.
     */
    void removeActionFromFeed(int position){
        //Update the relevant action cards
        if (!CardTypes.hasUpcoming()){
            notifyItemRemoved(position-1);
        }
        notifyItemRemoved(position);
        notifyItemRangeChanged(position-1, getItemCount()-(position-1));
    }



    public void didIt(){
        if (mSelectedItem != -1){
            didIt(mSelectedItem);
            mSelectedItem = -1;
        }
    }

    //Did it
    void didIt(int position){
        //1.- Mark the action as complete in the model
        //2.- Mark the action as complete in the webapp
        //3.- Update the model
        //4.- Update the adapter

        mDataHandler.didIt();
        if (CardTypes.isUpNext(position)){
            mFeedUtil.didIt(mContext, mDataHandler.getUpNext());
            mDataHandler.replaceUpNext();
            replaceUpNext();
        }
        else{
            mFeedUtil.didIt(mContext, mDataHandler.removeUpcoming(getActionPosition(position)));
            removeActionFromFeed(position);
        }
    }

    //Reschedule
    void reschedule(int position){
        mSelectedItem = position;
        if (CardTypes.isUpNext(position)){
            mListener.onTriggerSelected(getDataHandler().getUpNext());
        }
        else{
            mListener.onTriggerSelected(getDataHandler().getUpcoming().get(getActionPosition(position)));
        }
    }

    //Remove
    void remove(int position){
        //1.- Remove the action in the webapp
        //2.- Update the model
        //3.- Update the adapter

        Action action;
        if (CardTypes.isUpNext(position)){
            action = mDataHandler.getUpNext();
            mDataHandler.remove(action);
            mDataHandler.replaceUpNext();
            replaceUpNext();
        }
        else{
            action = mDataHandler.getUpcoming().get(getActionPosition(position));
            mDataHandler.remove(action);
            mDataHandler.removeUpcoming(getActionPosition(position));
            removeActionFromFeed(position);
        }
        new DeleteActionTask(mContext, null, action.getMappingId()+"").execute();
    }

    //View Goal
    void viewGoal(int position){
        Action action;
        if (CardTypes.isUpNext(position)){
            action = mUserData.getFeedData().getNextAction();
        }
        else{
            action = mUserData.getFeedData().getUpcomingActions().get(getActionPosition(position));
        }
        //TODO this is another workaround
        if (action.getPrimaryGoal() != null){
            mListener.onGoalSelected(action.getPrimaryGoal());
        }
    }


    void more(int position){
        if (CardTypes.isUpcomingFooter(position)){
            int footer = CardTypes.getUpcomingFooterPosition();
            int count = mDataHandler.loadMoreUpcoming();
            if (!CardTypes.hasUpcomingFooter()){
                notifyItemRemoved(footer);
            }
            notifyItemRangeInserted(footer, count);
            notifyItemRangeChanged(footer + count, getItemCount());
        }
        else if (CardTypes.isMyGoalsFooter(position)){
            int footer = CardTypes.getMyGoalsFooterPosition();
            int count = mDataHandler.loadMoreGoals();
            notifyItemRangeInserted(footer, count);
            if (!CardTypes.hasMyGoalsFooter()){
                notifyItemRemoved(footer+count+1);
            }
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (viewType == TYPE_BLANK){
            return new RecyclerView.ViewHolder(new CardView(mContext)){};
        }
        else if (viewType == TYPE_WELCOME){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.card_welcome, parent, false);
            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    mListener.onInstructionsSelected();
                }
            });
            return new RecyclerView.ViewHolder(view){};
        }
        else if (viewType == TYPE_UP_NEXT){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            return new UpNextHolder(this, inflater.inflate(R.layout.card_up_next, parent, false));
        }
        else if (viewType == TYPE_FEEDBACK){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            return new FeedbackHolder(this, inflater.inflate(R.layout.card_feedback, parent, false));
        }
        else if (viewType == TYPE_HEADER){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            return new HeaderHolder(this, inflater.inflate(R.layout.card_header, parent, false));
        }
        else if (viewType == TYPE_ACTION){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            return new ActionHolder(this, inflater.inflate(R.layout.card_action, parent, false));
        }
        else if (viewType == TYPE_GOAL){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            return new GoalHolder(this, inflater.inflate(R.layout.card_goal, parent, false));
        }
        else if (viewType == TYPE_FOOTER){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            return new FooterHolder(this, inflater.inflate(R.layout.card_footer, parent, false));
        }
        else if (viewType == TYPE_OTHER){
            return new RecyclerView.ViewHolder(new CardView(mContext)){};
        }
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
        //This is a possible fix to a crash where the application gets destroyed and the
        //  user data gets invalidated. In such a case, the app should restart and fetch
        //  the user data again. Bottomline, do not keep going
        if (mUserData.getFeedData() == null){
            return;
        }

        //Set the card parameters
        ((CardView)rawHolder.itemView).setRadius(CompassUtil.getPixels(mContext, 2));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            setPreLParameters((CardView)rawHolder.itemView);
        }

        //Blank space
        if (position == 0){
            int width = CompassUtil.getScreenWidth(mContext);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, (int)((width*2/3)*0.8));
            rawHolder.itemView.setLayoutParams(params);
            rawHolder.itemView.setVisibility(View.INVISIBLE);
        }
        //Up next
        else if (CardTypes.isUpNext(position)){
            UpNextHolder holder = (UpNextHolder)rawHolder;
            if (!CardTypes.hasUpNextAction()){
                holder.mOverflow.setVisibility(View.GONE);
                holder.mNoActionsContainer.setVisibility(View.VISIBLE);
                holder.mContentContainer.setVisibility(View.GONE);

                if (mUserData.getFeedData().getTotalActions() != 0){
                    holder.mHeader.setText(R.string.card_up_next_header_completed);
                    holder.mNoActionsTitle.setText(R.string.card_up_next_title_completed);
                    holder.mNoActionsSubtitle.setText(R.string.card_up_next_title_completed);
                }
                else{
                    holder.mHeader.setText(R.string.card_up_next_header);
                    holder.mNoActionsTitle.setText(R.string.card_up_next_title_empty);
                    holder.mNoActionsSubtitle.setText(R.string.card_up_next_subtitle_empty);
                }
                holder.mTime.setText("");
            }
            else{
                Action action = mUserData.getFeedData().getNextAction();
                holder.mOverflow.setVisibility(View.VISIBLE);
                holder.mNoActionsContainer.setVisibility(View.GONE);
                holder.mContentContainer.setVisibility(View.VISIBLE);

                holder.mHeader.setText(R.string.card_up_next_header);
                holder.mAction.setText(action.getTitle());
                //TODO this is a workaround
                if (action.getPrimaryGoal() != null){
                    String goalTitle = action.getPrimaryGoal().getTitle().substring(0, 1).toLowerCase();
                    goalTitle += action.getPrimaryGoal().getTitle().substring(1);
                    holder.mGoal.setText(mContext.getString(R.string.card_up_next_goal_title, goalTitle));
                }
                else{
                    holder.mGoal.setText("");
                }
                holder.mTime.setText(action.getNextReminderDate());
            }

            holder.mIndicator.setAutoTextSize(true);
            holder.mIndicator.setValue(mUserData.getFeedData().getProgress());
            holder.mIndicator.setTextMode(TextMode.TEXT);
            holder.mIndicator.setValueAnimated(0, mUserData.getFeedData().getProgress(), 1500);
            holder.mIndicatorCaption.setText(mContext.getString(R.string.card_up_next_indicator_caption,
                    mUserData.getFeedData().getProgressFraction()));

            Calendar calendar = Calendar.getInstance();
            String month = CompassUtil.getMonthString(calendar.get(Calendar.MONTH) + 1);
            holder.mIndicator.setText(month + " " + calendar.get(Calendar.DAY_OF_MONTH));
        }
        //Feedback
        else if (CardTypes.isFeedback(position)){
            FeedbackHolder holder = (FeedbackHolder)rawHolder;
            holder.mIcon.setImageResource(mUserData.getFeedData().getFeedbackIcon());
            holder.mTitle.setText(mUserData.getFeedData().getFeedbackTitle());
            holder.mSubtitle.setText(mUserData.getFeedData().getFeedbackSubtitle());
        }
        //Today's activities / upcoming header
        else if (CardTypes.isUpcomingHeader(position)){
            HeaderHolder holder = (HeaderHolder)rawHolder;
            ((CardView)holder.itemView).setRadius(0);
            holder.mTitle.setText(R.string.card_upcoming_header);
        }
        //Today's activities / upcoming
        else if (CardTypes.isUpcomingAction(position)){
            ((CardView)rawHolder.itemView).setRadius(0);

            ActionHolder holder = (ActionHolder)rawHolder;
            int actionPosition = position - CardTypes.getUpcomingHeaderPosition() - 1;
            Action action = mDataHandler.getUpcoming().get(actionPosition);
            holder.mAction.setText(action.getTitle());
            //TODO this shouldn't be happening
            if (action.getPrimaryGoal() != null){
                String goalTitle = action.getPrimaryGoal().getTitle().substring(0, 1).toLowerCase();
                goalTitle += action.getPrimaryGoal().getTitle().substring(1);
                holder.mGoal.setText(mContext.getString(R.string.card_upcoming_goal_title, goalTitle));
            }
            else{
                holder.mGoal.setText("");
            }
            holder.mTime.setText(action.getNextReminderDate());
        }
        else if (CardTypes.isUpcomingFooter(position)){
            ((CardView)rawHolder.itemView).setRadius(0);
        }
        //My goals / suggestions header
        else if (CardTypes.isMyGoalsHeader(position)){
            HeaderHolder holder = (HeaderHolder)rawHolder;
            ((CardView)holder.itemView).setRadius(0);
            if (mUserData.getGoals().isEmpty()){
                holder.mTitle.setText(R.string.card_suggestions_header);
            }
            else{
                holder.mTitle.setText(R.string.card_my_goals_header);
            }
        }
        //My goals / suggestions
        else if (CardTypes.isGoal(position)){
            ((CardView)rawHolder.itemView).setRadius(0);

            GoalHolder holder = (GoalHolder)rawHolder;
            int goalPosition = position - CardTypes.getMyGoalsHeaderPosition() - 1;
            Goal goal;
            if (mUserData.getGoals().isEmpty()){
                goal = mUserData.getFeedData().getSuggestions().get(goalPosition);
            }
            else{
                goal = mUserData.getGoals().get(goalPosition);

                GradientDrawable gradientDrawable = (GradientDrawable)holder.mIconContainer.getBackground();
                if (goal.getPrimaryCategory() != null){
                    gradientDrawable.setColor(Color.parseColor(goal.getPrimaryCategory().getColor()));
                }
                else{
                    //TODO another workaround
                    gradientDrawable.setColor(mContext.getResources().getColor(R.color.grow_primary));
                }
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
        else if (CardTypes.isMyGoalsFooter(position)){
            ((CardView)rawHolder.itemView).setRadius(0);
        }
    }

    @Override
    public int getItemCount(){
        return CardTypes.getItemCount();
    }

    @Override
    public int getItemViewType(int position){
        //The first card is always a blank card
        if (position == 0){
            return TYPE_BLANK;
        }
        //The second card may be a welcome card, but only if the user has no goals selected
        if (CardTypes.hasWelcomeCard() && position == 1){
            return TYPE_WELCOME;
        }
        //The rest of them have checker methods
        if (CardTypes.isUpNext(position)){
            return TYPE_UP_NEXT;
        }
        if (CardTypes.isFeedback(position)){
            return TYPE_FEEDBACK;
        }
        if (CardTypes.isUpcomingHeader(position) || CardTypes.isMyGoalsHeader(position)){
            return TYPE_HEADER;
        }
        if (CardTypes.isUpcomingAction(position)){
            return TYPE_ACTION;
        }
        if (CardTypes.isGoal(position)){
            return TYPE_GOAL;
        }
        if (CardTypes.isUpcomingFooter(position) || CardTypes.isMyGoalsFooter(position)){
            return TYPE_FOOTER;
        }

        return TYPE_OTHER;
    }

    /**
     * Gets the ItemDecoration object for the feed.
     *
     * @return the ItemDecoration object containing the information about the feed padding.
     */
    public MainFeedPadding getMainFeedPadding(){
        if (mMainFeedPadding == null){
            mMainFeedPadding = new MainFeedPadding(mContext);
        }
        return mMainFeedPadding;
    }

    /**
     * Updates the item marked as selected.
     */
    public void updateSelectedItem(){
        if (mSelectedItem != -1){
            notifyItemChanged(mSelectedItem);
            mSelectedItem = -1;
        }
    }

    /**
     * Deletes the item marked as selected.
     */
    public void deleteSelectedItem(){
        if (CardTypes.isUpNext(mSelectedItem)){
            mDataHandler.replaceUpNext();
            replaceUpNext();
        }
        else if (CardTypes.isUpcomingAction(mSelectedItem)){
            mDataHandler.removeUpcoming(getActionPosition(mSelectedItem));
            removeActionFromFeed(mSelectedItem);
        }
        mSelectedItem = -1;
    }

    /**
     * Sets the card parameters for pre L devices.
     *
     * @param view the card view whose parameters shall be set.
     */
    private void setPreLParameters(CardView view){
        view.setMaxCardElevation(0);
        view.setCardBackgroundColor(mContext.getResources().getColor(R.color.card_pre_l_background));
    }

    public int getMyGoalsHeaderPosition(){
        return CardTypes.getMyGoalsHeaderPosition();
    }
}
