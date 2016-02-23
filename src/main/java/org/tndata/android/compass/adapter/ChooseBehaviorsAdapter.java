package org.tndata.android.compass.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.BehaviorContent;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.model.GoalContent;
import org.tndata.android.compass.ui.ContentContainer;
import org.tndata.android.compass.util.CompassTagHandler;
import org.tndata.android.compass.util.CompassUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter for the behavior picker.
 *
 * @author Ismael Alonso
 * @version 2.0.0
 */
public class ChooseBehaviorsAdapter extends RecyclerView.Adapter{
    public static final int TYPE_BLANK = 0;
    public static final int TYPE_DESCRIPTION = TYPE_BLANK+1;
    public static final int TYPE_BEHAVIORS = TYPE_DESCRIPTION+1;
    public static final int TYPE_LOAD = TYPE_BEHAVIORS+1;
    public static final int ITEM_COUNT = TYPE_LOAD+1;


    private Context mContext;
    private ChooseBehaviorsListener mListener;
    private CategoryContent mCategory;
    private GoalContent mGoal;

    private BehaviorsViewHolder mBehaviorsHolder;
    private List<BehaviorContent> mBehaviors;

    private boolean mShowLoading;
    private String mLoadError;


    /**
     * Constructor,
     *
     * @param context the context.
     * @param listener an implementation of the listener to act upon events.
     * @param category the parent category of the goal whose behaviors are to be listed.
     * @param goal the goal whose behaviors are to be listed.
     */
    public ChooseBehaviorsAdapter(@NonNull Context context, @NonNull ChooseBehaviorsListener listener,
                                  @NonNull CategoryContent category, @NonNull GoalContent goal){

        //Assign the references
        mContext = context;
        mListener = listener;
        mCategory = category;
        mGoal = goal;

        mBehaviors = new ArrayList<>();
        mShowLoading = true;
        mLoadError = "";
    }

    @Override
    public int getItemCount(){
        int count = ITEM_COUNT;
        if (!mShowLoading){
            count--;
        }
        if (mBehaviors.isEmpty()){
            count--;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position){
        if (position < 2){
            return position;
        }
        else if (position == 2){
        //The third position can be either the progress bar or the behavior container
            if (mBehaviors.isEmpty()){
                if (mShowLoading){
                    return TYPE_LOAD;
                }
            }
            else{
                return TYPE_BEHAVIORS;
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
        else if (viewType == TYPE_BEHAVIORS){
            if (mBehaviorsHolder == null){
                View rootView = inflater.inflate(R.layout.card_library_content, parent, false);
                mBehaviorsHolder = new BehaviorsViewHolder(this, rootView);
                mBehaviorsHolder.addBehaviors(mBehaviors);
                mBehaviorsHolder.mBehaviorContainer.setAnimationsEnabled(true);
            }
            return mBehaviorsHolder;
        }
        else{
            View rootView = inflater.inflate(R.layout.item_library_progress, parent, false);
            return new RecyclerView.ViewHolder(rootView){};
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
        //Blank space
        if (position == 0){
            int width = CompassUtil.getScreenWidth(mContext);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (int)((width*2/3)*0.8)
            );
            rawHolder.itemView.setLayoutParams(params);
            rawHolder.itemView.setVisibility(View.INVISIBLE);
        }
        //Description
        else if (position == 1){
            ((DescriptionViewHolder)rawHolder).bind(mGoal);
        }
        //Behaviors or load switch
        else if (position == 2){
            if (mBehaviors.isEmpty()){
                if (mShowLoading){
                    if (mLoadError.isEmpty()){
                        mListener.loadMore();
                    }
                    else{
                        rawHolder.itemView.findViewById(R.id.library_progress_progress).setVisibility(View.GONE);
                        TextView error = (TextView)rawHolder.itemView.findViewById(R.id.library_progress_error);
                        error.setVisibility(View.VISIBLE);
                        error.setText(mLoadError);
                    }
                }
            }
            else{
                ((BehaviorsViewHolder)rawHolder).bind(mCategory);
            }
        }
        //Load switch, maybe
        else if (position == TYPE_LOAD){
            if (mLoadError.isEmpty()){
                mListener.loadMore();
            }
            else{
                rawHolder.itemView.findViewById(R.id.library_progress_progress).setVisibility(View.GONE);
                TextView error = (TextView)rawHolder.itemView.findViewById(R.id.library_progress_error);
                error.setVisibility(View.VISIBLE);
                error.setText(mLoadError);
            }
        }
    }

    /**
     * Adds a set of behaviors to the backing list.
     *
     * @param behaviors the list of behaviors to be added.
     * @param showLoading whether the load switch should be kept or removed.
     */
    public void addBehaviors(@NonNull List<BehaviorContent> behaviors, boolean showLoading){
        //If there are no goals, insert the goals card
        if (mBehaviors.isEmpty()){
            notifyItemInserted(TYPE_BEHAVIORS);
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

        //Add all the behaviors in the behavior list
        mBehaviors.addAll(behaviors);

        //If the holder has been created already
        if (mBehaviorsHolder != null){
            //Add the goals
            mBehaviorsHolder.addBehaviors(behaviors);
        }
    }

    /**
     * Removes a behavior from the list.
     *
     * @param behavior the behavior to be removed.
     */
    public void removeBehavior(BehaviorContent behavior){
        mBehaviors.remove(behavior);
        mBehaviorsHolder.removeBehavior(behavior);
    }

    /**
     * Displays an error in place of the load switch.
     *
     * @param error the error to be displayed.
     */
    public void displayError(String error){
        mLoadError = error;
        if (mBehaviors.isEmpty()){
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
        private TextView mGoalTitle;
        private TextView mGoalDescription;


        /**
         * Constructor.
         *
         * @param context a reference to the context.
         * @param rootView the view to be drawn.
         */
        public DescriptionViewHolder(@NonNull Context context, @NonNull View rootView){
            super(rootView);

            mContext = context;
            mGoalTitle = (TextView)rootView.findViewById(R.id.library_description_title);
            mGoalDescription = (TextView)rootView.findViewById(R.id.library_description_content);
        }

        /**
         * Binds a goal to the holder.
         *
         * @param goal the goal whose description is to be drawn.
         */
        public void bind(@NonNull GoalContent goal){
            mGoalTitle.setText(mContext.getString(R.string.goal_title, goal.getTitle()));
            if (!goal.getHTMLDescription().isEmpty()){
                mGoalDescription.setText(Html.fromHtml(goal.getHTMLDescription(), null,
                        new CompassTagHandler(mContext)));
            }
            else{
                mGoalDescription.setText(goal.getDescription());
            }
        }
    }


    /**
     * View holder for a list item.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    static class BehaviorsViewHolder
            extends RecyclerView.ViewHolder
            implements ContentContainer.ContentContainerListener<BehaviorContent>{

        private ChooseBehaviorsAdapter mAdapter;

        private TextView mTitle;
        private ContentContainer<BehaviorContent> mBehaviorContainer;


        /**
         * Constructor.
         *
         * @param rootView a view inflated from R.layout.item_choose_behavior
         */
        @SuppressWarnings("unchecked")
        public BehaviorsViewHolder(@NonNull ChooseBehaviorsAdapter adapter, @NonNull View rootView){
            super(rootView);

            mAdapter = adapter;

            //Fetch UI components
            mTitle = (TextView)rootView.findViewById(R.id.card_library_content_header);
            mBehaviorContainer = (ContentContainer<BehaviorContent>)rootView
                    .findViewById(R.id.card_library_content_container);
            mBehaviorContainer.setListener(this);
        }

        /**
         * Binds a a category to the holder.
         *
         * @param category the category to be bound.
         */
        public void bind(CategoryContent category){
            mTitle.setText(R.string.library_behaviors_content_header);
            String colorString = category.getSecondaryColor();
            if (colorString != null && !colorString.isEmpty()){
                mTitle.setBackgroundColor(Color.parseColor(colorString));
            }
        }

        /**
         * Adds a list of behaviors to the container.
         *
         * @param behaviors the list of behaviors to be added.
         */
        public void addBehaviors(List<BehaviorContent> behaviors){
            for (BehaviorContent behavior:behaviors){
                mBehaviorContainer.addContent(behavior);
            }
        }

        /**
         * Removes a behavior from the list.
         *
         * @param behavior the behavior to be removed.
         */
        public void removeBehavior(BehaviorContent behavior){
            mBehaviorContainer.removeContent(behavior);
        }

        @Override
        public void onContentClick(@NonNull BehaviorContent content){
            mAdapter.mListener.onBehaviorSelected(content);
        }
    }

    /**
     * Listener interface for the adapter.
     *
     * @author Ismael Alonso
     * @version 1.1.0
     */
    public interface ChooseBehaviorsListener{
        /**
         * Called when a behavior is selected.
         *
         * @param behavior the selected behavior.
         */
        void onBehaviorSelected(BehaviorContent behavior);

        /**
         * Called when the user scrolls to the bottom of the page.
         */
        void loadMore();
    }
}
