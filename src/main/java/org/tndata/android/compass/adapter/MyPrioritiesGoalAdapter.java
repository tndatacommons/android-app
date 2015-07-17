package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.ui.PriorityItemView;
import org.tndata.android.compass.util.ImageLoader;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by isma on 7/16/15.
 */
public class MyPrioritiesGoalAdapter extends RecyclerView.Adapter{

    private Context mContext;
    private List<Goal> mGoals;
    private ImageLoader mLoader;

    private boolean[] mExpandedGoals;


    /**
     * Constructor.
     *
     * @param context the application context.
     * @param goals the list of goals in a given category.
     */
    public MyPrioritiesGoalAdapter(@NonNull Context context, @NonNull List<Goal> goals, ImageLoader loader){
        mContext = context;
        mGoals = goals;
        mLoader = loader;

        mExpandedGoals = new boolean[mGoals.size()];
        for (int i = 0; i < mExpandedGoals.length; i++){
            mExpandedGoals[i] = false;
        }

        ViewHolder.viewPool = new LinkedList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_my_priorities_goal, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        ((ViewHolder)holder).name.setText(mGoals.get(position).getTitle());
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public int getItemCount(){
        return mGoals.size();
    }

    private void expand(ViewHolder holder, int position){
        Goal goal = mGoals.get(position);
        for (Behavior behavior:goal.getBehaviors()){
            PriorityItemView tv = getPriorityItemView();
            tv.setLeftPadding(20);
            tv.getTextView().setText(behavior.getTitle());
            mLoader.loadBitmap(tv.getImageView(), behavior.getIconUrl(), false);
            holder.offspring.addView(tv);
            Log.d("BehaviourActions", behavior.getActions().size()+"");
            for (Action action:behavior.getActions()){
                PriorityItemView tv2 = getPriorityItemView();
                tv2.setLeftPadding(40);
                tv2.getTextView().setText(action.getTitle());
                mLoader.loadBitmap(tv2.getImageView(), action.getIconUrl(), false);
                holder.offspring.addView(tv2);
            }
        }
        holder.offspring.setVisibility(View.VISIBLE);
    }

    private PriorityItemView getPriorityItemView(){
        if (ViewHolder.viewPool.isEmpty()){
            return new PriorityItemView(mContext);
        }
        else{
            return ViewHolder.viewPool.removeFirst();
        }
    }

    private void collapse(ViewHolder holder){
        for (int i = 0; i < holder.offspring.getChildCount(); i++){
            ViewHolder.viewPool.add((PriorityItemView)holder.offspring.getChildAt(i));
        }
        Log.d("Recycled views", ViewHolder.viewPool.size()+"");
        holder.offspring.removeAllViews();
        holder.offspring.setVisibility(View.GONE);
    }

    public void onItemClicked(ViewHolder holder, int position){
        if (mExpandedGoals[position]){
            collapse(holder);
        }
        else{
            expand(holder, position);
        }
        mExpandedGoals[position] = !mExpandedGoals[position];
    }

    /**
     * The item view holder. Also contains a pool of resources.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //This is a pool of TextViews to be reused. Any unused TextViews should be placed
        //  here. Before creating new ones, the list should be checked to see if there are
        //  any of them available.
        private static LinkedList<PriorityItemView> viewPool;

        private MyPrioritiesGoalAdapter mListener;

        //Components
        private TextView name;
        private LinearLayout offspring;

        public ViewHolder(View itemView, MyPrioritiesGoalAdapter listener){
            super(itemView);
            itemView.setOnClickListener(this);

            mListener = listener;

            name = (TextView)itemView.findViewById(R.id.my_priorities_goal_name);
            offspring = (LinearLayout)itemView.findViewById(R.id.my_priorities_goal_offspring);
        }

        @Override
        public void onClick(View v){
            mListener.onItemClicked(this, getAdapterPosition());
        }
    }
}
