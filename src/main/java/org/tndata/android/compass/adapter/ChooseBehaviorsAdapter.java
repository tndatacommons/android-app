package org.tndata.android.compass.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.filter.BehaviorFilter;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.UserGoal;
import org.tndata.android.compass.ui.parallaxrecyclerview.HeaderLayoutManagerFixed;
import org.tndata.android.compass.ui.parallaxrecyclerview.ParallaxRecyclerAdapter;
import org.tndata.android.compass.util.CompassTagHandler;
import org.tndata.android.compass.util.ImageLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Adapter for the behavior picker.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ChooseBehaviorsAdapter
        extends ParallaxRecyclerAdapter<Behavior>
        implements ParallaxRecyclerAdapter.OnClickEvent{

    private Context mContext;
    private CompassApplication mApplication;
    private ChooseBehaviorsListener mListener;
    private RecyclerView mRecyclerView;
    private Goal mGoal;
    private BehaviorFilter mFilter;

    private CompassTagHandler mTagHandler;

    private List<Behavior> mBehaviors;
    private int mExpandedBehavior;

    private TextView mAddGoalCurrentButton;
    private boolean mIsGoalAdded;

    private boolean mIsEditable;


    /**
     * Constructor,
     *
     * @param context the context.
     * @param listener an implementation of the listener to act upon events.
     * @param app a reference to the application class.
     * @param recyclerView the view that will contain this adapter.
     * @param category the parent category of this goal.
     * @param goal the goal whose behaviors are to be listed.
     * @param isGoalAdded whether the provided goal is in the user's list.
     */
    public ChooseBehaviorsAdapter(@NonNull Context context, @NonNull ChooseBehaviorsListener listener,
                                  @NonNull CompassApplication app, @NonNull RecyclerView recyclerView,
                                  @NonNull Category category, @NonNull Goal goal, boolean isGoalAdded){
        super(new ArrayList<Behavior>());

        //Assign the references
        mContext = context;
        mApplication = app;
        mListener = listener;
        mRecyclerView = recyclerView;
        mGoal = goal;
        mIsGoalAdded = isGoalAdded;
        mFilter = null;

        //The tag handler is used in a couple of places, so previous instantiation and
        //  reuse might help performance.
        mTagHandler = new CompassTagHandler(mContext);

        //Create an empty list and "nullify" the expanded behavior.
        mBehaviors = new ArrayList<>();
        mExpandedBehavior = -1;

        mIsEditable = true;
        UserGoal userGoal = mApplication.getUserData().getGoal(goal);
        if (userGoal != null){
            mIsEditable = userGoal.isEditable();
        }

        //Create and set the headers
        Behavior headerBehavior = new Behavior();
        headerBehavior.setDescription(mGoal.getDescription());
        headerBehavior.setId(0);
        mBehaviors.add(headerBehavior);
        setHeader(category);

        //Add listeners and interfaces
        implementRecyclerAdapterMethods(new ChooseBehaviorsAdapterMethods());
        setOnClickEvent(this);
    }

    /**
     * Creates the parallax header view containing the goal's icon and sets it in the list.
     *
     * @param category the parent category of the goal whose behaviors are to be listed.
     */
    @SuppressWarnings("deprecation")
    private void setHeader(Category category){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View header = inflater.inflate(R.layout.header_choose_behaviors, mRecyclerView, false);

        ImageView headerIcon = (ImageView)header.findViewById(R.id.header_choose_behaviors_icon);
        mGoal.loadIconIntoView(headerIcon);

        RelativeLayout circleView = (RelativeLayout)header.findViewById(R.id.header_choose_behaviors_circle_view);
        GradientDrawable gradientDrawable = (GradientDrawable) circleView.getBackground();
        if (category != null && !category.getSecondaryColor().isEmpty()){
            gradientDrawable.setColor(Color.parseColor(category.getSecondaryColor()));
        }
        else{
            gradientDrawable.setColor(mContext.getResources().getColor(R.color.grow_accent));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            circleView.setBackground(gradientDrawable);
        }
        else{
            circleView.setBackgroundDrawable(gradientDrawable);
        }

        ((HeaderLayoutManagerFixed)mRecyclerView.getLayoutManager()).setHeaderIncrementFixer(header);
        setShouldClipView(false);
        setParallaxHeader(header, mRecyclerView);
        setOnParallaxScroll(new ParallaxRecyclerAdapter.OnParallaxScroll(){
            @Override
            public void onParallaxScroll(float percentage, float offset, View parallax){
                mListener.onScroll(percentage, offset);
            }
        });
    }

    /**
     * Sets the list of behaviors. and notifies the adapter.
     *
     * @param behaviors the list of behaviors to be set.
     */
    public void setBehaviors(Collection<Behavior> behaviors){
        mBehaviors.clear();

        Behavior headerBehavior = new Behavior();
        headerBehavior.setDescription(mGoal.getDescription());
        headerBehavior.setId(0);
        mBehaviors.add(headerBehavior);

        mBehaviors.addAll(behaviors);
        notifyDataSetChanged();

        if (mFilter == null){
            mFilter = new BehaviorFilter(this, behaviors);
        }
    }

    public void filter(CharSequence constraint){
        if (mFilter != null){
            mFilter.filter(constraint);
        }
    }

    private void addGoalClicked(){
        mIsGoalAdded = true;
        mListener.addGoal();
    }

    /**
     * Called when the select button is clicked.
     *
     * @param holder the view holder containing the behavior.
     */
    private void selectBehaviorClicked(BehaviorViewHolder holder){
        Behavior behavior = mBehaviors.get(holder.getAdapterPosition()-1);
        boolean isBehaviorSelected = mApplication.getBehaviors().containsKey(behavior.getId());

        if (mIsEditable){
            //TODO could be nice to check if the piece of content is being removed or added
            if (isBehaviorSelected){
                //Tapping this again should remove the behavior
                Log.d("GoalTryActivity", "Trying to remove behavior: " + behavior.getTitle());
                mListener.deleteBehavior(behavior);
                holder.mSelectBehavior.setImageResource(R.drawable.ic_blue_plus_circle);
            }
            else{
                //We need to add the behavior to the user's selections.
                mListener.addBehavior(behavior);
                holder.mSelectBehavior.setImageResource(R.drawable.ic_blue_check_circle);
            }
        }
    }

    /**
     * Called when the select actions button is clicked.
     *
     * @param position the position of the containing behavior.
     */
    private void selectActionsClicked(int position){
        mListener.selectActions(mBehaviors.get(position));
    }

    /**
     * Called when the more info button is clicked.
     *
     * @param position the position of the containing behavior.
     */
    private void moreInfoClicked(int position){
        mListener.moreInfo(mBehaviors.get(position));
    }

    /**
     * Called when the do it now button is clicked.
     *
     * @param position the position of the containing behavior.
     */
    private void doItNowClicked(int position){
        mListener.doItNow(mBehaviors.get(position));
    }

    @Override
    public void onClick(View view, int position){
        if (position > 0){
            int lastExpanded = mExpandedBehavior;

            //Add one to the position to account for the header
            if (mExpandedBehavior == position+1){
                mExpandedBehavior = -1;
                notifyItemChanged(lastExpanded);
            }
            else{
                mExpandedBehavior = position+1;
                notifyItemChanged(mExpandedBehavior);
                //Let us redraw the item that has changed, this forces the RecyclerView to
                //  respect the layout of each item, and none will overlap
                if (lastExpanded != -1){
                    notifyItemChanged(lastExpanded);
                }
                mRecyclerView.scrollToPosition(mExpandedBehavior);
            }
        }
    }

    public void disableAddGoalButton(){
        mIsGoalAdded = true;
        if (mAddGoalCurrentButton != null){
            mAddGoalCurrentButton.setVisibility(View.GONE);
        }
    }

    /**
     * Implementation of the RecyclerAdapterMethods interface.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class ChooseBehaviorsAdapterMethods implements ParallaxRecyclerAdapter.RecyclerAdapterMethods{
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View rootView = inflater.inflate(R.layout.item_choose_behavior, viewGroup, false);
            return new BehaviorViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
            BehaviorViewHolder holder = (BehaviorViewHolder)rawHolder;
            Behavior behavior = mBehaviors.get(position);

            boolean isBehaviorSelected = mApplication.getBehaviors().containsKey(behavior.getId());

            if (position == 0 && behavior.getId() == 0){
                //Display the header card
                if (!behavior.getHTMLDescription().isEmpty()){
                    holder.mHeader.setText(Html.fromHtml(behavior.getHTMLDescription(), null, mTagHandler));
                }
                else{
                    holder.mHeader.setText(behavior.getDescription());
                }

                holder.mHeaderWrapper.setVisibility(View.VISIBLE);
                mAddGoalCurrentButton = holder.mAddGoal;
                if (!mIsGoalAdded){
                    holder.mAddGoal.setVisibility(View.VISIBLE);
                }
                holder.mIcon.setVisibility(View.GONE);
                holder.mDescription.setVisibility(View.GONE);
                holder.mTitle.setVisibility(View.GONE);
                holder.mActionWrapper.setVisibility(View.GONE);
            }
            else{
                //Handle all other cards
                holder.mHeaderWrapper.setVisibility(View.GONE);

                holder.mTitle.setText(behavior.getTitle());
                if (!behavior.getHTMLDescription().isEmpty()){
                    holder.mDescription.setText(Html.fromHtml(behavior.getHTMLDescription(), null, mTagHandler));
                }
                else{
                    holder.mDescription.setText(behavior.getDescription());
                }

                if (mExpandedBehavior == position+1){
                    holder.mDescription.setVisibility(View.VISIBLE);
                    holder.mActionWrapper.setVisibility(View.VISIBLE);
                    holder.mIcon.setVisibility(View.GONE);
                }
                else{
                    holder.mDescription.setVisibility(View.GONE);
                    holder.mActionWrapper.setVisibility(View.GONE);
                    holder.mIcon.setVisibility(View.VISIBLE);
                }

                if (behavior.getIconUrl() != null && !behavior.getIconUrl().isEmpty()){
                    ImageLoader.loadBitmap(holder.mIcon, behavior.getIconUrl(), new ImageLoader.Options());
                }

                if (isBehaviorSelected){
                    //If the user has already selected the behavior, update the icon
                    holder.mSelectBehavior.setImageResource(R.drawable.ic_blue_check_circle);
                }

                if (behavior.getMoreInfo().equals("")){
                    holder.mMoreInfo.setVisibility(View.GONE);
                }
                else{
                    holder.mMoreInfo.setVisibility(View.VISIBLE);
                }

                if (behavior.getActionCount() == 0){
                    holder.mSelectActions.setVisibility(View.GONE);
                }
                else{
                    holder.mSelectActions.setVisibility(View.VISIBLE);
                }

                if (behavior.getExternalResource().isEmpty()){
                    holder.mDoItNow.setVisibility(View.GONE);
                }
                else{
                    holder.mDoItNow.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public int getItemCount(){
            return mBehaviors.size();
        }
    }

    /**
     * View holder for a list item.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class BehaviorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //These are the views for the header-type card
        private View mHeaderWrapper;
        private TextView mHeader;
        private TextView mAddGoal;

        //These are the views for the behavior-type card
        private ImageView mIcon;
        private TextView mTitle;
        private TextView mDescription;

        private LinearLayout mActionWrapper;
        private ImageView mMoreInfo;
        private ImageView mSelectActions;
        private ImageView mSelectBehavior;
        private TextView mDoItNow;


        /**
         * Constructor.
         *
         * @param rootView a view inflated from R.layout.item_choose_behavior
         */
        public BehaviorViewHolder(View rootView){
            super(rootView);

            mHeaderWrapper = rootView.findViewById(R.id.choose_behavior_header_wrapper);
            mHeader = (TextView)rootView.findViewById(R.id.choose_behavior_header);
            mAddGoal = (TextView)rootView.findViewById(R.id.choose_behavior_add_goal);

            mIcon = (ImageView)rootView.findViewById(R.id.choose_behavior_icon);
            mTitle = (TextView)rootView.findViewById(R.id.choose_behavior_title);
            mDescription = (TextView)rootView.findViewById(R.id.choose_behavior_description);

            mActionWrapper = (LinearLayout)rootView.findViewById(R.id.choose_behavior_action_wrapper);
            mSelectBehavior = (ImageView)rootView.findViewById(R.id.choose_behavior_select);
            mSelectActions = (ImageView)rootView.findViewById(R.id.choose_behavior_select_actions);
            mMoreInfo = (ImageView)rootView.findViewById(R.id.choose_behavior_more_info);
            mDoItNow = (TextView)rootView.findViewById(R.id.choose_behavior_do_it_now);

            mAddGoal.setOnClickListener(this);
            mSelectBehavior.setOnClickListener(this);
            mSelectActions.setOnClickListener(this);
            mMoreInfo.setOnClickListener(this);
            mDoItNow.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            switch (view.getId()){
                case R.id.choose_behavior_add_goal:
                    mAddGoal.setVisibility(View.GONE);
                    addGoalClicked();
                    break;

                case R.id.choose_behavior_select:
                    selectBehaviorClicked(this);
                    break;

                case R.id.choose_behavior_select_actions:
                    selectActionsClicked(getAdapterPosition()-1);
                    break;

                case R.id.choose_behavior_more_info:
                    moreInfoClicked(getAdapterPosition()-1);
                    break;

                case R.id.choose_behavior_do_it_now:
                    doItNowClicked(getAdapterPosition()-1);
                    break;
            }
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
         * Called when the add goal button is clicked.
         */
        void addGoal();

        /**
         * Called when the add behavior button is clicked.
         *
         * @param behavior the containing behavior.
         */
        void addBehavior(Behavior behavior);

        /**
         * Called when the delete behavior button is clicked.
         *
         * @param behavior the containing behavior.
         */
        void deleteBehavior(Behavior behavior);

        /**
         * Called when the select actions button is clicked.
         *
         * @param behavior the containing behavior.
         */
        void selectActions(Behavior behavior);

        /**
         * Called when the more info button is clicked.
         *
         * @param behavior the containing behavior.
         */
        void moreInfo(Behavior behavior);

        /**
         * Called when the do it now button is clicked.
         *
         * @param behavior the containing behavior.
         */
        void doItNow(Behavior behavior);

        /**
         * Called when the RecyclerView scrolls.
         *
         * @param percentage the scroll percentage.
         * @param offset the scroll offset.
         */
        void onScroll(float percentage, float offset);
    }
}
