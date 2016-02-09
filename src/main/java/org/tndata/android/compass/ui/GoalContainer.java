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
import org.tndata.android.compass.model.FeedData;
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

    public void updateGoals(FeedData feedData){
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

        private RelativeLayout mIconContainer;
        private ImageView mIcon;
        private TextView mTitle;


        public GoalHolder(DisplayableGoal goal){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View rootView = inflater.inflate(R.layout.item_feed_goal, GoalContainer.this, false);

            mIconContainer = (RelativeLayout)rootView.findViewById(R.id.goal_icon_container);
            mIcon = (ImageView)rootView.findViewById(R.id.goal_icon);
            mTitle = (TextView)rootView.findViewById(R.id.goal_title);

            update(goal);

            addView(rootView);
            rootView.setOnClickListener(this);
        }

        @SuppressWarnings("deprecation")
        private void update(DisplayableGoal goal){
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

        public boolean contains(DisplayableGoal goal){
            return mGoal.equals(goal);
        }
    }


    public interface GoalContainerListener{
        void onGoalClick(DisplayableGoal goal);
    }
}
