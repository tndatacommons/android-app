package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.Trigger;
import org.tndata.android.compass.task.DeleteBehaviorTask;
import org.tndata.android.compass.ui.CompassPopupMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import at.grabner.circleprogress.CircleProgressView;


/**
 * Adapter for the goal activity.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class GoalAdapter extends RecyclerView.Adapter{
    public static final String TAG = "GoalAdapter";

    private static final int TYPE_SPACER = 0;
    private static final int TYPE_BEHAVIOR = 1;
    private static final int TYPE_SEPARATOR = 2;


    private Context mContext;
    private GoalAdapterListener mListener;
    private Goal mGoal;
    private int mSpacingRowHeight;

    private CompassApplication mApplication;

    //This is a pool of ActionHolders to be reused. Any unused ActionHolders should be
    //  placed here. Before creating new ones, the list should be checked to see if
    //  there are any of them available.
    private Stack<ActionHolder> mHolderPool;

    private int mSelectedBehaviorPosition;


    /**
     * Constructor.
     *
     * @param context the application context.
     * @param listener the object that should receive the events triggered from the adapter.
     * @param goal the goal whose content is to be displayed.
     * @param spacingRowHeight the height of the header.
     */
    public GoalAdapter(@NonNull Context context, @NonNull GoalAdapterListener listener,
                       @NonNull Goal goal, int spacingRowHeight){
        mContext = context;
        mListener = listener;
        mGoal = goal;
        mSpacingRowHeight = spacingRowHeight;

        mApplication = (CompassApplication)context.getApplicationContext();

        mHolderPool = new Stack<>();

        mSelectedBehaviorPosition = -1;

        Log.d(TAG, mGoal.getBehaviors().size()+"");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (viewType == TYPE_SPACER){
            return new RecyclerView.ViewHolder(new View(mContext)){};
        }

        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == TYPE_BEHAVIOR){
            return new BehaviorHolder(inflater.inflate(R.layout.item_behavior, parent, false));
        }
        else{
            return new RecyclerView.ViewHolder(inflater.inflate(R.layout.item_separator, parent, false)){};
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
        if (getItemViewType(position) == TYPE_SPACER){
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, mSpacingRowHeight);
            rawHolder.itemView.setLayoutParams(params);
            rawHolder.itemView.setVisibility(View.INVISIBLE);
        }
        else if (getItemViewType(position) == TYPE_BEHAVIOR){
            Behavior behavior = mGoal.getBehaviors().get(position/2);
            BehaviorHolder holder = (BehaviorHolder)rawHolder;
            holder.mTitle.setText(behavior.getTitle());
            holder.mIndicator.setValue(0);
            holder.mIndicator.setAutoTextSize(true);
            holder.mIndicator.setShowUnit(true);
            if (behavior.getProgress() != null){
                holder.mIndicator.setValue(behavior.getProgress().getDailyActionsProgressPercent());
            }
            populate(holder, behavior);
        }
    }

    @Override
    public int getItemCount(){
        return 2*mGoal.getBehaviors().size();
    }

    @Override
    public int getItemViewType(int position){
        if (position == 0){
            return TYPE_SPACER;
        }
        if (position % 2 == 1){
            return TYPE_BEHAVIOR;
        }
        return TYPE_SEPARATOR;
    }

    /**
     * Recycles and populates a row holder.
     *
     * @param holder the victim.
     * @param behavior the new behavior.
     */
    private void populate(BehaviorHolder holder, Behavior behavior){
        List<Action> actions = mApplication.getUserData().getBehavior(behavior).getActions();

        //Unnecessary holders (if any) are recycled
        while (actions.size() < holder.mActionContainer.getChildCount()){
            int last = holder.mActionContainer.getChildCount()-1;
            mHolderPool.push(getActionHolder(holder.mActionContainer, last));
            holder.mActionContainer.removeViewAt(last);
        }

        //For each action
        for (int i = 0; i < actions.size(); i++){
            ActionHolder actionHolder;
            //If a holder already exists, retrieve it
            if (i < holder.mActionContainer.getChildCount()){
                actionHolder = getActionHolder(holder.mActionContainer, i);
            }
            //Otherwise, get a new one
            else{
                actionHolder = getActionHolder(holder.mActionContainer);
            }
            actionHolder.mTitle.setText(actions.get(i).getTitle());
            actionHolder.setBehaviorPosition(mGoal.getBehaviors().indexOf(behavior));
            actionHolder.setActionPosition(i);

            Trigger trigger = actions.get(i).getTrigger();
            String triggerText = trigger.getRecurrencesDisplay();
            String date = trigger.getFormattedDate();
            if (!date.equals("")){
                triggerText += " " + date;
            }
            String triggerDate = trigger.getFormattedTime();
            if (!triggerDate.equals("")){
                triggerText += " " + triggerDate;
            }
            actionHolder.mTime.setText(triggerText);

            //Add the view to the container only if it wasn't already there
            if (i >= holder.mActionContainer.getChildCount()){
                holder.mActionContainer.addView(actionHolder.mItemView);
            }
        }
    }

    /**
     * Gets the action holder at a given position within the passed container.
     *
     * @param container the object containing the holders.
     * @param position the position of the holder.
     * @return the holder.
     */
    private ActionHolder getActionHolder(ViewGroup container, int position){
        return (ActionHolder)container.getChildAt(position).getTag();
    }

    /**
     * Gets a new action holder, either from the stack or a brand new one.
     *
     * @param parent the view where it will be placed in the future.
     * @return the holder.
     */
    private ActionHolder getActionHolder(ViewGroup parent){
        if (!mHolderPool.isEmpty()){
            return mHolderPool.pop();
        }
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new ActionHolder(inflater.inflate(R.layout.item_action, parent, false));
    }

    /**
     * Display the popup menu for a specific goal.
     *
     * @param anchor the view it should be anchored to.
     * @param position the position of the view.
     */
    private void showPopup(View anchor, final int position){
        CompassPopupMenu popup = CompassPopupMenu.newInstance(mContext, anchor);
        popup.getMenuInflater().inflate(R.menu.popup_behavior, popup.getMenu());
        popup.setOnMenuItemClickListener(new CompassPopupMenu.OnMenuItemClickListener(){
            public boolean onMenuItemClick(MenuItem item){
                switch (item.getItemId()){
                    case R.id.behavior_popup_remove:
                        Behavior behavior = mGoal.getBehaviors().get(position-1);
                        mApplication.removeBehavior(behavior);
                        List<String> behaviorId = new ArrayList<>();
                        behaviorId.add(behavior.getMappingId() + "");
                        new DeleteBehaviorTask(mApplication.getToken(), null, behaviorId).execute();
                        if (position == 1){
                            notifyItemRemoved(2);
                        }
                        else{
                            notifyItemRemoved(position-1);
                        }
                        notifyItemRemoved(position);
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    public void updateSelectedBehavior(){
        if (mSelectedBehaviorPosition != -1){
            notifyItemChanged(mSelectedBehaviorPosition);
            mSelectedBehaviorPosition = -1;
        }
    }


    /**
     * View holder for a behavior item.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class BehaviorHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private CircleProgressView mIndicator;
        private TextView mTitle;
        private LinearLayout mActionContainer;


        /**
         * Constructor.
         *
         * @param itemView the root view.
         */
        public BehaviorHolder(View itemView){
            super(itemView);

            mIndicator = (CircleProgressView)itemView.findViewById(R.id.behavior_indicator);
            mTitle = (TextView)itemView.findViewById(R.id.behavior_title);
            mActionContainer = (LinearLayout)itemView.findViewById(R.id.behavior_actions);

            itemView.findViewById(R.id.behavior_overflow).setOnClickListener(this);
            mTitle.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            switch (view.getId()){
                case R.id.behavior_overflow:
                    showPopup(view, getAdapterPosition());
                    break;

                case R.id.behavior_title:
                    mSelectedBehaviorPosition = getAdapterPosition();
                    mListener.onBehaviorSelected(mGoal.getBehaviors().get(getAdapterPosition()/2));
            }
        }
    }


    /**
     * View holder for an action item.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class ActionHolder implements View.OnClickListener{
        private View mItemView;
        private TextView mTitle;
        private TextView mTime;

        private int mBehaviorPosition;
        private int mActionPosition;


        /**
         * Constructor.
         *
         * @param itemView the root view.
         */
        public ActionHolder(View itemView){
            mItemView = itemView;
            mItemView.setTag(this);
            mItemView.setOnClickListener(this);
            mTitle = (TextView)itemView.findViewById(R.id.action_title);
            mTime = (TextView)itemView.findViewById(R.id.action_time);
        }

        /**
         * Sets a parent behavior for this holder.
         *
         * @param position the position of the parent behavior of the action represented by
         *                 this holder.
         */
        public void setBehaviorPosition(int position){
            mBehaviorPosition = position;
        }

        /**
         * Sets the position of this holder within the behavior.
         *
         * @param position the position of the holder.
         */
        public void setActionPosition(int position){
            mActionPosition = position;
        }

        @Override
        public void onClick(View v){
            //Take separators into account
            mSelectedBehaviorPosition = 2*mBehaviorPosition+1;
            Behavior behavior = mGoal.getBehaviors().get(mBehaviorPosition);
            mListener.onActionSelected(behavior, behavior.getActions().get(mActionPosition));
        }
    }


    /**
     * Listener interface for the adapter.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface GoalAdapterListener{
        /**
         * Called when a behavior title has been tapped.
         *
         * @param behavior the selected behavior.
         */
        void onBehaviorSelected(Behavior behavior);

        /**
         * Called when an action has been tapped.
         *
         * @param behavior the parent behavior of the selected action.
         * @param action the selected action.
         */
        void onActionSelected(Behavior behavior, Action action);
    }
}
