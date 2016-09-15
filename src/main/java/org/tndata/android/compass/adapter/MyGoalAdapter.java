package org.tndata.android.compass.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;

import org.tndata.android.compass.R;
import org.tndata.android.compass.databinding.CardDetailBinding;
import org.tndata.android.compass.databinding.CardEditableListBinding;
import org.tndata.android.compass.databinding.ItemProgressFooterBinding;
import org.tndata.android.compass.holder.DetailCardHolder;
import org.tndata.android.compass.holder.EditableListCardHolder;
import org.tndata.android.compass.holder.ProgressFooterHolder;
import org.tndata.android.compass.model.CustomAction;
import org.tndata.android.compass.model.TDCCategory;
import org.tndata.android.compass.model.UserGoal;
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
    private static final int TYPE_FOOTER = TYPE_CUSTOM_ACTIONS+1;


    private Context mContext;
    private Listener mListener;
    private UserGoal mUserGoal;
    private TDCCategory mCategory;
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
    public MyGoalAdapter(Context context, Listener listener, UserGoal userGoal, TDCCategory category){
        mContext = context;
        mListener = listener;
        mUserGoal = userGoal;
        mCategory = category;
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
        else{// if (position == 2){
            if (mCustomActions == null){
                return TYPE_FOOTER;
            }
            else{
                return TYPE_CUSTOM_ACTIONS;
            }
        }
    }

    @Override
    public int getItemCount(){
        //Blank, goal, and either the footer or the list of actions; always 3
        return 3;
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
            //Create the list of additional buttons
            List<EditableListCardHolder.ButtonSpec> buttons = new ArrayList<>();
            buttons.add(new EditableListCardHolder.ButtonSpec(
                    R.id.my_goal_custom_action_trigger, R.drawable.ic_bell_white_24dp
            ));
            mCustomActionListHolder = new EditableListCardHolder(binding, this, dataset, buttons);
            return mCustomActionListHolder;
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
                goalHolder.setTitle(mUserGoal.getTitle());
                goalHolder.setContent(mUserGoal.getDescription());
                break;

            case TYPE_CUSTOM_ACTIONS:
                EditableListCardHolder customActionsHolder = (EditableListCardHolder)rawHolder;
                customActionsHolder.setColor(Color.parseColor(mCategory.getColor()));
                customActionsHolder.setTitle(R.string.my_goal_custom_actions_title);
                customActionsHolder.setInputHint(R.string.my_goal_custom_actions_hint);
                break;
        }
    }

    public void setCustomActions(List<CustomAction> customActions){
        mCustomActions = customActions;
        notifyItemRemoved(2);
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
    public void onButtonClick(View view, int index){
        switch (view.getId()){
            case R.id.my_goal_custom_action_trigger:
                mListener.editTrigger(mCustomActions.get(index));
                break;
        }
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
