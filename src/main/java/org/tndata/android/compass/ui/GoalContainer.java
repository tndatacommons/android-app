package org.tndata.android.compass.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.feed.DisplayableGoal;
import org.tndata.android.compass.util.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 * Created by isma on 2/4/16.
 */
public class GoalContainer extends LinearLayout implements Animation.AnimationListener{
    private List<GoalHolder> mDisplayedGoals;
    private GoalContainerListener mListener;

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

    private void init(){
        setOrientation(VERTICAL);
        mDisplayedGoals = new ArrayList<>();
        mAnimate = false;
        mGoalQueue = new LinkedList<>();
        mOutAnimation = -1;
    }

    public void setGoalListener(GoalContainerListener listener){
        mListener = listener;
    }

    public void setAnimationsEnabled(boolean enabled){
        mAnimate = enabled;
    }

    public int getCount(){
        return mDisplayedGoals.size() + (mGoalQueue.isEmpty() ? 0 : mGoalQueue.size()-1);
    }

    public void addGoal(DisplayableGoal goal){
        if (mAnimate){
            Log.d("GoalContainer", mDisplayedGoals.size() + ", " + mGoalQueue.size());
            mGoalQueue.add(goal);
            if (mGoalQueue.size() == 1){
                inAnimation();
            }
        }
        else{
            mDisplayedGoals.add(new GoalHolder(goal));
        }
    }

    public void removeGoal(DisplayableGoal goal){
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

    }


    private class GoalHolder implements OnClickListener{
        private DisplayableGoal mGoal;


        @SuppressWarnings("deprecation")
        public GoalHolder(DisplayableGoal goal){
            mGoal = goal;

            LayoutInflater inflater = LayoutInflater.from(getContext());
            View rootView = inflater.inflate(R.layout.item_feed_goal, GoalContainer.this, false);


            RelativeLayout iconContainer = (RelativeLayout)rootView.findViewById(R.id.goal_icon_container);
            ImageView icon = (ImageView)rootView.findViewById(R.id.goal_icon);
            TextView title = (TextView)rootView.findViewById(R.id.goal_title);

            title.setText(mGoal.getTitle());

            GradientDrawable gradientDrawable = (GradientDrawable)iconContainer.getBackground();
            gradientDrawable.setColor(Color.parseColor(mGoal.getColor(getContext())));

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
                iconContainer.setBackgroundDrawable(gradientDrawable);
            }
            else{
                iconContainer.setBackground(gradientDrawable);
            }

            if (mGoal.getIconUrl() != null && !mGoal.getIconUrl().isEmpty()){
                ImageLoader.loadBitmap(icon, mGoal.getIconUrl());
            }

            addView(rootView);
            rootView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            mListener.onGoalClick(mGoal);
        }

        public boolean contains(DisplayableGoal goal){
            return mGoal.equals(goal);
        }
    }


    public interface GoalContainerListener{
        void onGoalClick(DisplayableGoal goal);
    }
}
