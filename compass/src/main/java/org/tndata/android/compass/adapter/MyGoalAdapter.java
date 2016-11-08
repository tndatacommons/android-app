package org.tndata.android.compass.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;

import org.tndata.android.compass.R;
import org.tndata.android.compass.databinding.CardDetailBinding;
import org.tndata.android.compass.databinding.CardEditableListBinding;
import org.tndata.android.compass.databinding.CardProgressBinding;
import org.tndata.android.compass.databinding.ItemProgressFooterBinding;
import org.tndata.android.compass.holder.DetailCardHolder;
import org.tndata.android.compass.holder.EditableListCardHolder;
import org.tndata.android.compass.holder.ProgressCardHolder;
import org.tndata.android.compass.holder.ProgressFooterHolder;
import org.tndata.compass.model.CustomAction;
import org.tndata.compass.model.TDCCategory;
import org.tndata.compass.model.UserGoal;
import org.tndata.android.compass.util.CompassUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter for MyGoalsActivity.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class MyGoalAdapter
        extends RecyclerView.Adapter
        implements
                EditableListCardHolder.Listener,
                ProgressFooterHolder.Listener{

    private static final int TYPE_BLANK = 0;
    private static final int TYPE_GOAL = TYPE_BLANK+1;
    private static final int TYPE_CUSTOM_ACTIONS = TYPE_GOAL+1;
    private static final int TYPE_PROGRESS = TYPE_CUSTOM_ACTIONS+1;
    private static final int TYPE_FOOTER = TYPE_PROGRESS+1;


    private Context mContext;
    private Listener mListener;
    private UserGoal mUserGoal;
    private int mColor;
    private List<CustomAction> mCustomActions;

    private EditableListCardHolder mCustomActionListHolder;
    private ProgressFooterHolder mProgressFooterHolder;


    /**
     * Constructor.
     *
     * @param context a reference to the context.
     * @param listener the listener.
     * @param userGoal the user goal to be displayed.
     * @param category the primary category of the goal provided.
     */
    @SuppressWarnings("deprecation")
    public MyGoalAdapter(Context context, Listener listener, UserGoal userGoal, TDCCategory category){
        mContext = context;
        mListener = listener;
        mUserGoal = userGoal;
        if (category == null){
            mColor = context.getResources().getColor(R.color.primary);
        }
        else{
            mColor = category.getColorInt();
        }
        mCustomActions = null;
    }

    @Override
    public int getItemViewType(int position){
        if (position == 0){
            return TYPE_BLANK;
        }
        else if (position == 1){
            return TYPE_GOAL;
        }
        else if (position == 2){
            if (mCustomActions == null){
                return TYPE_PROGRESS;
            }
            else{
                return TYPE_CUSTOM_ACTIONS;
            }
        }
        else{// if (position == 3){
            if (mCustomActions == null){
                return TYPE_FOOTER;
            }
            else{
                return TYPE_PROGRESS;
            }
        }
    }

    @Override
    public int getItemCount(){
        //Blank, goal, progress, and either the footer or the list of actions; always 4
        return 4;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (viewType == TYPE_BLANK){
            return new RecyclerView.ViewHolder(new Space(mContext)){};
        }
        else if (viewType == TYPE_GOAL){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            CardDetailBinding binding = DataBindingUtil.inflate(
                    inflater, R.layout.card_detail, parent, false
            );
            return new DetailCardHolder(binding);
        }
        else if (viewType == TYPE_CUSTOM_ACTIONS){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            CardEditableListBinding binding = DataBindingUtil.inflate(
                    inflater, R.layout.card_editable_list, parent, false
            );
            //Generate the dataset
            List<String> dataset = new ArrayList<>();
            for (CustomAction customAction:mCustomActions){
                dataset.add(customAction.getTitle());
            }
            mCustomActionListHolder = new EditableListCardHolder(
                    binding, this, dataset, R.menu.menu_custom_action
            );
            return mCustomActionListHolder;
        }
        else if (viewType == TYPE_PROGRESS){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            CardProgressBinding binding = DataBindingUtil.inflate(
                    inflater, R.layout.card_progress, parent, false
            );
            return new ProgressCardHolder(binding);
        }
        else if (viewType == TYPE_FOOTER){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ItemProgressFooterBinding binding = DataBindingUtil.inflate(
                    inflater, R.layout.item_progress_footer, parent, false
            );
            mProgressFooterHolder = new ProgressFooterHolder(binding);
            return mProgressFooterHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
        switch (getItemViewType(position)){
            case TYPE_BLANK:
                int width = CompassUtil.getScreenWidth(mContext);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        (int)((width * 2 / 3) * 0.8)
                );
                rawHolder.itemView.setLayoutParams(params);
                rawHolder.itemView.setVisibility(View.INVISIBLE);
                break;

            case TYPE_GOAL:
                DetailCardHolder goalHolder = (DetailCardHolder)rawHolder;
                goalHolder.setHeaderBackgroundColor(mColor);
                goalHolder.setTitleColor(0xFFFFFFFF);
                goalHolder.setTitle(mUserGoal.getTitle());
                goalHolder.setContent(mUserGoal.getDescription());
                break;

            case TYPE_CUSTOM_ACTIONS:
                EditableListCardHolder customActionsHolder = (EditableListCardHolder)rawHolder;
                customActionsHolder.setColor(mColor);
                customActionsHolder.setTitle(R.string.my_goal_custom_actions_title);
                customActionsHolder.setInputHint(R.string.my_goal_custom_actions_hint);
                break;

            case TYPE_PROGRESS:
                ProgressCardHolder holder = (ProgressCardHolder)rawHolder;
                holder.setCompletedItems(mUserGoal.getWeeklyCompletions());
                holder.setProgress(mUserGoal.getEngagementRank());
                break;
        }
    }

    public void setCustomActions(List<CustomAction> customActions){
        mCustomActions = customActions;
        notifyItemRemoved(3);
        notifyItemInserted(2);
    }

    public boolean areCustomActionsSet(){
        return mCustomActions != null;
    }

    public void fetchCustomActionsFailed(){
        mProgressFooterHolder.displayMessage("Couldn't load your custom content");
    }

    public void customActionAdded(CustomAction customAction){
        mCustomActions.add(customAction);
        mCustomActionListHolder.addInputToDataset();
    }

    public void addCustomActionFailed(){
        mCustomActionListHolder.inputAdditionFailed();
    }

    @Override
    public void onCreateItem(String name){
        mListener.addCustomAction(name);
    }

    @Override
    public void onEditItem(String newName, int index){
        CustomAction customAction = mCustomActions.get(index);
        customAction.setTitle(newName);
        mListener.saveCustomAction(customAction);
    }

    @Override
    public void onDeleteItem(int index){
        mListener.deleteCustomAction(mCustomActions.remove(index));
    }

    @Override
    public void onItemClick(int index){
        mListener.editTrigger(mCustomActions.get(index));
    }

    @Override
    public boolean onMenuItemClick(MenuItem item, int index){
        switch (item.getItemId()){
            case R.id.custom_action_reschedule:
                mListener.editTrigger(mCustomActions.get(index));
                return true;
        }
        return false;
    }

    @Override
    public void onMessageClick(){
        mListener.retryCustomActionLoad();
    }


    /**
     * Listener interface.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface Listener{
        /**
         * Called to retry loading the list of actions.
         */
        void retryCustomActionLoad();

        /**
         * Called when the user requests a new custom action.
         *
         * @param title the title of the new custom action.
         */
        void addCustomAction(String title);

        /**
         * Called when the user changes the title of a custom action.
         *
         * @param action the action to be saved.
         */
        void saveCustomAction(CustomAction action);

        /**
         * Called when the user deletes a custom action.
         *
         * @param action the action to be deleted.
         */
        void deleteCustomAction(CustomAction action);

        /**
         * Called when the user wants to edit a trigger.
         *
         * @param action the action whose trigger is to be edited.
         */
        void editTrigger(CustomAction action);
    }
}
