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

    public int getCount(){
        //It is safe to assume that there will always be an animation running if the queue
        // ain't empty, so we subtract one because it is already in the displayed list
        return mDisplayedUpcoming.size() + (mActionQueue.isEmpty() ? 0 : mActionQueue.size()-1);
    }

    public void addAction(Action action){
        Log.d("UpcomingContainer", mDisplayedUpcoming.size() + ", " + mActionQueue.size());
        mActionQueue.add(action);
        if (mActionQueue.size() == 1){
            inAnimation();
        }
    }

    public void updateAction(Action action){
        for (ActionHolder holder:mDisplayedUpcoming){
            if (holder.contains(action)){
                holder.update();
            }
        }
    }

    public void removeAction(Action action){
        for (int i = 0; i < mDisplayedUpcoming.size(); i++){
            if (mDisplayedUpcoming.get(i).contains(action)){
                outAnimation(i);
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
            mAction = action;

            LayoutInflater inflater = LayoutInflater.from(getContext());
            View rootView = inflater.inflate(R.layout.item_upcoming_action, UpcomingContainer.this, false);

            mTitle = (TextView)rootView.findViewById(R.id.action_title);
            mGoal = (TextView)rootView.findViewById(R.id.action_goal);
            mTime = (TextView)rootView.findViewById(R.id.action_time);

            update();

            addView(rootView);
            rootView.setOnClickListener(this);
            rootView.findViewById(R.id.action_overflow_box).setOnClickListener(this);
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
