package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;

import java.util.List;


/**
 * Adapter for the RecyclerView in CheckInReviewFragment.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CheckInReviewAdapter extends RecyclerView.Adapter<CheckInReviewAdapter.ActionHolder>{
    private Context mContext;
    private List<Action> mActions;


    /**
     * Constructor.
     *
     * @param context the context.
     * @param actions the list of actions to be displayed.
     */
    public CheckInReviewAdapter(Context context, List<Action> actions){
        mContext = context;
        mActions = actions;
    }

    @Override
    public ActionHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rootView = inflater.inflate(R.layout.item_check_in_action, parent, false);
        return new ActionHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ActionHolder holder, int position){
        //The header shold be shown only if this is the first element
        if (position == 0){
            holder.mHeader.setVisibility(View.VISIBLE);
        }
        else{
            holder.mHeader.setVisibility(View.GONE);
        }

        //Retrieve the data holders
        Action action = mActions.get(position);
        Behavior behavior = action.getBehavior();

        //Populate the UI
        holder.mAction.setText(action.getTitle());
        String behaviorTitle = behavior.getTitle().substring(0, 1).toLowerCase();
        behaviorTitle += behavior.getTitle().substring(1);
        holder.mBehavior.setText(mContext.getString(R.string.check_in_action_behavior, behaviorTitle));
        holder.mTime.setText(action.getTrigger().getFormattedTime());
    }

    @Override
    public int getItemCount(){
        return mActions.size();
    }


    /**
     * View holder for an action.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    class ActionHolder extends RecyclerView.ViewHolder{
        private TextView mHeader;
        private TextView mAction;
        private TextView mBehavior;
        private TextView mTime;


        /**
         * Constructor.
         *
         * @param rootView the root view of the item.
         */
        public ActionHolder(View rootView){
            super(rootView);

            mHeader = (TextView)rootView.findViewById(R.id.check_in_action_header);
            mAction = (TextView)rootView.findViewById(R.id.check_in_action_action);
            mBehavior = (TextView)rootView.findViewById(R.id.check_in_action_behavior);
            mTime = (TextView)rootView.findViewById(R.id.check_in_action_time);
        }
    }
}
