package org.tndata.android.compass.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.FeedData;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 * UI component that displays actions in the feed as requested.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class UpcomingContainer extends LinearLayout implements Animation.AnimationListener{
    //List and listener
    private List<ActionHolder> mDisplayedUpcoming;
    private UpcomingContainerListener mListener;

    //Animation stuff
    private boolean mAnimate;
    private Queue<Action> mActionQueue;
    private int mOutAnimation;


    public UpcomingContainer(Context context){
        super(context);
        init();
    }

    public UpcomingContainer(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    public UpcomingContainer(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Initializes the container.
     */
    private void init(){
        setOrientation(VERTICAL);
        mDisplayedUpcoming = new ArrayList<>();
        mAnimate = false;
        mActionQueue = new LinkedList<>();
        mOutAnimation = -1;
    }

    /**
     * Sets the container listener.
     *
     * @param listener the container listener.
     */
    public void setUpcomingListener(UpcomingContainerListener listener){
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
     * @return the number of actions added to this container.
     */
    public int getCount(){
        //It is safe to assume that there will always be an animation running if the queue
        // ain't empty, so we subtract one because it is already in the displayed list
        return mDisplayedUpcoming.size() + (mActionQueue.isEmpty() ? 0 : mActionQueue.size()-1);
    }

    /**
     * Adds an action to the container.
     *
     * @param action the action to be added.
     */
    public void addAction(Action action){
        if (mAnimate){
            mActionQueue.add(action);
            if (mActionQueue.size() == 1){
                inAnimation();
            }
        }
        else{
            mDisplayedUpcoming.add(new ActionHolder(action));
        }
    }

    /**
     * Updates a particular action in this container.
     *
     * @param action the action to be updated.
     */
    public void updateAction(Action action){
        for (ActionHolder holder:mDisplayedUpcoming){
            if (holder.contains(action)){
                holder.update();
            }
        }
    }

    /**
     * Refreshes the list of actions that are currently being displayed. Removes the actions
     * that the user has removed, adds the actions that the user has added prior to the last
     * action being displayed, and updates the actions modified by the user.
     *
     * @param feedData a reference to the feed data bundle.
     */
    public void updateActions(FeedData feedData){
        //First off, find the stopping point in the updated list
        Action stoppingPoint = null;
        //Start searching from the end of the list, it is more likely that the goal will be there
        for (int i = mDisplayedUpcoming.size()-1; i > 0; i--){
            if (feedData.getUpcomingActions().contains(mDisplayedUpcoming.get(i).mAction)){
                stoppingPoint = mDisplayedUpcoming.get(i).mAction;
                break;
            }
        }

        //Next, update the list of displayed goals
        for (int i = 0; i < feedData.getUpcomingActions().size(); i++){
            //Update the existing holder or create a new one according to needs
            if (i < mDisplayedUpcoming.size()){
                mDisplayedUpcoming.get(i).update(feedData.getUpcomingActions().get(i));
            }
            else{
                mDisplayedUpcoming.add(new ActionHolder(feedData.getUpcomingActions().get(i)));
            }
            //If the stopping point has been reached
            if (stoppingPoint != null && stoppingPoint.equals(mDisplayedUpcoming.get(i).mAction)){
                //Remove all the holders after it, if any
                i++;
                while (i < mDisplayedUpcoming.size()){
                    mDisplayedUpcoming.remove(i);
                    removeViewAt(i);
                }
                break;
            }
        }
    }

    /**
     * Removes an action from the container.
     *
     * @param action the action to be removed.
     */
    public void removeAction(Action action){
        for (int i = 0; i < mDisplayedUpcoming.size(); i++){
            if (mDisplayedUpcoming.get(i).contains(action)){
                if (mAnimate){
                    outAnimation(i);
                }
                else{
                    mDisplayedUpcoming.remove(i);
                    removeViewAt(i);
                }
                break;
            }
        }
    }

    /**
     * Removes the first animation from the container.
     */
    public void removeFirstAction(){
        outAnimation(0);
    }

    /**
     * Fires the in animation for the next action in the queue and adds it to the container.
     */
    private void inAnimation(){
        mDisplayedUpcoming.add(new ActionHolder(mActionQueue.peek()));

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
     * Fires the out animation for an action in the container. Removal is performed when
     * the animation is done.
     *
     * @param position the position of the action to be removed.
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
            mDisplayedUpcoming.remove(mOutAnimation);
            removeViewAt(mOutAnimation);
            mOutAnimation = -1;
        }
        else{
            mActionQueue.remove();
            if (!mActionQueue.isEmpty()){
                inAnimation();
            }
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation){
        //Unused
    }


    /**
     * Holder for an action being displayed in the container. The existence of this class
     * facilitates operations on the data set and updating single elements.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class ActionHolder implements OnClickListener{
        //The action being displayed
        private Action mAction;

        //UI components
        private TextView mTitle;
        private TextView mGoal;
        private TextView mTime;


        /**
         * Constructor.
         *
         * @param action the action to be bound.
         */
        public ActionHolder(Action action){
            //Inflate the layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View rootView = inflater.inflate(R.layout.item_upcoming_action, UpcomingContainer.this, false);

            //Grab the UI components
            mTitle = (TextView)rootView.findViewById(R.id.action_title);
            mGoal = (TextView)rootView.findViewById(R.id.action_goal);
            mTime = (TextView)rootView.findViewById(R.id.action_time);

            //Update the action
            update(action);

            //Add this view to the container and set listeners
            addView(rootView);
            rootView.setOnClickListener(this);
            rootView.findViewById(R.id.action_overflow_box).setOnClickListener(this);
        }

        /**
         * Replace the action contained by this holder.
         *
         * @param action the new action to be displayed.
         */
        private void update(Action action){
            mAction = action;
            update();
        }

        /**
         * Updates the UI with changes the action currently contained may have experienced.
         */
        private void update(){
            mTitle.setText(mAction.getTitle());
            mGoal.setText(mAction.getGoalTitle());
            mTime.setText(mAction.getNextReminderDisplay());
        }

        @Override
        public void onClick(View v){
            switch (v.getId()){
                case R.id.action_overflow_box:
                    mListener.onActionOverflowClick(v, mAction);
                    break;

                default:
                    mListener.onActionClick(mAction);
            }
        }

        /**
         * Checks if this holder contains a particular action.
         *
         * @param action the action to be compared.
         * @return true is the actions are the same, false otherwise.
         */
        public boolean contains(Action action){
            return mAction.equals(action);
        }
    }


    /**
     * Interface for the UpcomingContainer.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface UpcomingContainerListener{
        /**
         * Called when an action is tapped.
         *
         * @param action the tapped action.
         */
        void onActionClick(Action action);

        /**
         * Called when an overflow menu has been tapped.
         *
         * @param view the view containing the overflow.
         * @param action the action whose overflow was tapped.
         */
        void onActionOverflowClick(View view, Action action);
    }
}
