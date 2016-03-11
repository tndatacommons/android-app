package org.tndata.android.compass.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.BehaviorContent;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.model.GoalContent;
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

    private BehaviorsAdapter mBehaviorsAdapter;
    private List<BehaviorContent> mBehaviors;


    /**
     * Constructor,
     *
     * @param context the context.
     * @param listener an implementation of the listener to act upon events.
     */
    public ChooseBehaviorsAdapter(@NonNull Context context, @NonNull ChooseBehaviorsListener listener){

        super(context, ContentType.LIST, true);

        //Assign the references
        mContext = context;
        mListener = listener;
        mCategory = null;
        mGoal = null;
        mBehaviors = new ArrayList<>();
    }

    /**
     * Sets the necessary content to display the header card,
     *
     * @param category the parent category of the goal whose behaviors are to be listed.
     * @param goal the goal whose behaviors are to be listed.
     */
    public void setContent(@NonNull CategoryContent category, @NonNull GoalContent goal){
        mCategory = category;
        mGoal = goal;
        notifyHeaderInserted();
    }

    @Override
    protected boolean hasHeader(){
        return mCategory != null && mGoal != null;
    }

    @Override
    public boolean isEmpty(){
        return mBehaviors.isEmpty();
    }

    @Override
    protected void bindHeaderHolder(RecyclerView.ViewHolder rawHolder){
        HeaderViewHolder holder = (HeaderViewHolder)rawHolder;
        holder.setTitle(mGoal.getTitle());
        if (!mGoal.getHTMLDescription().isEmpty()){
            holder.setContent(Html.fromHtml(mGoal.getHTMLDescription(), null,
                    new CompassTagHandler(mContext)));
        }
        else{
            holder.setContent(mGoal.getDescription());
        }
    }

    @Override
    protected void bindListHolder(RecyclerView.ViewHolder rawHolder){
        ListViewHolder holder = (ListViewHolder)rawHolder;
        holder.setHeaderColor(Color.parseColor(mCategory.getColor()));
        holder.setTitleColor(Color.WHITE);
        holder.setTitle(mContext.getString(R.string.library_behaviors_content_header));
        mBehaviorsAdapter = new BehaviorsAdapter();
        holder.setAdapter(mBehaviorsAdapter);
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

        //Record the initial position of the new sub-list in the master list
        int positionStart = mBehaviors.size();
        //Add all the behaviors in the behavior list
        mBehaviors.addAll(behaviors);
        //If the adapter has been created already, trigger animations
        if (mBehaviorsAdapter != null){
            prepareListChange();
            mBehaviorsAdapter.notifyItemRangeInserted(positionStart, behaviors.size());
            notifyListChanged();
        }
    }

    /**
     * Removes a behavior from the list.
     *
     * @param behavior the behavior to be removed.
     */
    public void remove(BehaviorContent behavior){
        int index = mBehaviors.indexOf(behavior);
        prepareListChange();
        mBehaviors.remove(index);
        mBehaviorsAdapter.notifyItemRemoved(index);
        if (!mBehaviors.isEmpty()){
            mBehaviorsAdapter.notifyItemChanged(0);
        }
    }

    @Override
    protected void loadMore(){
        if (hasHeader()){
            mListener.loadMore();
        }
    }


    /**
     * Adapter for the list of behaviors.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class BehaviorsAdapter extends RecyclerView.Adapter<BehaviorViewHolder>{
        @Override
        public int getItemCount(){
            return mBehaviors.size();
        }

        @Override
        public BehaviorViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View rootView = inflater.inflate(R.layout.item_library_behavior, parent, false);
            return new BehaviorViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(BehaviorViewHolder holder, int position){
            holder.bind(mBehaviors.get(position));
        }
    }


    /**
     * View holder for a list item.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class BehaviorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private View mSeparator;
        private ImageView mIcon;
        private TextView mTitle;


        /**
         * Constructor.
         *
         * @param rootView the root view of the holder.
         */
        public BehaviorViewHolder(@NonNull View rootView){
            super(rootView);

            //Fetch UI components
            mSeparator = rootView.findViewById(R.id.library_behavior_separator);
            mIcon = (ImageView)rootView.findViewById(R.id.library_behavior_icon);
            mTitle = (TextView)rootView.findViewById(R.id.library_behavior_title);

            rootView.setOnClickListener(this);
        }

        /**
         * Binds a behavior to the holder.
         *
         * @param behavior the behavior to be bound.
         */
        public void bind(BehaviorContent behavior){
            //If this is the first item, do not show the separator
            if (getAdapterPosition() == 0){
                mSeparator.setVisibility(View.GONE);
            }
            else{
                mSeparator.setVisibility(View.VISIBLE);
            }

            behavior.loadIconIntoView(mIcon);
            mTitle.setText(behavior.getTitle());
        }

        @Override
        public void onClick(View v){
            mListener.onBehaviorSelected(mBehaviors.get(getAdapterPosition()));
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
