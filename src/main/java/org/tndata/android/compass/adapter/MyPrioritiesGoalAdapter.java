package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.Trigger;
import org.tndata.android.compass.ui.PriorityItemView;
import org.tndata.android.compass.util.ImageLoader;

import java.util.LinkedList;


/**
 * Adapter for the goal list on my priorities. It handles all of its events and animations.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class MyPrioritiesGoalAdapter extends RecyclerView.Adapter<MyPrioritiesGoalAdapter.ViewHolder>{
    //The expansion mode, if set to true, when a view expands the opened one (if any) collapses
    private final boolean mSingleExpandedGoalMode;

    //Context, category, and listener
    private Context mContext;
    private Category mCategory;
    private boolean mEmptyCategory;
    private OnItemClickListener mListener;

    private ViewHolder mClickedHolder;

    private boolean[] mExpandedGoals;
    private ViewHolder mExpandedGoal;

    private int mExpanded;
    private int mCollapsing;


    /**
     * Constructor.
     *
     * @param context the application context.
     * @param category the selected category.
     * @param listener the receiver of tap events.
     */
    public MyPrioritiesGoalAdapter(@NonNull Context context, @NonNull Category category,
                                   @NonNull OnItemClickListener listener){
        mSingleExpandedGoalMode = true;

        mContext = context;
        mCategory = category;
        mListener = listener;

        mEmptyCategory = mCategory.getGoals().isEmpty();

        mClickedHolder = null;

        mExpandedGoals = new boolean[mCategory.getGoals().size()];
        for (int i = 0; i < mExpandedGoals.length; i++){
            mExpandedGoals[i] = false;
        }
        mExpandedGoal = null;
        mExpanded = -1;
        mCollapsing = -1;

        ViewHolder.viewPool = new LinkedList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_my_priorities_goal, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        if (mCategory.getGoals().size() == 0){
            holder.name.setText("Add a new activity");
        }
        else{
            holder.name.setText(mCategory.getGoals().get(position).getTitle());
        }
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public int getItemCount(){
        return (!mEmptyCategory) ? mCategory.getGoals().size() : 1;
    }

    /**
     * Populates the offspring of a goal with behaviors, actions, and triggers.
     *
     * @param holder the view holder hosting the goal.
     * @param position the position of the goal in the backing list.
     */
    private void populate(ViewHolder holder, int position){
        Goal goal = mCategory.getGoals().get(position);
        //For each behavior in the goal
        for (Behavior behavior:goal.getBehaviors()){
            //A priority item view is retrieved and populated
            PriorityItemView behaviorView = getPriorityItemView();
            behaviorView.setItemHierarchy(new ItemHierarchy(mCategory, goal, behavior, null));
            behaviorView.setLeftPadding(20);
            behaviorView.getTextView().setText(behavior.getTitle());
            if (behavior.getIconUrl() != null){
                ImageLoader.loadBitmap(behaviorView.getImageView(), behavior.getIconUrl(), new ImageLoader.Options());
            }
            behaviorView.setOnClickListener(holder);

            //The view is added to the goal's offspring
            holder.offspring.addView(behaviorView);
            Log.d("BehaviourActions", behavior.getActions().size() + "");

            //For each action in the behavior
            for (Action action:behavior.getActions()){
                PriorityItemView actionView = getPriorityItemView();
                actionView.setItemHierarchy(new ItemHierarchy(mCategory, goal, behavior, action));
                actionView.setLeftPadding(30);
                actionView.getTextView().setText(action.getTitle());
                if (action.getIconUrl() != null){
                    ImageLoader.loadBitmap(actionView.getImageView(), action.getIconUrl(), new ImageLoader.Options());
                }
                actionView.setOnClickListener(holder);
                holder.offspring.addView(actionView);

                Trigger trigger = action.getCustomTrigger();
                if (trigger != null){
                    PriorityItemView triggerView = getPriorityItemView();
                    triggerView.setItemHierarchy(new ItemHierarchy(mCategory, goal, behavior, action));
                    triggerView.setLeftPadding(65);
                    String triggerText = trigger.getRecurrencesDisplay();
                    String date = trigger.getFormattedDate();
                    if (!date.equals("")){
                        triggerText += " " + date;
                    }
                    String triggerDate = trigger.getFormattedTime();
                    if (!triggerDate.equals("")){
                        triggerText += " " + triggerDate;
                    }
                    if (!triggerText.equals("")){
                        triggerView.getTextView().setText(triggerText);
                        triggerView.getImageView().setVisibility(View.GONE);
                        triggerView.setOnClickListener(holder);
                        holder.offspring.addView(triggerView);
                    }
                    else{
                        ViewHolder.recycleView(triggerView);
                    }
                }
            }
        }

        if (goal.getBehaviorCount() > 0){
            //Add behaviours view
            PriorityItemView addBehaviors = getPriorityItemView();
            addBehaviors.setItemHierarchy(new ItemHierarchy(mCategory, goal, null, null));
            addBehaviors.setLeftPadding(0);
            addBehaviors.getTextView().setText(R.string.my_priorities_edit_activities);
            addBehaviors.getImageView().setVisibility(View.GONE);
            addBehaviors.setOnClickListener(holder);
            holder.offspring.addView(addBehaviors);
        }
    }

    /**
     * Recycles the offspring of a goal.
     *
     * @param holder the view holder hosting the goal.
     */
    private void recycle(ViewHolder holder){
        //Add all the views to the recycled queue and clear the offspring.
        for (int i = 0; i < holder.offspring.getChildCount(); i++){
            ViewHolder.recycleView((PriorityItemView)holder.offspring.getChildAt(i));
        }
        holder.offspring.removeAllViews();
        Log.d("MyPriorities", ViewHolder.viewPool.size() + " views in the recycled queue");
    }

    /**
     * Expands a goal.
     *
     * @param holder the view holder hosting the goal.
     * @param position the position of the goal in the backing array.
     */
    private void expand(ViewHolder holder, int position){
        //The position is marked as expanded
        mExpanded = position;

        //Populate only if the view is not collapsing. Collapsing does not recycle until
        //  it is done, making it necessary to do this check.
        if (mCollapsing != position){
            populate(holder, position);
        }
        holder.offspring.setVisibility(View.VISIBLE);

        final View view = holder.offspring;
        view.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = view.getMeasuredHeight();
        view.getLayoutParams().height = 0;

        view.setVisibility(View.VISIBLE);
        Animation animation = new Animation(){
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t){
                view.getLayoutParams().height = (interpolatedTime == 1)
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds(){
                return true;
            }
        };

        //1dp/ms
        int length = (int)(targetHeight/view.getContext().getResources().getDisplayMetrics().density);
        animation.setDuration(length);
        view.startAnimation(animation);
    }

    /**
     * Returns a free PriorityItemView.
     *
     * @return a PriorityItemView ready to be populated.
     */
    private PriorityItemView getPriorityItemView(){
        //If the recycle queue is empty it creates a new one, otherwise, returns the first one
        if (ViewHolder.viewPool.isEmpty()){
            return new PriorityItemView(mContext);
        }
        else{
            return ViewHolder.viewPool.removeFirst();
        }
    }

    /**
     * Collapses a goal.
     *
     * @param holder the view holder hosting the goal.
     * @param position the position of the goal in the backing array.
     */
    private void collapse(final ViewHolder holder, int position){
        mCollapsing = position;

        final ViewGroup view = holder.offspring;
        final int initialHeight = view.getMeasuredHeight();

        Animation animation = new Animation(){
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t){
                if(interpolatedTime == 1){
                    if (mCollapsing != -1){
                        mCollapsing = -1;
                        view.setVisibility(View.GONE);
                        recycle(holder);
                    }
                }
                else{
                    view.getLayoutParams().height = initialHeight-(int)(initialHeight*interpolatedTime);
                    view.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds(){
                return true;
            }
        };

        //1dp/ms
        int length = (int)(initialHeight/view.getContext().getResources().getDisplayMetrics().density);
        animation.setDuration(length);
        view.startAnimation(animation);
    }

    /**
     * Handles click events on goals.
     *
     * @param holder the holder hosting the clicked goal.
     * @param position the position of the clicked goal in the backing array.
     */
    public void onItemClick(ViewHolder holder, int position){
        if (mEmptyCategory){
            mListener.onAddGoalsClick(mCategory);
        }
        else{
            if (mSingleExpandedGoalMode){
                //If there is an expanded goal, collapse it
                if (mExpandedGoal != null){
                    collapse(mExpandedGoal, mExpanded);
                }
                //If the item clicked is not expanded, then expand it
                if (mExpandedGoal != holder){
                    mExpandedGoal = holder;
                    expand(holder, position);
                }
                else{
                    mExpandedGoal = null;
                }
            }
            else{
                //Collapse if expanded, expand if collapsed
                if (mExpandedGoals[position]){
                    collapse(holder, mExpanded);
                }
                else{
                    expand(holder, position);
                }
                mExpandedGoals[position] = !mExpandedGoals[position];
            }
        }
    }

    /**
     * Handles click events on items other than goals.
     *
     * @param holder the holder hosting the clicked item.
     * @param view the clicked view.
     */
    public void onPriorityItemClick(ViewHolder holder, View view){
        //Determine the type of item and act accordingly
        mClickedHolder = holder;
        ItemHierarchy itemHierarchy = ((PriorityItemView)view).getItemHierarchy();
        if (itemHierarchy.mAction != null){
            mListener.onActionClick(itemHierarchy.mCategory, itemHierarchy.mGoal,
                    itemHierarchy.mBehavior, itemHierarchy.mAction);
        }
        else if (itemHierarchy.mBehavior != null){
            mListener.onBehaviorClick(itemHierarchy.mCategory, itemHierarchy.mGoal,
                    itemHierarchy.mBehavior);
        }
        else{
            mListener.onAddBehaviorsClick(itemHierarchy.mCategory, itemHierarchy.mGoal);
        }
    }

    /**
     * Update the data at the holder containing the last clicked item.
     */
    public void updateData(){
        if (mEmptyCategory){
            mEmptyCategory = mCategory.getGoals().isEmpty();
            notifyDataSetChanged();
        }
        else if (mClickedHolder != null){
            recycle(mClickedHolder);
            populate(mClickedHolder, mClickedHolder.getAdapterPosition());
        }
    }

    /**
     * The item view holder. Also contains a pool of resources.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //This is a pool of TextViews to be reused. Any unused TextViews should be placed
        //  here. Before creating new ones, the list should be checked to see if there are
        //  any of them available.
        private static LinkedList<PriorityItemView> viewPool;

        private MyPrioritiesGoalAdapter mListener;

        //Components
        private TextView name;
        private LinearLayout offspring;


        /**
         * Constructor.
         *
         * @param itemView the root view.
         * @param listener the listener.
         */
        public ViewHolder(View itemView, MyPrioritiesGoalAdapter listener){
            super(itemView);

            mListener = listener;

            name = (TextView)itemView.findViewById(R.id.my_priorities_goal_name);
            name.setOnClickListener(this);
            offspring = (LinearLayout)itemView.findViewById(R.id.my_priorities_goal_offspring);
        }

        /**
         * Recycles a priority item view.
         *
         * @param view the view to be recycled.
         */
        public static void recycleView(PriorityItemView view){
            view.getImageView().setVisibility(View.VISIBLE);
            view.setOnClickListener(null);
            viewPool.add(view);
        }

        @Override
        public void onClick(View view){
            if (view instanceof TextView){
                mListener.onItemClick(this, getAdapterPosition());
            }
            else if (view instanceof PriorityItemView){
                mListener.onPriorityItemClick(this, view);
            }
        }
    }


    /**
     * Data holder for the hierarchy of an item.
     *
     * @author Ismael Alonso
     * @version 1.0.1
     */
    public static class ItemHierarchy{
        private Category mCategory;
        private Goal mGoal;
        private Behavior mBehavior;
        private Action mAction;


        /**
         * Constructor.
         *
         * @param category the category.
         * @param goal the goal.
         * @param behavior the behavior.
         * @param action the action.
         */
        public ItemHierarchy(Category category, Goal goal, Behavior behavior, Action action){
            mCategory = category;
            mGoal = goal;
            mBehavior = behavior;
            mAction = action;
        }

        /**
         * Tells whether this hierarchy contains an action.
         *
         * @return true if the hierarchy contains an action, false otherwise.
         */
        public boolean hasAction(){
            return mAction != null;
        }
    }


    /**
     * Item click listener interface.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface OnItemClickListener{
        /**
         * Triggered when the add goals item is clicked.
         *
         * @param category the selected category.
         */
        void onAddGoalsClick(Category category);

        /**
         * Triggered when the add behaviors item is clicked.
         *
         * @param category the category containing the goal.
         * @param goal the goal.
         */
        void onAddBehaviorsClick(Category category, Goal goal);

        /**
         * Triggered when a behaviour is clicked.
         *
         * @param category the category containing the goal containing the behavior.
         * @param goal the goal containing the behavior.
         * @param behavior the behavior.
         */
        void onBehaviorClick(Category category, Goal goal, Behavior behavior);

        /**
         * Triggered when an action is clicked.
         *
         * @param category the category containing the goal containing the behavior containing
         *                 the action.
         * @param goal the goal containing the behavior containing the action.
         * @param behavior the behavior containing the action.
         * @param action the action.
         */
        void onActionClick(Category category, Goal goal, Behavior behavior, Action action);
    }
}
