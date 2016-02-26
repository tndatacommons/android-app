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
import org.tndata.android.compass.model.BehaviorContent;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.model.GoalContent;
import org.tndata.android.compass.ui.ContentContainer;
import org.tndata.android.compass.util.CompassTagHandler;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter for the behavior picker.
 *
 * @author Ismael Alonso
 * @version 2.0.0
 */
public class ChooseBehaviorsAdapter extends MaterialAdapter{
    private Context mContext;
    private ChooseBehaviorsListener mListener;
    private CategoryContent mCategory;
    private GoalContent mGoal;

    private BehaviorsViewHolder mBehaviorsHolder;
    private List<BehaviorContent> mBehaviors;


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

        super(context, ContentType.LIST, true);

        //Assign the references
        mContext = context;
        mListener = listener;
        mCategory = category;
        mGoal = goal;
        mBehaviors = new ArrayList<>();
    }

    @Override
    protected boolean isEmpty(){
        return mBehaviors.isEmpty();
    }

    @Override
    protected RecyclerView.ViewHolder getListHolder(ViewGroup parent){
        if (mBehaviorsHolder == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View rootView = inflater.inflate(R.layout.card_material_content, parent, false);
            mBehaviorsHolder = new BehaviorsViewHolder(this, rootView);
            mBehaviorsHolder.addBehaviors(mBehaviors);
            mBehaviorsHolder.mBehaviorContainer.setAnimationsEnabled(true);
        }
        return mBehaviorsHolder;
    }

    @Override
    protected void bindDescriptionHolder(MaterialAdapter.DescriptionViewHolder holder){
        holder.setTitle(mGoal.getTitle());
        if (!mGoal.getHTMLDescription().isEmpty()){
            holder.setDescription(Html.fromHtml(mGoal.getHTMLDescription(), null,
                    new CompassTagHandler(mContext)));
        }
        else{
            holder.setDescription(mGoal.getDescription());
        }
    }

    @Override
    protected void bindListHolder(RecyclerView.ViewHolder rawHolder){
        ((BehaviorsViewHolder)rawHolder).bind(mCategory);
    }

    public boolean hasBehaviors(){
        return !mBehaviors.isEmpty();
    }

    /**
     * Adds a set of behaviors to the backing list.
     *
     * @param behaviors the list of behaviors to be added.
     * @param showLoading whether the load switch should be kept or removed.
     */
    public void add(@NonNull List<BehaviorContent> behaviors, boolean showLoading){
        //If there are no behaviors, insert the goals card
        if (isEmpty()){
            notifyListInserted();
        }
        //Update the load switch
        updateLoading(showLoading);

        //Add all the behaviors in the behavior list
        mBehaviors.addAll(behaviors);

        //If the holder has been created already
        if (mBehaviorsHolder != null){
            //Add the behaviors
            mBehaviorsHolder.addBehaviors(behaviors);
        }
    }

    /**
     * Removes a behavior from the list.
     *
     * @param behavior the behavior to be removed.
     */
    public void remove(BehaviorContent behavior){
        mBehaviors.remove(behavior);
        mBehaviorsHolder.removeBehavior(behavior);
    }

    @Override
    protected void loadMore(){
        mListener.loadMore();
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
            mTitle = (TextView)rootView.findViewById(R.id.material_content_header);
            mBehaviorContainer = (ContentContainer<BehaviorContent>)rootView
                    .findViewById(R.id.material_content_container);
            mBehaviorContainer.setListener(this);
        }

        /**
         * Binds a a category to the holder.
         *
         * @param category the category to be bound.
         */
        public void bind(CategoryContent category){
            mTitle.setText(R.string.library_behaviors_content_header);
            String colorString = category.getColor();
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
