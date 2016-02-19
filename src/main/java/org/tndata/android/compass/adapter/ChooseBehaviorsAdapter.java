package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.filter.BehaviorFilter;
import org.tndata.android.compass.model.BehaviorContent;
import org.tndata.android.compass.model.GoalContent;
import org.tndata.android.compass.util.CompassTagHandler;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.ImageLoader;

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
    public static final int TYPE_BEHAVIOR = TYPE_DESCRIPTION+1;


    private Context mContext;
    private ChooseBehaviorsListener mListener;
    private GoalContent mGoal;
    private BehaviorFilter mFilter;

    private List<BehaviorContent> mBehaviors;


    /**
     * Constructor,
     *
     * @param context the context.
     * @param listener an implementation of the listener to act upon events.
     * @param goal the goal whose behaviors are to be listed.
     */
    public ChooseBehaviorsAdapter(@NonNull Context context, @NonNull ChooseBehaviorsListener listener,
                                  @NonNull GoalContent goal){

        //Assign the references
        mContext = context;
        mListener = listener;
        mGoal = goal;
        mFilter = new BehaviorFilter(this);

        //Create an empty list
        mBehaviors = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position){
        if (position == 0){
            return TYPE_BLANK;
        }
        else if (position == 1){
            return TYPE_DESCRIPTION;
        }
        else{
            return TYPE_BEHAVIOR;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (viewType == TYPE_BLANK){
            return new RecyclerView.ViewHolder(new CardView(mContext)){};
        }
        else if (viewType == TYPE_DESCRIPTION){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View rootView = inflater.inflate(R.layout.card_library_description, parent, false);
            return new DescriptionViewHolder(mContext, rootView);
        }
        else{
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View rootView = inflater.inflate(R.layout.card_library_behavior, parent, false);
            return new BehaviorViewHolder(this, rootView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
        if (position == 0){
            int width = CompassUtil.getScreenWidth(mContext);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (int)((width*2/3)*0.8)
            );
            rawHolder.itemView.setLayoutParams(params);
            rawHolder.itemView.setVisibility(View.INVISIBLE);
        }
        else if (position == 1){
            ((DescriptionViewHolder)rawHolder).bind(mGoal);
        }
        else{
            ((BehaviorViewHolder)rawHolder).bind(mBehaviors.get(position - 2));
        }
    }

    @Override
    public int getItemCount(){
        return mBehaviors.size()+2;
    }

    /**
     * Sets the list of behaviors. and notifies the adapter.
     *
     * @param behaviors the list of behaviors to be set.
     */
    public void setBehaviors(List<BehaviorContent> behaviors){
        mBehaviors = behaviors;
    }

    public void update(){
        mFilter.setBehaviorList(mBehaviors);
        notifyDataSetChanged();
    }

    public BehaviorFilter getFilter(){
        return mFilter;
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
         * Binds a goal to the holder.
         *
         * @param goal the goal whose description is to be drawn.
         */
        public void bind(@NonNull GoalContent goal){
            mCategoryTitle.setText(goal.getTitle());
            if (!goal.getHTMLDescription().isEmpty()){
                mCategoryDescription.setText(Html.fromHtml(goal.getHTMLDescription(), null,
                        new CompassTagHandler(mContext)));
            }
            else{
                mCategoryDescription.setText(goal.getDescription());
            }
        }
    }


    /**
     * View holder for a list item.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    static class BehaviorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ChooseBehaviorsAdapter mAdapter;

        private ImageView mIcon;
        private TextView mTitle;


        /**
         * Constructor.
         *
         * @param rootView a view inflated from R.layout.item_choose_behavior
         */
        public BehaviorViewHolder(@NonNull ChooseBehaviorsAdapter adapter, @NonNull View rootView){
            super(rootView);

            mAdapter = adapter;

            mIcon = (ImageView)rootView.findViewById(R.id.library_behavior_icon);
            mTitle = (TextView)rootView.findViewById(R.id.library_behavior_title);

            rootView.setOnClickListener(this);
        }

        /**
         * Binds a behavior to the holder.
         *
         * @param behavior the behavior to display.
         */
        public void bind(BehaviorContent behavior){
            if (behavior.getIconUrl() != null && !behavior.getIconUrl().isEmpty()){
                ImageLoader.loadBitmap(mIcon, behavior.getIconUrl(), new ImageLoader.Options());
            }
            mTitle.setText(behavior.getTitle());
        }

        @Override
        public void onClick(View view){
            mAdapter.mListener.onBehaviorSelected(mAdapter.mBehaviors.get(getAdapterPosition() - 2));
        }
    }

    /**
     * Listener interface for the adapter.
     *
     * @author Ismael Alonso
     * @version 1.0.1
     */
    public interface ChooseBehaviorsListener{
        /**
         * Called when a behavior is selected.
         *
         * @param behavior the selected behavior.
         */
        void onBehaviorSelected(BehaviorContent behavior);
    }
}
