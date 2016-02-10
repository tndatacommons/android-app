package org.tndata.android.compass.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
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
 * Created by isma on 2/4/16.
 */
public class UpcomingContainer extends LinearLayout implements Animation.AnimationListener{
    private List<ActionHolder> mDisplayedUpcoming;
    private UpcomingContainerListener mListener;

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

    public void setAnimationsEnabled(boolean enabled){
        mAnimate = enabled;
    }

    public int getCount(){
        //It is safe to assume that there will always be an animation running if the queue
        // ain't empty, so we subtract one because it is already in the displayed list
        return mDisplayedUpcoming.size() + (mActionQueue.isEmpty() ? 0 : mActionQueue.size()-1);
    }

    public void addAction(Action action){
        if (mAnimate){
            Log.d("UpcomingContainer", mDisplayedUpcoming.size() + ", " + mActionQueue.size());
            mActionQueue.add(action);
            if (mActionQueue.size() == 1){
                inAnimation();
            }
        }
        else{
            mDisplayedUpcoming.add(new ActionHolder(action));
        }
    }

    public void updateAction(Action action){
        for (ActionHolder holder:mDisplayedUpcoming){
            if (holder.contains(action)){
                holder.update();
            }
        }
    }

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

    public void removeFirstAction(){
        outAnimation(0);
    }

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

    }


    private class ActionHolder implements OnClickListener{
        private Action mAction;

        private TextView mTitle;
        private TextView mGoal;
        private TextView mTime;


        public ActionHolder(Action action){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View rootView = inflater.inflate(R.layout.item_upcoming_action, UpcomingContainer.this, false);

            mTitle = (TextView)rootView.findViewById(R.id.action_title);
            mGoal = (TextView)rootView.findViewById(R.id.action_goal);
            mTime = (TextView)rootView.findViewById(R.id.action_time);

            update(action);

            addView(rootView);
            rootView.setOnClickListener(this);
            rootView.findViewById(R.id.action_overflow_box).setOnClickListener(this);
        }

        private void update(Action action){
            mAction = action;
            update();
        }

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

        public boolean contains(Action action){
            return mAction.equals(action);
        }
    }


    public interface UpcomingContainerListener{
        void onActionClick(Action action);
        void onActionOverflowClick(View view, Action action);
    }
}
