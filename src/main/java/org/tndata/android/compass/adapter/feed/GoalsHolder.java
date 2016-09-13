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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.TDCCategory;
import org.tndata.android.compass.model.CustomGoal;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.TDCGoal;
import org.tndata.android.compass.model.UserGoal;

import java.util.ArrayList;
import java.util.List;


/**
 * View holder for a goals card, either recommendations or my goals.
 *
 * @author Ismael Alonso
 * @version 1.1.0
 */
class GoalsHolder<T> extends MainFeedAdapter.ViewHolder implements View.OnClickListener{
    private TextView mHeader;
    private RecyclerView mList;
    private View mMore;
    private TextView mMoreButton;
    private ProgressBar mMoreProgress;

    private GoalsAdapter mGoalsAdapter;
    private List<T> mGoals;
    private int mPreviousSize;


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
        mMore = rootView.findViewById(R.id.card_goals_more_container);
        mMoreButton = (TextView)rootView.findViewById(R.id.card_goals_more);
        mMoreProgress = (ProgressBar)rootView.findViewById(R.id.card_goals_more_progress);

        mMoreButton.setOnClickListener(this);
    }

    void bind(String title){
        mHeader.setText(title);
    }

    @Override
    public void onClick(View view){
        mMoreButton.setVisibility(View.GONE);
        mMoreProgress.setVisibility(View.VISIBLE);
        mAdapter.moreGoals();
    }

    void setGoals(@NonNull List<T> goals){
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

    void prepareGoalAddition(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            ViewGroup target = (ViewGroup)itemView.getRootView();
            Transition transition = new ChangeBounds();
            transition.setDuration(500);
            TransitionManager.beginDelayedTransition(target, transition);
        }
        mPreviousSize = mGoals.size();
    }

    void onGoalsAdded(){
        mMoreButton.setVisibility(View.VISIBLE);
        mMoreProgress.setVisibility(View.GONE);
        mGoalsAdapter.notifyItemRangeInserted(mPreviousSize, mGoals.size());
        mList.requestLayout();
    }

    void updateGoals(){
        mAdapter.notifyDataSetChanged();
        mList.requestLayout();
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

    void notifyGoalRemoved(int position){
        //<= because the goal has already been removed
        if (position >= 0 && position <= mGoalsAdapter.getItemCount()){
            mGoalsAdapter.notifyItemRemoved(position);
        }
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
        private RelativeLayout mIconContainer;
        private ImageView mIcon;
        private TextView mTitle;


        public GoalsItemHolder(View rootView){
            super(rootView);

            //Fetch UI components
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
        public void bind(@NonNull T goal){
            CompassApplication app = (CompassApplication)mAdapter.mContext.getApplicationContext();
            GradientDrawable gradientDrawable = (GradientDrawable)mIconContainer.getBackground();
            if (goal instanceof TDCGoal){
                TDCGoal goalContent = (TDCGoal)goal;
                TDCCategory category = null;
                for (Long id:goalContent.getCategoryIdSet()){
                    if (id > 22){
                        category = app.getAvailableCategories().get(id);
                    }
                }
                if (category == null){
                    gradientDrawable.setColor(mAdapter.mContext.getResources().getColor(R.color.primary));
                }
                else{
                    gradientDrawable.setColor(Color.parseColor(category.getColor()));
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
                    mIconContainer.setBackgroundDrawable(gradientDrawable);
                }
                else{
                    mIconContainer.setBackground(gradientDrawable);
                }
                goalContent.loadIconIntoView(mIcon);
                mTitle.setText(goalContent.getTitle());
            }
            else if (goal instanceof UserGoal){
                UserGoal userGoal = (UserGoal)goal;
                TDCCategory category = app.getAvailableCategories().get(userGoal.getPrimaryCategoryId());
                if (category == null){
                    gradientDrawable.setColor(mAdapter.mContext.getResources().getColor(R.color.primary));
                }
                else{
                    gradientDrawable.setColor(Color.parseColor(category.getColor()));
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
                    mIconContainer.setBackgroundDrawable(gradientDrawable);
                }
                else{
                    mIconContainer.setBackground(gradientDrawable);
                }
                userGoal.getGoal().loadIconIntoView(mIcon);
                mTitle.setText(userGoal.getTitle());
            }
            else if (goal instanceof CustomGoal){
                CustomGoal customGoal = (CustomGoal)goal;
                if (app.getUser().isFemale()){
                    mIconContainer.setBackgroundResource(R.drawable.ic_lady);
                }
                else{
                    mIconContainer.setBackgroundResource(R.drawable.ic_guy);
                }
                mIcon.setImageResource(0);
                mTitle.setText(customGoal.getTitle());
            }
        }

        @Override
        public void onClick(View v){
            T goal = mGoals.get(getAdapterPosition());
            if (goal instanceof TDCGoal){
                mAdapter.mListener.onSuggestionSelected((TDCGoal)goal);
            }
            else if (goal instanceof Goal){
                mAdapter.mListener.onGoalSelected((Goal)goal);
            }
        }
    }
}
