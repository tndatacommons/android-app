package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.GoalContent;


/**
 * Adapter for GoalActivity.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class GoalAdapter extends MaterialAdapter implements View.OnClickListener{
    private Context mContext;
    private GoalListener mListener;
    private GoalContent mGoal;


    /**
     * Constructor.
     *
     * @param context a reference to the context.
     * @param listener the listener.
     * @param goal the goal to be displayed.
     */
    public GoalAdapter(@NonNull Context context, @NonNull GoalListener listener,
                       @NonNull GoalContent goal){

        super(context, ContentType.DETAIL, false);

        mContext = context;
        mListener = listener;
        mGoal = goal;
    }

    @Override
    protected boolean hasDetails(){
        return false;
    }

    @Override
    protected void bindHeaderHolder(RecyclerView.ViewHolder rawHolder){
        HeaderViewHolder holder = (HeaderViewHolder)rawHolder;
        holder.setTitle(mContext.getString(R.string.library_behavior_title, mGoal.getTitle()));
        holder.setTitleBold();
        holder.setContent(mGoal.getDescription());
        holder.addButton(R.id.behavior_yes, R.string.library_behavior_yes, this);
    }

    @Override
    public void onClick(View v){
        if (v.getId() == R.id.behavior_yes){
            mListener.acceptGoal();
        }
    }


    /**
     * Listener interface for the adapter.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface GoalListener{
        /**
         * Called when the user taps the 'yes, I'm in' button.
         */
        void acceptGoal();
    }
}
