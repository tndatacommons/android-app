package org.tndata.android.compass.adapter.feed;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.model.UpcomingAction;

import java.util.ArrayList;
import java.util.List;


/**
 * View holder for the upcoming card.
 *
 * @author Ismael Alonso
 * @version 2.0.0
 */
class UpcomingHolder extends MainFeedViewHolder implements View.OnClickListener{
    private RecyclerView mList;
    private View mMore;

    private UpcomingAdapter mUpcomingAdapter;
    private List<UpcomingAction> mActions;
    private UpcomingAction mSelectedAction;


    /**
     * Constructor.
     *
     * @param adapter a reference to the adapter that will handle the holder.
     * @param rootView the root view held by the holder.
     */
    UpcomingHolder(@NonNull MainFeedAdapter adapter, @NonNull View rootView){
        super(adapter, rootView);

        mActions = new ArrayList<>();
        mUpcomingAdapter = new UpcomingAdapter();

        mList = (RecyclerView)rootView.findViewById(R.id.card_upcoming_list);
        mList.setLayoutManager(new LinearLayoutManager(adapter.mContext));
        mList.setAdapter(mUpcomingAdapter);
        mMore = rootView.findViewById(R.id.card_upcoming_more);
        mMore.setOnClickListener(this);


    }

    @Override
    public void onClick(View view){
        if (view.getId() == R.id.card_upcoming_more){
            mAdapter.moreActions();
        }
    }

    /**
     * Adds a list of actions to the data set
     *
     * @param actions the list of actions to be added.
     */
    void addActions(@NonNull List<UpcomingAction> actions){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            ViewGroup target = (ViewGroup)itemView.getRootView();
            Transition transition = new ChangeBounds();
            transition.setDuration(500);
            TransitionManager.beginDelayedTransition(target, transition);
        }
        int start = mActions.size();
        mActions.addAll(actions);
        mUpcomingAdapter.notifyItemRangeInserted(start, mActions.size());
        mList.requestLayout();
    }

    /**
     * Refreshes the list of actions to be updated.
     *
     * @param feedData a reference to the feed data. bundle.
     */
    void updateActions(@NonNull FeedData feedData){
        mActions = feedData.getUpcomingList(mActions.size());
        mUpcomingAdapter.notifyDataSetChanged();
        mList.requestLayout();
    }

    UpcomingAction didIt(){
        if (mSelectedAction != null){
            removeAction(mSelectedAction);
        }
        UpcomingAction selectedAction = mSelectedAction;
        mSelectedAction = null;
        return selectedAction;
    }

    /**
     * Removes an action from the list.
     *
     * @param action the action to be removed.
     */
    void removeAction(@NonNull UpcomingAction action){
        int index = mActions.indexOf(action);
        mActions.remove(index);
        mUpcomingAdapter.notifyItemRemoved(index);
    }

    /**
     * Removes the first action in the list.
     */
    void removeFirstAction(){
        mActions.remove(0);
        mAdapter.notifyItemRemoved(0);
    }

    /**
     * Hides the footer of the card.
     */
    void hideFooter(){
        mMore.setVisibility(View.GONE);
    }

    /**
     * Gets the number of items in the list.
     *
     * @return the number of items in the list.
     */
    int getItemCount(){
        return mUpcomingAdapter.getItemCount();
    }

    public void onActionOverflowClick(@NonNull View view, @NonNull UpcomingAction action){
        //mAdapter.showActionPopup(view, action);
    }


    private class UpcomingAdapter extends RecyclerView.Adapter<UpcomingItemHolder>{
        @Override
        public UpcomingItemHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(mAdapter.mContext);
            View rootView = inflater.inflate(R.layout.item_upcoming_action, parent, false);
            return new UpcomingItemHolder(rootView);
        }

        @Override
        public void onBindViewHolder(UpcomingItemHolder holder, int position){
            holder.bind(mActions.get(position));
        }

        @Override
        public int getItemCount(){
            return mActions.size();
        }
    }


    private class UpcomingItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitle;
        private TextView mGoal;
        private TextView mTime;


        public UpcomingItemHolder(View rootView){
            super(rootView);

            mTitle = (TextView)rootView.findViewById(R.id.action_title);
            mGoal = (TextView)rootView.findViewById(R.id.action_goal);
            mTime = (TextView)rootView.findViewById(R.id.action_time);

            rootView.setOnClickListener(this);
            rootView.findViewById(R.id.action_overflow_box).setOnClickListener(this);
        }

        public void bind(@NonNull UpcomingAction action){
            mTitle.setText(action.getTitle());
            mGoal.setText(action.getGoalTitle());
            mTime.setText(action.getTriggerDisplay());
        }

        @Override
        public void onClick(View view){
            switch (view.getId()){
                case R.id.action_overflow_box:
                    break;

                default:
                    mSelectedAction = mActions.get(getAdapterPosition());
                    mAdapter.mListener.onActionSelected(mSelectedAction);
            }
        }
    }
}
