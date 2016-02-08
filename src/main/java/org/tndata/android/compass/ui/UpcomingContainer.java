package org.tndata.android.compass.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by isma on 2/4/16.
 */
public class UpcomingContainer extends LinearLayout implements Animation.AnimationListener{
    private List<ActionHolder> mDisplayedUpcoming;
    private UpcomingListener mListener;

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
        mOutAnimation = -1;
    }

    public int getCount(){
        return mDisplayedUpcoming.size();
    }

    public void setUpcomingListener(UpcomingListener listener){
        mListener = listener;
    }

    public void addAction(Action action){
        mDisplayedUpcoming.add(new ActionHolder(action));
        //TODO animation
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

    private void outAnimation(int position){
        mOutAnimation = position;

        final ViewGroup view = (ViewGroup)getChildAt(position);
        final int initialHeight = view.getMeasuredHeight();

        Animation animation = new Animation(){
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t){
                if(interpolatedTime != 1){
                    view.getLayoutParams().height = initialHeight-(int)(initialHeight*interpolatedTime);
                    view.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds(){
                return true;
            }
        };

        //1dp/ms
        int length = (int)(initialHeight/view.getContext().getResources().getDisplayMetrics().density);
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
    }

    @Override
    public void onAnimationRepeat(Animation animation){

    }

    private class ActionHolder implements OnClickListener{
        private Action mAction;

        public ActionHolder(Action action){
            mAction = action;

            LayoutInflater inflater = LayoutInflater.from(getContext());
            View rootView = inflater.inflate(R.layout.item_upcoming_action, UpcomingContainer.this, false);

            TextView title = (TextView)rootView.findViewById(R.id.action_title);
            TextView goal = (TextView)rootView.findViewById(R.id.action_goal);
            TextView time = (TextView)rootView.findViewById(R.id.action_time);

            title.setText(mAction.getTitle());
            goal.setText(mAction.getGoalTitle());
            time.setText(mAction.getNextReminderDisplay());

            addView(rootView);
            rootView.setOnClickListener(this);
            rootView.findViewById(R.id.action_overflow_box).setOnClickListener(this);
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

    public interface UpcomingListener{
        void onActionClick(Action action);
        void onActionOverflowClick(View view, Action action);
    }
}
