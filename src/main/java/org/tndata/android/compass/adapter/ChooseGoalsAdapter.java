package org.tndata.android.compass.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.filter.GoalFilter;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.model.GoalContent;
import org.tndata.android.compass.ui.ContentContainer;
import org.tndata.android.compass.util.CompassTagHandler;
import org.tndata.android.compass.util.CompassUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter for the goal picker.
 *
 * @author Ismael Alonso
 * @version 2.0.0
 */
public class ChooseGoalsAdapter extends RecyclerView.Adapter{
    private static final int TYPE_BLANK = 0;
    private static final int TYPE_DESCRIPTION = TYPE_BLANK+1;
    private static final int TYPE_GOALS = TYPE_DESCRIPTION+1;
    private static final int TYPE_LOAD = TYPE_GOALS+1;
    private static final int ITEM_COUNT = TYPE_LOAD+1;


    private Context mContext;
    private ChooseGoalsListener mListener;
    private CategoryContent mCategory;

    private GoalsViewHolder mGoalsHolder;
    private List<GoalContent> mGoals;
    private GoalFilter mFilter;

    private boolean mShowLoading;
    private String mLoadError;


    /**
     * Constructor.
     *
     * @param context the application context.
     * @param listener the event listener.
     * @param category the category from where the goals are pulled.
     */
    public ChooseGoalsAdapter(@NonNull Context context, @NonNull ChooseGoalsListener listener,
                              @NonNull CategoryContent category){

        mContext = context;
        mListener = listener;
        mCategory = category;

        mGoals = new ArrayList<>();
        mFilter = new GoalFilter(this);

        mShowLoading = true;
        mLoadError = "";
    }

    @Override
    public int getItemCount(){
        int count = ITEM_COUNT;
        if (!mShowLoading){
            count--;
        }
        if (mGoals.isEmpty()){
            count--;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position){
        if (position < 2){
            //The two initial items are always the same, a blank space and the description
            return position;
        }
        else if (position == 2){
            //The third position can be either the progress bar or the goal container
            if (mGoals.isEmpty()){
                if (mShowLoading){
                    return TYPE_LOAD;
                }
            }
            else{
                return TYPE_GOALS;
            }
        }
        //If there is a fourth element, that would be loading
        return TYPE_LOAD;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);

        if (viewType == TYPE_BLANK){
            return new RecyclerView.ViewHolder(new CardView(mContext)){};
        }
        else if (viewType == TYPE_DESCRIPTION){
            View rootView = inflater.inflate(R.layout.card_library_description, parent, false);
            return new DescriptionViewHolder(mContext, rootView);
        }
        else if (viewType == TYPE_GOALS){
            if (mGoalsHolder == null){
                View rootView = inflater.inflate(R.layout.card_library_goals, parent, false);
                mGoalsHolder = new GoalsViewHolder(this, rootView);
                mGoalsHolder.addGoals(mGoals);
                mGoalsHolder.mGoalContainer.setAnimationsEnabled(true);
            }
            return mGoalsHolder;
        }
        else{
            View rootView = inflater.inflate(R.layout.item_progress, parent, false);
            return new RecyclerView.ViewHolder(rootView){};
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
        Log.d("ChooseGoalsAdapter", "bind: " + position);
        //Blank space
        if (position == TYPE_BLANK){
            int width = CompassUtil.getScreenWidth(mContext);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (int)((width*2/3)*0.8)
            );
            rawHolder.itemView.setLayoutParams(params);
            rawHolder.itemView.setVisibility(View.INVISIBLE);
        }
        //Description
        else if (position == TYPE_DESCRIPTION){
            ((DescriptionViewHolder)rawHolder).bind(mCategory);
        }
        //Goals or load switch
        else if (position == 2){
            if (mGoals.isEmpty()){
                if (mShowLoading){
                    mListener.loadMore();
                }
            }
            else{
                ((GoalsViewHolder)rawHolder).bind(mCategory);
            }
        }
        //Load switch, maybe
        else if (position == TYPE_LOAD){
            if (mLoadError.isEmpty()){
                mListener.loadMore();
            }
            else{
                rawHolder.itemView.findViewById(R.id.progress_progress).setVisibility(View.GONE);
                TextView error = (TextView)rawHolder.itemView.findViewById(R.id.progress_error);
                error.setVisibility(View.VISIBLE);
                error.setText(mLoadError);
            }
        }
    }

    /**
     * Adds a set of goals to the backing list.
     *
     * @param goals the list of goals to be added.
     * @param showLoading whether the load switch should be kept or removed.
     */
    public void addGoals(@NonNull List<GoalContent> goals, boolean showLoading){
        //If there are no goals, insert the goals card
        if (mGoals.isEmpty()){
            notifyItemInserted(TYPE_GOALS);
        }

        //Set the new loading state
        mShowLoading = showLoading;
        //If we should no longer load, remove the load switch
        if (!mShowLoading){
            notifyItemRemoved(TYPE_LOAD);
        }
        //Otherwise, schedule an item refresh for the load switch half a second from now
        //  to avoid the load callback getting called twice
        else{
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    notifyItemChanged(TYPE_LOAD);
                }
            }, 500);
        }

        //Insert all the goals in the goal list and set the filter
        mGoals.addAll(goals);
        mFilter.setGoalList(mGoals);
        //Set the icon colors
        for (GoalContent goal:goals){
            goal.setColor(mCategory.getColor());
        }
        //If the holder has been created already
        if (mGoalsHolder != null){
            //Add the goals
            mGoalsHolder.addGoals(goals);
        }
    }

    /**
     * Gets the filter.
     *
     * @return the goal filter.
     */
    public GoalFilter getFilter(){
        return mFilter;
    }

    /**
     * Displays an error in place of the load switch.
     *
     * @param error the error to be displayed.
     */
    public void displayError(String error){
        mLoadError = error;
        if (mGoals.isEmpty()){
            notifyItemChanged(TYPE_LOAD-1);
        }
        else if (mShowLoading){
            notifyItemChanged(TYPE_LOAD);
        }
    }


    /**
     * View holder for a description card.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    static class DescriptionViewHolder extends RecyclerView.ViewHolder{
        private Context mContext;
        private TextView mCategoryTitle;
        private TextView mCategoryDescription;


        /**
         * Constructor.
         *
         * @param context a reference to the context.
         * @param rootView the view to be drawn.
         */
        public DescriptionViewHolder(@NonNull Context context, @NonNull View rootView){
            super(rootView);

            mContext = context;
            mCategoryTitle = (TextView)rootView.findViewById(R.id.library_description_title);
            mCategoryDescription = (TextView)rootView.findViewById(R.id.library_description_content);
        }

        /**
         * Binds a category to the holder.
         *
         * @param category the category whose description is to be drawn.
         */
        public void bind(@NonNull CategoryContent category){
            mCategoryTitle.setText(category.getTitle());
            if (!category.getHTMLDescription().isEmpty()){
                mCategoryDescription.setText(Html.fromHtml(category.getHTMLDescription(), null,
                        new CompassTagHandler(mContext)));
            }
            else{
                mCategoryDescription.setText(category.getDescription());
            }
        }
    }


    /**
     * The ViewHolder for a goal.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    static class GoalsViewHolder
            extends RecyclerView.ViewHolder
            implements ContentContainer.ContentContainerListener<GoalContent>{

        private ChooseGoalsAdapter mAdapter;

        private TextView mTitle;
        private ContentContainer<GoalContent> mGoalContainer;


        /**
         * Constructor. Extracts the UI components from the root view.
         *
         * @param rootView the root view held by this view holder.
         */
        @SuppressWarnings("unchecked")
        public GoalsViewHolder(ChooseGoalsAdapter adapter, View rootView){
            super(rootView);

            mAdapter = adapter;

            //Fetch UI components
            mTitle = (TextView)rootView.findViewById(R.id.card_library_goals_header);
            mGoalContainer = (ContentContainer<GoalContent>)rootView
                    .findViewById(R.id.card_library_goals_container);
            mGoalContainer.setListener(this);
        }

        /**
         * Binds a category to the holder.
         *
         * @param category the category from which the color should be extracted.
         */
        public void bind(@NonNull CategoryContent category){
            String colorString = category.getSecondaryColor();
            if (colorString != null && !colorString.isEmpty()){
                mTitle.setBackgroundColor(Color.parseColor(colorString));
            }
        }

        /**
         * Adds a list of goals to the container.
         *
         * @param goals the list of goals to be added.
         */
        public void addGoals(List<GoalContent> goals){
            for (GoalContent goal:goals){
                mGoalContainer.addContent(goal);
            }
        }

        @Override
        public void onContentClick(@NonNull GoalContent content){
            mAdapter.mListener.onGoalSelected(content);
        }
    }


    /**
     * Listener interface for the adapter.
     *
     * @author Ismael Alonso
     * @version 1.1.0
     */
    public interface ChooseGoalsListener{
        /**
         * Called when the add button is tapped.
         *
         * @param goal the goal whose add was tapped.
         */
        void onGoalSelected(@NonNull GoalContent goal);

        /**
         * Called when the user scrolls to the bottom of the page.
         */
        void loadMore();
    }
}
