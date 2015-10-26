package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;

import java.util.List;


/**
 * Created by isma on 10/26/15.
 */
public class CheckInReviewAdapter extends RecyclerView.Adapter<CheckInReviewAdapter.ActionHolder>{
    private Context mContext;
    private List<Action> mActions;

    private CompassApplication mApplication;


    public CheckInReviewAdapter(Context context, List<Action> actions){
        mContext = context;
        mActions = actions;

        mApplication = (CompassApplication)mContext.getApplicationContext();
    }

    @Override
    public ActionHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rootView = inflater.inflate(R.layout.item_check_in_action, parent, false);
        return new ActionHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ActionHolder holder, int position){
        if (position == 0){
            holder.mHeader.setVisibility(View.VISIBLE);
        }
        else{
            holder.mHeader.setVisibility(View.GONE);
        }

        Action action = mActions.get(position);
        holder.mAction.setText(action.getTitle());
        Behavior behavior = mApplication.getUserData().getAction(action).getBehavior();
        String behaviorTitle = behavior.getTitle().substring(0, 1).toLowerCase();
        behaviorTitle += behavior.getTitle().substring(1);
        holder.mBehavior.setText(mContext.getString(R.string.check_in_action_behavior, behaviorTitle));
        holder.mTime.setText(action.getTrigger().getFormattedTime());
    }

    @Override
    public int getItemCount(){
        return mActions.size();
    }


    class ActionHolder extends RecyclerView.ViewHolder{
        private TextView mHeader;
        private TextView mAction;
        private TextView mBehavior;
        private TextView mTime;

        public ActionHolder(View rootView){
            super(rootView);

            mHeader = (TextView)rootView.findViewById(R.id.check_in_action_header);
            mAction = (TextView)rootView.findViewById(R.id.check_in_action_action);
            mBehavior = (TextView)rootView.findViewById(R.id.check_in_action_behavior);
            mTime = (TextView)rootView.findViewById(R.id.check_in_action_time);
        }
    }
}
