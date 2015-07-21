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
 * Created by isma on 7/16/15.
 */
public class MyPrioritiesGoalAdapter extends RecyclerView.Adapter{
    private Context mContext;
    private Category mCategory;
    private OnItemClickListener mListener;

    private ViewHolder mClickedHolder;

    private boolean[] mExpandedGoals;


    /**
     * Constructor.
     *
     * @param context the application context.
     * @param category the selected category.
     */
    public MyPrioritiesGoalAdapter(@NonNull Context context, @NonNull Category category,
                                   @NonNull OnItemClickListener listener){
        mContext = context;
        mCategory = category;
        mListener = listener;

        mClickedHolder = null;

        mExpandedGoals = new boolean[mCategory.getGoals().size()];
        for (int i = 0; i < mExpandedGoals.length; i++){
            mExpandedGoals[i] = false;
        }

        ViewHolder.viewPool = new LinkedList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_my_priorities_goal, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        ((ViewHolder)holder).name.setText(mCategory.getGoals().get(position).getTitle());
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public int getItemCount(){
        return mCategory.getGoals().size();
    }

    private void populate(ViewHolder holder, int position){
        Goal goal = mCategory.getGoals().get(position);
        //For each behavior in the goal
        for (Behavior behavior:goal.getBehaviors()){
            //A priority item view is retrieved and populated
            PriorityItemView behaviorView = getPriorityItemView();
            behaviorView.setItemHierarchy(new ItemHierarchy(mCategory, goal, behavior, null));
            behaviorView.setLeftPadding(20);
            behaviorView.getTextView().setText(behavior.getTitle());
            ImageLoader.loadBitmap(behaviorView.getImageView(), behavior.getIconUrl(), false);
            behaviorView.setOnClickListener(holder);

            //The view is added to the goal's offspring
            holder.offspring.addView(behaviorView);
            Log.d("BehaviourActions", behavior.getActions().size() + "");

            //For each action in the behavior
            for (Action action:behavior.getActions()){
                PriorityItemView actionView = getPriorityItemView();
                actionView.setItemHierarchy(new ItemHierarchy(mCategory, goal, behavior, action));
                actionView.setLeftPadding(40);
                actionView.getTextView().setText(action.getTitle());
                ImageLoader.loadBitmap(actionView.getImageView(), action.getIconUrl(), false);
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
                    triggerText += trigger.getFormattedTime();
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

        //Add behaviours view
        PriorityItemView addBehaviors = getPriorityItemView();
        addBehaviors.setItemHierarchy(new ItemHierarchy(mCategory, goal, null, null));
        addBehaviors.setLeftPadding(0);
        addBehaviors.getTextView().setText("Add behaviors");
        addBehaviors.getImageView().setVisibility(View.GONE);
        addBehaviors.setOnClickListener(holder);
        holder.offspring.addView(addBehaviors);
    }

    private void recycle(ViewHolder holder){
        for (int i = 0; i < holder.offspring.getChildCount(); i++){
            ViewHolder.recycleView((PriorityItemView)holder.offspring.getChildAt(i));
        }
        holder.offspring.removeAllViews();
        Log.d("MyPriorities", ViewHolder.viewPool.size() + " views in the recycled queue");
    }

    private void expand(ViewHolder holder, int position){
        populate(holder, position);
        holder.offspring.setVisibility(View.VISIBLE);

        final View view = holder.offspring;
        view.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = view.getMeasuredHeight();
        view.getLayoutParams().height = 0;

        view.setVisibility(View.VISIBLE);
        Animation animation = new Animation(){
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t){
                view.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds(){
                return true;
            }
        };

        // 1dp/ms
        animation.setDuration((int)(targetHeight/view.getContext().getResources().getDisplayMetrics().density));
        view.startAnimation(animation);
    }

    private PriorityItemView getPriorityItemView(){
        if (ViewHolder.viewPool.isEmpty()){
            return new PriorityItemView(mContext);
        }
        else{
            return ViewHolder.viewPool.removeFirst();
        }
    }

    private void collapse(final ViewHolder holder){
        final ViewGroup view = holder.offspring;
        final int initialHeight = view.getMeasuredHeight();

        Animation a = new Animation(){
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t){
                if(interpolatedTime == 1){
                    view.setVisibility(View.GONE);
                    recycle(holder);
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

        // 1dp/ms
        a.setDuration((int)(initialHeight/view.getContext().getResources().getDisplayMetrics().density));
        view.startAnimation(a);
    }

    public void onItemClick(ViewHolder holder, int position){
        if (mExpandedGoals[position]){
            collapse(holder);
        }
        else{
            expand(holder, position);
        }
        mExpandedGoals[position] = !mExpandedGoals[position];
    }

    public void onPriorityItemClick(ViewHolder holder, View view){
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

    public void updateData(){
        if (mClickedHolder != null){
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

        public ViewHolder(View itemView, MyPrioritiesGoalAdapter listener){
            super(itemView);

            mListener = listener;

            name = (TextView)itemView.findViewById(R.id.my_priorities_goal_name);
            name.setOnClickListener(this);
            offspring = (LinearLayout)itemView.findViewById(R.id.my_priorities_goal_offspring);
        }

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

    public static class ItemHierarchy{
        private Category mCategory;
        private Goal mGoal;
        private Behavior mBehavior;
        private Action mAction;

        public ItemHierarchy(Category category, Goal goal, Behavior behavior, Action action){
            mCategory = category;
            mGoal = goal;
            mBehavior = behavior;
            mAction = action;
        }
    }

    public interface OnItemClickListener{
        void onAddBehaviorsClick(Category category, Goal goal);
        void onBehaviorClick(Category category, Goal goal, Behavior behavior);
        void onActionClick(Category category, Goal goal, Behavior behavior, Action action);
    }
}
