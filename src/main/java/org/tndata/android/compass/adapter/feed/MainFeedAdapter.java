package org.tndata.android.compass.adapter.feed;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import org.json.JSONObject;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.model.UserData;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.NetworkRequest;

import java.util.List;


/**
 * Adapter for the main feed.
 *
 * @author Ismael Alonso
 * @version 1.1.0
 */
public class MainFeedAdapter extends RecyclerView.Adapter{
    //Item view types
    private static final int TYPE_BLANK = 0;
    private static final int TYPE_WELCOME = TYPE_BLANK+1;
    private static final int TYPE_UP_NEXT = TYPE_WELCOME+1;
    private static final int TYPE_FEEDBACK = TYPE_UP_NEXT+1;
    private static final int TYPE_SUGGESTION = TYPE_FEEDBACK+1;
    private static final int TYPE_HEADER = TYPE_SUGGESTION+1;
    private static final int TYPE_ACTION = TYPE_HEADER+1;
    private static final int TYPE_USER_GOAL = TYPE_ACTION+1;
    private static final int TYPE_GOAL_SUGGESTION = TYPE_USER_GOAL+1;
    private static final int TYPE_FOOTER = TYPE_GOAL_SUGGESTION +1;
    private static final int TYPE_OTHER = TYPE_FOOTER+1;


    final Context mContext;
    final MainFeedAdapterListener mListener;

    private UserData mUserData;
    private DataHandler mDataHandler;
    private FeedUtil mFeedUtil;
    private Goal mSuggestion;

    private MainFeedPadding mMainFeedPadding;

    private int mSelectedItem;


    /**
     * Constructor.
     *
     * @param context the context,
     * @param listener the listener.
     * @param initialSuggestion true the feed should display an initial suggestion.
     */
    public MainFeedAdapter(@NonNull Context context, @NonNull MainFeedAdapterListener listener,
                           boolean initialSuggestion){
        mContext = context;
        mListener = listener;
        mUserData = ((CompassApplication)mContext.getApplicationContext()).getUserData();

        if (mUserData.getFeedData() == null){
            mListener.onNullData();
        }
        else{
            mDataHandler = new DataHandler(mUserData);
            CardTypes.setDataSource(mDataHandler);
            List<Goal> suggestions = mUserData.getFeedData().getSuggestions();
            if (suggestions.isEmpty()){
                mSuggestion = null;
            }
            else{
                mSuggestion = suggestions.get((int)(Math.random()*suggestions.size()));
            }
            mFeedUtil = new FeedUtil(this);
        }
        mMainFeedPadding = null;

        if (mSuggestion != null){
            if (initialSuggestion){
                CardTypes.displaySuggestion(true);
            }
            else{
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        CardTypes.displaySuggestion(true);
                        notifyItemInserted(CardTypes.getSuggestionPosition());
                        notifyItemRangeChanged(CardTypes.getSuggestionPosition() + 1, getItemCount() - 1);
                    }
                }, 2000);
            }
        }
    }


    /*------------------------------------*
     * OVERRIDDEN ADAPTER RELATED METHODS *
     *------------------------------------*/

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
        if (CardTypes.isSuggestion(position)){
            return TYPE_SUGGESTION;
        }
        if (CardTypes.isUpcomingHeader(position) || CardTypes.isMyGoalsHeader(position)){
            return TYPE_HEADER;
        }
        if (CardTypes.isUpcomingAction(position)){
            return TYPE_ACTION;
        }
        if (CardTypes.isMyGoal(position)){
            return TYPE_USER_GOAL;
        }
        if (CardTypes.isGoalSuggestion(position)){
            return TYPE_GOAL_SUGGESTION;
        }
        if (CardTypes.isUpcomingFooter(position) || CardTypes.isMyGoalsFooter(position)){
            return TYPE_FOOTER;
        }
        return TYPE_OTHER;
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
        else if (viewType == TYPE_SUGGESTION){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            return new GoalSuggestionHolder(this, inflater.inflate(R.layout.card_goal_suggestion, parent, false));
        }
        else if (viewType == TYPE_HEADER){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            return new HeaderHolder(this, inflater.inflate(R.layout.card_header, parent, false));
        }
        else if (viewType == TYPE_ACTION){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            return new ActionHolder(this, inflater.inflate(R.layout.card_action, parent, false));
        }
        else if (viewType == TYPE_USER_GOAL || viewType == TYPE_GOAL_SUGGESTION){
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
            ((UpNextHolder)rawHolder).bind(mDataHandler.getUpNext());
        }
        //Feedback
        else if (CardTypes.isFeedback(position)){
            FeedbackHolder holder = (FeedbackHolder)rawHolder;
            holder.mIcon.setImageResource(mUserData.getFeedData().getFeedbackIcon());
            holder.mTitle.setText(mUserData.getFeedData().getFeedbackTitle());
            holder.mSubtitle.setText(mUserData.getFeedData().getFeedbackSubtitle());
        }
        //Goal suggestion card
        else if (CardTypes.isSuggestion(position)){
            GoalSuggestionHolder holder = (GoalSuggestionHolder)rawHolder;
            holder.mTitle.setText(mSuggestion.getTitle());
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
            ((ActionHolder)rawHolder).bind(mDataHandler.getUpcoming(getActionPosition(position)));
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
        else if (CardTypes.isMyGoal(position)){
            ((CardView)rawHolder.itemView).setRadius(0);

            int goalPosition = position - CardTypes.getMyGoalsHeaderPosition()-1;
            ((GoalHolder)rawHolder).bind(mDataHandler.getUserGoals().get(goalPosition));
        }
        //My goals / suggestions
        else if (CardTypes.isGoalSuggestion(position)){
            ((CardView)rawHolder.itemView).setRadius(0);

            int goalPosition = position - CardTypes.getMyGoalsHeaderPosition()-1;
            ((GoalHolder)rawHolder).bind(mDataHandler.getSuggestions().get(goalPosition));
        }
        else if (CardTypes.isMyGoalsFooter(position)){
            ((CardView)rawHolder.itemView).setRadius(0);
        }
    }

    @Override
    public int getItemCount(){
        return CardTypes.getItemCount();
    }


    /*----------------------*
     * FEED ADAPTER METHODS *
     *----------------------*/

    /**
     * Sets the card parameters for pre L devices.
     *
     * @param view the card view whose parameters shall be set.
     */
    private void setPreLParameters(CardView view){
        view.setMaxCardElevation(0);
        view.setCardBackgroundColor(mContext.getResources().getColor(R.color.card_pre_l_background));
    }

    DataHandler getDataHandler(){
        return mDataHandler;
    }

    void setSelectedItem(int selectedItem){
        mSelectedItem = selectedItem;
    }

    public int getMyGoalsHeaderPosition(){
        return CardTypes.getMyGoalsHeaderPosition();
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


    /*------------------------*
     * ACTION RELATED METHODS *
     *------------------------*/

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
     * Generic did it to be called when events are triggered outside the adapter. It
     * uses the selected item set using the setSelectedItem(int) method. If no item
     * was set as selected prior to the call, it is ignored. The selected item is
     * reset when this method is called.
     */
    public void didIt(){
        if (mSelectedItem != -1){
            didIt(mSelectedItem);
            mSelectedItem = -1;
        }
    }

    /**
     * Marks an action as done in every possible way. This is done in three steps:
     * (1) mark the action as complete within the model and the webapp through an
     * api request, (2) update the data set, and (3) update the adapter to reflect
     * the changes in the data set.
     *
     * @param position the adapter position of the item to be marked as done.
     */
    void didIt(int position){
        if (CardTypes.isUpNext(position)){
            mDataHandler.didIt();
            mFeedUtil.didIt(mContext, mDataHandler.getUpNext());
            mDataHandler.replaceUpNext();
            replaceUpNext();
        }
        else if (CardTypes.isUpcomingAction(position)){
            mDataHandler.didIt();
            mFeedUtil.didIt(mContext, mDataHandler.removeUpcoming(getActionPosition(position)));
            removeActionFromFeed(position);
        }
    }

    /**
     * Marks the given item as selected and then lets the listener know that the
     * trigger editor needs to be opened. If the trigger was modified when the
     * activity is finished the updateSelectedItem() method should be called to
     * make the change reflect in the UI.
     *
     * @param position the adapter position of the item to be rescheduled.
     */
    void reschedule(int position){
        mSelectedItem = position;
        if (CardTypes.isUpNext(position)){
            mListener.onTriggerSelected(getDataHandler().getUpNext());
        }
        else if (CardTypes.isUpcomingAction(position)){
            mListener.onTriggerSelected(getDataHandler().getUpcoming().get(getActionPosition(position)));
        }
        else{
            mSelectedItem = -1;
        }
    }

    /**
     * Removes the selected action from the user data bundle. This is done in three steps:
     * (1) the action is removed in the webapp through an api call, (2) the action is
     * removed in the local model, and (3) the adapter is updated to reflect the changes.
     *
     * @param position the adapter position of the item to be removed.
     */
    void remove(int position){
        UserAction action;
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
        NetworkRequest.delete(mContext, null, API.getDeleteActionUrl(action),
                ((CompassApplication)mContext.getApplicationContext()).getToken(), new JSONObject());
    }

    /**
     * Lets the listener know that GoalActivity should be opened for the primary goal of
     * the selected action, if the action has one.
     *
     * @param position the adapter position of the action whose goal is to be viewed.
     */
    void viewGoal(int position){
        UserAction action;
        if (CardTypes.isUpNext(position)){
            action = mUserData.getFeedData().getNextAction();
        }
        else{
            action = mUserData.getFeedData().getUpcomingActions().get(getActionPosition(position));
        }
        //TODO this is another workaround
        if (action.getPrimaryGoal() != null){
            mListener.onGoalSelected(mUserData.getGoal(action.getPrimaryGoal()));
        }
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
            notifyItemRemoved(position - 1);
        }
        notifyItemRemoved(position);
        notifyItemRangeChanged(position - 1, getItemCount() - (position - 1));
    }


    /*----------------------*
     * GOAL RELATED METHODS *
     *----------------------*/

    void showSuggestionPopup(View anchor){
        mFeedUtil.showSuggestionPopup(anchor);
    }

    void refreshSuggestion(){
        List<Goal> suggestions = mUserData.getFeedData().getSuggestions();
        mSuggestion = suggestions.get((int)(Math.random()*suggestions.size()));
        notifyItemChanged(CardTypes.getSuggestionPosition());
    }

    public void dismissSuggestion(){
        CardTypes.displaySuggestion(false);
        notifyItemRemoved(CardTypes.getSuggestionPosition());
        notifyItemRangeChanged(CardTypes.getSuggestionPosition() + 1, getItemCount() - 1);
        mListener.onSuggestionDismissed();
    }

    void viewSuggestion(){
        mListener.onSuggestionOpened(mSuggestion);
    }


    /*------------------------*
     * FOOTER RELATED METHODS *
     *------------------------*/

    /**
     * Makes the feed display more actions or goals, depending on which footer was tapped.
     *
     * @param position the adapter position of the tapped footer.
     */
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


    /*--------------------------------------------------*
     * ACTIONS TO BE CARRIED OUT UPON THE SELECTED ITEM *
     *--------------------------------------------------*/

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

    public void dataSetChanged(){
        mDataHandler.reload();
        notifyDataSetChanged();
    }
}
