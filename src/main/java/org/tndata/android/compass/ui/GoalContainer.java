package org.tndata.android.compass.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.feed.DisplayableGoal;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.util.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 * UI component that displays goals as requested.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class GoalContainer extends LinearLayout implements Animation.AnimationListener{
    //Goal list and listener
    private List<GoalHolder> mDisplayedGoals;
    private GoalContainerListener mListener;

    //Animation stuff
    private boolean mAnimate;
    private Queue<DisplayableGoal> mGoalQueue;
    private int mOutAnimation;


    public GoalContainer(Context context){
        super(context);
        init();
    }

    public GoalContainer(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    public GoalContainer(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Initializes the container.
     */
    private void init(){
        setOrientation(VERTICAL);
        mDisplayedGoals = new ArrayList<>();
        mAnimate = false;
        mGoalQueue = new LinkedList<>();
        mOutAnimation = -1;
    }

    /**
     * Sets the goal listener.
     *
     * @param listener the new listener.
     */
    public void setGoalListener(@NonNull GoalContainerListener listener){
        mListener = listener;
    }

    /**
     * Enables or disables animations.
     *
     * @param enabled true to enable animations, false to disable them.
     */
    public void setAnimationsEnabled(boolean enabled){
        mAnimate = enabled;
    }

    /**
     * Gets the number of items either displayed or queued to be displayed.
     *
     * @return the number of goals added to this container.
     */
    public int getCount(){
        return mDisplayedGoals.size() + (mGoalQueue.isEmpty() ? 0 : mGoalQueue.size()-1);
    }

    /**
     * Adds a goal to the container.
     *
     * @param goal the goal to be added.
     */
    public void addGoal(@NonNull DisplayableGoal goal){
        if (mAnimate){
            mGoalQueue.add(goal);
            if (mGoalQueue.size() == 1){
                inAnimation();
            }
        }
        else{
            mDisplayedGoals.add(new GoalHolder(goal));
        }
    }

    /**
     * Refreshes the list of goals that are currently being displayed. Removes the goals
     * that the user has removed, adds the goals that the user has added prior to the last
     * goal being displayed, and updates the goals modified by the user.
     *
     * @param feedData a reference to the feed data bundle.
     */
    public void updateGoals(@NonNull FeedData feedData){
        //First off, find the stopping point in the updated list
        DisplayableGoal stoppingPoint = null;
        //Start searching from the end of the list, it is more likely that the goal will be there
        for (int i = mDisplayedGoals.size()-1; i > 0; i--){
            if (feedData.getGoals().contains(mDisplayedGoals.get(i).mGoal)){
                stoppingPoint = mDisplayedGoals.get(i).mGoal;
                break;
            }
        }

        //Next, update the list of displayed goals
        for (int i = 0; i < feedData.getGoals().size(); i++){
            //Update the existing holder or create a new one according to needs
            if (i < mDisplayedGoals.size()){
                mDisplayedGoals.get(i).update(feedData.getGoals().get(i));
            }
            else{
                mDisplayedGoals.add(new GoalHolder(feedData.getGoals().get(i)));
            }
            //If the stopping point has been reached
            if (stoppingPoint != null && stoppingPoint.equals(mDisplayedGoals.get(i).mGoal)){
                //Remove all the holders after it, if any
                i++;
                while (i < mDisplayedGoals.size()){
                    mDisplayedGoals.remove(i);
                    removeViewAt(i);
                }
                break;
            }
        }
    }

    /**
     * Removes a goal from the container.
     *
     * @param goal the goal to be removed.
     */
    public void removeGoal(@NonNull DisplayableGoal goal){
        for (int i = 0; i < mDisplayedGoals.size(); i++){
            if (mDisplayedGoals.get(i).contains(goal)){
                if (mAnimate){
                    outAnimation(i);
                }
                else{
                    mDisplayedGoals.remove(i);
                    removeViewAt(i);
                }
                break;
            }
        }
    }

    /**
     * Fires the in animation for the next goal in the queue and adds it to the container.
     */
    private void inAnimation(){
        mDisplayedGoals.add(new GoalHolder(mGoalQueue.peek()));

        View view = getChildAt(getChildCount() - 1);
        view.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        int targetHeight = view.getMeasuredHeight();
        view.getLayoutParams().height = 1;

        Animation animation = new ExpandCollapseAnimation(view, targetHeight, true);

        //2ms/dp
        int length = (int)(2*targetHeight/getContext().getResources().getDisplayMetrics().density);
        animation.setDuration(length);
        animation.setAnimationListener(this);
        view.startAnimation(animation);
    }
    /**
     * Fires the out animation for a goal in the container. Removal is performed when
     * the animation is done.
     *
     * @param position the position of the goal to be removed.
     */
    private void outAnimation(int position){
        mOutAnimation = position;

        View view = getChildAt(position);
        int initialHeight = view.getMeasuredHeight();

        Animation animation = new ExpandCollapseAnimation(view, initialHeight, false);

        //1dp/ms
        int length = (int)(initialHeight/getContext().getResources().getDisplayMetrics().density);
        animation.setDuration(length);
        animation.setAnimationListener(this);
        view.startAnimation(animation);
    }

    @Override
    public void onAnimationStart(Animation animation){
        //Unused
    }

    @Override
    public void onAnimationEnd(Animation animation){
        if (mOutAnimation != -1){
            mDisplayedGoals.remove(mOutAnimation);
            removeViewAt(mOutAnimation);
            mOutAnimation = -1;
        }
        else{
            mGoalQueue.remove();
            if (!mGoalQueue.isEmpty()){
                inAnimation();
            }
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation){
        //Unused
    }


    /**
     * Holder for a goal being displayed in the container. The existence of this class
     * facilitates operations on the data set and updating single elements.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class GoalHolder implements OnClickListener{
        //The goal being displayed
        private DisplayableGoal mGoal;

        //UI components
        private RelativeLayout mIconContainer;
        private ImageView mIcon;
        private TextView mTitle;


        /**
         * Constructor.
         *
         * @param goal the goal to be bound
         */
        public GoalHolder(@NonNull DisplayableGoal goal){
            //Inflate the layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View rootView = inflater.inflate(R.layout.item_feed_goal, GoalContainer.this, false);

            //Grab the UI components
            mIconContainer = (RelativeLayout)rootView.findViewById(R.id.goal_icon_container);
            mIcon = (ImageView)rootView.findViewById(R.id.goal_icon);
            mTitle = (TextView)rootView.findViewById(R.id.goal_title);

            //Update the goal
            update(goal);

            //Add the view to the container and set listeners
            addView(rootView);
            rootView.setOnClickListener(this);
        }

        /**
         * Replace the goal contained by this holder.
         *
         * @param goal the new goal to be displayed.
         */
        @SuppressWarnings("deprecation")
        private void update(@NonNull DisplayableGoal goal){
            mGoal = goal;

            mTitle.setText(mGoal.getTitle());

            GradientDrawable gradientDrawable = (GradientDrawable)mIconContainer.getBackground();
            gradientDrawable.setColor(Color.parseColor(mGoal.getColor(getContext())));

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
                mIcon.setBackgroundDrawable(gradientDrawable);
            }
            else{
                mIconContainer.setBackground(gradientDrawable);
            }

            if (mGoal.getIconUrl() != null && !mGoal.getIconUrl().isEmpty()){
                ImageLoader.loadBitmap(mIcon, mGoal.getIconUrl());
            }
        }

        @Override
        public void onClick(View v){
            mListener.onGoalClick(mGoal);
        }

        /**
         * Checks if this holder contains a particular goal.
         *
         * @param goal the goal to be compared.
         * @return true is the goals are the same, false otherwise.
         */
        public boolean contains(@NonNull DisplayableGoal goal){
            return mGoal.equals(goal);
        }
    }


    /**
     * Listener interface for GoalContainer.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface GoalContainerListener{
        /**
         * Called when a goal is selected.
         *
         * @param goal the selected goal.
         */
        void onGoalClick(@NonNull DisplayableGoal goal);
    }
}
