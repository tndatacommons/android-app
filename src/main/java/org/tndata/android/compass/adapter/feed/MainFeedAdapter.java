package org.tndata.android.compass.adapter.feed;

import android.content.Context;
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
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.GoalContent;
import org.tndata.android.compass.model.UserData;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.NetworkRequest;

import java.util.List;


/**
 * Adapter for the main feed.
 *
 * @author Ismael Alonso
 * @version 2.0.0
 */
public class MainFeedAdapter extends RecyclerView.Adapter{
    //Item view types
    private static final int TYPE_BLANK = 0;
    private static final int TYPE_WELCOME = TYPE_BLANK+1;
    private static final int TYPE_UP_NEXT = TYPE_WELCOME+1;
    private static final int TYPE_FEEDBACK = TYPE_UP_NEXT+1;
    private static final int TYPE_SUGGESTION = TYPE_FEEDBACK+1;
    private static final int TYPE_UPCOMING = TYPE_SUGGESTION+1;
    private static final int TYPE_GOALS = TYPE_UPCOMING +1;
    private static final int TYPE_OTHER = TYPE_GOALS+1;


    final Context mContext;
    final MainFeedAdapterListener mListener;

    private UserData mUserData;
    private DataHandler mDataHandler;
    private FeedUtil mFeedUtil;
    private GoalContent mSuggestion;

    private MainFeedPadding mMainFeedPadding;

    private UpcomingHolder mUpcomingHolder;
    private GoalsHolder mGoalsHolder;

    private Action mSelectedAction;


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
            List<GoalContent> suggestions = mUserData.getFeedData().getSuggestions();
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
        if (CardTypes.isUpcoming(position)){
            return TYPE_UPCOMING;
        }
        if (CardTypes.isGoals(position)){
            return TYPE_GOALS;
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
        else if (viewType == TYPE_UPCOMING){
            if (mUpcomingHolder == null){
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View rootView = inflater.inflate(R.layout.card_upcoming, parent, false);
                mUpcomingHolder = new UpcomingHolder(this, rootView);
            }
            return mUpcomingHolder;
        }
        else if (viewType == TYPE_GOALS){
            if (mGoalsHolder == null){
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View rootView = inflater.inflate(R.layout.card_goals, parent, false);
                mGoalsHolder = new GoalsHolder(this, rootView);
            }
            return mGoalsHolder;
        }
        else if (viewType == TYPE_OTHER){
            return new RecyclerView.ViewHolder(new CardView(mContext)){};
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
        //This is a possible fix to a crash where the application gets destroyed and the
        //  user data gets invalidated. In such a case, the app should restart and fetch
        //  the user data again. Bottomline, do not keep going
        if (mUserData.getFeedData() == null){
            return;
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
            ((FeedbackHolder)rawHolder).bind(mUserData.getFeedData());
        }
        //Goal suggestion card
        else if (CardTypes.isSuggestion(position)){
            GoalSuggestionHolder holder = (GoalSuggestionHolder)rawHolder;
            holder.mTitle.setText(mSuggestion.getTitle());
        }
        //Today's activities / upcoming
        else if (CardTypes.isUpcoming(position)){
            if (mUpcomingHolder.getItemCount() == 0){
                moreActions();
            }
        }
        //My goals / suggestions
        else if (CardTypes.isGoals(position)){
            if (mGoalsHolder.getItemCount() == 0){
                String title;
                if (mUserData.getGoals().isEmpty()){
                    title = mContext.getString(R.string.card_suggestions_header);
                }
                else{
                    title = mContext.getString(R.string.card_my_goals_header);
                }
                ((GoalsHolder)rawHolder).bind(title);
                moreGoals();
            }
        }
    }

    @Override
    public int getItemCount(){
        return CardTypes.getItemCount();
    }


    /*----------------------*
     * FEED ADAPTER METHODS *
     *----------------------*/
    DataHandler getDataHandler(){
        return mDataHandler;
    }

    void setSelectedAction(Action selectedAction){
        mSelectedAction = selectedAction;
    }

    public int getGoalsPosition(){
        return CardTypes.getGoalsPosition();
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
     * Display the popup menu for a specific action.
     *
     * @param anchor the view it should be anchored to.
     * @param action the action in question.
     */
    void showActionPopup(View anchor, Action action){
        mFeedUtil.showActionPopup(anchor, action);
    }

    /**
     * Generic did it to be called when events are triggered outside the adapter. It
     * uses the selected item set using the setSelectedAction(int) method. If no item
     * was set as selected prior to the call, it is ignored. The selected item is
     * reset when this method is called.
     */
    public void didIt(){
        if (mSelectedAction != null){
            didIt(mSelectedAction);
            mSelectedAction = null;
        }
    }

    /**
     * Marks an action as done in every possible way. This is done in three steps:
     * (1) mark the action as complete within the model and the webapp through an
     * api request, (2) update the data set, and (3) update the adapter to reflect
     * the changes in the data set.
     *
     * @param action the action to be marked as done.
     */
    void didIt(Action action){
        if (mUserData.getFeedData().getNextAction().equals(action)){
            mDataHandler.didIt();
            mFeedUtil.didIt(mContext, mDataHandler.getUpNext());
            mDataHandler.replaceUpNext();
            mUpcomingHolder.removeFirstAction();
        }
        else{
            mDataHandler.didIt();
            mFeedUtil.didIt(mContext, action);
            mUpcomingHolder.removeAction(action);
        }
    }

    /**
     * Marks the given item as selected and then lets the listener know that the
     * trigger editor needs to be opened. If the trigger was modified when the
     * activity is finished the updateSelectedItem() method should be called to
     * make the change reflect in the UI.
     *
     * @param action the action to be rescheduled.
     */
    void reschedule(Action action){
        mSelectedAction = action;
        mListener.onTriggerSelected(mSelectedAction);
    }

    /**
     * Removes the selected action from the user data bundle. This is done in three steps:
     * (1) the action is removed in the webapp through an api call, (2) the action is
     * removed in the local model, and (3) the adapter is updated to reflect the changes.
     *
     * @param action the action to be removed.
     */
    void remove(Action action){
        mDataHandler.remove(action);
        mUpcomingHolder.removeAction(action);
        NetworkRequest.delete(mContext, null, API.getDeleteActionUrl(action),
                ((CompassApplication)mContext.getApplicationContext()).getToken(), new JSONObject());
    }

    /**
     * Called when any view goal action is triggered, either from my goals, upcoming,
     * or suggestions.
     *
     * @param action action whose view goal menu item was tapped.
     */
    void viewGoal(Action action){
        mListener.onGoalSelected(action.getGoal());
    }


    /*----------------------*
     * GOAL RELATED METHODS *
     *----------------------*/

    void viewGoal(DisplayableGoal goal){
        if (goal instanceof Goal){
            mListener.onGoalSelected((Goal)goal);
        }
        else if (goal instanceof GoalContent){
            mListener.onSuggestionSelected((GoalContent)goal);
        }
    }

    void showSuggestionPopup(View anchor){
        //TODO I cannot test this yet
        mFeedUtil.showSuggestionPopup(anchor);
    }

    void refreshSuggestion(){
        List<GoalContent> suggestions = mUserData.getFeedData().getSuggestions();
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
        mListener.onSuggestionSelected(mSuggestion);
    }


    /*------------------------*
     * FOOTER RELATED METHODS *
     *------------------------*/

    /**
     * Loads the next batch of actions into the feed.
     */
    void moreActions(){
        for (Action action:mDataHandler.loadMoreUpcoming(mUpcomingHolder.getItemCount())){
            mUpcomingHolder.addAction(action);
        }
        if (!mDataHandler.canLoadMoreActions(mUpcomingHolder.getItemCount())){
            mUpcomingHolder.hideFooter();
        }
        mUpcomingHolder.setAnimationsEnabled(true);
    }

    /**
     * Loads the next batch of goals into the feed.
     */
    void moreGoals(){
        for (DisplayableGoal goal:mDataHandler.loadMoreGoals(mGoalsHolder.getItemCount())){
            mGoalsHolder.addGoal(goal);
        }
        if (!mDataHandler.canLoadMoreGoals(mGoalsHolder.getItemCount())){
            mGoalsHolder.hideFooter();
        }
        mGoalsHolder.setAnimationsEnabled(true);
    }


    /*--------------------------------------------------*
     * ACTIONS TO BE CARRIED OUT UPON THE SELECTED ITEM *
     *--------------------------------------------------*/

    /**
     * Updates the item marked as selected.
     */
    public void updateSelectedAction(){
        if (mSelectedAction != null){
            mUpcomingHolder.updateAction(mSelectedAction);
            mSelectedAction = null;
        }
    }

    /**
     * Deletes the item marked as selected.
     */
    /*public void deleteSelectedItem(){
        if (CardTypes.isUpNext(mSelectedItem)){
            mDataHandler.replaceUpNext();
            replaceUpNext();
        }
        else if (CardTypes.isUpcomingAction(mSelectedItem)){
            mDataHandler.removeUpcoming(getActionPosition(mSelectedItem));
            removeActionFromFeed(mSelectedItem);
        }
        mSelectedItem = -1;
    }*/

    public void dataSetChanged(){
        //mDataHandler.reload();
        notifyDataSetChanged();
    }
}
