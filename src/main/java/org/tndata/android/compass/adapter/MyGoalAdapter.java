package org.tndata.android.compass.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
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
import org.tndata.android.compass.model.UserGoal;
import org.tndata.android.compass.util.CompassUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by isma on 9/14/16.
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
    private List<CustomAction> mCustomActions;

    private EditableListCardHolder mCustomActionListHolder;
    private ProgressFooterHolder mProgressFooterHolder;


    public MyGoalAdapter(Context context, Listener listener, UserGoal userGoal){
        mContext = context;
        mListener = listener;
        mUserGoal = userGoal;
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
                customActionsHolder.setInputHint("Type an idea here");
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
    public void onCreateItem(String title){
        mListener.addCustomAction(title);
    }

    @Override
    public void onEditItem(int row, String newTitle){

    }

    @Override
    public void onDeleteItem(int row){

    }

    @Override
    public void onButtonClick(View view, int index){

    }

    @Override
    public void onMessageClick(){
        mListener.retryCustomActionLoad();
    }


    public interface Listener{
        void retryCustomActionLoad();
        void addCustomAction(String title);
    }
}
