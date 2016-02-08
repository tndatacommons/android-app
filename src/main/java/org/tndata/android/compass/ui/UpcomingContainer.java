package org.tndata.android.compass.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by isma on 2/4/16.
 */
public class UpcomingContainer extends LinearLayout{
    private List<ActionHolder> mDisplayedUpcoming;
    private UpcomingListener mListener;


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

    }

    public void removeFirstAction(){

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
