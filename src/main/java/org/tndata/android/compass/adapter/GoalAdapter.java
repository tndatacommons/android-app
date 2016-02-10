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

import org.json.JSONObject;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Trigger;
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.model.UserBehavior;
import org.tndata.android.compass.model.UserGoal;
import org.tndata.android.compass.ui.CompassPopupMenu;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.NetworkRequest;

import java.util.Calendar;
import java.util.List;
import java.util.Stack;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;


/**
 * Adapter for the goal activity.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class GoalAdapter extends RecyclerView.Adapter{
    //Class tag
    public static final String TAG = "GoalAdapter";

    //Item view types
    private static final int TYPE_SPACER = 0;
    private static final int TYPE_BEHAVIOR = 1;
    private static final int TYPE_SEPARATOR = 2;


    private CompassApplication mApplication;

    private Context mContext;
    private GoalAdapterListener mListener;
    private UserGoal mUserGoal;
    private int mSpacingRowHeight;

    //This is a pool of ActionHolders to be reused. Any unused ActionHolders should be
    //  placed here. Before creating new ones, the list should be checked to see if
    //  there are any of them available.
    private Stack<ActionHolder> mHolderPool;

    //The position of the last selected behavior. This needs to be recorded to ensure the
    //  list is updated after calling a library activity and editing the selected content
    private int mSelectedBehaviorPosition;


    /**
     * Constructor.
     *
     * @param context the application context.
     * @param listener the object that should receive the events triggered from the adapter.
     * @param userGoal the goal whose content is to be displayed.
     * @param spacingRowHeight the height of the header.
     */
    public GoalAdapter(@NonNull Context context, @NonNull GoalAdapterListener listener,
                       @NonNull UserGoal userGoal, int spacingRowHeight){
        mContext = context;
        mListener = listener;
        mUserGoal = userGoal;
        mSpacingRowHeight = spacingRowHeight;

        mApplication = (CompassApplication)context.getApplicationContext();

        mHolderPool = new Stack<>();

        mSelectedBehaviorPosition = -1;

        Log.d(TAG, mUserGoal.getBehaviors().size()+"");
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
            Calendar calendar = Calendar.getInstance();
            String month = CompassUtil.getMonthString(calendar.get(Calendar.MONTH) + 1);

            UserBehavior userBehavior = mUserGoal.getBehaviors().get(position / 2);
            BehaviorHolder holder = (BehaviorHolder)rawHolder;
            holder.mTitle.setText(userBehavior.getTitle());
            holder.mIndicator.setValue(0);
            holder.mIndicator.setAutoTextSize(true);
            holder.mIndicator.setShowUnit(false);
            holder.mIndicator.setText(month);
            holder.mIndicator.setTextMode(TextMode.TEXT);

            if (userBehavior.getProgress() != null){
                holder.mIndicator.setValue(userBehavior.getProgress().getDailyActionsProgressPercent());
                holder.mIndicatorCaption.setText(mContext.getString(R.string.goal_indicator_caption,
                        userBehavior.getProgress().getDailyActionsProgressPercent()));
            }
            populate(holder, userBehavior);
        }
    }

    @Override
    public int getItemCount(){
        if (mUserGoal.getBehaviors().isEmpty()){
            return 1;
        }
        return 2* mUserGoal.getBehaviors().size();
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
     * @param userBehavior the new behavior.
     */
    private void populate(BehaviorHolder holder, UserBehavior userBehavior){
        List<UserAction> userActions = userBehavior.getActions();

        //Unnecessary holders (if any) are recycled
        while (userActions.size() < holder.mActionContainer.getChildCount()){
            int last = holder.mActionContainer.getChildCount()-1;
            mHolderPool.push(getActionHolder(holder.mActionContainer, last));
            holder.mActionContainer.removeViewAt(last);
        }

        //For each action
        for (int i = 0; i < userActions.size(); i++){
            ActionHolder actionHolder;
            //If a holder already exists, retrieve it
            if (i < holder.mActionContainer.getChildCount()){
                actionHolder = getActionHolder(holder.mActionContainer, i);
            }
            //Otherwise, get a new one
            else{
                actionHolder = getActionHolder(holder.mActionContainer);
            }

            //Populate the ui contained by the holder
            actionHolder.mTitle.setText(userActions.get(i).getTitle());
            actionHolder.setBehaviorPosition(mUserGoal.getBehaviors().indexOf(userBehavior));
            actionHolder.setActionPosition(i);

            Trigger trigger = userActions.get(i).getTrigger();
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
                        UserBehavior userBehavior = mUserGoal.getBehaviors().get(position/2);
                        mApplication.removeBehavior(userBehavior);
                        NetworkRequest.delete(mContext, null, API.getDeleteBehaviorUrl(userBehavior),
                                mApplication.getToken(), new JSONObject());
                        Log.d(TAG, "Position: " + position);
                        if (position == 1){
                            Log.d(TAG, "Size: " + mUserGoal.getBehaviors().size());
                            if (mUserGoal.getBehaviors().size() > 0){
                                notifyItemRemoved(2);
                            }
                        }
                        else{
                            notifyItemRemoved(position-1);
                        }
                        if (mUserGoal.getBehaviors().size() > 0){
                            Log.d("GoalAdapter", "behaviors left");
                            notifyItemRemoved(position);
                        }
                        else{
                            Log.d("GoalAdapter", "no behaviors left");
                            notifyDataSetChanged();
                        }
                        //notifyDataSetChanged();
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    /**
     * Updates the content of the behavior marked as selected.
     */
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
        private TextView mIndicatorCaption;
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
            mIndicatorCaption = (TextView)itemView.findViewById(R.id.behavior_indicator_caption);
            mTitle = (TextView)itemView.findViewById(R.id.behavior_title);
            mActionContainer = (LinearLayout)itemView.findViewById(R.id.behavior_actions);

            //Granularity of editable content goes only down to a goal
            if (mUserGoal.isEditable()){
                itemView.findViewById(R.id.behavior_overflow).setOnClickListener(this);
            }
            else{
                itemView.findViewById(R.id.behavior_overflow).setVisibility(View.INVISIBLE);
            }
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
                    mListener.onBehaviorSelected(mUserGoal.getBehaviors().get(getAdapterPosition()/2));
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
            UserBehavior userBehavior = mUserGoal.getBehaviors().get(mBehaviorPosition);
            mListener.onActionSelected(userBehavior, userBehavior.getActions().get(mActionPosition));
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
         * @param userBehavior the selected behavior.
         */
        void onBehaviorSelected(UserBehavior userBehavior);

        /**
         * Called when an action has been tapped.
         *
         * @param userBehavior the parent behavior of the selected action.
         * @param userAction the selected action.
         */
        void onActionSelected(UserBehavior userBehavior, UserAction userAction);
    }
}
