package org.tndata.android.compass.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.feed.DisplayableGoal;
import org.tndata.android.compass.util.ImageLoader;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by isma on 2/4/16.
 */
public class GoalContainer extends LinearLayout{
    private List<GoalHolder> mDisplayedGoals;
    private GoalListener mListener;


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
    }

    public int getCount(){
        return mDisplayedGoals.size();
    }

    public void setGoalListener(GoalListener listener){
        mListener = listener;
    }

    public void addGoal(DisplayableGoal goal){
        mDisplayedGoals.add(new GoalHolder(goal));
        //TODO animation
    }

    public void removeGoal(DisplayableGoal goal){

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

    public interface GoalListener{
        void onGoalClick(DisplayableGoal goal);
    }
}
