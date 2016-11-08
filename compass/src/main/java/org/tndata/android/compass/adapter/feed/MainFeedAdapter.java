package org.tndata.android.compass.adapter.feed;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.Space;
import android.widget.Toast;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.databinding.CardBaseItemBinding;
import org.tndata.android.compass.databinding.CardDynamicListBinding;
import org.tndata.android.compass.databinding.CardProgressBinding;
import org.tndata.android.compass.databinding.ItemBaseBinding;
import org.tndata.android.compass.holder.BaseItemCardHolder;
import org.tndata.android.compass.holder.BaseItemHolder;
import org.tndata.android.compass.holder.DynamicListCardHolder;
import org.tndata.android.compass.holder.ProgressCardHolder;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.CustomGoal;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.Reward;
import org.tndata.android.compass.model.TDCCategory;
import org.tndata.android.compass.model.TDCGoal;
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.model.UserGoal;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.FeedDataLoader;

import java.util.List;


/**
 * Adapter for the main feed.
 *
 * @author Ismael Alonso
 * @version 2.1.0
 */
public class MainFeedAdapter
        extends RecyclerView.Adapter
        implements
                View.OnClickListener,
                DynamicListCardHolder.DynamicListAdapter,
                FeedDataLoader.GoalLoadCallback{

    private static final String TAG = "MainFeedAdapter";

    //Item view types
    private static final int TYPE_BLANK = 0;
    private static final int TYPE_WELCOME = TYPE_BLANK+1;
    public static final int TYPE_UP_NEXT = TYPE_WELCOME+1;
    private static final int TYPE_SUGGESTION = TYPE_UP_NEXT+1;
    public static final int TYPE_STREAKS = TYPE_SUGGESTION+1;
    private static final int TYPE_REWARD = TYPE_STREAKS+1;
    private static final int TYPE_PROGRESS = TYPE_REWARD+1;
    private static final int TYPE_GOALS = TYPE_PROGRESS+1;
    private static final int TYPE_OTHER = TYPE_GOALS+1;


    final Context mContext;
    final Listener mListener;

    private CompassApplication mApp;

    private FeedData mFeedData;
    private FeedUtil mFeedUtil;
    private TDCGoal mSuggestion;

    private DynamicListCardHolder mGoalsHolder;


    /**
     * Constructor.
     *
     * @param context the context,
     * @param listener the listener.
     * @param initialSuggestion true the feed should display an initial suggestion.
     */
    public MainFeedAdapter(@NonNull Context context, @NonNull Listener listener,
                           boolean initialSuggestion){
        mContext = context;
        mListener = listener;
        mFeedData = ((CompassApplication)mContext.getApplicationContext()).getFeedData();

        mApp = (CompassApplication)mContext.getApplicationContext();

        if (mFeedData == null){
            mListener.onNullData();
        }
        else{
            CardTypes.setDataSource(mFeedData);
            List<TDCGoal> suggestions = mFeedData.getSuggestions();
            if (suggestions.isEmpty()){
                mSuggestion = null;
            }
            else{
                mSuggestion = suggestions.get((int)(Math.random()*suggestions.size()));
            }
            mFeedUtil = new FeedUtil(this);
        }

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
    public int getItemCount(){
        return CardTypes.getItemCount();
    }

    @Override
    public int getItemViewType(int position){
        if (position == 0){
            return TYPE_BLANK;
        }
        if (CardTypes.isWelcome(position)){
            return TYPE_WELCOME;
        }
        if (CardTypes.isUpNext(position)){
            return TYPE_UP_NEXT;
        }
        if (CardTypes.isStreaks(position)){
            return TYPE_STREAKS;
        }
        if (CardTypes.isSuggestion(position)){
            return TYPE_SUGGESTION;
        }
        if (CardTypes.isReward(position)){
            return TYPE_REWARD;
        }
        if (CardTypes.isProgress(position)){
            return TYPE_PROGRESS;
        }
        if (CardTypes.isGoals(position)){
            return TYPE_GOALS;
        }
        return TYPE_OTHER;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType){
        RecyclerView.ViewHolder holder = null;
        if (viewType == TYPE_BLANK){
            holder = new RecyclerView.ViewHolder(new Space(mContext)){};
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
            holder = new RecyclerView.ViewHolder(view){};
        }
        else if (viewType == TYPE_UP_NEXT || viewType == TYPE_REWARD){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            CardBaseItemBinding binding = DataBindingUtil.inflate(
                    inflater, R.layout.card_base_item, parent, false
            );
            holder = new BaseItemCardHolder(binding);
        }
        else if (viewType == TYPE_STREAKS){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            holder = new StreaksHolder(this, inflater.inflate(R.layout.card_streaks, parent, false));
        }
        else if (viewType == TYPE_SUGGESTION){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            holder = new GoalSuggestionHolder(this, inflater.inflate(R.layout.card_goal_suggestion, parent, false));
        }
        else if (viewType == TYPE_PROGRESS){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            CardProgressBinding binding = DataBindingUtil.inflate(
                    inflater, R.layout.card_progress, parent, false
            );
            holder = new ProgressCardHolder(binding);
        }
        else if (viewType == TYPE_GOALS){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            CardDynamicListBinding binding = DataBindingUtil.inflate(
                    inflater, R.layout.card_dynamic_list, parent, false
            );
            mGoalsHolder = new DynamicListCardHolder(binding, this);
            holder = mGoalsHolder;
        }

        final RecyclerView.ViewHolder vtHolder = holder;
        ViewTreeObserver vto = vtHolder.itemView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
            @Override
            public void onGlobalLayout(){
                if (Build.VERSION.SDK_INT < 16){
                    vtHolder.itemView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                else{
                    vtHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                mListener.onItemLoaded(vtHolder.itemView, viewType);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
        //Log.d(TAG, "onBindViewHolder(): " + position);
        //This is a possible fix to a crash where the application gets destroyed and the
        //  user data gets invalidated. In such a case, the app should restart and fetch
        //  the user data again. Bottomline, do not keep going
        if (mFeedData == null){
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
            BaseItemCardHolder holder = (BaseItemCardHolder)rawHolder;
            holder.setIcon(R.drawable.ic_up_next);
            holder.setIconBackgroundColor(Color.WHITE);

            Action action = mFeedData.getUpNext();
            if (action == null){
                if (mFeedData.getProgress().getTotalActions() != 0){
                    holder.setTitle(R.string.card_up_next_title_completed);
                    holder.setSubtitle(R.string.card_up_next_subtitle_completed);
                }
                else{
                    holder.setTitle(R.string.card_up_next_title_empty);
                    holder.setSubtitle(R.string.card_up_next_subtitle_empty);
                }
            }
            else{
                holder.setTitle(action.getTitle());
                if (action instanceof UserAction){
                    String description = ((UserAction) action).getDescription();
                    if (description.length() > 30){
                        description = description.substring(0, 30) + "...";
                    }
                    holder.setSubtitle(description);
                }
                else{
                    holder.hideSubtitle();
                }
                holder.setOnClickListener(this, R.id.feed_up_next);
            }
        }
        //Streaks
        else if (CardTypes.isStreaks(position)){
            ((StreaksHolder)rawHolder).bind(mFeedData.getStreaks());
        }
        //Goal suggestion card
        else if (CardTypes.isSuggestion(position)){
            GoalSuggestionHolder holder = (GoalSuggestionHolder)rawHolder;
            holder.mTitle.setText(mSuggestion.getTitle());
        }
        //Reward
        else if (CardTypes.isReward(position)){
            BaseItemCardHolder holder = (BaseItemCardHolder)rawHolder;
            Reward reward = mFeedData.getReward();

            String message = reward.getMessage();
            message = message.substring(0, Math.min(30, message.length()));
            if (reward.getMessage().length() >= 30){
                message += "...";
            }
            holder.setIcon(reward.getIcon());
            holder.setTitle(reward.getHeader());
            holder.setSubtitle(message);
            holder.setOnClickListener(this, R.id.feed_reward);
        }
        //Progress
        else if (CardTypes.isProgress(position)){
            ProgressCardHolder holder = (ProgressCardHolder)rawHolder;
            holder.setCompletedItems(mFeedData.getProgress().getWeeklyCompletions());
            holder.setProgress(mFeedData.getProgress().getEngagementRank());
        }
        //Goals
        else if (CardTypes.isGoals(position)){
            if (CardTypes.hasMyGoals()){
                mGoalsHolder.setTitle(R.string.card_my_goals_header);
            }
            else{
                mGoalsHolder.setTitle(R.string.card_suggestions_header);
            }
            if (!FeedDataLoader.getInstance().canLoadMoreGoals()){
                mGoalsHolder.hideLoadMore();
            }
        }
    }


    /*-----------------*
     * OnClickListener *
     *-----------------*/

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.feed_up_next:
                if (mFeedData.getUpNext() != null){
                    mListener.onActionSelected(mFeedData.getUpNext());
                }
                break;

            case R.id.feed_reward:
                mListener.onRewardSelected(mFeedData.getReward());
                break;
        }
    }


    /*---------------------------*
     * DYNAMIC LIST CARD METHODS *
     *---------------------------*/

    @Override
    public int getDynamicListItemCount(){
        if (CardTypes.hasMyGoals()){
            return mFeedData.getGoals().size();
        }
        else if (CardTypes.hasGoalSuggestions()){
            return mFeedData.getSuggestions().size();
        }
        return 0;
    }

    @Override
    public int getDynamicListViewType(int position){
        //There is only one type of item
        return 0;
    }

    @Override
    public DynamicListCardHolder.DynamicItemHolder onCreateDynamicListViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ItemBaseBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.item_base, parent, false
        );
        return new BaseItemHolder(binding);
    }

    //TODO simplify.
    @Override
    @SuppressWarnings("deprecation")
    public void onBindDynamicListViewHolder(DynamicListCardHolder.DynamicItemHolder rawHolder, int position){
        BaseItemHolder holder = (BaseItemHolder)rawHolder;
        holder.showSeparator(true);
        if (CardTypes.hasMyGoals()){
            Goal goal = mFeedData.getGoals().get(position);
            holder.setTitle(goal.getTitle());
            if (goal instanceof UserGoal){
                UserGoal userGoal = (UserGoal)goal;
                long categoryId = userGoal.getPrimaryCategoryId();
                TDCCategory category = mApp.getAvailableCategories().get(categoryId);
                if (category == null){
                    holder.setIconBackgroundColor(mContext.getResources().getColor(R.color.primary));
                }
                else{
                    holder.setIconBackgroundColor(Color.parseColor(category.getColor()));
                }
                holder.setIconPadding(CompassUtil.getPixels(mContext, 20));
                holder.setIcon(((UserGoal)goal).getGoal().getIconUrl());
            }
            else if (goal instanceof CustomGoal){
                if (mApp.getUser().isFemale()){
                    holder.setIcon(R.drawable.ic_lady);
                }
                else{
                    holder.setIcon(R.drawable.ic_guy);
                }
                holder.setIconPadding(0);
            }
        }
        else if (CardTypes.hasGoalSuggestions()){
            TDCGoal goal = mFeedData.getSuggestions().get(position);
            TDCCategory category = null;
            for (Long id:goal.getCategoryIdSet()){
                if (id > 22){
                    category = mApp.getAvailableCategories().get(id);
                }
            }
            if (category == null){
                holder.setIconBackgroundColor(mContext.getResources().getColor(R.color.primary));
            }
            else{
                holder.setIconBackgroundColor(Color.parseColor(category.getColor()));
            }
            holder.setIcon(goal.getIconUrl());
            holder.setTitle(goal.getTitle());
        }
    }

    @Override
    public void onDynamicListItemClick(int position){
        if (CardTypes.hasGoals()){
            mListener.onGoalSelected(mFeedData.getGoals().get(position));
        }
        else if (CardTypes.hasGoalSuggestions()){
            mListener.onSuggestionSelected(mFeedData.getSuggestions().get(position));
        }
    }

    @Override
    public void onDynamicListLoadMore(){
        FeedDataLoader.getInstance().loadNextGoalBatch(this);
    }


    /*----------------------*
     * FEED ADAPTER METHODS *
     *----------------------*/

    /**
     * Gets the position of the goals card.
     *
     * @return the index of the goals card.
     */
    public int getGoalsPosition(){
        return CardTypes.getGoalsPosition();
    }

    /**
     * Updates the data set (up next, upcoming, and my goals).
     */
    public void updateDataSet(){
        updateGoals();
    }

    /**
     * Updates the goals card.
     */
    private void updateGoals(){
        if (mGoalsHolder != null){
            mGoalsHolder.notifyDataSetChanged();
            notifyItemChanged(CardTypes.getGoalsPosition());
        }
    }


    /*------------------------*
     * ACTION RELATED METHODS *
     *------------------------*/

    /**
     * Marks an action as done in he data set.
     */
    public void didIt(){
        mFeedData.replaceUpNext();
    }

    /**
     * Updates upcoming and up next.
     */
    public void updateUpNext(Action upNext){
        mFeedData.updateAction(upNext);
    }


    /*----------------------*
     * GOAL RELATED METHODS *
     *----------------------*/

    public void notifyGoalRemoved(int position){
        if (mGoalsHolder != null && position != -1){
            mGoalsHolder.notifyItemRemoved(position);
        }
    }

    /**
     * Shows the suggestion card popup menu.
     *
     * @param anchor the anchor view.
     */
    void showSuggestionPopup(View anchor){
        mFeedUtil.showSuggestionPopup(anchor);
    }

    /**
     * Refreshes the suggestion card.
     */
    void refreshSuggestion(){
        List<TDCGoal> suggestions = mFeedData.getSuggestions();
        mSuggestion = suggestions.get((int)(Math.random()*suggestions.size()));
        notifyItemChanged(CardTypes.getSuggestionPosition());
    }

    /**
     * Dismisses the suggestion card.
     */
    public void dismissSuggestion(){
        CardTypes.displaySuggestion(false);
        notifyItemRemoved(CardTypes.getSuggestionPosition());
        notifyItemRangeChanged(CardTypes.getSuggestionPosition() + 1, getItemCount() - 1);
        mListener.onSuggestionDismissed();
    }

    /**
     * Opens up a view to display the suggestion in the suggestion card.
     */
    void viewSuggestion(){
        mListener.onSuggestionSelected(mSuggestion);
    }


    /*----------------*
     * LOADER METHODS *
     *----------------*/

    @Override
    public void onGoalsLoaded(@Nullable List<Goal> goals){
        if (goals == null){
            mGoalsHolder.notifyItemsInserted(0);
            Toast.makeText(mContext, R.string.feed_goal_load_error, Toast.LENGTH_LONG).show();
        }
        else{
            mFeedData.addGoals(goals);
            mGoalsHolder.notifyItemsInserted(goals.size());
            if (!FeedDataLoader.getInstance().canLoadMoreGoals()){
                mGoalsHolder.hideLoadMore();
            }
        }
    }


    /**
     * Parent class of all the view holders in for the main feed adapter. Provides a reference
     * to the adapter that needs to be passed through the constructor.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    abstract static class ViewHolder extends RecyclerView.ViewHolder{
        protected MainFeedAdapter mAdapter;

        /**
         * Constructor,
         *
         * @param adapter a reference to the adapter that will handle the holder.
         * @param rootView the root view of this adapter.
         */
        protected ViewHolder(MainFeedAdapter adapter, View rootView){
            super(rootView);
            mAdapter = adapter;
        }
    }


    /**
     * Listener interface for the main feed adapter.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface Listener{
        /**
         * Called when items are created.
         *
         * @param view the root view of the item that was loaded.
         * @param type the type of the item that was loaded.
         */
        void onItemLoaded(View view, int type);

        /**
         * Called when the user data is null.
         */
        void onNullData();

        /**
         * Called when the welcome card is tapped.
         */
        void onInstructionsSelected();

        /**
         * Called when a suggestion is dismissed.
         */
        void onSuggestionDismissed();

        /**
         * Called when a goal from a goal list is selected.
         *
         * @param suggestion the selected goal suggestion.
         */
        void onSuggestionSelected(TDCGoal suggestion);

        /**
         * Called when a goal is selected.
         *
         * @param goal the selected goal.
         */
        void onGoalSelected(Goal goal);

        /**
         * Called when the streaks card is tapped.
         *
         * @param streaks list of Feedback streaks data.
         */
        void onStreaksSelected(List<FeedData.Streak> streaks);

        /**
         * Called when an action card is tapped.
         *
         * @param action the action being displayed at the card.
         */
        void onActionSelected(Action action);

        /**
         * Called when the reward card is tapped.
         *
         * @param reward the reward in the card
         */
        void onRewardSelected(Reward reward);
    }
}
