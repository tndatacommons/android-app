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
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.ui.parallaxrecyclerview.HeaderLayoutManagerFixed;
import org.tndata.android.compass.ui.parallaxrecyclerview.ParallaxRecyclerAdapter;
import org.tndata.android.compass.util.CompassTagHandler;
import org.tndata.android.compass.util.ImageLoader;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by isma on 8/31/15.
 */
public class ChooseBehaviorsAdapter
        extends ParallaxRecyclerAdapter<Behavior>
        implements ParallaxRecyclerAdapter.OnClickEvent{

    private Context mContext;
    private CompassApplication mApplication;
    private ChooseBehaviorsListener mListener;
    private RecyclerView mRecyclerView;
    private Goal mGoal;

    private CompassTagHandler mTagHandler;

    private List<Behavior> mBehaviors;
    private int mExpandedBehavior;


    public ChooseBehaviorsAdapter(@NonNull Context context, @NonNull ChooseBehaviorsListener listener,
                                  @NonNull CompassApplication app, @NonNull RecyclerView recyclerView,
                                  @NonNull Category category, @NonNull Goal goal){
        super(new ArrayList<Behavior>());

        mContext = context;
        mApplication = app;
        mListener = listener;
        mRecyclerView = recyclerView;
        mGoal = goal;

        mTagHandler = new CompassTagHandler(mContext);

        mBehaviors = new ArrayList<>();
        mExpandedBehavior = -1;

        Behavior headerBehavior = new Behavior();
        headerBehavior.setDescription(mGoal.getDescription());
        headerBehavior.setId(0);
        mBehaviors.add(headerBehavior);

        //Set the header
        setHeader(category);

        //Add listeners and interfaces
        implementRecyclerAdapterMethods(new ChooseBehaviorsAdapterMethods());
        setOnClickEvent(this);
    }

    /**
     * Creates the parallax header view and sets it in the list.
     */
    @SuppressWarnings("deprecation")
    private void setHeader(Category category){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View header = inflater.inflate(R.layout.header_choose_behaviors, mRecyclerView, false);

        ImageView headerIcon = (ImageView)header.findViewById(R.id.header_choose_behaviors_icon);
        mGoal.loadIconIntoView(mContext, headerIcon);

        RelativeLayout circleView = (RelativeLayout)header.findViewById(R.id.header_choose_behaviors_circle_view);
        GradientDrawable gradientDrawable = (GradientDrawable) circleView.getBackground();
        if (!category.getSecondaryColor().isEmpty()){
            gradientDrawable.setColor(Color.parseColor(category.getSecondaryColor()));
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

    public void addBehaviors(List<Behavior> behaviors){
        mBehaviors.clear();

        Behavior headerBehavior = new Behavior();
        headerBehavior.setDescription(mGoal.getDescription());
        headerBehavior.setId(0);
        mBehaviors.add(headerBehavior);

        mBehaviors.addAll(behaviors);
        notifyDataSetChanged();
    }

    private void selectBehaviorClicked(ChooseBehaviorsViewHolder holder, int position){
        Behavior behavior = mBehaviors.get(position);
        boolean isBehaviorSelected = mApplication.getBehaviors().contains(behavior);

        if (mGoal.areCustomTriggersAllowed()){
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

    private void selectActionsClicked(int position){
        mListener.selectActions(mBehaviors.get(position));
    }

    private void moreInfoClicked(int position){
        mListener.moreInfo(mBehaviors.get(position));
    }

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

    private class ChooseBehaviorsAdapterMethods implements ParallaxRecyclerAdapter.RecyclerAdapterMethods{
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View rootView = inflater.inflate(R.layout.item_choose_behavior, viewGroup, false);
            return new ChooseBehaviorsViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
            ChooseBehaviorsViewHolder holder = (ChooseBehaviorsViewHolder)rawHolder;
            Behavior behavior = mBehaviors.get(position);

            boolean isBehaviorSelected = mApplication.getBehaviors().contains(behavior);

            if (position == 0 && behavior.getId() == 0){
                //Display the Header Card
                if (!behavior.getHTMLDescription().isEmpty()){
                    holder.mHeader.setText(Html.fromHtml(behavior.getHTMLDescription(), null, mTagHandler));
                }
                else{
                    holder.mHeader.setText(behavior.getDescription());
                }

                holder.mHeader.setVisibility(View.VISIBLE);
                holder.mIcon.setVisibility(View.GONE);
                holder.mDescription.setVisibility(View.GONE);
                holder.mExternalResource.setVisibility(View.GONE);
                holder.mTitle.setVisibility(View.GONE);
                holder.mActionWrapper.setVisibility(View.GONE);
            }
            else{
                //Handle all other cards
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
                    holder.mExternalResource.setVisibility(View.GONE);
                }
                else{
                    if (mExpandedBehavior == position+1){
                        holder.mExternalResource.setVisibility(View.VISIBLE);
                    }
                    else{
                        holder.mExternalResource.setVisibility(View.GONE);
                    }
                    holder.mDoItNow.setVisibility(View.VISIBLE);
                    holder.mExternalResource.setText(behavior.getExternalResource());
                }
            }
        }

        @Override
        public int getItemCount(){
            return mBehaviors.size();
        }
    }


    private class ChooseBehaviorsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mHeader;
        private ImageView mIcon;
        private TextView mTitle;
        private TextView mDescription;
        private TextView mExternalResource;

        private LinearLayout mActionWrapper;
        private ImageView mMoreInfo;
        private ImageView mSelectActions;
        private ImageView mSelectBehavior;
        private TextView mDoItNow;


        public ChooseBehaviorsViewHolder(View rootView){
            super(rootView);

            mHeader = (TextView)rootView.findViewById(R.id.list_item_behavior_header_textview);
            mIcon = (ImageView)rootView.findViewById(R.id.choose_behavior_icon);
            mTitle = (TextView)rootView.findViewById(R.id.choose_behavior_title);
            mDescription = (TextView)rootView.findViewById(R.id.choose_behavior_description);
            mExternalResource = (TextView)rootView.findViewById(R.id.choose_behavior_external_resource);

            mActionWrapper = (LinearLayout)rootView.findViewById(R.id.choose_behavior_action_wrapper);
            mSelectBehavior = (ImageView)rootView.findViewById(R.id.choose_behavior_select);
            mSelectActions = (ImageView)rootView.findViewById(R.id.choose_behavior_select_actions);
            mMoreInfo = (ImageView)rootView.findViewById(R.id.choose_behavior_more_info);
            mDoItNow = (TextView)rootView.findViewById(R.id.choose_behavior_do_it_now);

            mSelectBehavior.setOnClickListener(this);
            mSelectActions.setOnClickListener(this);
            mMoreInfo.setOnClickListener(this);
            mDoItNow.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            switch (view.getId()){
                case R.id.choose_behavior_select:
                    selectBehaviorClicked(this, getAdapterPosition()-1);
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


    public interface ChooseBehaviorsListener{
        void addBehavior(Behavior behavior);
        void deleteBehavior(Behavior behavior);
        void selectActions(Behavior behavior);
        void moreInfo(Behavior behavior);
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
