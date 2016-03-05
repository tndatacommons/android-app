package org.tndata.android.compass.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.model.GoalContent;
import org.tndata.android.compass.ui.ContentContainer;
import org.tndata.android.compass.util.CompassTagHandler;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter for the goal picker.
 *
 * @author Ismael Alonso
 * @version 2.0.0
 */
public class ChooseGoalsAdapter extends MaterialAdapter{
    private Context mContext;
    private ChooseGoalsListener mListener;
    private CategoryContent mCategory;

    private GoalsViewHolder mGoalsHolder;
    private List<GoalContent> mGoals;


    /**
     * Constructor.
     *
     * @param context the application context.
     * @param listener the event listener.
     * @param category the category from where the goals are pulled.
     */
    public ChooseGoalsAdapter(@NonNull Context context, @NonNull ChooseGoalsListener listener,
                              @NonNull CategoryContent category){

        super(context, ContentType.LIST, true);

        mContext = context;
        mListener = listener;
        mCategory = category;

        mGoals = new ArrayList<>();
    }

    @Override
    protected boolean isEmpty(){
        return mGoals.isEmpty();
    }

    @Override
    protected @NonNull RecyclerView.ViewHolder getListHolder(ViewGroup parent){
        if (mGoalsHolder == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View rootView = inflater.inflate(R.layout.card_material_content, parent, false);
            mGoalsHolder = new GoalsViewHolder(this, rootView);
            mGoalsHolder.add(mGoals);
            mGoalsHolder.mGoalContainer.setAnimationsEnabled(true);
        }
        return mGoalsHolder;
    }

    @Override
    protected void bindHeaderHolder(RecyclerView.ViewHolder rawHolder){
        HeaderViewHolder holder = (HeaderViewHolder)rawHolder;
        holder.setTitle(mCategory.getTitle());
        if (!mCategory.getHTMLDescription().isEmpty()){
            holder.setContent(Html.fromHtml(mCategory.getHTMLDescription(), null,
                    new CompassTagHandler(mContext)));
        }
        else{
            holder.setContent(mCategory.getDescription());
        }
    }

    @Override
    protected void bindListHolder(RecyclerView.ViewHolder rawHolder){
        ((GoalsViewHolder)rawHolder).bind(mCategory);
    }

    /**
     * Tells whether the adapter is displaying goals or not.
     *
     * @return true if there are displayed goals, false otherwise.
     */
    public boolean hasGoals(){
        return !mGoals.isEmpty();
    }

    /**
     * Adds a set of goals to the backing list.
     *
     * @param goals the list of goals to be added.
     * @param showLoading whether the load switch should be kept or removed.
     */
    public void add(@NonNull List<GoalContent> goals, boolean showLoading){
        //If there are no goals, insert the goals card
        if (isEmpty()){
            notifyListInserted();
        }
        //Update the load switch
        updateLoading(showLoading);

        //Add all the goals in the goal list
        mGoals.addAll(goals);
        //Set the icon colors
        for (GoalContent goal:goals){
            goal.setColor(mCategory.getColor());
        }
        //If the holder has been created already
        if (mGoalsHolder != null){
            //Add the goals
            mGoalsHolder.add(goals);
        }
    }

    /**
     * Removes a goal from the adapter.
     *
     * @param goal the goal to be removed.
     */
    public void remove(GoalContent goal){
        mGoals.remove(goal);
        mGoalsHolder.remove(goal);
    }

    @Override
    protected void loadMore(){
        mListener.loadMore();
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
            mTitle = (TextView)rootView.findViewById(R.id.material_list_header);
            mGoalContainer = (ContentContainer<GoalContent>)rootView
                    .findViewById(R.id.material_content_container);
            mGoalContainer.setListener(this);
        }

        /**
         * Binds a category to the holder.
         *
         * @param category the category from which the color should be extracted.
         */
        public void bind(@NonNull CategoryContent category){
            mTitle.setText(R.string.library_goals_content_header);
            String colorString = category.getColor();
            if (colorString != null && !colorString.isEmpty()){
                mTitle.setBackgroundColor(Color.parseColor(colorString));
            }
        }

        /**
         * Adds a list of goals to the container.
         *
         * @param goals the list of goals to be added.
         */
        public void add(List<GoalContent> goals){
            for (GoalContent goal:goals){
                mGoalContainer.addContent(goal);
            }
        }

        /**
         * Removes a goal from the container.
         *
         * @param goal the goal to be removed.
         */
        public void remove(GoalContent goal){
            mGoalContainer.removeContent(goal);
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
