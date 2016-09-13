package org.tndata.android.compass.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Space;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.databinding.CardContentBinding;
import org.tndata.android.compass.databinding.CardDetailBinding;
import org.tndata.android.compass.databinding.CardGoalBinding;
import org.tndata.android.compass.holder.ContentCardHolder;
import org.tndata.android.compass.holder.DetailCardHolder;
import org.tndata.android.compass.holder.GoalCardHolder;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.CustomAction;
import org.tndata.android.compass.model.TDCCategory;
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.util.CompassUtil;


/**
 * Adapter to display an Action, including information about it's primary goal and behavior.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class NewActionAdapter
        extends RecyclerView.Adapter
        implements
                GoalCardHolder.Listener,
                View.OnClickListener,
                PopupMenu.OnMenuItemClickListener{

    private static final int TYPE_BLANK = 0;
    private static final int TYPE_GOAL = TYPE_BLANK+1;
    private static final int TYPE_CONTENT = TYPE_GOAL+1;
    private static final int TYPE_DETAIL = TYPE_CONTENT+1;


    private Context mContext;
    private Listener mListener;
    private Action mAction;
    private TDCCategory mCategory;

    private Button mGotItButton;


    /**
     * Constructor.
     *
     * @param context a reference to the context.
     * @param action the action to be displayed.
     */
    public NewActionAdapter(@NonNull Context context, @NonNull Listener listener,
                            @NonNull Action action){

        mContext = context;
        mListener = listener;
        mAction = action;
        if (mAction instanceof UserAction){
            CompassApplication app = (CompassApplication)mContext.getApplicationContext();
            long categoryId = ((UserAction)mAction).getPrimaryCategoryId();
            mCategory = app.getAvailableCategories().get(categoryId);
        }
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
            return TYPE_CONTENT;
        }
        else{
            return TYPE_DETAIL;
        }
    }

    @Override
    public int getItemCount(){
        if (mAction instanceof UserAction){
            return 4;
        }
        else if (mAction instanceof CustomAction){
            return 3;
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (viewType == TYPE_BLANK){
            return new RecyclerView.ViewHolder(new Space(mContext)){};
        }
        else if (viewType == TYPE_GOAL){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            CardGoalBinding binding = DataBindingUtil.inflate(
                    inflater, R.layout.card_goal, parent, false
            );
            return new GoalCardHolder(binding, this);
        }
        else if (viewType == TYPE_CONTENT){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            CardContentBinding binding = DataBindingUtil.inflate(
                    inflater, R.layout.card_content, parent, false
            );
            return new ContentCardHolder(binding);
        }
        else if (viewType == TYPE_DETAIL){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            CardDetailBinding binding = DataBindingUtil.inflate(
                    inflater, R.layout.card_detail, parent, false
            );
            return new DetailCardHolder(binding);
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
                        (int)((width*2/3)*0.8)
                );
                rawHolder.itemView.setLayoutParams(params);
                rawHolder.itemView.setVisibility(View.INVISIBLE);
                break;

            case TYPE_GOAL:
                GoalCardHolder goalHolder = (GoalCardHolder)rawHolder;
                goalHolder.setTitle(mAction.getGoalTitle());
                if (mAction instanceof UserAction){
                    goalHolder.setColor(Color.parseColor(mCategory.getColor()));
                    goalHolder.setIcon(((UserAction)mAction).getPrimaryGoalIconUrl());
                }
                break;

            case TYPE_CONTENT:
                ContentCardHolder contentHolder = (ContentCardHolder)rawHolder;
                if (mAction instanceof UserAction){
                    contentHolder.setColor(Color.parseColor(mCategory.getColor()));
                    contentHolder.setTitle(mAction.getTitle());
                    contentHolder.setContent(((UserAction)mAction).getDescription());
                }
                else if (mAction instanceof CustomAction){
                    contentHolder.setTitle("Your Reminder");
                    contentHolder.setContent(mAction.getTitle());
                }
                mGotItButton = contentHolder.addButton(R.id.action_got_it, R.string.action_got_it);
                mGotItButton.setOnClickListener(this);

                final View contentView = contentHolder.itemView;
                ViewTreeObserver vto = contentView.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
                    @Override
                    @SuppressWarnings("deprecation")
                    public void onGlobalLayout(){
                        if (Build.VERSION.SDK_INT < 16){
                            contentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        else{
                            contentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        mListener.onContentCardLoaded();
                    }
                });
                break;

            case TYPE_DETAIL:
                if (mAction instanceof UserAction){
                    DetailCardHolder detailHolder = (DetailCardHolder)rawHolder;
                    detailHolder.setTitle("How will this help?");
                    detailHolder.setContent(((UserAction)mAction).getAction().getBehaviorDescription());
                    detailHolder.setOverflowMenu(R.menu.menu_remove_behavior, this);
                }
                break;
        }
    }

    /**
     * Gor it button getter.
     *
     * @return the instance of the got it button.
     */
    public Button getGotItButton(){
        return mGotItButton;
    }

    @Override
    public void onGoalCardClick(){
        mListener.onGoalClick();
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.action_got_it:
                mListener.onGotItClick();
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_remove_behavior:
                mListener.onDeleteBehaviorClick();
                return true;
        }
        return false;
    }


    /**
     * Listener interface for NewActionAdapter.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface Listener{
        /**
         * Called when the content card loads.
         */
        void onContentCardLoaded();

        /**
         * Called when the goal card is clicked.
         */
        void onGoalClick();

        /**
         * Called when the got it button is clicked.
         */
        void onGotItClick();

        /**
         * Called when the remove behavior menu item is clicked.
         */
        void onDeleteBehaviorClick();
    }
}
