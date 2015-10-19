package org.tndata.android.compass.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.UserData;
import org.tndata.android.compass.service.ActionReportService;
import org.tndata.android.compass.task.DeleteActionTask;
import org.tndata.android.compass.ui.CompassPopupMenu;
import org.tndata.android.compass.util.CompassUtil;

import java.util.Calendar;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;


//TODO this class is getting way too big to be comfortable. Try to extract generic
//TODO  functions to utility.

/**
 * Adapter for the main feed.
 *
 * @author Ismael Alonso
 * @version 1.0.0
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
    private static final int TYPE_OTHER = 7;


    private Context mContext;
    private MainFeedAdapterListener mListener;
    private UserData mUserData;

    private MainFeedPadding mMainFeedPadding;

    private Goal mFeedbackGoal;

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
            if (mUserData.getFeedData().getNextAction() != null){
                mFeedbackGoal = mUserData.getFeedData().getNextAction().getPrimaryGoal();
            }
        }
        mMainFeedPadding = null;
    }

    /**
     * Tells whether the feed has a welcome card, which happens when the user has no goals.
     *
     * @return true if there is a welcome card, false otherwise.
     */
    private boolean hasWelcomeCard(){
        return mUserData.getGoals().isEmpty();
    }

    /**
     * Tells whether the feed has an up next action card.
     *
     * @return true if there is an up next card, false otherwise.
     */
    private boolean hasUpNextAction(){
        return mUserData.getFeedData().getNextAction() != null;
    }

    /**
     * Gets the position of the up next card, which depends on whether there is a welcome card.
     *
     * @return the position of the up next card.
     */
    private int getUpNextPosition(){
        return hasWelcomeCard() ? 2 : 1;
    }

    /**
     * Tells whether a position is that of the up next card.
     *
     * @param position the position to be checked.
     * @return true if it is the position of up next, false otherwise.
     */
    private boolean isUpNextPosition(int position){
        return getUpNextPosition() == position;
    }

    /**
     * Gets the position of the feedback card.
     *
     * @return the position of the feedback card.
     */
    private int getFeedbackPosition(){
        return getUpNextPosition()+1;
    }

    /**
     * Tells whether a position is that of the feedback card.
     *
     * @param position the position to be checked.
     * @return true if it is the position of the feedback, false otherwise.
     */
    private boolean isFeedbackPosition(int position){
        return hasUpNextAction() && getFeedbackPosition() == position;
    }

    /**
     * Tells whether there are upcoming actions.
     *
     * @return true if there are upcoming actions, false otherwise.
     */
    private boolean hasUpcoming(){
        return !mUserData.getFeedData().getUpcomingActions().isEmpty();
    }

    /**
     * Gets the position of the upcoming header card.
     *
     * @return the position of the upcoming header card.
     */
    private int getUpcomingHeaderPosition(){
        //If there is up next, then there is feedback
        if (hasUpNextAction()){
            return getFeedbackPosition()+1;
        }
        //If there ain't up next, then there is no feedback and up next displays something else
        return getUpNextPosition()+1;
    }

    /**
     * Tells whether a position is that of the upcoming header card.
     *
     * @param position the position to be checked.
     * @return true if it is the position of the upcoming header, false otherwise.
     */
    private boolean isUpcomingHeaderPosition(int position){
        return hasUpcoming() && getUpcomingHeaderPosition() == position;
    }

    /**
     * Gets the position of the upcoming actions last item position.
     *
     * @return the position of the upcoming actions last item.
     */
    private int getUpcomingLastItemPosition(){
        return getUpcomingHeaderPosition()+mUserData.getFeedData().getUpcomingActions().size();
    }

    /**
     * Tells whether a position is that of the upcoming actions last item.
     *
     * @param position the position to be checked.
     * @return true if the position id that of upcoming's last item, false otherwise.
     */
    private boolean isUpcomingLastItemPosition(int position){
        return hasUpcoming() && position == getUpcomingLastItemPosition();
    }

    /**
     * Tells whether a position is that of an upcoming's inner item.
     *
     * @param position the position to be checked.
     * @return true if the position is that of an upcoming's inner item, false otherwise.
     */
    private boolean isUpcomingInnerPosition(int position){
        return hasUpcoming() &&
                position > getUpcomingHeaderPosition() && position <= getUpcomingLastItemPosition();
    }

    /**
     * Tells whether a position is that of my goals' header.
     *
     * @return true if the position id that of my goals header item, false otherwise.
     */
    public int getMyGoalsHeaderPosition(){
        //If there are upcoming actions then my goals are right after
        if (hasUpcoming()){
            return getUpcomingLastItemPosition() + 1;
        }
        //If there ain't upcoming actions then my goals takes its place
        return getUpcomingHeaderPosition();
    }

    /**
     * Tells whether a position is that of the my goals header card.
     *
     * @param position the position to be checked.
     * @return true if it is the position of the my goals header, false otherwise.
     */
    private boolean isMyGoalsHeaderPosition(int position){
        return getMyGoalsHeaderPosition() == position;
    }

    /**
     * Gets the position of my goals' last item.
     *
     * @return the position of my goals' last item.
     */
    private int getMyGoalsLastItemPosition(){
        //My goals can be either my goals or suggestions
        if (mUserData.getGoals().isEmpty()){
            return getMyGoalsHeaderPosition()+mUserData.getFeedData().getSuggestions().size();
        }
        return getMyGoalsHeaderPosition()+mUserData.getGoals().size();
    }

    /**
     * Tells whether the position is that of a my goals inner item.
     *
     * @param position the position to be checked.
     * @return true if the position is that of a my goals inner item, false otherwise.
     */
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
        else if (isUpNextPosition(position)){
            UpNextHolder holder = (UpNextHolder)rawHolder;
            if (!hasUpNextAction()){
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
            String month = CompassUtil.getMonthString(calendar.get(Calendar.MONTH)+1);
            holder.mIndicator.setText(month + " " + calendar.get(Calendar.DAY_OF_MONTH));
        }
        //Feedback
        else if (isFeedbackPosition(position)){
            FeedbackHolder holder = (FeedbackHolder)rawHolder;
            holder.mTitle.setText(mUserData.getFeedData().getFeedbackTitle());
            holder.mSubtitle.setText(mUserData.getFeedData().getFeedbackSubtitle());
        }
        //Today's activities / upcoming header
        else if (isUpcomingHeaderPosition(position)){
            HeaderHolder holder = (HeaderHolder)rawHolder;
            ((CardView)holder.itemView).setRadius(0);
            holder.mTitle.setText(R.string.card_upcoming_header);
        }
        //Today's activities / upcoming
        else if (isUpcomingInnerPosition(position)){
            ((CardView)rawHolder.itemView).setRadius(0);

            ActionHolder holder = (ActionHolder)rawHolder;
            int actionPosition = position - getUpcomingHeaderPosition() - 1;
            Action action = mUserData.getFeedData().getUpcomingActions().get(actionPosition);
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
        //My goals / suggestions header
        else if (isMyGoalsHeaderPosition(position)){
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
        else if (isMyGoalsInnerPosition(position)){
            ((CardView)rawHolder.itemView).setRadius(0);

            GoalHolder holder = (GoalHolder)rawHolder;
            int goalPosition = position - getMyGoalsHeaderPosition() - 1;
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
            return TYPE_ACTION;
        }
        if (isMyGoalsInnerPosition(position)){
            return TYPE_GOAL;
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
            mMainFeedPadding = new MainFeedPadding(mContext, this);
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
        if (isUpcomingInnerPosition(mSelectedItem)){
            //Update the data set
            int index = mSelectedItem - (getUpcomingHeaderPosition()+1);
            mUserData.getFeedData().getUpcomingActions().remove(index);

            //Animate the removal
            if (!hasUpcoming()){
                notifyItemRemoved(mSelectedItem-1);
            }
            notifyItemRemoved(mSelectedItem);
            mSelectedItem = -1;

            //Update the items at the end of the list (fixes card splitting problem)
            notifyItemChanged(getUpcomingLastItemPosition()+1);
            notifyItemChanged(getUpcomingLastItemPosition());
            notifyItemChanged(getUpcomingLastItemPosition()-1);
        }
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

    /**
     * Display the popup menu for a specific goal.
     *
     * @param anchor the view it should be anchored to.
     * @param position the position of the view.
     */
    private void showActionPopup(View anchor, final int position){
        final CompassPopupMenu popup = CompassPopupMenu.newInstance(mContext, anchor);

        //The category of the selected action needs to be retrieved to determine which menu
        //  should be inflated.
        Category category = null;
        if (position == getUpNextPosition()){
            if (mUserData.getFeedData().getNextAction().getPrimaryGoal() != null){
                category = mUserData.getFeedData().getNextAction()
                        .getPrimaryGoal().getPrimaryCategory();
                if (category == null){
                    Goal goal = mUserData.getGoal(mUserData.getFeedData().getNextAction().getPrimaryGoal());
                    if (goal.getCategories().size() > 0){
                        category = goal.getCategories().get(0);
                    }
                }
            }
        }
        else{
            int actionPosition = getActionPosition(position);
            Action action = mUserData.getFeedData().getUpcomingActions().get(actionPosition);
            if (action.getPrimaryGoal() != null){
                category = action.getPrimaryGoal().getPrimaryCategory();
                if (category == null){
                    Goal goal = mUserData.getGoal(action.getPrimaryGoal());
                    if (goal.getCategories().size() > 0){
                        category = goal.getCategories().get(0);
                    }
                }
            }
        }

        //If the category couldn't be found or it is packaged, exclude removal options.
        if (category == null || category.isPackagedContent()){
            popup.getMenuInflater().inflate(R.menu.popup_action_packaged, popup.getMenu());
        }
        else{
            popup.getMenuInflater().inflate(R.menu.popup_action, popup.getMenu());
        }

        //Set the listener
        popup.setOnMenuItemClickListener(new CompassPopupMenu.OnMenuItemClickListener(){
            public boolean onMenuItemClick(MenuItem item){
                switch (item.getItemId()){
                    case R.id.popup_action_did_it:
                        if (position == getUpNextPosition()){
                            didIt(mUserData.getFeedData().getNextAction());
                            replaceUpNext();
                        }
                        else{
                            int actionPosition = getActionPosition(position);
                            didIt(mUserData.getFeedData().getUpcomingActions().remove(actionPosition));
                            removeActionFromFeed(actionPosition);
                        }
                        break;

                    case R.id.popup_action_reschedule:
                        Action action;
                        if (position == getUpNextPosition()){
                            action = mUserData.getFeedData().getNextAction();
                        }
                        else{
                            action = mUserData.getFeedData().getUpcomingActions().get(getActionPosition(position));
                        }
                        mListener.onTriggerSelected(action);
                        break;

                    case R.id.popup_action_remove:
                        if (position == getUpNextPosition()){
                            removeAction(mUserData.getFeedData().getNextAction());
                            replaceUpNext();
                        }
                        else{
                            int actionPosition = getActionPosition(position);
                            removeAction(mUserData.getFeedData().getUpcomingActions().remove(actionPosition));
                            removeActionFromFeed(actionPosition);
                        }
                        break;

                    case R.id.popup_action_view_goal:
                        Action selectedAction;
                        if (position == getUpNextPosition()){
                            selectedAction = mUserData.getFeedData().getNextAction();
                        }
                        else{
                            selectedAction = mUserData.getFeedData().getUpcomingActions().get(getActionPosition(position));
                        }
                        //TODO this is another workaround
                        if (selectedAction.getPrimaryGoal() != null){
                            mListener.onGoalSelected(selectedAction.getPrimaryGoal());
                        }
                        break;
                }
                return true;
            }
        });

        //Show the menu.
        popup.show();
    }

    /**
     * Marks an action as done.
     *
     * @param action the action to be marked as done.
     */
    private void didIt(Action action){
        Intent completeAction = new Intent(mContext, ActionReportService.class)
                .putExtra(ActionReportService.ACTION_MAPPING_ID_KEY, action.getMappingId())
                .putExtra(ActionReportService.STATE_KEY, ActionReportService.STATE_COMPLETED);
        mContext.startService(completeAction);

        FeedData feedData = mUserData.getFeedData();
        feedData.setCompletedActions(feedData.getCompletedActions()+1);
        feedData.setProgressPercentage(feedData.getCompletedActions() * 100 / feedData.getTotalActions());
        notifyItemChanged(getUpNextPosition());
    }

    /**
     * Calculates the position of an action given its position in the list,
     *
     * @param adapterPosition a position in the list.
     * @return the position of the action in the backing array.
     */
    private int getActionPosition(int adapterPosition){
        return adapterPosition-(getUpcomingHeaderPosition()+1);
    }

    /**
     * Removes an action from the user list.
     *
     * @param action the action to be removed.
     */
    private void removeAction(Action action){
        mUserData.removeAction(action);

        FeedData feedData = mUserData.getFeedData();
        feedData.setTotalActions(feedData.getTotalActions() - 1);
        feedData.setProgressPercentage(feedData.getCompletedActions() * 100 / feedData.getTotalActions());
        notifyItemChanged(getUpNextPosition());

        new DeleteActionTask(mContext, null, action.getMappingId()+"").execute();
    }

    /**
     * Removes an action from the feed.
     *
     * @param position the position if the item in the feed.
     */
    private void removeActionFromFeed(int position){
        //Update the relevant action cards
        if (mUserData.getFeedData().getUpcomingActions().isEmpty()){
            int headerPosition = getUpcomingHeaderPosition();
            notifyItemRemoved(headerPosition);
            notifyItemRemoved(headerPosition+1);
        }
        else{
            notifyItemRemoved(position);
        }

        notifyItemChanged(getUpcomingLastItemPosition()-1);
        notifyItemChanged(getUpcomingLastItemPosition());
        notifyItemChanged(getUpcomingLastItemPosition()+1);
        notifyItemChanged(getMyGoalsLastItemPosition());
    }

    /**
     * Animates the replacement of the up next card.
     */
    private void replaceUpNext(){
        if (mUserData.getFeedData().getUpcomingActions().isEmpty()){
            //If there are no upcoming actions, net the up next action to null
            mUserData.getFeedData().setNextAction(null);
        }
        else{
            //Otherwise, remove the next from the queue and set it as next
            Action action = mUserData.getFeedData().getUpcomingActions().remove(0);
            mUserData.getFeedData().setNextAction(action);

            //Update the relevant action cards
            int headerPosition = getUpcomingHeaderPosition();
            if (mUserData.getFeedData().getUpcomingActions().isEmpty()){
                notifyItemRemoved(headerPosition);
            }
            notifyItemRemoved(headerPosition+1);

            notifyItemRangeChanged(headerPosition+2, getMyGoalsHeaderPosition());
            /*notifyItemChanged(getUpcomingLastItemPosition()+1);
            notifyItemChanged(getUpcomingLastItemPosition());
            notifyItemChanged(getUpcomingLastItemPosition()-1);*/
        }

        //Update the up next card
        notifyItemChanged(getUpNextPosition());
    }


    /**
     * View holder for the up next card.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class UpNextHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //Header
        private TextView mHeader;
        private View mOverflow;

        //Indicator
        private CircleProgressView mIndicator;

        //No actions content
        private View mNoActionsContainer;
        private TextView mNoActionsTitle;
        private TextView mNoActionsSubtitle;

        //Action content
        private View mContentContainer;
        private TextView mAction;
        private TextView mGoal;

        //Footer
        private TextView mIndicatorCaption;
        private TextView mTime;


        /**
         * Constructor.
         *
         * @param rootView the root view held by the holder.
         */
        public UpNextHolder(View rootView){
            super(rootView);

            mHeader = (TextView)rootView.findViewById(R.id.up_next_header);
            mOverflow = rootView.findViewById(R.id.up_next_overflow_box);

            mIndicator = (CircleProgressView)rootView.findViewById(R.id.up_next_indicator);

            mNoActionsContainer = rootView.findViewById(R.id.up_next_no_actions);
            mNoActionsTitle = (TextView)rootView.findViewById(R.id.up_next_no_actions_title);
            mNoActionsSubtitle = (TextView)rootView.findViewById(R.id.up_next_no_actions_subtitle);

            mContentContainer = rootView.findViewById(R.id.up_next_content);
            mAction = (TextView)rootView.findViewById(R.id.up_next_action);
            mGoal = (TextView)rootView.findViewById(R.id.up_next_goal);

            mIndicatorCaption = (TextView)rootView.findViewById(R.id.up_next_indicator_caption);
            mTime = (TextView)rootView.findViewById(R.id.up_next_time);

            rootView.setOnClickListener(this);
            mOverflow.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            mSelectedItem = getAdapterPosition();
            switch (view.getId()){
                case R.id.up_next_overflow_box:
                    showActionPopup(view, getAdapterPosition());
                    break;

                default:
                    if (hasUpNextAction()){
                        mListener.onActionSelected(mUserData.getFeedData().getNextAction());
                    }
            }
        }
    }


    /**
     * View holder for the feedback card.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class FeedbackHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitle;
        private TextView mSubtitle;


        /**
         * Constructor.
         *
         * @param rootView the root view held by this holder.
         */
        public FeedbackHolder(View rootView){
            super(rootView);

            mTitle = (TextView)rootView.findViewById(R.id.card_feedback_title);
            mSubtitle = (TextView)rootView.findViewById(R.id.card_feedback_subtitle);

            rootView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            mListener.onFeedbackSelected(mFeedbackGoal);
        }
    }


    /**
     * View holder for a header view.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class HeaderHolder extends RecyclerView.ViewHolder{
        private TextView mTitle;


        /**
         * Constructor.
         *
         * @param rootView the root view held by this holder.
         */
        public HeaderHolder(View rootView){
            super(rootView);
            mTitle = (TextView)rootView.findViewById(R.id.header_title);
        }
    }


    /**
     * View holder for an action card.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class ActionHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private View mOverflow;
        private TextView mAction;
        private TextView mGoal;
        private TextView mTime;


        /**
         * Constructor.
         *
         * @param rootView the root view held by the holder.
         */
        public ActionHolder(View rootView){
            super(rootView);

            mOverflow = rootView.findViewById(R.id.action_overflow_box);
            mAction = (TextView)rootView.findViewById(R.id.action_title);
            mGoal = (TextView)rootView.findViewById(R.id.action_goal);
            mTime = (TextView)rootView.findViewById(R.id.action_time);

            rootView.setOnClickListener(this);
            mOverflow.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            mSelectedItem = getAdapterPosition();
            int index = getAdapterPosition()-(getUpcomingHeaderPosition()+1);
            Action action = mUserData.getFeedData().getUpcomingActions().get(index);
            switch (view.getId()){
                case R.id.action_overflow_box:
                    showActionPopup(view, getAdapterPosition());
                    break;

                default:
                    mListener.onActionSelected(action);
            }
        }
    }


    /**
     * View holder for a goal card.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class GoalHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private RelativeLayout mIconContainer;
        private ImageView mIcon;
        private TextView mTitle;


        /**
         * Constructor.
         *
         * @param rootView the root view held by this holder.
         */
        public GoalHolder(View rootView){
            super(rootView);

            mIconContainer = (RelativeLayout)rootView.findViewById(R.id.card_goal_icon_container);
            mIcon = (ImageView)rootView.findViewById(R.id.card_goal_icon);
            mTitle = (TextView)rootView.findViewById(R.id.card_goal_title);
            
            rootView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            Goal goal;
            int position = getAdapterPosition()-(getMyGoalsHeaderPosition()+1);
            if (mUserData.getGoals().isEmpty()){
                goal = mUserData.getFeedData().getSuggestions().get(position);
            }
            else{
                goal = mUserData.getGoals().get(position);
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
         *
         * @param context the context.
         * @param adapter a reference to the adapter.
         */
        private MainFeedPadding(Context context, MainFeedAdapter adapter){
            mAdapter = adapter;
            mMargin = CompassUtil.getPixels(context, 12);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state){

            int position = parent.getChildLayoutPosition(view);

            //Header: padding on top
            if (mAdapter.isUpcomingHeaderPosition(position) || mAdapter.isMyGoalsHeaderPosition(position)){
                outRect.top = mMargin / 2;
                outRect.left = mMargin;
                outRect.bottom = 0;
                outRect.right = mMargin;
            }
            //Last item: padding on the bottom
            else if (mAdapter.isUpcomingLastItemPosition(position) || mAdapter.getMyGoalsLastItemPosition() == position){
                outRect.top = 0;
                outRect.left = mMargin;
                outRect.bottom = mMargin/2;
                outRect.right = mMargin;
            }
            //Inner item: no padding at either side
            else if (mAdapter.isUpcomingInnerPosition(position) || mAdapter.isMyGoalsInnerPosition(position)){
                outRect.top = 0;
                outRect.left = mMargin;
                outRect.bottom = 0;
                outRect.right = mMargin;
            }
            //Other cards: padding everywhere
            else{
                outRect.top = mMargin / 2;
                outRect.left = mMargin;
                outRect.bottom = mMargin / 2;
                outRect.right = mMargin;
            }
        }
    }


    /**
     * Listener interface for the main feed adapter.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface MainFeedAdapterListener{
        /**
         * Called when the user data is null.
         */
        void onNullData();

        /**
         * Called when the welcome card is tapped.
         */
        void onInstructionsSelected();

        /**
         * Called when a goal is selected from either the context menu in actions or
         * goals/recommendations.
         *
         * @param goal the selected goal.
         */
        void onGoalSelected(Goal goal);

        /**
         * Called when the feedback card is tapped.
         *
         * @param goal the goal being displayed at the feedback card.
         */
        void onFeedbackSelected(Goal goal);

        /**
         * Called when an action card is tapped.
         *
         * @param action the action being displayed at the card.
         */
        void onActionSelected(Action action);

        /**
         * Called when a trigger is selected from the context menu.
         *
         * @param action the action being displayed at the card.
         */
        void onTriggerSelected(Action action);
    }
}
