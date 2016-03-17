package org.tndata.android.compass.adapter.feed;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.GoalContent;
import org.tndata.android.compass.ui.ContentContainer;

import java.util.ArrayList;
import java.util.List;


/**
 * View holder for a goal card.
 *
 * @author Ismael Alonso
 * @version 1.1.0
 */
class GoalsHolder extends MainFeedViewHolder implements View.OnClickListener{
    private TextView mHeader;
    private RecyclerView mList;
    private View mMore;

    private GoalsAdapter mGoalsAdapter;
    private List<Goal> mGoals;


    /**
     * Constructor.
     *
     * @param adapter a reference to the adapter that will handle the holder.
     * @param rootView the root view held by this holder.
     */
    GoalsHolder(@NonNull MainFeedAdapter adapter, @NonNull View rootView){
        super(adapter, rootView);

        mGoals = new ArrayList<>();
        mGoalsAdapter = new GoalsAdapter();

        mHeader = (TextView)rootView.findViewById(R.id.card_goals_header);
        mList = (RecyclerView)rootView.findViewById(R.id.card_goals_list);
        mList.setLayoutManager(new LinearLayoutManager(mAdapter.mContext));
        mList.setAdapter(mGoalsAdapter);
        mMore = rootView.findViewById(R.id.card_goals_more);
        mMore.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        mAdapter.moreGoals();
    }

    /**
     * Binds a title to the goals card.
     *
     * @param title the title to be used as header.
     */
    void bind(@NonNull String title){
        mHeader.setText(title);
    }

    void setGoals(@NonNull List<Goal> goals){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            ViewGroup target = (ViewGroup)itemView.getRootView();
            Transition transition = new ChangeBounds();
            transition.setDuration(500);
            TransitionManager.beginDelayedTransition(target, transition);
        }
        int start = mGoals.size();
        mGoals = goals;
        mGoalsAdapter.notifyItemRangeInserted(start, mGoals.size());
        mList.requestLayout();
    }

    void addGoals(@NonNull List<Goal> goals){if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
        ViewGroup target = (ViewGroup)itemView.getRootView();
        Transition transition = new ChangeBounds();
        transition.setDuration(500);
        TransitionManager.beginDelayedTransition(target, transition);
    }
        int start = mGoals.size();
        mGoals.addAll(goals);
        mGoalsAdapter.notifyItemRangeInserted(start, mGoals.size());
        mList.requestLayout();
    }

    /**
     * Adds a goal to the list.
     *
     * @param goal the goal to be added.
     */
    void addGoal(@NonNull Goal goal){
        //mContentContainer.addContent(goal);
    }

    /**
     * Refreshes the list from the feed data bundle.
     *
     * @param dataSet the new data set.
     */
    void updateGoals(@NonNull List<ContentContainer.ContainerGoal> dataSet){
        /*mContentContainer.updateContent(dataSet);
        if (mContentContainer.getCount() == dataSet.size()){
            hideFooter();
        }*/
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
        return mGoalsAdapter.getItemCount();
    }


    private class GoalsAdapter extends RecyclerView.Adapter<GoalsItemHolder>{
        @Override
        public GoalsItemHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(mAdapter.mContext);
            View rootView = inflater.inflate(R.layout.item_goal, parent, false);
            return new GoalsItemHolder(rootView);
        }

        @Override
        public void onBindViewHolder(GoalsItemHolder holder, int position){
            holder.bind(mGoals.get(position));
        }

        @Override
        public int getItemCount(){
            return mGoals.size();
        }
    }


    private class GoalsItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private View mSeparator;
        private RelativeLayout mIconContainer;
        private ImageView mIcon;
        private TextView mTitle;


        public GoalsItemHolder(View rootView){
            super(rootView);

            //Fetch UI components
            mSeparator = rootView.findViewById(R.id.goal_separator);
            mIconContainer = (RelativeLayout)rootView.findViewById(R.id.goal_icon_container);
            mIcon = (ImageView)rootView.findViewById(R.id.goal_icon);
            mTitle = (TextView)rootView.findViewById(R.id.goal_title);

            rootView.setOnClickListener(this);
        }

        /**
         * Binds a behavior to the holder.
         *
         * @param goal the behavior to be bound.
         */
        @SuppressWarnings("deprecation")
        public void bind(@NonNull Goal goal){
            //If this is the first item, do not show the separator
            if (getAdapterPosition() == 0){
                mSeparator.setVisibility(View.GONE);
            }
            else{
                mSeparator.setVisibility(View.VISIBLE);
            }

            GradientDrawable gradientDrawable = (GradientDrawable)mIconContainer.getBackground();
            //gradientDrawable.setColor(Color.parseColor(mCategory.getColor()));
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
                mIconContainer.setBackgroundDrawable(gradientDrawable);
            }
            else{
                mIconContainer.setBackground(gradientDrawable);
            }
            //goal.g.loadIconIntoView(mIcon);
            mTitle.setText(goal.getTitle());
        }

        @Override
        public void onClick(View v){

        }
    }
}
